package com.example.formation.controllers;

import com.example.formation.controllers.exception.BadRequestException;
import com.example.formation.controllers.exception.NotFoundException;
import com.example.formation.controllers.request.formation.FormationEditRequest;
import com.example.formation.controllers.request.formation.FormationRequest;
import com.example.formation.controllers.response.formation.FormationResponse;
import com.example.formation.data.dto.FormationDto;
import com.example.formation.data.models.Formation;
import com.example.formation.data.models.Session;
import com.example.formation.data.servers.FormationService;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileWriter;
import java.util.List;
import java.util.Optional;

/** FormationController */
@RestController
@RequestMapping("/formation/admin")
public class FormationAdminController {

    @Autowired
    FormationService formationService;
    @Autowired
    UserService userService;

    @GetMapping
    public List<FormationDto> findAll() {
        var formations = formationService.findAll();
        return formations == null ? null : formations.stream().map(formation -> new FormationDto(formation)).toList();
    }

    @GetMapping("/{formation}")
    public Formation getFormation(@PathVariable Integer formationId) {
        var formationInfo = formationService.getFormation(formationId);
        if (formationInfo.isEmpty())
            throw new NotFoundException("formation does not exist");
        return formationInfo.get();
    }

    @PostMapping("/create")
    public FormationDto registerFormation(@Valid @RequestBody FormationRequest formationRequest) {

        var user = userService.getUser(formationRequest.getPublicherId());
        if (user.isEmpty())
            throw new BadRequestException("publicher does not exist!");

        Formation formation = formationService.registerFormation(
                new Formation(
                        formationRequest.getTitle(),
                        formationRequest.getDuration(),
                        formationRequest.getContent(),
                        user.get()));
        return new FormationDto(formation);
    }

    @DeleteMapping("/{formationId}")
    public ResponseEntity<String> deleteFormation(@PathVariable Integer formationId) {

        Optional<Formation> formationExist = formationService.getFormation(formationId);
        if (formationExist.isEmpty()) {
            return ResponseEntity.badRequest().body("formation does not exist");
        }
        Formation formation = formationExist.get();
        try {
            formationService.deleteFormation(formation.getId());
            return ResponseEntity.ok("formation deleted");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{formation}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody FormationResponse editFormation(
            @PathVariable(name = "formation") Integer formationId,
            @Valid @RequestBody FormationEditRequest editRequest) {
        var formationExist = formationService.getFormation(formationId);
        if (formationExist.isEmpty())
            throw new BadRequestException("formation Does Not Exist");
        var formation = formationExist.get();
        if (editRequest.getTitle() != null)
            formation.setTitle(editRequest.getTitle());
        if (editRequest.getContent() != null)
            formation.setContent(editRequest.getContent());
        if (editRequest.getDuration() != null)
            formation.setDuration(editRequest.getDuration());
        FormationResponse response = new FormationResponse(formation);
        return response;
    }

    @GetMapping("/{formation}/sessions")
    public List<Session> getAllSessions(@PathVariable Integer formationId) {
        var formationInfo = formationService.getFormation(formationId);
        if (formationInfo.isEmpty())
            throw new NotFoundException("formation does not exist");
        return formationInfo.get().getSessions();
    }
}
