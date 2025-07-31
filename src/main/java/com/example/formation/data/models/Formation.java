package com.example.formation.data.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "formation")
public class Formation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private Long durationSeconds;

  @Column(nullable = false)
  private String content;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private UserInfo publicher;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentFormation")
  private List<Session> sessions;

  public Formation(String title, Long duration_sec, String content, UserInfo publicher) {
    this.title = title;
    this.durationSeconds = duration_sec;
    this.content = content;
    this.publicher = publicher;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public void setPublicher(UserInfo publicher) {
    this.publicher = publicher;
  }

  public UserInfo getPublicher() {
    return this.publicher;
  }

  public void setFormationContent(String content) {
    this.content = content;
  }

  public String getFormationContent() {
    return content;
  }

  public Integer getId() {
    return id;
  }

  public Long getDuration() {
    return durationSeconds;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setDuration(Long duration) {
    this.durationSeconds = duration;
  }
}
