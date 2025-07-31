package com.example.formation.data.servers;

import com.example.formation.data.models.Formation;
import com.example.formation.data.models.Session;
import com.example.formation.data.models.UserInfo;
import com.example.formation.data.models.AttendedSession;
import com.example.formation.data.repositories.FormationRepository;
import com.example.formation.data.repositories.SessionRepository;
import com.example.formation.data.repositories.AttendedSessionRepository;
import com.example.formation.data.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** FormationLinkerService */
@Service
public class AttendedSessionService {
  @Autowired
  UserRepository userRepository;
  @Autowired
  AttendedSessionRepository attendedSessionRepository;
  @Autowired
  FormationRepository formationRepository;
  @Autowired
  SessionRepository sessionRepository;

  public AttendedSession register(AttendedSession linker) {
    if (linker == null)
      throw new NullPointerException();
    if (sessionRepository.findById(linker.getSession().getId()).isEmpty())
      return null;
    return attendedSessionRepository.save(linker);
  }

  public Optional<AttendedSession> getAttendedSession(Integer sessionId, Integer userId) throws NullPointerException {
    if (sessionId == null || userId == null)
      throw new NullPointerException();
    return attendedSessionRepository.findBySessionAndUser(sessionId, userId);
  }

  public void delete(AttendedSession linker) {
    if (linker == null)
      throw new NullPointerException();
    attendedSessionRepository.deleteById(linker.getId());
  }

  public List<UserInfo> getUsersOfSession(Session session) throws IllegalArgumentException {
    Optional<Session> sessionExist = sessionRepository.findById(session.getId());
    if (sessionExist.isEmpty()) {
      throw new IllegalArgumentException("session not exist");
    }
    List<AttendedSession> linkers = attendedSessionRepository.findAllUsersInSession(session.getId());
    return linkers.stream().map(linker -> linker.getUser()).toList();
  }

  public List<UserInfo> getUsersOfFormation(Formation formation) throws IllegalArgumentException {
    if (formation == null)
      throw new NullPointerException();
    Optional<Formation> formationExist = formationRepository.findById(formation.getId());
    if (formationExist.isEmpty()) {
      throw new IllegalArgumentException("formation not exist");
    }
    List<AttendedSession> linkers = attendedSessionRepository.findAllUsersInFormation(formation.getId());
    return linkers.stream().map(linker -> linker.getUser()).toList();
  }

  // public

  // public List<PresenceResponse> getPresenceStat(Session session, User user)
  // throws Exception {
  // var doesFormationExist =
  // formationRepository.findById(session.getFormationId());
  // var formation = doesFormationExist.get();
  // Optional<AttendedSession> linker =
  // attendedSessionRepository.findBySessionAndUser(session.getId(), user.getId());
  // if (linker.isEmpty()) {
  // throw new Exception();
  // }
  // List<Presence> presences =
  // presenceRepository.findAllByFormationUserLinkerId(linker.get().getId());
  // List<PresenceResponse> responses = new ArrayList<>();
  // for (Presence presence : presences) {
  // PresenceResponse response = new PresenceResponse();
  // response.setUserName(user.getName());
  // response.setStart(presence.getStart());
  // response.setEnd(presence.getEnd());
  // response.setTitle(formation.getTitle());
  // }
  // return responses;
  // }

  // public List<PresenceResponse> getPresenceStat(User user) {
  //
  // List<AttendedSession> linkers =
  // attendedSessionRepository.findAllFormationsForUser(user.getId());
  //
  // List<List<Presence>> presences = new ArrayList<>();
  // for (AttendedSession linker : linkers) {
  // presences.addLast(presenceRepository.findAllByFormationUserLinkerId(linker.getId()));
  // }
  //
  // List<Integer> formationIds = new ArrayList<>();
  // for (AttendedSession formationUserLinker : linkers) {
  // formationIds.addLast(formationUserLinker.getformationId());
  // }
  // List<Formation> formations = formationRepository.findAllById(formationIds);
  // List<PresenceResponse> responses = new ArrayList<>();
  //
  // for (var ppresences : presences) {
  // for (Presence presence : ppresences) {
  // PresenceResponse response = new PresenceResponse();
  // response.setUserName(user.getName());
  // response.setStart(presence.getStart());
  // response.setEnd(presence.getEnd());
  // response.setTitle(
  // formations.stream()
  // .filter(
  // formation -> formation
  // .getId()
  // .equals(
  // linkers.stream()
  // .filter(
  // linker -> linker.getId()
  // .equals(
  // presence
  // .getFormUserLinkerId()))
  // .findFirst()
  // .get()
  // .getformationId()))
  // .findFirst()
  // .get()
  // .getTitle());
  // }
  // }
  // return responses;
  // }

  // public List<PresenceResponse> getPresenceStat(Formation formation) {
  // List<Presence> presences =
  // presenceRepository.findAllPresenceOfFormation(formation.getId());
  // List<Integer> formationLinkerIds = new ArrayList<>();
  // for (Presence presence : presences) {
  // formationLinkerIds.addLast(presence.getFormUserLinkerId());
  // }
  // List<AttendedSession> linkers =
  // attendedSessionRepository.findAllById(formationLinkerIds);
  // List<Integer> userIds = new ArrayList<>();
  // for (AttendedSession formationformationLinker : linkers) {
  // userIds.addLast(formationformationLinker.getuserId());
  // }
  // List<User> users = userRepository.findAllById(userIds);
  // List<PresenceResponse> responses = new ArrayList<>();
  // for (Presence presence : presences) {
  // PresenceResponse response = new PresenceResponse();
  // response.setTitle(formation.getTitle());
  // response.setStart(presence.getStart());
  // response.setEnd(presence.getEnd());
  // response.setUserName(
  // users.stream()
  // .filter(
  // user -> user.getId()
  // .equals(
  // linkers.stream()
  // .filter(
  // linker -> linker.getId()
  // .equals(
  // presence
  // .getFormUserLinkerId()))
  // .findFirst()
  // .get()
  // .getuserId()))
  // .findFirst()
  // .get()
  // .getName());
  //
  // responses.addLast(response);
  // }
  // return responses;
  // }
}
