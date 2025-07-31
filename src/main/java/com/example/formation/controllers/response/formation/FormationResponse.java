package com.example.formation.controllers.response.formation;

import com.example.formation.data.models.Formation;

import lombok.Data;

/** FormationRegisterResponse */
@Data
public class FormationResponse {
  private Integer id;

  private String title;

  private Long duration;

  private String content;

  private Integer publicherId;

  public FormationResponse(Formation formation) {
    id = formation.getId();
    title = formation.getTitle();
    duration = formation.getDuration();
    content = formation.getContent();
    publicherId = formation.getPublicher().getId();
  }
}
