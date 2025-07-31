package com.example.formation.data.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.formation.data.models.Formation;

import lombok.Data;

/**
 * FormationFullDto
 */
@Data
public class FormationFullDto extends FormationDto {

  private List<SessionDto> ongoingSessions;

  public FormationFullDto(Formation formation) {
    super(formation);
    ongoingSessions = (formation.getSessions() != null) ? formation.getSessions().stream()
        .map(
            session -> session.getEndDate().isAfter(LocalDateTime.now())
                ? new SessionDto(session)
                : null)
        .filter(session -> session != null)
        .toList() : null;
  }

}
