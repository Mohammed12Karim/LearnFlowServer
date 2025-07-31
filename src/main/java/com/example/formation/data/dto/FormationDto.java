package com.example.formation.data.dto;

import com.example.formation.data.models.Formation;

import lombok.Data;

/** FormationDto */
@Data
public class FormationDto {

    private Integer id;
    private String title;
    private String content;
    private Long duration;
    private UserShortDto publicher;

    public FormationDto(Formation formation) {
        this.id = formation.getId();
        title = formation.getTitle();
        content = formation.getContent();
        duration = formation.getDuration();
        publicher = new UserShortDto(formation.getPublicher());
    }
}
