package com.example.formation.controllers.request.user;

import java.util.Set;

/** EditRequest */
public record EditRequest(String name, String password, Set<String>emails) {
}
