package com.marine.gestionecole.controller;

import com.marine.gestionecole.dto.UserResponse;
import com.marine.gestionecole.entity.User;
import com.marine.gestionecole.entity.User.Role;
import com.marine.gestionecole.service.UserService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

  final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public List<UserResponse> getAllUsers() {
    return userService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
      Optional<UserResponse> user = userService.findById(id);
      return user
          .map(ResponseEntity::ok)
          .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}/role")
  public ResponseEntity<UserResponse> updateUserRole(
    @PathVariable Long id,
    @RequestBody Map<String, String> body
  ) {
    Role role = Role.valueOf(body.get("role"));
    UserResponse updated = userService.updateRole(id, role);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    if (!userService.findById(id).isPresent()) {
      return ResponseEntity.notFound().build();
    }

    userService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
