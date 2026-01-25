package com.a2z.services.impl;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
public class ForgotPasswordTokenGeneratorService {
    @Value("${forgot.password.token.expiry.hours}")
    Integer token_expiry; // in hours
    @Value("${forgot.password.secret.key.encoded}")
    String token_secret;
    private static final String SEPARATOR = "|";

    // Your app secret (keep secure!)


    public record ResetToken(String token, String userId, Instant expiresAt) {}

    public String generateToken(String userId) {
        Instant expiresAt = Instant.now().plus(Duration.ofHours(token_expiry));
        String payload = userId + SEPARATOR + expiresAt.getEpochSecond();

        Mac mac = getHmac();
        String signature = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(mac.doFinal(payload.getBytes()));

        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString((payload + SEPARATOR + signature).getBytes());
    }

    public ResetToken verifyToken(String tokenStr) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(tokenStr);
            String[] parts = new String(decoded, StandardCharsets.UTF_8)
                    .split("\\" + SEPARATOR);

            if (parts.length != 3) return null;

            String userId = parts[0];
            long expiryUnix = Long.parseLong(parts[1]);
            Instant expiresAt = Instant.ofEpochSecond(expiryUnix);

            // Check expiry first
            if (Instant.now().isAfter(expiresAt)) return null;

            // Verify signature
            String payload = userId + SEPARATOR + expiryUnix;
            String expectedSig = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(getHmac().doFinal(payload.getBytes()));

            if (!parts[2].equals(expectedSig)) return null;

            return new ResetToken(tokenStr, userId, expiresAt);

        } catch (Exception e) {
            return null;
        }
    }

    private Mac getHmac() {
        final byte[] SECRET_KEY = token_secret.getBytes();
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY, "HmacSHA256");
            mac.init(keySpec);
            return mac;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
