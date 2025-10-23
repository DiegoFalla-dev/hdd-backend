package com.cineplus.cineplus.persistence.service.impl;

import com.cineplus.cineplus.domain.dto.JwtResponseDto;
import com.cineplus.cineplus.domain.dto.LoginRequestDto;
import com.cineplus.cineplus.domain.dto.RegisterRequestDto;
import com.cineplus.cineplus.domain.entity.Role;
import com.cineplus.cineplus.domain.entity.Role.RoleName;
import com.cineplus.cineplus.domain.entity.User;
import com.cineplus.cineplus.domain.repository.RoleRepository;
import com.cineplus.cineplus.domain.repository.UserRepository;
import com.cineplus.cineplus.domain.service.AuthService;
import com.cineplus.cineplus.web.security.jwt.JwtUtils;
import com.cineplus.cineplus.persistence.util.Encryptor; // Importa tu clase Encryptor
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public void registerUser(RegisterRequestDto registerRequest) {

        // Check email uniqueness
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Email is already in use!");
        }

        // Generate username from firstName and lastName
        // Limpiamos los nombres para asegurar un username válido y legible
        String baseUsername = (registerRequest.getFirstName() + registerRequest.getLastName())
                .toLowerCase()
                .replaceAll("\\s+", "") // Eliminar espacios
                .replaceAll("[^a-z0-9]", ""); // Eliminar caracteres no alfanuméricos (mantener solo letras y números)

        String finalUsername = baseUsername;
        int suffix = 0;
        // Check for username uniqueness and append suffix if necessary
        while (userRepository.existsByUsername(finalUsername)) {
            suffix++;
            finalUsername = baseUsername + suffix;
        }

        // Validate password confirmation (already done by @NotBlank, but this adds the match check)
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Password and confirm password do not match.");
        }

        // Create new user
        User user = new User();
        user.setUsername(finalUsername); // Set the generated username
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());

        // Mapeo de campos opcionales
        user.setNationalId(registerRequest.getNationalId());
        user.setBirthDate(registerRequest.getBirthDate());
        user.setGender(registerRequest.getGender()); // Agregado: mapea el género
        user.setAvatar(registerRequest.getAvatar());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Encode password

        // Encrypt phone if provided
        if (registerRequest.getPhone() != null && !registerRequest.getPhone().isBlank()) {
            user.setPhoneEncrypted(Encryptor.encrypt(registerRequest.getPhone())); // Usa tu clase Encryptor
        }

        Set<String> strRoles = registerRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // If no roles are specified, assign the default USER role
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error: Role USER is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toUpperCase()) {
                    case "ADMIN":
                        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error: Role ADMIN is not found."));
                        roles.add(adminRole);
                        break;
                    case "MANAGER":
                        Role managerRole = roleRepository.findByName(RoleName.ROLE_MANAGER)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error: Role MANAGER is not found."));
                        roles.add(managerRole);
                        break;
                    case "USER":
                        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error: Role USER is not found."));
                        roles.add(userRole);
                        break;
                    default:
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Role " + role + " is invalid.");
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public JwtResponseDto authenticateUser(LoginRequestDto loginRequest) {
        // Asegúrate de que LoginRequestDto tenga un campo para usernameOrEmail
        // y que tu UserDetailsService sepa cómo buscar por ambos.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return new JwtResponseDto(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles,
                "Bearer");
    }
}