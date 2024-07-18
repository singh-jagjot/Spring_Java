package com.backend.project.service;

import com.backend.project.entity.User;
import com.backend.project.enums.Messages;
import com.backend.project.exception.AccessDeniedException;
import com.backend.project.exception.UserNotFoundException;
import com.backend.project.model.UserAuthDetails;
import com.backend.project.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
//    private final ProjectConfig config;

    private final JwtService util;

    @Autowired
    AuthService(UserRepository userRepo, PasswordEncoder encoder, JwtService util) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.util = util;
    }

    public String signup(UserAuthDetails user) {
        try {
            logger.info("Saving new user '{}': {}", user.username(), Messages.START);
            userRepo.save(new User(user.username(), encoder.encode(user.password())));
            return Messages.SUCCESS.toString();
//        } catch (DataIntegrityViolationException e) {
//            logger.error("Error while saving new user '{}': {}", user.username(), e.getMessage());
//            throw e;
        } catch (Exception e) {
            logger.error("Error while saving new user '{}': {}", user.username(), e.getMessage());
            throw e;
        } finally {
            logger.info("Saving new user '{}': {}", user.username(), Messages.END);
        }
    }

    public String login(UserAuthDetails potentialUser) {
        logger.info("Logging in user '{}': {}", potentialUser.username(), Messages.START);
        try {
            Optional<User> user = userRepo.findByUsername(potentialUser.username());
            if (user.isEmpty()) {
                logger.info("Logging in {}: {}", potentialUser.username(), Messages.FAILED);
                throw new UserNotFoundException(Messages.NO_USR_FND.toString());
            } else if (!encoder.matches(potentialUser.password(), user.get().getPasswordHash())){
                logger.info("Logging in {}: {}", potentialUser.username(), Messages.FAILED);
                throw new AccessDeniedException(Messages.INVLD_CRED.toString());
            } else {
                logger.info("Sending JWT to {}", user.get().getUsername());
                return util.createJwts(user.get().getUsername());
            }
        } catch (Exception e) {
            logger.info("Error while logging in user '{}': {}", potentialUser.username(), e.getMessage());
            throw e;
        } finally {
            logger.info("Logging in user '{}': {}", potentialUser.username(), Messages.END);
        }
    }

}
