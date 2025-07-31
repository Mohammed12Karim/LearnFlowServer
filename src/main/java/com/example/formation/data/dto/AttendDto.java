package com.example.formation.data.dto;

import com.example.formation.data.models.AttendedSession;

import lombok.Data;

/**
 * AttendDto
 */
@Data
public class AttendDto {
  private Integer userId;
  private Integer sessionId;
  private Boolean isPresent;
  private String evaluation;

  public AttendDto(AttendedSession attendedSession) {
    userId = attendedSession.getUser().getId();
    sessionId = attendedSession.getSession().getId();
    isPresent = attendedSession.getIsPresent();
    evaluation = attendedSession.getEvaluation();
  }

}
