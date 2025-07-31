package com.example.formation.controllers.request.user;

import com.example.formation.data.models.UserInfo;
import com.example.formation.data.servers.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/** UserDetails */
@Data
@NoArgsConstructor
public class UserInfoDetails implements UserDetails {
  private Integer userId;
  private String name;
  private String username;
  private String email;
  private String password;
  private List<GrantedAuthority> authorities;

  public UserInfoDetails(UserInfo userInfo) {
    this.username = userInfo.getEmail();
    this.name = userInfo.getName();
    // this.email = userInfo.getEmail();
    this.password = userInfo.getPassword();
    this.authorities = List.of(userInfo.getRoles().split(",")).stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
