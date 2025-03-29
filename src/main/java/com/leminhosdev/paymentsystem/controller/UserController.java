package com.leminhosdev.paymentsystem.controller;

import com.leminhosdev.paymentsystem.dto.UserCreateRequest;
import com.leminhosdev.paymentsystem.dto.UserResponse;
import com.leminhosdev.paymentsystem.entity.User;
import com.leminhosdev.paymentsystem.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid UserCreateRequest userCreateRequest) throws MessagingException, UnsupportedEncodingException {
        User user = userCreateRequest.toModel();
        UserResponse userSaved = userService.registerUser(user);
        return ResponseEntity.ok().body(userSaved);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@Param("code") String code) {
        if (userService.verify(code)) {
            return ResponseEntity.ok().body("<h1>Email verificado com sucesso! 🎉</h1>");
        } else {
            return ResponseEntity.badRequest().body("<h1>Falha na verificação do email. 😞</h1>");
        }
    }

    @GetMapping("/teste")
    public String teste(){
        return "você está logado";
    }

}
