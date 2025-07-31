package com.example.formation.data.dto;

import com.example.formation.data.models.Session;

import lombok.Data;

import java.time.LocalDateTime;

/** SessionDto */
@Data
public class SessionDto {
  private Integer id;
  private Integer formationId;
  private UserShortDto responsible;
  private LocalDateTime start;
  private LocalDateTime end;

  public SessionDto(Session session) {
    this.id = session.getId();
    this.formationId = session.getParentFormation().getId();
    this.responsible = new UserShortDto(session.getResponsible());
    this.start = session.getStartDate();
    this.end = session.getEnd();
  }
}
