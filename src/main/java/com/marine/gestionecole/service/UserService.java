package com.marine.gestionecole.service;

import com.marine.gestionecole.dto.UserResponse;
import com.marine.gestionecole.entity.User;
import com.marine.gestionecole.entity.User.Role;
import com.marine.gestionecole.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

  final UserRepository userRepository;
  final PasswordEncoder passwordEncoder;

  UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException {
    User user = userRepository
      .findByUsername(username)
      .orElseThrow(() ->
        new UsernameNotFoundException("User not found: " + username)
      );

    return new org.springframework.security.core.userdetails.User(
      user.getUsername(),
      user.getPassword(),
      Collections.singletonList(
        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
      )
    );
  }

  public User registerUser(String username, String password, String email) {
    if (userRepository.existsByUsername(username)) {
      throw new RuntimeException("Username already exists");
    }
    if (userRepository.existsByEmail(email)) {
      throw new RuntimeException("Email already exists");
    }

    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password));
    user.setEmail(email);
    user.setRole(User.Role.USER);

    return userRepository.save(user);
  }

  public Optional<UserResponse> findById(Long id) {
    return userRepository
      .findById(id)
      .map(user ->
        new UserResponse(
          user.getId(),
          user.getUsername(),
          user.getEmail(),
          user.getRole().name()
        )
      );
  }

  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public List<UserResponse> findAll() {
    return userRepository
      .findAll()
      .stream()
      .map(user ->
        new UserResponse(
          user.getId(),
          user.getUsername(),
          user.getEmail(),
          user.getRole().name()
        )
      )
      .toList();
  }

  public void deleteById(Long id) {
    userRepository.deleteById(id);
  }

  public UserResponse updateRole(Long id, Role role) {
    User user = userRepository
      .findById(id)
      .orElseThrow(() -> new RuntimeException("User not found"));
    user.setRole(role);
    User saved = userRepository.save(user);
    return new UserResponse(
      saved.getId(),
      saved.getUsername(),
      saved.getEmail(),
      saved.getRole().name()
    );
  }
}
