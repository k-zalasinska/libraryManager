package com.example.libraryapi.users;

import com.example.libraryapi.exceptions.ObjectNotFoundException;
import com.example.libraryapi.users.CustomUserDetails;
import com.example.libraryapi.users.UserDto;
import com.example.libraryapi.usersService.UserService;
import io.swagger.annotations.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "User Management System", tags = {"User"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @ApiOperation(value = "Get all users")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @ApiOperation(value = "Get user by ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @ApiParam(value = "User ID", required = true) @PathVariable Long id) {
        UserDto userDto = userService.getUserById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User with id " + id + " was not found."));
        return ResponseEntity.ok(userDto);
    }


    @ApiOperation(value = "Check if username is available")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Username is available"),
            @ApiResponse(code = 409, message = "Username is not available")
    })
    @GetMapping("/check-username")
    public ResponseEntity<Void> checkUsernameAvailability(
            @ApiParam(value = "Username to check", required = true) @RequestParam String username) {
        boolean isAvailable = userService.isUsernameAvailable(username);

        if (isAvailable) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @ApiOperation(value = "Update user field")
    @PutMapping("/{username}")
    public ResponseEntity<UserDto> updateUserField(
            @ApiParam(value = "Username", required = true) @PathVariable String username,
            @ApiParam(value = "Updated user data", required = true) @Valid @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUserField(username, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @ApiOperation(value = "Delete user by username")
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteUser(
            @ApiParam(value = "Username", required = true) @PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Delete user by ID")
    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteUserById(
            @ApiParam(value = "User ID", required = true) @PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Delete user account")
    @DeleteMapping("/{username}/account")
    public ResponseEntity<Void> deleteAccount(
            @ApiParam(value = "Username", required = true) @PathVariable String username,
            @ApiParam(value = "Password", required = true) @RequestParam String password) {
        userService.deleteAccount(username, password);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Change user password")
    @PutMapping("/{username}/change-password")
    public ResponseEntity<Void> changePassword(
            @ApiParam(value = "Username", required = true) @PathVariable String username,
            @ApiParam(value = "Old Password", required = true) @RequestParam String oldPassword,
            @ApiParam(value = "New Password", required = true) @RequestParam String newPassword) {
        userService.changePassword(username, oldPassword, newPassword);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Handle login failure")
    @PutMapping("/{username}/login-failure")
    public ResponseEntity<Void> handleLoginFailure(
            @ApiParam(value = "Username", required = true) @PathVariable String username) {
        userService.handleLoginFailure(username);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Handle successful login")
    @PutMapping("/{username}/successful-login")
    public ResponseEntity<Void> handleSuccessfulLogin(
            @ApiParam(value = "Username", required = true) @PathVariable String username) {
        userService.handleSuccessfulLogin(username);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Load user details by username")
    @GetMapping("/{username}/details")
    public ResponseEntity<CustomUserDetails> loadUserDetailsByUsername(
            @ApiParam(value = "Username", required = true) @PathVariable String username) {
        CustomUserDetails userDetails = (CustomUserDetails) userService.loadUserDetailsByUsername(username);
        return ResponseEntity.ok(userDetails);
    }
}
