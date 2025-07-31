package com.example.formation.data.dto;

import com.example.formation.data.models.UserInfo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserShortDto
 */
@Data
@NoArgsConstructor
public class UserShortDto {
  private Integer id;
  private String name;

  public UserShortDto(UserInfo user) {
    id = user.getId();
    name = user.getName();
  }
}
