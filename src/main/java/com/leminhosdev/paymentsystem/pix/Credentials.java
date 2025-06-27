package com.leminhosdev.paymentsystem.pix;

import java.io.IOException;
import java.io.InputStream;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * A classe {@code Credentials} é responsável por carregar e disponibilizar
 * as credenciais necessárias para autenticação com o sistema de pagamentos Pix.
 * <p>
 * Os dados são carregados automaticamente a partir de um arquivo JSON
 * localizado
 * em {@code resources/credentials.json}. Esse arquivo deve conter as seguintes
 * chaves:
 * <ul>
 * <li>{@code client_id} - Identificador do cliente</li>
 * <li>{@code client_secret} - Segredo do cliente</li>
 * <li>{@code certificate} - Caminho para o certificado digital</li>
 * <li>{@code sandbox} - Indica se o ambiente é de testes</li>
 * <li>{@code debug} - Indica se o modo debug está ativado</li>
 * </ul>
 * </p>
 *
 * <p>
 * Exemplo de conteúdo esperado no arquivo {@code credentials.json}:
 * </p>
 *
 * <pre>
 * {
 *   "client_id": "seu-client-id",
 *   "client_secret": "seu-client-secret",
 *   "certificate": "caminho/do/certificado.p12",
 *   "sandbox": true,
 *   "debug": false
 * }
 * </pre>
 *
 * @author Leminhos
 */
public class Credentials {

    private String clientId;
    private String clientSecret;
    private String certificate;
    private boolean sandbox;
    private boolean debug;

    /**
     * Construtor padrão que lê automaticamente o arquivo {@code credentials.json}
     * e inicializa os atributos da classe.
     *
     * @throws RuntimeException se o arquivo não for encontrado.
     */
    public Credentials() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream credentialsFile = classLoader.getResourceAsStream("credentials.json");
        if (credentialsFile == null) {
            throw new RuntimeException("Arquivo credentials.json não encontrado em resources/");
        }

        JSONTokener tokener = new JSONTokener(credentialsFile);
        JSONObject credentials = new JSONObject(tokener);

        try {
            credentialsFile.close();
        } catch (IOException e) {
            System.out.println("Erro ao fechar o arquivo credentials.json");
        }

        this.clientId = credentials.getString("client_id");
        this.clientSecret = credentials.getString("client_secret");
        this.certificate = credentials.getString("certificate");
        this.sandbox = credentials.getBoolean("sandbox");
        this.debug = credentials.getBoolean("debug");
    }

    /**
     * Retorna o client ID utilizado para autenticação.
     *
     * @return o client ID.
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Retorna o client secret utilizado para autenticação.
     *
     * @return o client secret.
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * Retorna o caminho do certificado digital utilizado na comunicação.
     *
     * @return o caminho do certificado.
     */
    public String getCertificate() {
        return certificate;
    }

    /**
     * Informa se o ambiente atual está configurado para sandbox (testes).
     *
     * @return {@code true} se for sandbox, {@code false} se for produção.
     */
    public boolean isSandbox() {
        return sandbox;
    }

    /**
     * Informa se o modo debug está ativado.
     *
     * @return {@code true} se estiver em modo debug, {@code false} caso contrário.
     */
    public boolean isDebug() {
        return debug;
    }
}
