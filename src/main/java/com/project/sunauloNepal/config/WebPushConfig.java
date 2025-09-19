package com.project.sunauloNepal.config;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Utils;

@Configuration
public class WebPushConfig {

    @Value("${vapid.public.key}")
    private String publicKey;

    @Value("${vapid.private.key}")
    private String privateKey;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Bean
    public PushService pushService() throws Exception {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        PushService pushService = new PushService();
        pushService.setPublicKey(Utils.loadPublicKey(publicKey));
        pushService.setPrivateKey(Utils.loadPrivateKey(privateKey));
        pushService.setSubject("mailto:admin@sunauloNepal.com"); // Your email or contact email
        return pushService;
    }
}
