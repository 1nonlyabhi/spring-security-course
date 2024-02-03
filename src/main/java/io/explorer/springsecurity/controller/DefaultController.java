package io.explorer.springsecurity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class DefaultController {

  @GetMapping("/auth")
  public String hello() {
    return "Hello, World!";
  }

  @GetMapping("/test-endpoint")
  @PreAuthorize("hasAnyAuthority('MANAGER','USER')")
  public ResponseEntity<String> sayHello() {
    return ResponseEntity.ok("Hello from secured endpoint! GET");
  }

  @PostMapping("/test-endpoint")
  @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
  public ResponseEntity<String> heyHello() {
    return ResponseEntity.ok("Hello from secured endpoint! POST");
  }
}
