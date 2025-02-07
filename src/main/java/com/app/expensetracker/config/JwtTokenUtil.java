package com.app.expensetracker.config;

import com.app.expensetracker.domain.user.User;
import com.app.expensetracker.dto.UserClaimsDTO;
import com.app.expensetracker.error.exception.GenericBadRequestException;
import com.app.expensetracker.repository.UserRepository;
import com.app.expensetracker.shared.rest.enumeration.ErrorType;
import com.app.expensetracker.utils.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenUtil {

    private final UserRepository userRepository;

    private static final String CLAIMS_USER_CLAIMS_DTO = "userClaims";
    private static final String CLAIMS_IS_AUTHENTICATION = "isAuthentication";

    private static final Dotenv dotenv = Dotenv.load();
    private static final String SECRET_KEY = dotenv.get("JWT_SECRET_KEY");

    @Value("${application.jwt.expiration-millis}")
    private Integer jwtExpirationMillis;


    public String generateAccessToken(UserClaimsDTO user) {
        Header<?> header = Jwts.header();
        header.setType(Header.JWT_TYPE);
        user.setPassword(null);
        Map<String, Object> userClaims = new HashMap<>();
        userClaims.put(CLAIMS_IS_AUTHENTICATION, true);
        userClaims.put(CLAIMS_USER_CLAIMS_DTO, user);
        return Jwts.builder()
                .setHeader((Map<String, Object>) header)
                .setSubject(format("%s,%s", user.getId(), user.getUsername()))
                .setIssuer("localhost")
                .setIssuedAt(Utils.convertLocalDateTimeToDate(LocalDateTime.now()))
                .addClaims(userClaims)
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMillis))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public UserClaimsDTO getUserClaimsDTO(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();

        return new ObjectMapper().convertValue(claims.get(CLAIMS_USER_CLAIMS_DTO), new TypeReference<UserClaimsDTO>() {
        });
    }

    public boolean validate(String token) {
        try {
            UserClaimsDTO userClaimsDTO = getUserClaimsDTO(token);
            User user = userRepository.findByUsername(userClaimsDTO.getUsername()).orElseThrow(() -> new GenericBadRequestException("User not found", ErrorType.IM_USER_NOT_FOUND));
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature - {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token - {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.warn("Expired JWT token - {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token - {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty - {}", ex.getMessage());
        }
        return false;
    }

}
