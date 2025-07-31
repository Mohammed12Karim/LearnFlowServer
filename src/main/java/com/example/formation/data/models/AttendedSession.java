package com.example.formation.data.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

/** AttendedSessions */
@Data
@NoArgsConstructor
@Entity
@Table(name = "attended_sessions")
public class AttendedSession {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "session_id", referencedColumnName = "id")
  private Session session;

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private UserInfo user;

  @Column(nullable = false)
  private Boolean isPresent = false;

  @Column(nullable = false)
  private String evaluation;

  public AttendedSession(Session session, UserInfo user) {
    this.session = session;
    this.user = user;
    this.isPresent = false;
    this.evaluation = "";
  }

  public AttendedSession(Session session, UserInfo user, String evaluation) {
    this.session = session;
    this.user = user;
    this.isPresent = false;
    this.evaluation = evaluation;
  }

  public AttendedSession(Session session, UserInfo user, Boolean isPresent) {
    this.session = session;
    this.user = user;
    this.isPresent = isPresent;
    this.evaluation = "";
  }

  public AttendedSession(Session session, UserInfo user, Boolean isPresent, String evaluation) {
    this.session = session;
    this.user = user;
    this.isPresent = isPresent;
    this.evaluation = evaluation;
  }
}
