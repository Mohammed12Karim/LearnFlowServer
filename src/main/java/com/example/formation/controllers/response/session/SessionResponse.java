
package com.example.formation.controllers.response.session;

import java.time.LocalDateTime;

import com.example.formation.data.models.Formation;
import com.example.formation.data.models.Session;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * SessionResponse
 */
@Data
@AllArgsConstructor
public class SessionResponse {
  private Integer id;
  private Formation formation;

  private LocalDateTime start;
  private LocalDateTime end;

  public SessionResponse(Session session){
    this.id = session.getId();
    this.formation = session.getParentFormation();
    this.start = session.getStart();
    this.end  = session.getEnd();
  }
  
}
