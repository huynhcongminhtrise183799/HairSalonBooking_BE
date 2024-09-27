package com.example.hairSalonBooking.service;

import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.repository.CustomerRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenService {

    public final String SECRET_KEY ="iTx5DOgYrW3LEeEmnd9EG4cI5HxKKlhFUjYoytO3xDDMJN7xtPpgtDhrcTCUOrvk";

    @Autowired
    CustomerRepository customerRepository;


    private SecretKey getSigninKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);

    }


    public String generateToken(Account account){
        String token = Jwts.builder()
                .subject(account.getAccountid()+"")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+ 1000*60*60*24))
                .signWith(getSigninKey())
                .compact();
        return token;
    }


    public Account getAccountByToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String idString =claims.getSubject();
        long accountid =Long.parseLong(idString);

        return customerRepository.findAccountByAccountid(accountid);
    }

}
