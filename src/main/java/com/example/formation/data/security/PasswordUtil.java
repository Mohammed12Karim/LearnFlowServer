package com.example.formation.data.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

  // Hash a password with auto-generated salt
  public static String hash(String plainTextPassword) {
    return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
  }

  // Verify password against stored hash
  public static boolean verify(String plainTextPassword, String hashedPassword) {
    try {
      return BCrypt.checkpw(plainTextPassword, hashedPassword);
    } catch (Exception e) {
      return false; // Handle invalid hash format
    }
  }
}
