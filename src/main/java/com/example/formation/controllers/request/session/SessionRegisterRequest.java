package com.example.formation.controllers.request.session;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.example.formation.data.models.Formation;
import com.example.formation.data.models.UserInfo;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionRegisterRequest {

  @NotNull
  private Integer formationId;
  @NotNull
  private LocalDateTime start;
  @NotNull
  private LocalDateTime end;
  @NotNull
  private Integer responsibleId;
}
