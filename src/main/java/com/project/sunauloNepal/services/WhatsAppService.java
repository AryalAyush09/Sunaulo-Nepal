//package com.project.sunauloNepal.services;
//
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class WhatsAppService {
//
//    @Value("${twilio.whatsapp.from}")
//    private String from;
//
//    public void sendWhatsAppMessage(String to, String body) {
//        Message message = Message.creator(
//                new PhoneNumber("whatsapp:" + to),   // Receiver number
//                new PhoneNumber(from),               // Twilio Sandbox number
//                body
//        ).create();
//
//        System.out.println("📩 WhatsApp message sent: " + message.getSid());
//    }
//}

package com.project.sunauloNepal.services;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {

    @Value("${twilio.whatsapp.from}")
    private String from;

    /**
     * Send a WhatsApp message.
     * Automatically adds "whatsapp:" prefix if missing.
     *
     * @param to   phone number in format +97798xxxxxxx
     * @param body message text
     */
    public void sendWhatsAppMessage(String to, String body) {
        String toWithPrefix = to.startsWith("whatsapp:") ? to : "whatsapp:" + to;

        Message message = Message.creator(
                new PhoneNumber(toWithPrefix),   // Receiver number with prefix
                new PhoneNumber(from),           // Twilio Sandbox number
                body
        ).create();

        System.out.println("📩 WhatsApp message sent: " + message.getSid());
    }
}
