package com.app.expensetracker.service;

import com.app.expensetracker.domain.user.User;
import com.app.expensetracker.domain.user.UserRegistry;
import com.app.expensetracker.dto.request.RegisteringUserRequestDTO;
import com.app.expensetracker.dto.response.RegisteringUserResponseDTO;
import com.app.expensetracker.mapper.UserRegisteredMapper;
import com.app.expensetracker.mapper.UserRegistryMapper;
import com.app.expensetracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserRegisteredMapper userRegisteredMapper;


    //if an exception comes out rollout the transaction
    @Transactional(rollbackOn = Exception.class)
    public RegisteringUserResponseDTO registerUser(RegisteringUserRequestDTO userRequestDTO) {

        //check if provided username already exists
        userService.checkUsername(userRequestDTO.getUsername());

        //Create User object
        User user = new User();
        //Create UserRegistry Object to map it with User
        UserRegistry registry = new UserRegistry();

        user.setUsername(userRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        registry.setFirstName(userRequestDTO.getFirstName());
        registry.setLastName(userRequestDTO.getLastName());
        registry.setUser(user);
        user.setUserRegistry(registry);

        User savedUser = userRepository.save(user);

        return userRegisteredMapper.toDto(savedUser);

    }
}
