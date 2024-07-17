package com.backend.project.controller;

import com.backend.project.model.UserAuthDetails;
import com.backend.project.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService service;

    @Autowired
    AuthController(AuthService service){
        this.service = service;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserAuthDetails user){
        logger.info("START - signup method");
        ResponseEntity<String> response = new ResponseEntity<>(service.signup(user), HttpStatus.OK);
        logger.info("END - signup method");
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserAuthDetails user){
        logger.info("START - login method");
        ResponseEntity<String> response = new ResponseEntity<>(service.login(user), HttpStatus.OK);
        logger.info("END - login method");
        return response;
    }
}
