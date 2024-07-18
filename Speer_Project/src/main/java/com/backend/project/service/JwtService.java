package com.backend.project.service;

import com.backend.project.config.ProjectConfig;
import com.backend.project.enums.Messages;
import com.backend.project.exception.UserNotFoundException;
import com.backend.project.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private final ProjectConfig config;
    private final UserRepository repo;

    @Autowired
    JwtService(ProjectConfig config, UserRepository repo){
        this.config = config;
        this.repo = repo;
    }
    public String createJwts(String subject){
        logger.info("Creating JWT for subject '{}': {}", subject, Messages.START);
        Date currDate = new Date();
        Date expDate = Date.from(currDate.toInstant().plusSeconds(config.getExpiryDurationSecs()));
//        SecretKey key = Jwts.SIG.HS384.key().build();
//        String secretString = Encoders.BASE64.encode(key.getEncoded());

        SecretKey secretKey =  Keys.hmacShaKeyFor(Decoders.BASE64.decode(config.getSecret()));


//        System.out.println(key);
//        System.out.println(secretString);
//        System.out.println(secretKey);
//        System.out.println(Arrays.equals(key.getEncoded(), secretKey.getEncoded()));
        String token = Jwts.builder()
                .subject(subject)
                .issuedAt(currDate)
                .expiration(expDate)
                .signWith(secretKey)
                .compact();
        logger.info("Creating JWT for subject '{}': {}", subject, Messages.END);
        return token;
    }

    public String verifyJwts(String token){
        logger.debug("Verifying JWT: {}", Messages.START);
        SecretKey secretKey =  Keys.hmacShaKeyFor(Decoders.BASE64.decode(config.getSecret()));
        try {
            Jws<Claims> jwt = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            String subject = jwt.getPayload().getSubject();
            if(repo.findByUsername(subject).isEmpty()) throw new UserNotFoundException(Messages.NO_USR_FND.toString());
            return Messages.TKN_VALD.toString();
        } catch (ExpiredJwtException e){
            logger.error("JWT expired: {}", e.getMessage());
            return Messages.TKN_EXPIRED.toString();
        } catch (JwtException e) {
            logger.error("JWT verification failed: {}", e.getMessage());
            return Messages.TKN_INVALD + " :" + e.getMessage();
        } finally {
            logger.debug("Verifying JWT: {}", Messages.END);
        }
    }

    public String getSubject(String token){
        logger.debug("Extracting subject from JWT: {}", Messages.START);
        SecretKey secretKey =  Keys.hmacShaKeyFor(Decoders.BASE64.decode(config.getSecret()));
        try {
            Jws<Claims> jwt = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return jwt.getPayload().getSubject();
        } catch (JwtException e){
            logger.error("JWT verification failed: {}", e.getMessage());
            return Messages.TKN_INVALD + " :" + e.getMessage();
        } finally {
            logger.debug("Extracting subject from JWT: {}", Messages.END);
        }
    }
}
