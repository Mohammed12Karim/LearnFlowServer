package com.example.formation.controllers;

import com.example.formation.controllers.exception.BadRequestException;
import com.example.formation.controllers.exception.NotAuthorizedException;
import com.example.formation.controllers.exception.NotFoundException;
import com.example.formation.controllers.request.user.*;
import com.example.formation.data.dto.UserDto;
import com.example.formation.data.dto.UserShortDto;
import com.example.formation.data.models.Session;
import com.example.formation.data.models.UserInfo;
import com.example.formation.data.security.PasswordUtil;
import com.example.formation.data.servers.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  private final PasswordUtil passwordEncoder;

  @Autowired
  public UserController(UserService userService, PasswordUtil passwordEncoder) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  public void checkUser(Integer userId, Integer actionerId) throws NotAuthorizedException {
    if (!userId.equals(actionerId))
      throw new NotAuthorizedException("User can't modify other users informaitions");
  }

  public void checkUser(Integer userId, Integer actionerId, String msg) throws NotAuthorizedException {
    if (!userId.equals(actionerId))
      throw new NotAuthorizedException(msg);
  }

  @PostMapping
  public UserDto create(@Valid @RequestBody SignUpForm request) {
    try {
      UserInfo user = new UserInfo(request.name(), request.password(), "user");
      UserInfo created = userService.registerUser(user, request.email());
      return new UserDto(created);
    } catch (IllegalArgumentException ex) {
      throw new NotFoundException(ex.getMessage());
    }
  }

  @PostMapping("/auth")
  @ResponseStatus(HttpStatus.OK)
  public UserDto authenticate(@Valid @RequestBody LogInForm request) {
    var user = userService.authenticate(request.email(), request.password());
    if (user.isEmpty())
      throw new NotAuthorizedException("not authorized");
    return new UserDto(user.get());
  }

  @GetMapping("/{userId}/by/{informer}")
  public UserShortDto getUser(
      @PathVariable(name = "userId") Integer userId,
      @PathVariable(name = "informer") Integer informer
      ){
    var Exist = userService.getUser(userId);
    if (Exist.isEmpty())
      throw new NotFoundException("user does not exist");

    var user = Exist.get();
    if (informer.equals(userId))
      return new UserDto(user);

    return new UserShortDto(user);
  }

  @GetMapping("/{user}/sessions/by/{informer}")
  List<Session> getAllSessions(
      @PathVariable(name = "user") Integer userId,
      @PathVariable(name = "informer") Integer informer){
      
    var userExist = userService.getUser(userId);
    if (userExist.isEmpty())
      throw new NotFoundException("user does not exist");

    if (informer.equals(userId))
      throw new NotAuthorizedException("Not Authorized");

    return userExist.get().getSessions();
  }

  @DeleteMapping("{userId}/drop-email/by/{dropper}")
  ResponseEntity<String> dropEmailFromUser(
      @PathVariable(name = "userId") Integer userId,
      @PathVariable(name = "dropper") Integer dropperId,
      @RequestParam(name = "email") String email){
    checkUser(userId, dropperId);
    if (email == null)
    throw new BadRequestException("email must be set");
    try {
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

  @PostMapping("{user}/add-email/by/{adder}")
  public ResponseEntity<String> addEmailToUser(
      @PathVariable(name = "user") Integer userId,
      @PathVariable(name = "adder") Integer adderId,
      @Valid @RequestBody EmailRequest request) {
    checkUser(userId, adderId);

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

  @PutMapping("/{userId}/by/{editorId}")
  public UserShortDto editUser(
      @PathVariable Integer userId,
      @PathVariable(name = "editorId") Integer editorId,
      @Valid @RequestBody EditRequest edit) {
    checkUser(userId, editorId);

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

  @DeleteMapping("/{userId}/by/{deleter}")
  public ResponseEntity<String> deleteUser(
      @PathVariable(name = "userId") Integer userId,
      @PathVariable(name = "deleter") Integer deleterId) {
    if (userService.getUser(deleterId).isEmpty())
      throw new NotFoundException("deleter does not exist");
    try {
      userService.deleteUser(userId, deleterId);
      return ResponseEntity.ok("User deleted");
    } catch (NullPointerException e) {
      return ResponseEntity.badRequest().body("Missing required parameters");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("user does not exist");
    }
  }

  @Data
  static class EmailRequest {
    @NotBlank(message = "email is nessecary")
    @Email()
    private String email;
  }
}
