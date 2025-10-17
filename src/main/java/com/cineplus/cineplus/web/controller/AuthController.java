package com.cineplus.cineplus.web.controller;

import com.cineplus.cineplus.domain.entity.User;
import com.cineplus.cineplus.domain.repository.UserRepository;
import com.cineplus.cineplus.web.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
		String username = body.get("username");
		String password = body.get("password");
		if (userRepository.findByUsername(username).isPresent()) {
			return ResponseEntity.badRequest().body(Map.of("error", "username_taken"));
		}
		User user = new User();
		user.setUsername(username);
		user.setPassword(new BCryptPasswordEncoder().encode(password));
		user.setRoles(Set.of("ROLE_USER"));
		userRepository.save(user);
		return ResponseEntity.ok(Map.of("status", "ok"));
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
		String username = body.get("username");
		String password = body.get("password");
	authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		String token = jwtUtil.generateToken(username);
		return ResponseEntity.ok(Map.of("token", token));
	}
}
