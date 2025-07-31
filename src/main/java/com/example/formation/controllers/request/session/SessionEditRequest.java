package com.example.formation.controllers.request.session;

import com.example.formation.data.models.Formation;
import com.example.formation.data.models.Session;
import com.example.formation.data.models.UserInfo;

import lombok.Data;

import java.time.LocalDateTime;

/** SessionEditRequest */
@Data
public class SessionEditRequest {
  private Formation formation;
  private LocalDateTime start;
  private LocalDateTime end;
  private UserInfo responsible;

  public Session toSession(Integer id) {
    Session session = new Session();
    session.setId(id);
    session.setParentFormation(formation);
    session.setStartDate(start);
    session.setEndDate(end);
    session.setResponsibleId(responsible);
    return session;
  }
}
