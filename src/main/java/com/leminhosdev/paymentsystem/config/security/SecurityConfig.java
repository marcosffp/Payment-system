package com.leminhosdev.paymentsystem.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração de segurança da aplicação usando Spring Security.
 * <p>
 * Define regras de autorização, desabilita CSRF, configura sessão sem estado
 * (stateless),
 * e adiciona filtro de segurança personalizado para autenticação via token JWT.
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    /**
     * Configura o filtro de segurança HTTP, definindo:
     * <ul>
     * <li>Desabilitação do CSRF (Cross-Site Request Forgery)</li>
     * <li>Gerenciamento de sessão stateless (sem estado)</li>
     * <li>Permissões públicas para endpoints de registro, verificação e login</li>
     * <li>Autenticação obrigatória para outras requisições</li>
     * <li>Adição do filtro de autenticação JWT antes do filtro padrão do Spring
     * Security</li>
     * </ul>
     *
     * @param httpSecurity Objeto para configuração HTTP.
     * @return {@link SecurityFilterChain} configurado.
     * @throws Exception Em caso de falha na configuração.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/v1/user/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/user/verify").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Expõe o {@link AuthenticationManager} para ser usado no processo de
     * autenticação.
     *
     * @param authenticationConfiguration Configuração de autenticação do Spring.
     * @return {@link AuthenticationManager} configurado.
     * @throws Exception Em caso de falha na obtenção do manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Define o codificador de senha usando o algoritmo BCrypt.
     * <p>
     * É usado para armazenar senhas de forma segura no banco de dados.
     * </p>
     *
     * @return {@link PasswordEncoder} configurado com BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
