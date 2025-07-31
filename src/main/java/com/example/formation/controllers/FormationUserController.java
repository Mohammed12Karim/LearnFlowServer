package com.example.formation.controllers;

import com.example.formation.controllers.exception.BadRequestException;
import com.example.formation.controllers.response.formation.FormationResponse;
import com.example.formation.data.dto.FormationDto;
import com.example.formation.data.servers.FormationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** FormationController */
@RestController
@RequestMapping("/formation/user")
public class FormationUserController {

    @Autowired
    FormationService formationService;

    @GetMapping
    public List<FormationDto> findAll() {
        var formations = formationService.findAll();
        return formations == null ? null : formations.stream().map(formation -> new FormationDto(formation)).toList();
    }

    @GetMapping("/{formationId}")
    @ResponseStatus(value = HttpStatus.OK)
    public FormationDto getFormation(@PathVariable(name = "formationId") Integer formationId) {
        var formation = formationService.getFormation(formationId);
        if (formation.isEmpty())
            throw new BadRequestException();
        return new FormationDto(formation.get());
    }
}
