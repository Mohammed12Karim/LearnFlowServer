package com.example.formation.data.servers;

import com.example.formation.data.models.AttendedSession;
import com.example.formation.data.models.Session;
import com.example.formation.data.models.UserInfo;
import com.example.formation.data.repositories.FormationRepository;
import com.example.formation.data.repositories.AttendedSessionRepository;
import com.example.formation.data.repositories.SessionRepository;
import com.example.formation.data.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** SessionService */
@Service
public class SessionService {
  @Autowired
  FormationRepository formationRepository;
  @Autowired
  AttendedSessionRepository AttendedSessionRepository;
  @Autowired
  SessionRepository sessionRepository;
  @Autowired
  UserRepository userRepository;

  public List<Session> findAll() {
    return sessionRepository.findAll();
  }

  public Session registerSession(Session session) throws NullPointerException {
    if (session == null) {
      throw new NullPointerException("formation null");
    }
    return sessionRepository.save(session);
  }

  public Optional<Session> getSession(Integer sessionId) throws NullPointerException {
    if (sessionId == null)
      throw new NullPointerException();
    return sessionRepository.findById(sessionId);
  }

  public void deleteSession(Integer sessionId) throws NullPointerException {
    if (sessionId == null)
      throw new NullPointerException();
    AttendedSessionRepository.deleteAllBySessionId(sessionId);
    sessionRepository.deleteById(sessionId);
  }

  public Session updateSession(Session session) throws NullPointerException, IllegalArgumentException {
    if (session == null)
      throw new NullPointerException("session must be non null");
    if (sessionRepository.findById(session.getId()).isEmpty())
      throw new IllegalArgumentException("session does not exist");

    return sessionRepository.save(session);
  }

  public Session editSession(Session session, Integer editorId) throws NullPointerException, IllegalArgumentException {
    if (session == null || editorId == null)
      throw new NullPointerException("session and editorId must be non null");
    var S = sessionRepository.findById(session.getId());
    if (S.isEmpty() || userRepository.findById(editorId).isEmpty())
      throw new IllegalArgumentException("session does not exist");
    session = S.get();
    if (session.getId().equals(editorId))
      throw new IllegalArgumentException(
          String.format("editor: %d does not have permission to edit Session: %d", editorId, session.getId()));

    return sessionRepository.save(session);
  }

  public List<UserInfo> getAllUsers(Session session) throws IllegalArgumentException, NullPointerException {
    if (session == null)
      throw new NullPointerException();
    var AttendedSessions = AttendedSessionRepository.findAllUsersInSession(session.getId());
    if (AttendedSessions.isEmpty()) {
      throw new IllegalArgumentException("session does not have users");
    }
    return AttendedSessions.stream().map(u -> u.getUser()).toList();
  }

  public AttendedSession addParticipantToSession(
      Integer sessionId, Integer participantId) throws NullPointerException {
    if (sessionId == null || participantId == null)
      throw new NullPointerException("all field must be non null");
    var session = sessionRepository.findById(sessionId);
    var participant = userRepository.findById(participantId);
    return AttendedSessionRepository.save(
        new AttendedSession(session.get(), participant.get()));
  }

  public void removeParticipantFromSession(AttendedSession linker) {
    if (linker == null)
      throw new NullPointerException();
    if (AttendedSessionRepository.findById(linker.getId()).isEmpty())
      throw new IllegalArgumentException();
    AttendedSessionRepository.deleteById(linker.getId());
  }

  public void removeParticipantFromSession(Integer sessionId, Integer userId) {
    if (sessionId == null || userId == null)
      throw new NullPointerException();
    var linker = AttendedSessionRepository.findBySessionAndUser(sessionId, userId);
    if (linker.isEmpty())
      throw new IllegalArgumentException();
    AttendedSessionRepository.deleteById(linker.get().getId());
  }

  public void removeParticipantFromSession(Session session, UserInfo user) {
    if (session == null || user == null)
      throw new NullPointerException();
    var linker = AttendedSessionRepository.findBySessionAndUser(session.getId(), user.getId());
    if (linker.isEmpty())
      throw new IllegalArgumentException();
    AttendedSessionRepository.deleteById(linker.get().getId());
  }

  public List<AttendedSession> getUserInfoOfAllSessions(Integer userId) throws NullPointerException {
    if (userId == null)
      throw new NullPointerException();
    return AttendedSessionRepository.findByUserId(userId);
  }
}
