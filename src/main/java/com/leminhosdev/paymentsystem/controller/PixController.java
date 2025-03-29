package com.leminhosdev.paymentsystem.controller;

import com.leminhosdev.paymentsystem.dto.PixChargeRequest;
import com.leminhosdev.paymentsystem.dto.PixSendRequest;
import com.leminhosdev.paymentsystem.service.PixService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pix")
public class PixController {

    @Autowired
    private PixService pixService;

    @GetMapping("/criar")
    public ResponseEntity pixCreateEVP() {
        JSONObject response = this.pixService.pixCreateEVP();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.toString());
    }

    @PostMapping("/cobrar")
    public ResponseEntity pixCreateCharge(@RequestBody PixChargeRequest pixChargeRequest) {
        JSONObject response = this.pixService.pixCreateCharge(pixChargeRequest);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.toString());
    }

    @PostMapping("/enviar")
    public ResponseEntity pixSend(@RequestBody PixSendRequest pixSendRequest) {
        JSONObject response = pixService.pixSend(pixSendRequest);

        if (response == null) {
            return ResponseEntity.badRequest().body("Erro ao enviar Pix");
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.toString());
    }

    
}