package com.backend.project.controller;

import com.backend.project.model.UserAuthDetails;
import com.backend.project.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService service;

    @Autowired
    AuthController(AuthService service){
        this.service = service;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserAuthDetails user){
        return new ResponseEntity<>(service.signup(user), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserAuthDetails user){
        return new ResponseEntity<>(service.login(user), HttpStatus.OK);
    }

//    @GetMapping("/verify")
//    public ResponseEntity<String> isok(@RequestBody String token){
////        ResponseEntity.ok();
//        return new ResponseEntity<>(service.verify(token), HttpStatus.OK);
//    }

//    @GetMapping("/")
//    public String isOk(@RequestBody String token){
//        return service.verifyJwt(token);
//    }
}
