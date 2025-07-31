package com.example.formation.data.repositories;

import com.example.formation.data.models.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, Integer> {
  @Query("SELECT u FROM UserInfo u JOIN u.emails e WHERE e.email = :email")
  Optional<UserInfo> findByEmail(String email);

  boolean existsByEmailsEmail(String email);
}
