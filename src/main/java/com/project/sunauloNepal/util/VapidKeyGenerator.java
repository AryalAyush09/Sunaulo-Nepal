package com.project.sunauloNepal.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;

public class VapidKeyGenerator {
    public static void main(String[] args) throws Exception {
        KeyPair keyPair = generateVapidKeys();
        String publicKey = encodePublicKey((ECPublicKey) keyPair.getPublic());
        String privateKey = encodePrivateKey((ECPrivateKey) keyPair.getPrivate());

        System.out.println("Public Key: " + publicKey);
        System.out.println("Private Key: " + privateKey);
    }

    public static KeyPair generateVapidKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        keyPairGenerator.initialize(256);  // secp256r1 curve
        return keyPairGenerator.generateKeyPair();
    }

    public static String encodePublicKey(ECPublicKey publicKey) {
        byte[] encoded = publicKey.getEncoded();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encoded);
    }

    public static String encodePrivateKey(ECPrivateKey privateKey) {
        byte[] encoded = privateKey.getEncoded();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encoded);
    }
}
