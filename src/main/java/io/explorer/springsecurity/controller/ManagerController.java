package io.explorer.springsecurity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/manager")
@PreAuthorize("hasAuthority('MANAGER')")
public class ManagerController {

  @GetMapping
  public String get() {
    return "Manager :: GET";
  }

  @PostMapping
  public String post() {
    return "Manager :: POST";
  }
}
