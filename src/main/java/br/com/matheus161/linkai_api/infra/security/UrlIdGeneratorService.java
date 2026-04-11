package br.com.matheus161.linkai_api.infra.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Service
public class UrlIdGeneratorService {

    private static final String BASE62_ALPHABET =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final int OUTPUT_LENGTH = 7;

    // BASE62^7 = 3.521.614.606.208 combinações
    // Representável em 42 bits → usamos 6 bytes do HMAC e mascaramos
    private static final long BASE62_7_MODULUS = 3_521_614_606_208L; // 62^7

    private final byte[] secretKey;
    private final SecureRandom secureRandom;

    public UrlIdGeneratorService(@Value("${url.id.secret-key}") String secretKey) {
        this.secretKey = secretKey.getBytes(StandardCharsets.UTF_8);
        this.secureRandom = new SecureRandom();
    }

    public String generate() {
        byte[] payload = buildPayload();
        byte[] hash = hmacSha256(payload, secretKey);

        // Pega os primeiros 6 bytes do HMAC → Long de 48 bits
        // Aplica módulo 62^7 para caber exatamente no espaço de 7 chars
        long value = extractLong6(hash) % BASE62_7_MODULUS;

        return encodeBase62Fixed(value, OUTPUT_LENGTH);
    }

    /**
     * Payload: 4 bytes de timestamp (segundos) + 4 bytes de entropia.
     * Timestamp em segundos ocupa 32 bits — válido até 2106.
     */
    private byte[] buildPayload() {
        long nowSeconds = System.currentTimeMillis() / 1000;
        byte[] entropy = new byte[4];
        secureRandom.nextBytes(entropy);

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putInt((int) nowSeconds);
        buffer.put(entropy);
        return buffer.array();
    }

    private byte[] hmacSha256(byte[] data, byte[] key) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(key, HMAC_ALGORITHM));
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Falha ao inicializar HMAC-SHA256", e);
        }
    }

    /**
     * Lê 6 bytes como long positivo de 48 bits.
     * Evita negativos com máscara 0xFFFFFFFFFFFFL.
     */
    private long extractLong6(byte[] hash) {
        long v = 0;
        for (int i = 0; i < 6; i++) {
            v = (v << 8) | (hash[i] & 0xFF);
        }
        return v & 0xFFFFFFFFFFFFL;
    }

    /**
     * Codifica um long em BASE62 com comprimento fixo garantido.
     * Sem BigInteger — operações diretas em long são suficientes para 62^7.
     */
    private String encodeBase62Fixed(long value, int length) {
        char[] result = new char[length];
        for (int i = length - 1; i >= 0; i--) {
            result[i] = BASE62_ALPHABET.charAt((int) (value % 62));
            value /= 62;
        }
        return new String(result);
    }
}
