package com.example.virtual_account.util.signature;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface SignatureStrategy {
    byte[] sign(byte[] data, PrivateKey privateKey) throws Exception;

    boolean verify(byte[] data, byte[] signatureBytes, PublicKey publicKey) throws Exception;

    String algorithmName(); // Dùng để lookup khi cần

    PublicKey loadPublicKey(String key) throws Exception;
}
