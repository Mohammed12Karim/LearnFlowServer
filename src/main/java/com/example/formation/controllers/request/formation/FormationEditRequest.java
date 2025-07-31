package com.example.formation.controllers.request.formation;

import com.example.formation.data.models.Formation;
import com.example.formation.data.models.UserInfo;

import lombok.Data;

/** FormationEditRequest */
@Data
public class FormationEditRequest {
  private String title;
  private String content;
  private Long Duration;
  private Integer publicherId;

  public Formation toFormation(Integer id, UserInfo publicher) {
    Formation formation = new Formation();
    formation.setId(id);
    formation.setTitle(title);
    formation.setContent(content);
    formation.setDuration(Duration);
    formation.setPublicher(publicher);
    return formation;
  }
}
