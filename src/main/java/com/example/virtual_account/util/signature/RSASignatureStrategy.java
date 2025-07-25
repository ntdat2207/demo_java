package com.example.virtual_account.util.signature;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RSASignatureStrategy implements SignatureStrategy {

    @Override
    public byte[] sign(byte[] data, PrivateKey privateKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(data);
        return sig.sign();
    }

    @Override
    public boolean verify(byte[] data, byte[] signatureBytes, PublicKey publicKey) throws Exception {
        log.info("Data: {}", new String(data));
        log.info("Signature (base64): {}", Base64.getEncoder().encodeToString(signatureBytes));
        log.info("Public key: {}", publicKey.toString());
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(signatureBytes);
    }

    @Override
    public String algorithmName() {
        return "SHA256";
    }

    @Override
    public PublicKey loadPublicKey(String key) throws Exception {
        String cleanPem = key
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decoded = Base64.getDecoder().decode(cleanPem);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new X509EncodedKeySpec(decoded));
    }

}
