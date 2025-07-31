package com.example.formation.data.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;

/** Session */
@Data
@NoArgsConstructor
@Entity
@Table(name = "sessions")
public class Session {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(nullable = false, referencedColumnName = "id")
  private Formation parentFormation;

  @Column
  private LocalDateTime start;
  @Column
  private LocalDateTime end;

  @OneToOne
  private UserInfo responsible;

  @ManyToMany(mappedBy = "sessions")
  private Collection<UserInfo> participants;

  public Session(Formation parent, LocalDateTime start, LocalDateTime end) {
    this.parentFormation = parent;
    this.start = start;
    this.end = end;
  }

  public Session(Formation parent, LocalDateTime start, LocalDateTime end, UserInfo responsible) {
    this.parentFormation = parent;
    this.start = start;
    this.end = end;
    this.responsible = responsible;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setResponsibleId(UserInfo user) {
    responsible = user;
  }

  public void setParentFormation(Formation formation) {
    parentFormation = formation;
  }

  public void setStartDate(LocalDateTime start) {
    this.start = start;
  }

  public void setEndDate(LocalDateTime end) {
    this.end = end;
  }

  public Integer getId() {
    return id;
  }

  public UserInfo getResponsibleId() {
    return responsible;
  }

  public Formation getParentFormation() {
    return parentFormation;
  }

  public LocalDateTime getStartDate() {
    return start;
  }

  public LocalDateTime getEndDate() {
    return end;
  }

  public boolean checkUser(UserInfo user) {
    return participants.contains(user);
  }

  public void addUser(UserInfo user) {
    participants.add(user);
  }

  public boolean removeUser(UserInfo user) {
    return participants.remove(user);
  }
}
