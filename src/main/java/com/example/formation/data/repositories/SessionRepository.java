package com.example.formation.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.formation.data.models.Session;

/**
 * SessionRepository
 */
@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {

}
