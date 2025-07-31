package com.example.formation.controllers;

import com.example.formation.controllers.exception.BadRequestException;
import com.example.formation.controllers.exception.NotFoundException;
import com.example.formation.controllers.request.session.AttendanceRequest;
import com.example.formation.controllers.request.session.SessionEditRequest;
import com.example.formation.controllers.request.session.SessionRegisterRequest;
import com.example.formation.data.dto.AttendDto;
import com.example.formation.data.dto.SessionDto;
import com.example.formation.data.dto.SessionFullDto;
import com.example.formation.data.dto.UserShortDto;
import com.example.formation.data.models.AttendedSession;
import com.example.formation.data.models.Session;
import com.example.formation.data.servers.AttendedSessionService;
import com.example.formation.data.servers.FormationService;
import com.example.formation.data.servers.SessionService;
import com.example.formation.data.servers.UserService;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** SessionController */
@RestController
@AllArgsConstructor
@RequestMapping("/session/admin")
public class SessionAdminController {

    private final SessionService sessionService;
    private final AttendedSessionService AttendedSessionService;
    private final UserService userService;
    private final FormationService formationService;

    @Autowired
    public SessionAdminController(
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    SessionFullDto create(@Valid @RequestBody SessionRegisterRequest request) {

        var formation = formationService.getFormation(request.getFormationId());
        if (formation.isEmpty())
            throw new NotFoundException("formation does not exist");

        var responsible = userService.getUser(request.getResponsibleId());
        if (responsible.isEmpty())
            throw new NotFoundException("user" + request.getResponsibleId() + " does not exist");

        var session = sessionService.registerSession(
                new Session(
                        formation.get(),
                        request.getStart(),
                        request.getEnd(),
                        responsible.get()));
        return new SessionFullDto(session);
    }

    @DeleteMapping("/{sessionId}")
    ResponseEntity<String> deleteSession(@PathVariable("sessionId") Integer sessionId) {
        if (sessionService.getSession(sessionId).isEmpty())
            throw new NotFoundException("session not Found");
        sessionService.deleteSession(sessionId);
        return ResponseEntity.ok("Session: " + sessionId + " deleted successfully");
    }

    @PutMapping("{session}/edit")
    @ResponseStatus(value = HttpStatus.OK)
    String editSession(
            @PathVariable Integer sessionId, @RequestBody SessionEditRequest editRequest) {
        var sesssionInfo = sessionService.getSession(sessionId);
        if (sesssionInfo.isEmpty())
            throw new NotFoundException("session does not exist");
        var session = sesssionInfo.get();
        if (editRequest.getFormation() != null)
            session.setParentFormation(editRequest.getFormation());
        if (editRequest.getResponsible() != null)
            session.setResponsible(editRequest.getResponsible());
        if (editRequest.getStart() != null)
            session.setStart(editRequest.getStart());
        if (editRequest.getEnd() != null)
            session.setEnd(editRequest.getEnd());

        sessionService.updateSession(session);

        return "session has been updated";
    }

    @PostMapping("/{session}/addparticipant/{paticipantId}")
    public String addParticipant(
            @PathVariable(name = "session") Integer sessionId,
            @PathVariable(name = "paticipantId") Integer userId) {

        var sessionInfo = sessionService.getSession(sessionId);
        if (sessionInfo.isEmpty())
            throw new NotFoundException("session does not exist");
        var userInfo = userService.getUser(userId);
        if (userInfo.isEmpty())
            throw new NotFoundException("user does not exist");

        var user = userInfo.get();
        var session = sessionInfo.get();

        if (user.checkSession(session) || session.checkUser(user))
            throw new BadRequestException("user is already participant in session");

        user.addSession(session);
        session.addUser(user);
        userService.updateUser(user);
        sessionService.updateSession(session);

        return "user has been added to session";
    }

    @DeleteMapping("/{session}/remove-participant/{paticipantId}")
    @ResponseStatus(value = HttpStatus.OK)
    public String removeParticipant(
            @PathVariable(name = "session") Integer sessionId,
            @PathVariable(name = "paticipantId") Integer userId) {
        var userExist = userService.getUser(userId);
        if (userExist.isEmpty())
            throw new NotFoundException("user not found");

        var sessionExist = sessionService.getSession(sessionId);
        if (sessionExist.isEmpty())
            throw new NotFoundException("session not found");

        var user = userExist.get();
        var session = sessionExist.get();
        if (user.checkSession(session))
            throw new BadRequestException(("user is not participating in session"));
        if (session.checkUser(user))
            throw new BadRequestException("session does not have participant");

        user.removeSession(session);
        session.removeUser(user);
        userService.updateUser(user);
        sessionService.updateSession(session);

        return "user has been deleted from session";
    }

    @GetMapping("/{session}/get-participants")
    List<UserShortDto> getUserInfoOfAllSessions(@PathVariable Integer sessionId) {
        var sessionInfo = sessionService.getSession(sessionId);
        if (sessionInfo.isEmpty())
            throw new NotFoundException("session does not exist");
        return sessionService.getAllUsers(sessionInfo.get()).stream()
                .map(user -> new UserShortDto(user))
                .toList();
    }

    @PostMapping("/{session}/{user}/set-attendance")
    AttendDto setAttendance(
            @PathVariable(name = "session") Integer sessionId,
            @PathVariable(name = "user") Integer userId,
            @RequestBody AttendanceRequest request) {
        var session = sessionService.getSession(sessionId);
        if (session.isEmpty())
            throw new NotFoundException("session not Found");
        var user = userService.getUser(userId);
        if (user.isEmpty())
            throw new NotFoundException("user not Found");
        var attend = new AttendedSession(
                session.get(), user.get(), request.isPresent(), request.evaluation());
        attend = AttendedSessionService.register(attend);
        return new AttendDto(attend);
    }

    @PutMapping("/{session}/{user}/edit-attendance")
    AttendDto editAttendance(
            @PathVariable(name = "session") Integer sessionId,
            @PathVariable(name = "user") Integer userId,
            @RequestBody AttendanceRequest request) {
        var session = sessionService.getSession(sessionId);
        if (session.isEmpty())
            throw new NotFoundException("session not Found");
        var user = userService.getUser(userId);
        if (user.isEmpty())
            throw new NotFoundException("user not Found");
        var attendance = AttendedSessionService.getAttendedSession(sessionId, userId);
        if (attendance.isEmpty())
            throw new NotFoundException("attendance is not set!");

        var attend = attendance.get();
        Boolean isPresent = (request.isPresent() != null) ? request.isPresent() : attend.getIsPresent();
        String evaluation = (request.evaluation() != null) ? request.evaluation() : attend.getEvaluation();
        attend = new AttendedSession(session.get(), user.get(), isPresent, evaluation);
        attend = AttendedSessionService.register(attend);
        return new AttendDto(attend);
    }

    @DeleteMapping("/{session}/{user}/delete-attendance")
    String deleteAttendance(
            @PathVariable(name = "session") Integer sessionId,
            @PathVariable(name = "user") Integer userId) {
        var session = sessionService.getSession(sessionId);
        if (session.isEmpty())
            throw new NotFoundException("session not Found");
        var user = userService.getUser(userId);
        if (user.isEmpty())
            throw new NotFoundException("user not Found");

        var attendance = AttendedSessionService.getAttendedSession(sessionId, userId);
        if (attendance.isEmpty())
            throw new NotFoundException("attendance is not set!");

        AttendedSessionService.delete(attendance.get());
        return "user attendance on session is deleted";
    }
}
