package com.example.formation.controllers;

import com.example.formation.controllers.exception.NotAuthorizedException;
import com.example.formation.controllers.exception.NotFoundException;
import com.example.formation.controllers.exception.TokenNotValid;
import com.example.formation.controllers.request.user.*;
import com.example.formation.data.dto.UserDto;
import com.example.formation.data.dto.UserShortDto;
import com.example.formation.data.models.Session;
import com.example.formation.data.models.UserInfo;
import com.example.formation.data.servers.JwtService;
import com.example.formation.data.servers.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  @Autowired
  public UserController(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @PostMapping("/create")
  public String create(@Valid @RequestBody SignUpForm request) {
    try {
      UserInfo user = new UserInfo(request.name(), request.password(), "user");
      UserInfo created = userService.registerUser(user, request.email());
      return jwtService.generateToken(created.getId(), created.getEmail());
    } catch (IllegalArgumentException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }

  @PostMapping("/auth")
  @ResponseStatus(HttpStatus.OK)
  public String authenticate(@Valid @RequestBody LogInForm request) {
    var user = userService.authenticate(request.email(), request.password());
    if (user.isEmpty())
      throw new NotAuthorizedException("not authorized");
    return jwtService.generateToken(user.get().getId(), request.email());
  }

  @GetMapping("/{userId}")
  public UserShortDto getUser(
      @PathVariable Integer userId, @AuthenticationPrincipal UserInfoDetails userDetails) {
    var Exist = userService.getUser(userId);
    if (Exist.isEmpty())
      throw new NotFoundException("user does not exist");

    var user = Exist.get();
    if (userDetails.getUserId().equals(userId))
      return new UserDto(user);

    return new UserShortDto(user);
  }

  @GetMapping("/{user}/sessions")
  List<Session> getAllSessions(
      @PathVariable(name = "user") Integer userId,
      @AuthenticationPrincipal UserInfoDetails userDetails) {
    var userExist = userService.getUser(userId);
    if (userExist.isEmpty())
      throw new NotFoundException("user does not exist");

    if (!userDetails.getUserId().equals(userId))
      throw new NotAuthorizedException("Not Authorized");

    return userExist.get().getSessions();
  }

  @DeleteMapping("{userId}/drop-email")
  ResponseEntity<String> dropEmailFromUser(
      @PathVariable Integer userId,
      @RequestParam(name = "email") String email,
      @AuthenticationPrincipal UserInfoDetails userDetails) {
    if (email == null)
      return ResponseEntity.badRequest().body("email must be set");
    try {
      var deleter = userService.getUser(userDetails.getUsername());
      if (deleter.isEmpty())
        return ResponseEntity.badRequest().build();
      if (!deleter.get().isAdmin() && !deleter.get().getId().equals(userId))
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      userService.removeEmailFromUser(userId, email);
      return ResponseEntity.ok(String.format("email %s has been dropped", email));
    } catch (IllegalArgumentException e) {
      if (e.getMessage() == "user does not have email") {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(String.format("user: %d does not have email: %s", userId, email));
      }
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("{user}/add-email")
  public ResponseEntity<String> addEmailToUser(
      @PathVariable(name = "user") Integer userId, @Valid @RequestBody EmailRequest request) {
    if (request.getEmail() == null)
      return ResponseEntity.badRequest().body("email and userId must be set");
    try {
      userService.addEmailToUser(userId, request.getEmail());
      var doesExist = userService.getUser(userId);
      var user = doesExist.get();
      return ResponseEntity.ok(
          "Email "
              + request.getEmail()
              + "  is saved for user: "
              + user.getId()
              + " with name: "
              + user.getName());
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.badRequest().body(ex.getMessage());
    }
  }

  @PutMapping("/{userId}")
  public UserShortDto editUser(
      @PathVariable Integer userId,
      @AuthenticationPrincipal UserInfoDetails userInfoDetails,
      @Valid @RequestBody EditRequest edit) {
    if (!userInfoDetails.getUserId().equals(userId)) {
      throw new NotAuthorizedException("user does not have permission");
    }

    var userInfo = userService.getUser(userId);

    if (userInfo.isEmpty())
      throw new NotFoundException("user does not exist");
    var user = userInfo.get();

    if (edit.name() != null)
      user.setName(edit.name());
    if (edit.password() != null) {
      user.setPassword(edit.password());
      user.hashPassword(passwordEncoder);
    }
    if (edit.emails() != null)
      user.setEmails(edit.emails());

    user = userService.updateUser(user);

    return new UserDto(user);
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<String> deleteUser(
      @PathVariable Integer userId, @AuthenticationPrincipal UserInfoDetails userDetails) {
    try {
      Integer deleterId = userDetails.getUserId();
      userService.deleteUser(userId, deleterId);
      return ResponseEntity.ok("User deleted");
    } catch (NullPointerException e) {
      return ResponseEntity.badRequest().body("Missing required parameters");
    } catch (AccessDeniedException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Permission denied");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("user does not exist");
    }
  }
}

@Data
class EmailRequest {
  @NotBlank(message = "email is nessecary")
  @Email()
  private String email;
}
