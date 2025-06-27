package com.leminhosdev.paymentsystem.service;

import com.leminhosdev.paymentsystem.dto.UserResponse;
import com.leminhosdev.paymentsystem.entity.User;
import com.leminhosdev.paymentsystem.repository.UserRepository;
import com.leminhosdev.paymentsystem.util.RandomString;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * Serviço responsável pelo gerenciamento de usuários, incluindo
 * registro, verificação de conta e envio de e-mails de confirmação.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    /**
     * Registra um novo usuário no sistema:
     * <ul>
     * <li>Verifica se o e-mail já está cadastrado</li>
     * <li>Codifica a senha</li>
     * <li>Gera um código de verificação aleatório</li>
     * <li>Salva o usuário com conta desativada</li>
     * <li>Envia um e-mail de verificação</li>
     * </ul>
     *
     * @param user Objeto {@link User} com os dados preenchidos.
     * @return {@link UserResponse} com os dados do usuário registrado.
     * @throws MessagingException           Se houver erro no envio do e-mail.
     * @throws UnsupportedEncodingException Se o remetente não puder ser codificado
     *                                      corretamente.
     * @throws RuntimeException             Se o e-mail já estiver cadastrado.
     */
    public UserResponse registerUser(User user) throws MessagingException, UnsupportedEncodingException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("This email already exists");
        }

        // Codifica a senha
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Gera código de verificação e desativa conta
        String randomCode = RandomString.generateRandomString(64);
        user.setVerificationCode(randomCode);
        user.setEnabled(false);

        // Salva no banco
        User savedUser = userRepository.save(user);

        // Cria resposta DTO
        UserResponse userResponse = new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPassword());

        // Envia e-mail de verificação
        mailService.sendVerificationEmail(user);
        return userResponse;
    }

    /**
     * Verifica o código de verificação recebido via e-mail e ativa a conta do
     * usuário.
     *
     * @param verificationCode Código enviado por e-mail para ativação da conta.
     * @return {@code true} se o código for válido e o usuário for ativado com
     *         sucesso,
     *         {@code false} se o código for inválido ou se o usuário já estiver
     *         ativado.
     */
    public boolean verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            return false;
        }

        user.setVerificationCode(null);
        user.setEnabled(true);
        userRepository.save(user);

        return true;
    }
}
