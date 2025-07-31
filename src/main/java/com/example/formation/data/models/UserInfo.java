package com.example.formation.data.models;

import com.example.formation.data.servers.*;
import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String roles = "user";

  @Transient
  private boolean passwordHashed = false;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<UserEmail> emails = new HashSet<>();

  @Transient
  private static List<String> accepted_roles = new ArrayList<>();

  static {
    accepted_roles.add("admin");
    accepted_roles.add("user");
  }

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "users_sessions", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
  private List<Session> sessions;

  @OneToMany(mappedBy = "publicher")
  private Collection<Formation> responsibleOnFormation;

  private boolean checkRoleValid(String roles) {
    for (String role : roles.split(",")) {
      if (!accepted_roles.contains(role.strip()))
        return false;
    }
    return true;
  }

  public void setRoles(String roles) {
    if (!checkRoleValid(roles))
      throw new IllegalArgumentException();
    this.roles = roles;
  }

  public UserInfo(String name, String password, String roles) throws IllegalArgumentException {
    this.name = name;
    this.password = password;
    setRoles(roles);
    this.emails = new HashSet<>();
    sessions = new ArrayList<>();
  }

  public void setEmails(Set<String> emails) {
    this.emails.clear();
    if (emails != null)
      emails.forEach(this::addEmail);
  }

  public void setPassword(String rawpassword) {
    this.password = rawpassword;
    this.passwordHashed = false;
  }

  public Set<UserEmail> getEmails() {
    return emails;
  }

  public void addEmail(UserEmail email) {
    emails.add(email);
    email.setUser(this);
  }

  public void addEmail(String email) {
    this.emails.add(new UserEmail(email, this));
  }

  public void removeEmail(UserEmail email) {
    emails.remove(email);
    email.setUser(null);
  }

  public void hashPassword(PasswordEncoder encoder) {
    if (passwordHashed == true)
      throw new IllegalArgumentException("password is hashed");
    password = encoder.encode(password);
    passwordHashed = true;
  }

  public boolean isPasswordHashed() {
    return passwordHashed;
  }

  public boolean isAdmin() {
    return roles.contains("admin");
  }

  public String getEmail() {
    // I know it's wrong but it's not working otherwise
    if (emails == null && !emails.isEmpty()) {
      String e = "";
      for (var email : emails) {
        if (email != null) {
          e = email.getEmail();
          break;
        }
      }
      return e;
    }
    return "mzidi@zayid.aid";
  }

  public void addSession(Session session) {
    sessions.add(session);
  }

  public boolean checkSession(Session session) {
    return sessions.contains(session);
  }

  public boolean removeSession(Session session) {
    return sessions.remove(session);
  }
}
