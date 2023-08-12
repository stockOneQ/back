package umc.stockoneqback.product.service;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

@Service
public class PassProductFCMService {
    private static final String BODY = "유통기한이 지났습니다.\n당장 폐기해주세요!";

    public void sendNotification(String token, String storeCondition, String productName) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(makeTitle(storeCondition, productName))
                        .setBody(BODY)
                        .build())
                .setToken(token)
                .build();

        FirebaseMessaging.getInstance().send(message);
    }

    private String makeTitle(String storeCondition, String productName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[")
                    .append(storeCondition)
                    .append("-")
                    .append(productName)
                    .append("]");
        return stringBuilder.toString();
    }
}
