package com.example.formation.controllers.response.user;

import com.example.formation.data.models.UserInfo;

import lombok.Data;

import java.util.List;

/** UserResponse */
@Data
public class UserResponse {
  private Integer id;
  private String name;

  public UserResponse(UserInfo user) {
    name = user.getName();
  }
}
