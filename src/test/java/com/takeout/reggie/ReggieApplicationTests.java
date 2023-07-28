package com.takeout.reggie;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest
class ReggieApplicationTests {

    @Autowired
    private JavaMailSender mailSender;
    @Test
    void contextLoads() {
    }

    @Test
    public void decodeJWT(){
        Map<String,Object> setClaims = new HashMap<>();
        setClaims.put("id","12345");
        String jwt = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256,"itheima") //set generate algorithm
                .setClaims(setClaims)
                .setExpiration(new Date(System.currentTimeMillis()+3600 * 1000))
                .compact();
        Claims claims = Jwts.parser()
                .setSigningKey("itheima")
                .parseClaimsJws(jwt)
                .getBody();

        System.out.println(claims.get("id"));
    }

}
