package com.example.formation.controllers.request.formation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** FormationRequest */
public class FormationRequest {

  @NotBlank(message = "title is nessecary")
  private String title;

  @NotNull(message = "duration is nessecary")
  private Long duration;

  @NotBlank(message = "content must not be blank")
  private String content;

  @NotNull(message = "publicher must be set")
  private Integer publicherId;

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  public Integer getPublicherId() {
    return publicherId;
  }

  public Long getDuration() {
    return duration;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setPublicherId(Integer publicherId) {
    this.publicherId = publicherId;
  }

  public void setDuration(Long duration) {
    this.duration = duration;
  }
}
