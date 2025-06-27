package com.leminhosdev.paymentsystem.service;

import br.com.efi.efisdk.EfiPay;
import br.com.efi.efisdk.exceptions.EfiPayException;

import com.leminhosdev.paymentsystem.dto.PixChargeRequest;
import com.leminhosdev.paymentsystem.dto.PixSendRequest;
import com.leminhosdev.paymentsystem.pix.Credentials;

import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.awt.Desktop;

import javax.imageio.ImageIO;


@Service
public class PixService {

    private final Credentials credentials;

    public PixService() {
        this.credentials = new Credentials(); // Carrega as credenciais
    }

    public JSONObject pixCreateEVP() {

        JSONObject options = configuringJsonObject();

        try {
            EfiPay efi = new EfiPay(options);
            JSONObject response = efi.call("pixCreateEvp", new HashMap<String, String>(), new JSONObject());
            System.out.println(response.toString());
            return response;
        } catch (EfiPayException e) {
            System.out.println(e.getError());
            System.out.println(e.getErrorDescription());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private JSONObject configuringJsonObject() {
        JSONObject options = new JSONObject();
        options.put("client_id", credentials.getClientId());
        options.put("client_secret", credentials.getClientSecret());
        options.put("certificate", credentials.getCertificate());
        options.put("sandbox", credentials.isSandbox());

        return options;
    }

    public JSONObject pixCreateCharge(PixChargeRequest pixChargeRequest) {

        JSONObject options = configuringJsonObject();

        JSONObject body = new JSONObject();
        body.put("calendario", new JSONObject().put("expiracao", 3600));
        body.put("devedor", new JSONObject().put("cpf", "12345678909").put("nome", "Francisco da Silva"));
        body.put("valor", new JSONObject().put("original", pixChargeRequest.valor()));
        body.put("chave", pixChargeRequest.chave());

        JSONArray infoAdicionais = new JSONArray();
        infoAdicionais
                .put(new JSONObject().put("nome", "Campo 1").put("valor", "Informação Adicional1 do PSP-Recebedor"));
        infoAdicionais
                .put(new JSONObject().put("nome", "Campo 2").put("valor", "Informação Adicional2 do PSP-Recebedor"));
        body.put("infoAdicionais", infoAdicionais);

        try {
            EfiPay efi = new EfiPay(options);
            JSONObject response = efi.call("pixCreateImmediateCharge", new HashMap<String, String>(), body);

            int idFromJson = response.getJSONObject("loc").getInt("id");
            pixGenerateQRCode(String.valueOf(idFromJson));

            return response;
        } catch (EfiPayException e) {
            System.out.println(e.getError());
            System.out.println(e.getErrorDescription());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void pixGenerateQRCode(String id) {

        JSONObject options = configuringJsonObject();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", id);

        try {
            EfiPay efi = new EfiPay(options);
            Map<String, Object> response = efi.call("pixGenerateQRCode", params, new HashMap<String, Object>());

            System.out.println(response);

            File outputfile = new File("qrCodeImage.png");
            ImageIO.write(
                    ImageIO.read(new ByteArrayInputStream(javax.xml.bind.DatatypeConverter
                            .parseBase64Binary(((String) response.get("imagemQrcode")).split(",")[1]))),
                    "png", outputfile);
            Desktop desktop = Desktop.getDesktop();
            desktop.open(outputfile);

        } catch (EfiPayException e) {
            System.out.println(e.getError());
            System.out.println(e.getErrorDescription());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public JSONObject pixSend(PixSendRequest pixSendRequest) {
        JSONObject options = configuringJsonObject();

        // Gerando um idEnvio único
        String idEnvio = UUID.randomUUID().toString();

        HashMap<String, String> params = new HashMap<>();
        params.put("idEnvio", idEnvio);

        JSONObject body = new JSONObject();
        body.put("valor", pixSendRequest.valor());
        body.put("pagador", new JSONObject().put("chave", pixSendRequest.chavePagador()));
        body.put("favorecido", new JSONObject().put("chave", pixSendRequest.chave()));

        try {
            EfiPay efi = new EfiPay(options);
            JSONObject response = efi.call("pixSend", params, body);
            System.out.println("Pagamento enviado com sucesso: " + response);
            return response;
        } catch (EfiPayException e) {
            System.out.println("Erro na transação: " + e.getError() + " - " + e.getErrorDescription());
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
        }
        return null;
    }

}
