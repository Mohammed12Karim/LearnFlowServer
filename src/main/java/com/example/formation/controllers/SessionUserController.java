package com.example.formation.controllers;

import com.example.formation.controllers.exception.BadRequestException;
import com.example.formation.controllers.request.user.UserInfoDetails;
import com.example.formation.controllers.response.session.SessionResponse;
import com.example.formation.data.dto.SessionDto;
import com.example.formation.data.servers.AttendedSessionService;
import com.example.formation.data.servers.FormationService;
import com.example.formation.data.servers.SessionService;
import com.example.formation.data.servers.UserService;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** SessionController */
@RestController
@AllArgsConstructor
@RequestMapping("/session/user")
public class SessionUserController {

    private final SessionService sessionService;
    private final AttendedSessionService AttendedSessionService;
    private final UserService userService;
    private final FormationService formationService;

    @Autowired
    public SessionUserController(
            SessionService sessionService,
            AttendedSessionService AttendedSessionService,
            FormationService formationService,
            UserService userService) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.AttendedSessionService = AttendedSessionService;
        this.formationService = formationService;
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<SessionDto> findAll() {
        var sessions = sessionService.findAll();
        return sessions == null
                ? null
                : sessions.stream().map(session -> new SessionDto(session)).toList();
    }

    @GetMapping("/{sessionId}")
    public SessionDto getSession(@PathVariable(name = "sessionId") Integer sessionId) {
        var session = sessionService.getSession(sessionId);
        if (session.isEmpty())
            throw new BadRequestException();
        return new SessionDto(session.get());
    }

    @PostMapping("/{session}/participate")
    public SessionResponse addParticipant(
            @PathVariable(name = "sesssion") Integer sesionId,
            @AuthenticationPrincipal UserInfoDetails userDetails) {
        var session = sessionService.getSession(sesionId);
        if (session.isEmpty())
            throw new BadRequestException("session does not exist");
        var user = userService.getUser(userDetails.getUsername());
        if (user.isEmpty())
            throw new BadRequestException("user does not exist");
        if (session.get().checkUser(user.get()))
            throw new BadRequestException("user already participating in session");
        session.get().addUser(user.get());
        return new SessionResponse(session.get());
    }

    @DeleteMapping("/{session}/unparticipate")
    public ResponseEntity<String> removeParticipant(
            @PathVariable Integer sessionId, @AuthenticationPrincipal UserInfoDetails userDetails) {

        var userExist = userService.getUser(userDetails.getUsername());
        if (userExist.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user does not exist!");
        var sessionExist = sessionService.getSession(sessionId);
        if (sessionExist.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("session does not exist!");

        var user = userExist.get();
        var session = sessionExist.get();
        user.removeSession(session);
        session.removeUser(user);
        userService.updateUser(user);
        sessionService.updateSession(session);

        return ResponseEntity.ok("user has been deleted from session");
    }
}
