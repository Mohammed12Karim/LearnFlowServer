package com.example.formation.data.servers;

import com.example.formation.data.models.Session;
import com.example.formation.data.models.UserEmail;
import com.example.formation.data.models.UserInfo;
import com.example.formation.data.repositories.AttendedSessionRepository;
import com.example.formation.data.repositories.UserRepository;
import com.example.formation.data.security.PasswordUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AttendedSessionRepository attendedSessionRepository;
    private final PasswordUtil passwordEncoder;

    @Autowired
    public UserService(
            UserRepository userRepository,
            AttendedSessionRepository attendedSessionRepository,
            PasswordUtil passwordEncoder) {
        this.userRepository = userRepository;
        this.attendedSessionRepository = attendedSessionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserInfo> findAll() {
        return userRepository.findAll();
    }

    public Optional<UserInfo> getUser(String email) throws NullPointerException {
        if (email == null)
            throw new NullPointerException("email cannot be empty");
        return userRepository.findByEmail(email);
    }

    public Optional<UserInfo> getUser(Integer userId) throws NullPointerException {
        if (userId == null)
            throw new NullPointerException();
        return userRepository.findById(userId);
    }

    public List<Session> getAllSessions(UserInfo user)
            throws NullPointerException, IllegalArgumentException {
        if (user == null)
            throw new NullPointerException();
        var AttendedSessions = attendedSessionRepository.findByUserId(user.getId());
        if (AttendedSessions.isEmpty())
            throw new IllegalArgumentException("user isn't participating in any session");

        return AttendedSessions.stream()
                .map(AttendedSession -> AttendedSession.getSession())
                .toList();
    }

    @Transactional
    public UserInfo registerUser(UserInfo user, String email) {
        if (userRepository.existsByEmailsEmail(email)) {
            throw new IllegalArgumentException("Email used!");
        }

        if (!user.isPasswordHashed())
            user.hashPassword(passwordEncoder);
        user.addEmail(new UserEmail(email, user));
        return userRepository.save(user);
    }

    @Transactional
    public UserInfo updateUser(UserInfo user) throws NullPointerException {
        if (user == null)
            throw new NullPointerException();
        // if(userRepository.findById(user.getId()).isEmpty())throw new
        // IllegalArgumentException("user does not exist");
        return userRepository.save(user);
    }

    public Optional<UserInfo> authenticate(String email, String rawPassword) {
        return userRepository
                .findByEmail(email)
                .filter(user -> user.matchPassword(passwordEncoder, rawPassword));
    }

    @Transactional
    public void addEmailToUser(Integer userId, String email) {
        UserInfo user = userRepository
                .findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (userRepository.existsByEmailsEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        user.addEmail(new UserEmail(email, user));
        userRepository.save(user);
    }

    @Transactional
    public void removeEmailFromUser(Integer userId, String email) {
        UserInfo user = userRepository
                .findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.getEmails().stream()
                .filter(e -> e.getEmail().equals(email))
                .findFirst()
                .ifPresentOrElse(
                        user::removeEmail,
                        () -> {
                            throw new IllegalArgumentException("Email not found");
                        });

        userRepository.save(user);
    }

    public void deleteUser(Integer userId) throws NullPointerException, IllegalArgumentException {
        if (userId == null)
            throw new NullPointerException();
        var user = userRepository.findById(userId);
        if (user.isEmpty())
            throw new IllegalArgumentException("user does not exist");
        userRepository.deleteById(userId);
    }

    public void deleteUser(Integer userId, Integer deleterId)
            throws NullPointerException, IllegalArgumentException {
        if (userId == null || deleterId == null)
            throw new NullPointerException();
        var user = userRepository.findById(userId);
        var deleter = userRepository.findById(deleterId);
        if (user.isEmpty())
            throw new IllegalArgumentException("user does not exist");
        if (deleter.isEmpty())
            throw new IllegalArgumentException("deleter does not exist");
        if (!deleter.get().isAdmin() && !deleter.get().getId().equals(user.get().getId()))
            throw new IllegalArgumentException("access denied");
        userRepository.deleteById(userId);
    }
}
