package com.example.formation.data.dto;

import java.util.List;

import com.example.formation.data.models.Session;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SessionFullDto
 */
@Data
public class SessionFullDto extends SessionDto {
  private List<UserShortDto> participants;

  public SessionFullDto(Session session) {
    super(session);
    participants = (session.getParticipants() != null)?session.getParticipants().stream().filter(par -> par !=null).map(participant -> new UserShortDto(participant)).toList(): null;
  }

}
