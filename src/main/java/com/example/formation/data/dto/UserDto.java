
package com.example.formation.data.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.formation.data.models.UserInfo;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserDto
 */
@Data
@NoArgsConstructor
public class UserDto extends UserShortDto {

  private Set<String> emails;
  private List<SessionDto> sessions;

  public UserDto(UserInfo user) {
    super(user);
    this.emails = user.getEmails().stream().map(email -> email.getEmail()).collect(Collectors.toSet());
    this.sessions = (user.getSessions() != null)
        ? user.getSessions().stream().map(session -> new SessionDto(session)).toList()
        : null;
  }

}
