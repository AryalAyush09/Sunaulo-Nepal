package com.project.sunauloNepal.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.Base64;

public class VapidKeyConverter {

    // Convert base64-encoded DER public key to raw base64url public key
    public static String getRawPublicKey(String base64DerPubKey) throws Exception {
        byte[] pubKeyBytes = Base64.getDecoder().decode(base64DerPubKey);
        KeyFactory kf = KeyFactory.getInstance("EC");

        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubKeyBytes);
        PublicKey pubKey = kf.generatePublic(pubSpec);
        ECPublicKey ecPubKey = (ECPublicKey) pubKey;

        // Get the uncompressed EC point (X and Y concatenated with 0x04 prefix)
        ECPoint w = ecPubKey.getW();
        byte[] x = w.getAffineX().toByteArray();
        byte[] y = w.getAffineY().toByteArray();

        // Remove leading zeros if any (because BigInteger.toByteArray() may add leading zero for sign)
        x = stripLeadingZeroes(x);
        y = stripLeadingZeroes(y);

        // Concatenate 0x04 + X + Y
        byte[] rawKey = new byte[1 + x.length + y.length];
        rawKey[0] = 0x04;
        System.arraycopy(x, 0, rawKey, 1, x.length);
        System.arraycopy(y, 0, rawKey, 1 + x.length, y.length);

        return base64UrlEncode(rawKey);
    }

    // Convert base64-encoded DER private key to raw base64url private key
    public static String getRawPrivateKey(String base64DerPrivKey) throws Exception {
        byte[] privKeyBytes = Base64.getDecoder().decode(base64DerPrivKey);
        KeyFactory kf = KeyFactory.getInstance("EC");

        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privKeyBytes);
        PrivateKey privKey = kf.generatePrivate(privSpec);
        ECPrivateKey ecPrivKey = (ECPrivateKey) privKey;

        byte[] s = ecPrivKey.getS().toByteArray();
        s = stripLeadingZeroes(s);

        return base64UrlEncode(s);
    }

    private static byte[] stripLeadingZeroes(byte[] arr) {
        int i = 0;
        while (i < arr.length - 1 && arr[i] == 0) {
            i++;
        }
        byte[] result = new byte[arr.length - i];
        System.arraycopy(arr, i, result, 0, result.length);
        return result;
    }

    private static String base64UrlEncode(byte[] data) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }

    public static void main(String[] args) throws Exception {
        // Your input keys (ASN.1 DER, base64 encoded)
    	String base64DerPublicKey = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEOA3kbN96zAunthe41WburpeQceX88HFxq65gigCHmTOQd8enq9Yy7fOeQB4XFlSbf2uu6_cnHaVB9QsHEx0H8g";
    	String base64DerPrivateKey = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCCOvkPVNI0NpOPxF_TRquwLqsPO6Vwwmw-qHLZUVyWuxw";


        // Replace '_' and '-' back to standard base64 chars for decoding:
        base64DerPublicKey = base64DerPublicKey.replace('-', '+').replace('_', '/');
        base64DerPrivateKey = base64DerPrivateKey.replace('-', '+').replace('_', '/');

        String rawPubKey = getRawPublicKey(base64DerPublicKey);
        String rawPrivKey = getRawPrivateKey(base64DerPrivateKey);

        System.out.println("Raw base64url Public Key: " + rawPubKey);
        System.out.println("Raw base64url Private Key: " + rawPrivKey);
    }
}

