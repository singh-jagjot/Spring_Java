package com.backend.project.service;

import com.backend.project.config.ProjectConfig;
import com.backend.project.entity.User;
import com.backend.project.enums.Messages;
import com.backend.project.model.UserAuthDetails;
import com.backend.project.repository.UserRepository;
import com.backend.project.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final ProjectConfig config;

    private final JwtUtil util;

    @Autowired
    AuthService(UserRepository userRepo, PasswordEncoder encoder, ProjectConfig config, JwtUtil util){
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.config = config;
        this.util = util;
    }

    public String signup(UserAuthDetails user){
        try {
            logger.info("Saving new user '{}' details: {}", user.username(), Messages.START);
            userRepo.save(new User(user.username(), encoder.encode(user.password())));
            return Messages.SUCCESS.toString();
        } catch (DataIntegrityViolationException e){
            logger.info("Saving new user '{}' details: {}", user.username(), Messages.FAILED);
            return Messages.USR_EXIST.toString();
        } catch (Exception e){
            logger.info("Saving new user '{}' details: {}", user.username(), Messages.FAILED);
            return Messages.SERVER_ERR + e.getMessage();
        } finally {
            logger.info("Saving new user '{}' details: {}", user.username(), Messages.END);
        }
    }

    public String login(UserAuthDetails potentialUser){
        logger.info("Logging in {}: {}", potentialUser.username(), Messages.START);
        try {
            Optional<User> user = userRepo.findByUsername(potentialUser.username());
            if(user.isEmpty()){
                logger.info("Logging in {}: {}", potentialUser.username(), Messages.START);
                return Messages.NO_USR_FND.toString();
            }
            if(encoder.matches(potentialUser.password(), user.get().getPasswordHash())){
                return Messages.TOKEN.toString() + ": " + util.createJwts(user.getUsername());
            }
            return Messages.INVLD_PWD.toString();
        } catch (Exception e){
            return Messages.SERVER_ERR + e.getMessage();
        } finally {
            logger.info("Logging in {}: {}", potentialUser.username(), Messages.END);
        }
    }

    public String verify(String t){
        return util.verifyJwts(t);
    }
}
