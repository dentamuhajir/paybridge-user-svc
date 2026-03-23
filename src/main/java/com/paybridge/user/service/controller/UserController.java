package com.paybridge.user.service.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    @GetMapping("/me/profile")
    public ResponseEntity<?> profile() {
        return ResponseEntity.status(200).body("Welcome to me profile (this is protected endpoint)");
    }

}
