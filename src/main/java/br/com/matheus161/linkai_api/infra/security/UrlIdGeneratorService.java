package br.com.matheus161.linkai_api.infra.security;

import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

@Service
public class UrlIdGeneratorService {

    public String generate() {
        UUID urlId = UUID.randomUUID();

        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        byte[] bytes = convertToBytes(urlId);
        return encoder.encodeToString(bytes);
    }

    private byte[] convertToBytes(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }
}
