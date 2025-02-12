//package com.ssafy.goose.security;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//import java.security.Key;
//
//@Component
//public class JwtTokenProvider {
//
//    private final Key secretKey;
//    private final long validityInMilliseconds;
//
//    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
//                            @Value("${jwt.expiration:3600000}") long validity) {
//        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
//        this.validityInMilliseconds = validity;
//    }
//
//    // JWT 토큰 생성
//    public String createToken(String nickname) {
//        Claims claims = Jwts.claims().setSubject(nickname);
//        Date now = new Date();
//        Date expiry = new Date(now.getTime() + validityInMilliseconds);
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setIssuedAt(now)
//                .setExpiration(expiry)
//                .signWith(secretKey, SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    // 토큰에서 사용자 식별자(nickname) 추출
//    public String getNickname(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(secretKey)
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
//
//    // 토큰 유효성 검증
//    public boolean validateToken(String token) {
//        try {
//            Jws<Claims> claims = Jwts.parserBuilder()
//                    .setSigningKey(secretKey)
//                    .build()
//                    .parseClaimsJws(token);
//
//            return !claims.getBody().getExpiration().before(new Date());
//        } catch (ExpiredJwtException e) {
//            // 로그아웃 요청에서 만료된 토큰 허용
//            return false;
//        } catch (JwtException | IllegalArgumentException e) {
//            return false;
//        }
//    }
//}
