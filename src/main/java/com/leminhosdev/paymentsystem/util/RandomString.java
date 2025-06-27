package com.leminhosdev.paymentsystem.util;

import java.security.SecureRandom;

/**
 * Classe utilitária para geração de strings aleatórias seguras,
 * utilizando {@link SecureRandom} e um conjunto de caracteres alfanuméricos.
 */
public class RandomString {

    /**
     * Conjunto de caracteres permitido na string gerada.
     * Contém letras maiúsculas, minúsculas e números.
     */
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * Gera uma string aleatória com o tamanho especificado.
     *
     * @param length O comprimento desejado da string.
     * @return Uma string aleatória segura com os caracteres definidos.
     */
    public static String generateRandomString(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }
}
