package com.leminhosdev.paymentsystem.config.security;

import com.leminhosdev.paymentsystem.repository.UserRepository;
import com.leminhosdev.paymentsystem.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de segurança que intercepta todas as requisições HTTP para validar o
 * token JWT.
 * <p>
 * Este filtro extrai o token JWT do cabeçalho Authorization, valida-o e, caso
 * válido,
 * autentica o usuário no contexto do Spring Security, permitindo acesso aos
 * recursos protegidos.
 * </p>
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Método principal do filtro que é executado uma vez por requisição.
     * <p>
     * Recupera o token JWT do cabeçalho Authorization, valida o token,
     * busca os detalhes do usuário e configura o contexto de segurança.
     * </p>
     *
     * @param request     A requisição HTTP recebida.
     * @param response    A resposta HTTP que será enviada.
     * @param filterChain O filtro da cadeia para continuar o processamento da
     *                    requisição.
     * @throws ServletException Caso ocorra erro no processamento do filtro.
     * @throws IOException      Caso ocorra erro de entrada/saída.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = this.recoverToken(request);
        if (token != null) {
            // Valida o token e obtém o "subject" (normalmente o email do usuário)
            String subject = tokenService.validateToken(token);

            // Busca o usuário no banco pelo email
            UserDetails user = userRepository.findByEmail(subject);

            // Cria objeto de autenticação com os dados do usuário e suas permissões
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities());

            // Define a autenticação no contexto do Spring Security
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Continua a cadeia de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Recupera o token JWT do cabeçalho Authorization da requisição.
     *
     * @param request A requisição HTTP.
     * @return O token JWT sem o prefixo "Bearer ", ou null se o cabeçalho estiver
     *         ausente.
     */
    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}
