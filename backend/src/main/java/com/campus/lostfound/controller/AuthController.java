package com.campus.lostfound.controller;

import com.campus.lostfound.model.*;
import com.campus.lostfound.repository.UserRepository;
import com.campus.lostfound.security.JwtService;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UserRepository users, PasswordEncoder encoder, JwtService jwt){
        this.users=users; this.encoder=encoder; this.jwt=jwt;
    }

    record RegisterRequest(@NotBlank String name,@Email @NotBlank String email,@Size(min=6) String password){}
    record LoginRequest(@Email @NotBlank String email,@NotBlank String password){}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest r){
        if(users.existsByEmail(r.email())) return ResponseEntity.badRequest().body(Map.of("message","Email already registered"));
        User u=new User();
        u.setName(r.name()); u.setEmail(r.email().toLowerCase()); u.setPassword(encoder.encode(r.password()));
        users.save(u);
        return ResponseEntity.ok(Map.of("token",jwt.generate(u.getEmail()),"name",u.getName(),"email",u.getEmail(),"role",u.getRole()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest r){
        return users.findByEmail(r.email().toLowerCase())
            .filter(u->encoder.matches(r.password(),u.getPassword()))
            .<ResponseEntity<?>>map(u->ResponseEntity.ok(Map.of("token",jwt.generate(u.getEmail()),"name",u.getName(),"email",u.getEmail(),"role",u.getRole())))
            .orElseGet(()->ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Invalid email or password")));
    }
}
