package com.example.formation.data.servers;

import com.example.formation.controllers.request.user.UserInfoDetails;
import com.example.formation.data.models.Session;
import com.example.formation.data.models.UserInfo;
import com.example.formation.data.models.UserEmail;
import com.example.formation.data.repositories.AttendedSessionRepository;
import com.example.formation.data.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AttendedSessionRepository attendedSessionRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(
            UserRepository userRepository,
            AttendedSessionRepository attendedSessionRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.attendedSessionRepository = attendedSessionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserInfo> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<UserInfo> userInfo = userRepository.findByEmail(username);

        if (userInfo.isEmpty())
            throw new UsernameNotFoundException("");

        try {

            FileWriter fileWriter = new FileWriter("/home/mohamed-karim/workspace/java/spring/learnFlow/work.txt");
            if (userInfo.get().getEmails() != null && !userInfo.get().getEmails().isEmpty())
                fileWriter.write("NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
            else
                fileWriter.write("YESSSSSSSSSSSSSSSSESESESSSSSSSSSSSSSSSSSS");
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            try {

                FileWriter fileWriter = new FileWriter("/home/mohamed-karim/workspace/java/spring/learnFlow/work.txt");
                fileWriter.write("AAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHj");
                fileWriter.flush();
                fileWriter.close();
            } catch (Exception ex) {
                // TODO: handle exception
            }
        }

        return userInfo.filter(user -> user != null).map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
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
            user.setPassword(passwordEncoder.encode(user.getPassword()));
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
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()));
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
            throws NullPointerException, AccessDeniedException, IllegalArgumentException {
        if (userId == null || deleterId == null)
            throw new NullPointerException();
        var user = userRepository.findById(userId);
        var deleter = userRepository.findById(deleterId);
        if (user.isEmpty())
            throw new IllegalArgumentException("user does not exist");
        if (deleter.isEmpty())
            throw new IllegalArgumentException("deleter does not exist");
        if (!deleter.get().isAdmin() && !deleter.get().getId().equals(user.get().getId()))
            throw new AccessDeniedException("access denied");
        userRepository.deleteById(userId);
    }
}
