package com.leminhosdev.paymentsystem.controller;

import com.leminhosdev.paymentsystem.dto.PixChargeRequest;
import com.leminhosdev.paymentsystem.dto.PixSendRequest;
import com.leminhosdev.paymentsystem.service.PixService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST responsável por operações relacionadas ao sistema de
 * pagamentos via Pix.
 * <p>
 * Inclui endpoints para:
 * </p>
 * <ul>
 * <li>Criação de chave EVP</li>
 * <li>Criação de cobrança</li>
 * <li>Envio de pagamento Pix</li>
 * </ul>
 */
@RestController
@RequestMapping("/pix")
public class PixController {

    @Autowired
    private PixService pixService;

    /**
     * Endpoint para gerar uma nova chave EVP (chave aleatória Pix).
     *
     * @return {@link ResponseEntity} contendo a resposta JSON com os dados da chave
     *         criada.
     */
    @GetMapping("/criar")
    public ResponseEntity<String> pixCreateEVP() {
        JSONObject response = this.pixService.pixCreateEVP();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.toString());
    }

    /**
     * Endpoint para criar uma cobrança Pix com base nos dados enviados.
     *
     * @param pixChargeRequest Objeto contendo os dados da cobrança.
     * @return {@link ResponseEntity} com os detalhes da cobrança gerada em formato
     *         JSON.
     */
    @PostMapping("/cobrar")
    public ResponseEntity<String> pixCreateCharge(@RequestBody PixChargeRequest pixChargeRequest) {
        JSONObject response = this.pixService.pixCreateCharge(pixChargeRequest);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.toString());
    }

    /**
     * Endpoint para realizar o envio de um Pix.
     *
     * @param pixSendRequest Objeto com as informações da transação a ser enviada.
     * @return {@link ResponseEntity} com os dados da transação ou erro se falhar.
     */
    @PostMapping("/enviar")
    public ResponseEntity<String> pixSend(@RequestBody PixSendRequest pixSendRequest) {
        JSONObject response = pixService.pixSend(pixSendRequest);

        if (response == null) {
            return ResponseEntity.badRequest().body("Erro ao enviar Pix");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.toString());
    }
}
