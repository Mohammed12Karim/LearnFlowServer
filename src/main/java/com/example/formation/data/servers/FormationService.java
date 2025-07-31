package com.example.formation.data.servers;

import com.example.formation.data.models.Formation;
import com.example.formation.data.models.AttendedSession;
import com.example.formation.data.repositories.FormationRepository;
import com.example.formation.data.repositories.AttendedSessionRepository;
import com.example.formation.data.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FormationService {

  @Autowired
  FormationRepository formationRepository;
  @Autowired
  AttendedSessionRepository AttendedSessionRepository;
  @Autowired
  UserRepository userRepository;

  public Formation registerFormation(Formation formation) throws NullPointerException {
    if (formation == null) {
      throw new NullPointerException("formation must not be null");
    }
    return formationRepository.save(formation);
  }

  public Formation editFormation(Formation formation)
      throws NullPointerException, IllegalArgumentException {
    if (formation == null)
      throw new NullPointerException("formation field must not be null");
    Optional<Formation> formationExist = formationRepository.findById(formation.getId());
    if (formationExist.isEmpty()) {
      throw new IllegalArgumentException("formation does not exits");
    }

    return formationRepository.save(formation);
  }

  public Formation editFormation(Formation formation, Integer userId)
      throws NullPointerException, IllegalArgumentException, AccessDeniedException {
    if (formation == null || userId == null) {
      throw new NullPointerException("formation and userId fiels must not benull");
    }
    Optional<Formation> formationExist = formationRepository.findById(formation.getId());
    var user = userRepository.findById(userId);
    if (formationExist.isEmpty()) {
      throw new IllegalArgumentException("formation does not exits");
    }
    if (user.isEmpty())
      throw new IllegalArgumentException("user does not exits");
    if (!user.get().isAdmin() && formation.getPublicher().getId().equals(userId) == false) {
      throw new AccessDeniedException("user does not have permission to delete formation");
    }
    return formationRepository.save(formation);
  }

  public Optional<Formation> getFormation(Integer formationId) {
    if (formationId == null)
      throw new NullPointerException();
    return formationRepository.findById(formationId);
  }

  public List<Formation> findAll() {
    return formationRepository.findAll();
  }

  public void deleteFormation(Integer formationId)
      throws NullPointerException, IllegalArgumentException {
    if (formationId == null)
      throw new NullPointerException();
    Optional<Formation> formation = formationRepository.findById(formationId);
    if (formation.isEmpty())
      throw new IllegalArgumentException("formation does not exist");
    formationRepository.deleteById(formationId);
  }

  public void deleteFormation(Integer formationId, Integer userId)
      throws IllegalArgumentException {
    if (formationId == null || userId == null)
      throw new NullPointerException("formationId and userId must not be null");

    var formation = formationRepository.findById(formationId);
    var user = userRepository.findById(userId);
    if (formation.isEmpty()) {
      throw new IllegalArgumentException("Formation does not exist");
    }
    if (user.isEmpty()) {
      throw new IllegalArgumentException("user does not exist");
    }
    if (!user.get().isAdmin() && formation.get().getPublicher().getId().equals(userId) == false) {
      throw new IllegalArgumentException("user does not have permission to delete formation");
    }
    formationRepository.deleteById(formationId);
  }

  public List<AttendedSession> getParticipantPerformanceInFromation(
      Integer formationId, Integer userId) throws NullPointerException {
    if (formationId == null || userId == null)
      throw new NullPointerException();
    return AttendedSessionRepository.findAllByFormationAndUser(formationId, userId);
  }
}
