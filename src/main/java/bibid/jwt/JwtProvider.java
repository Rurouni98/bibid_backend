package bibid.jwt;

import bibid.entity.Member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

// JWT를 발행하고 받아온 JWT가 유효한지 검사하는 클래스
@Component
public class JwtProvider {

    private static final String SECRET_KEY = "Yml0Y2FtcGRldm9wczEydG9kb2Jvb3RhcHA1MDJyZWFjdHNwcmluZ2Jvb3Q=";

    SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public String createJwt(Member member) {

        Date expireDate = Date.from(Instant.now().plus(100, ChronoUnit.DAYS));

        return Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(member.getMemberId())
                .issuer("final backend")
                .issuedAt(new Date())
                .expiration(expireDate)
                .compact();
    }

    public String createOauthJwt(Member member) {

        Date expireDate = Date.from(Instant.now().plus(100, ChronoUnit.DAYS));

        return Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setSubject(member.getMemberId())
                .issuer("final backend")
                .issuedAt(new Date())
                .expiration(expireDate)
                .compact();
    }

    // 받아온 JWT 유효성을 검사하고
    // 유효한 JWT일 경우 토큰의 주인(subject(username))를 리턴하는 메소드
    public String validateAndGetSubject(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }











}