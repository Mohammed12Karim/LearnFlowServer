package com.example.formation.controllers;

import com.example.formation.controllers.exception.BadRequestException;
import com.example.formation.controllers.exception.NotAuthorizedException;
import com.example.formation.controllers.exception.NotFoundException;
import com.example.formation.controllers.request.user.*;
import com.example.formation.data.dto.FormationDto;
import com.example.formation.data.dto.SessionFullDto;
import com.example.formation.data.dto.UserDto;
import com.example.formation.data.models.UserInfo;
import com.example.formation.data.security.PasswordUtil;
import com.example.formation.data.servers.UserService;

import jakarta.validation.Valid;

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final PasswordUtil passwordEncoder;

    @Autowired
    public AdminController(UserService userService, PasswordUtil passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public void checkAdmin(Integer adminId) {
        var adminInfo = userService.getUser(adminId);
        if (adminInfo.isEmpty())
            throw new NotFoundException("Admin not Found");
        if (!adminInfo.get().isAdmin())
            throw new NotAuthorizedException("User is not Admin");
    }

    public void checkAdmin(Integer adminId, String msg) {
        var adminInfo = userService.getUser(adminId);
        if (adminInfo.isEmpty())
            throw new NotFoundException("Admin not Found");
        if (!adminInfo.get().isAdmin())
            throw new NotAuthorizedException(msg);
    }

    @GetMapping
    public List<UserDto> findAll() {
        var users = userService.findAll();
        return users == null ? null : users.stream().map(user -> new UserDto(user)).toList();
    }

    @GetMapping("/hello")
    String hello() {
        return "hello";
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody SignUpForm request) {
        try {
            UserInfo user = new UserInfo(request.name(), request.password(), "user,admin");
            UserInfo created = userService.registerUser(user, request.email());
            return new UserDto(created);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }

    @GetMapping("/{userId}/by/{admin}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUser(@PathVariable(name = "userId") Integer userId,
            @PathVariable(name = "admin") Integer adminId) {
        checkAdmin(adminId);
        var user = userService.getUser(userId);
        if (user.isEmpty())
            throw new NotFoundException("user does not exist");
        return new UserDto(user.get());
    }

    @PutMapping("/{user}/setadmin/by/{admin}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto setToAdmin(
            @PathVariable(name = "user") Integer userId,
            @PathVariable(name = "admin") Integer adminId) {
        checkAdmin(adminId, "User does not have the permession to set other users to admin");

        var user = userService.getUser(userId);
        if (user.isEmpty())
            throw new BadRequestException("user does not exist");
        var roles = user.get().getRoles();
        user.get().setRoles((roles != null && roles.isBlank()) ? roles + "," : "" + "admin");
        return new UserDto(userService.updateUser(user.get()));
    }

    @GetMapping("/{user}/formations/by/{admin}")
    @ResponseStatus(HttpStatus.OK)
    Set<FormationDto> getAllFormations(
            @PathVariable(name = "user") Integer userId,
            @PathVariable(name = "admin") Integer adminId) {
        checkAdmin(adminId);

        var userExist = userService.getUser(userId);
        if (userExist.isEmpty())
            throw new NotFoundException("user does not exist");

        return userExist.get().getSessions().stream()
                .map(session -> new FormationDto(session.getParentFormation()))
                .collect(Collectors.toCollection(HashSet::new));
    }

    @GetMapping("/{user}/sessions/by/{admin}")
    @ResponseStatus(HttpStatus.OK)
    List<SessionFullDto> getAllSessions(
            @PathVariable(name = "user") Integer userId,
            @PathVariable(name = "admin") Integer adminId) {
        checkAdmin(adminId);
        var userExist = userService.getUser(userId);

        if (userExist.isEmpty())
            throw new NotFoundException("user does not exist");

        return userExist.get().getSessions().stream()
                .map(session -> new SessionFullDto(session))
                .toList();
    }

    @DeleteMapping("{userId}/drop-email/by/{admin}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<String> dropEmailFromUserInfo(
            @PathVariable Integer userId,
            @PathVariable(name = "admin") Integer adminId,
            @RequestParam(name = "email") String email) {
        checkAdmin(adminId);
        if (email == null)
            return ResponseEntity.badRequest().body("email must be set");
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

    @PostMapping("{user}/add-email/by/{admin}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> addEmailToUser(
            @PathVariable(name = "user") Integer userId,
            @PathVariable(name = "admin") Integer adminId,
            @Valid @RequestBody EmailRequest request) {
        checkAdmin(adminId);
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

    @PutMapping("/{userId}/by/{admin}")
    @ResponseStatus(value = HttpStatus.OK)
    public UserDto editUser(@PathVariable Integer userId,
            @PathVariable(name = "admin") Integer adminId,
            @Valid @RequestBody EditRequest edit) {
        checkAdmin(adminId);
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

    @DeleteMapping("/{userId}/by/{admin}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteUser(@PathVariable Integer userId,
            @PathVariable(name = "admin") Integer adminId) {
        checkAdmin(adminId);
        try {
            userService.deleteUser(userId);
            return "user deleted";
        } catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
