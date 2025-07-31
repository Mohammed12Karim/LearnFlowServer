package com.example.formation.data.repositories;

import com.example.formation.data.models.Formation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
/** FormationRepository */
public interface FormationRepository extends JpaRepository<Formation, Integer> {

  public Optional<Formation> findById(Integer id);

  public Optional<Formation> findByTitle(String title);

  public List<Formation> findAll();

  public Formation save(Formation formation);

  public void deleteById(Integer id);

  public void deleteBytitle(String title);
}
