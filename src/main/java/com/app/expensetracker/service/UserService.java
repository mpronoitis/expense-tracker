package com.app.expensetracker.service;

import com.app.expensetracker.error.exception.BadUsernameException;
import com.app.expensetracker.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public void checkUsername(@NotNull String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            log.error("Username {} already exists", username);
            throw new BadUsernameException("");
        }
    }
}
