package com.leminhosdev.paymentsystem.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.leminhosdev.paymentsystem.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Serviço responsável por gerar e validar tokens JWT utilizados para
 * autenticação de usuários.
 */
@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Gera um token JWT com base nos dados do usuário fornecido.
     *
     * @param user O usuário autenticado.
     * @return Token JWT gerado.
     * @throws RuntimeException Se ocorrer erro durante a criação do token.
     */
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("auth")
                    .withSubject(user.getEmail())
                    .withExpiresAt(expirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("ERRO: Token não foi gerado", exception);
        }
    }

    /**
     * Valida um token JWT e retorna o e-mail (subject) do usuário.
     *
     * @param token Token JWT a ser validado.
     * @return O e-mail do usuário contido no subject do token.
     * @throws RuntimeException Se o token for inválido ou estiver expirado.
     */
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token inválido", exception);
        }
    }

    /**
     * Define a data de expiração do token, que é 60 minutos a partir do momento
     * atual.
     *
     * @return Um {@link Instant} representando o horário de expiração.
     */
    private Instant expirationDate() {
        return LocalDateTime.now()
                .plusMinutes(60)
                .toInstant(ZoneOffset.of("-03:00")); // Fuso horário de Brasília
    }
}
