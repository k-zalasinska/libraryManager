package service;

import exception.ObjectNotFoundInRepositoryException;
import lombok.RequiredArgsConstructor;
import model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BorrowService {
    public final BorrowRepository borrowRepository;
    public final CopyRepository copyRepository;
    public final UserRepository userRepository;
    public final UserActivityRepository userActivityRepository;
    public final LateFeeRepository lateFeeRepository;

    @Transactional
    public void borrowCopy(Long copyId, Long userId) {
        Copy copy = copyRepository.findById(copyId)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("The copy with the given ID was not found.", copyId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("The user with the given ID was not found.", userId));

        LocalDate currentDate = LocalDate.now();

        if (!isCopyAvailable(copyId)) {
            throw new CopyNotAvailableException("The copy is not available for borrowing.", copyId);
        }

        Borrow borrow = createBorrowRecord(copy, user, currentDate);
        copy.setBorrowedDate(currentDate);
        updateCopyAndSave(copy, borrow);
        logUserActivity(user, copy, "borrow", currentDate, null);

    }

    private Borrow createBorrowRecord(Copy copy, User user, LocalDate currentDate) {
        LocalDate expectedReturnDate = currentDate.plusDays(30);
        Borrow borrow = new Borrow();
        borrow.setCopy(copy);
        borrow.setUser(user);
        borrow.setDateOfBorrow(currentDate);
        borrow.setReturnDate(expectedReturnDate);
        return borrow;
    }

    private void updateCopyAndSave(Copy copy, Borrow borrow) {
        borrowRepository.save(borrow);
        copyRepository.save(copy);
    }

    public boolean isCopyAvailable(final Long id) {
        Optional<Copy> optionalCopy = copyRepository.findById(id);
        if (optionalCopy.isEmpty()) {
            return false;
        }

        Copy copy = optionalCopy.get();

        if (copy.getBorrowedDate() == null) {
            return true;
        } else if (copy.getReturnDate() == null) {
            return false;
        } else {
            LocalDate currentDate = LocalDate.now();
            LocalDate returnDate = copy.getReturnDate();

            return returnDate.isBefore(currentDate);
        }
    }

    @Transactional
    public void returnCopy(Long copyId, Long userId) {
        Copy copy = copyRepository.findById(copyId)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("The copy with the given ID was not found.", copyId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundInRepositoryException("The user with the given ID was not found.", userId));

        LocalDate currentDate = LocalDate.now();

        Borrow borrow = new Borrow();
        borrow.setCopy(copy);
        borrow.setUser(user);
        borrow.setDateOfBorrow(currentDate);

        handleLateReturn(borrow, currentDate);
        updateBorrowAndCopy(borrow, copy, currentDate);
        logUserActivity(user, copy, "return", borrow.getDateOfBorrow(), currentDate);
    }

    private void handleLateReturn(Borrow borrow, LocalDate returnDate) {
        if (returnDate.isAfter(borrow.getExpectedReturnDate())) {
            long daysLate = ChronoUnit.DAYS.between(borrow.getExpectedReturnDate(), returnDate);
            double lateFeePerDay = 5.0;
            double lateFee = daysLate * lateFeePerDay;

            LateFee lateFeeRecord = new LateFee();
            lateFeeRecord.setLoan(borrow);
            lateFeeRecord.setAmount(lateFee);
            lateFeeRecord.setDate(returnDate);
            lateFeeRepository.save(lateFeeRecord);
        }
    }

    private void updateBorrowAndCopy(Borrow borrow, Copy copy, LocalDate returnDate) {
        borrow.setReturnDate(returnDate);
        copy.setExpectedReturnDate(returnDate);
        borrowRepository.save(borrow);
        copyRepository.save(copy);
    }

    private void logUserActivity(User user, Copy copy, String actionType, LocalDate borrowDate, LocalDate returnDate) {
        UserActivity userActivity = new UserActivity();
        userActivity.setUser(user);
        userActivity.setCopy(copy);
        userActivity.setActionType(actionType);
        userActivity.setBorrowDate(borrowDate);
        userActivity.setReturnDate(returnDate);
        if ("return".equals(actionType)) {
            userActivity.setReturnDate(returnDate);
        }
        userActivityRepository.save(userActivity);
    }


}