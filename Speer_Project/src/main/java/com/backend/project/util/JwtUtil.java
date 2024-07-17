package com.backend.project.util;

import com.backend.project.config.ProjectConfig;
import com.backend.project.enums.Messages;
import com.backend.project.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    private final ProjectConfig config;
    private final UserRepository repo;

    @Autowired
    JwtUtil(ProjectConfig config, UserRepository repo){
        this.config = config;
        this.repo = repo;
    }
    public String createJwts(String subject){
        Date currDate = new Date();
        Date expDate = Date.from(currDate.toInstant().plusSeconds(config.getSecsToExpire()));
//        SecretKey key = Jwts.SIG.HS384.key().build();
//        String secretString = Encoders.BASE64.encode(key.getEncoded());

        SecretKey secretKey =  Keys.hmacShaKeyFor(Decoders.BASE64.decode(config.getSecret()));


//        System.out.println(key);
//        System.out.println(secretString);
//        System.out.println(secretKey);
//        System.out.println(Arrays.equals(key.getEncoded(), secretKey.getEncoded()));
        return Jwts.builder()
                .subject(subject)
                .issuedAt(currDate)
                .expiration(expDate)
                .signWith(secretKey)
                .compact();
    }

    public String verifyJwts(String token){
        SecretKey secretKey =  Keys.hmacShaKeyFor(Decoders.BASE64.decode(config.getSecret()));
        try {
            Jws<Claims> jwt = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            String subject = jwt.getPayload().getSubject();
            if(repo.findByUsername(subject) == null) throw new RuntimeException(Messages.NO_USR_FND.toString());
        } catch (ExpiredJwtException e){
            return Messages.TKN_EXPIRED.toString();
        } catch (Exception e) {
            return Messages.TKN_INVALD + " " + e.getMessage();
        }
        return Messages.TKN_VALD.toString();
    }

    public String getSubject(String token){
        SecretKey secretKey =  Keys.hmacShaKeyFor(Decoders.BASE64.decode(config.getSecret()));
        try {
            Jws<Claims> jwt = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return jwt.getPayload().getSubject();
        } catch (Exception e){
            return Messages.TKN_INVALD + " " + e.getMessage();
        }
    }
}
