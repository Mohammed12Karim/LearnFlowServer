package com.example.formation.data.repositories;

import com.example.formation.data.models.AttendedSession;
import com.example.formation.data.models.Formation;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendedSessionRepository extends JpaRepository<AttendedSession, Integer> {

  List<AttendedSession> findAllByIdIn(List<Integer> ids);

  @Query("SELECT a FROM AttendedSession a WHERE a.user.id = :userId AND a.session.id = :sessionId")
  List<AttendedSession> findAllBySessionAndUser(@Param("sessionId") Integer sessionId,
      @Param("userId") Integer userId);

  @Query("SELECT a FROM AttendedSession a " +
      "WHERE a.user.id = :userId " +
      "AND a.session.parentFormation.id = :formationId")
  List<AttendedSession> findAllByFormationAndUser(@Param("formationId") Integer formationId,
      @Param("userId") Integer userId);

  @Query("SELECT a FROM AttendedSession a " +
      "WHERE a.session.id = :sessionId " +
      "AND a.user.id = :userId")
  Optional<AttendedSession> findBySessionAndUser(@Param("sessionId") Integer sessionId,
      @Param("userId") Integer userId);

  @Query("SELECT a FROM AttendedSession a " +
      "WHERE a.session.parentFormation.id = :formationId")
  List<AttendedSession> findAllUsersInFormation(@Param("formationId") Integer formationId);

  @Query("SELECT a FROM AttendedSession a WHERE a.session.id = :sessionId")
  List<AttendedSession> findAllUsersInSession(@Param("sessionId") Integer sessionId);

  // Fixed: Optimized query
  @Query("SELECT DISTINCT s.parentFormation " +
      "FROM AttendedSession a " +
      "JOIN a.session s " +
      "WHERE a.user.id = :userId")
  List<Formation> findAllFormationsForUser(@Param("userId") Integer userId);

  // Fixed: Simplified to derived query
  List<AttendedSession> findByUserId(@Param("userId") Integer userId);

  // Fixed: Corrected query and added modifying annotation
  @Modifying
  @Transactional
  @Query("DELETE FROM AttendedSession a WHERE a.session.id = :sessionId")
  void deleteAllBySessionId(@Param("sessionId") Integer sessionId);

  // New: Delete by list of session IDs
  @Modifying
  @Transactional
  @Query("DELETE FROM AttendedSession a WHERE a.session.id IN :sessionIds")
  void deleteAllBySessionIds(@Param("sessionIds") List<Integer> sessionIds);
}
