package umc.stockoneqback.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class FcmConfig {
    @Value("${TYPE}")
    private String type;
    @Value("${PROJECT_ID}")
    private String projectId;
    @Value("${PRIVATE_KEY_ID}")
    private String privateKeyId;
    @Value("${PRIVATE_KEY}")
    private String privateKey;
    @Value("${CLIENT_EMAIL}")
    private String clientEmail;
    @Value("${CLIENT_ID}")
    private String clientId;
    @Value("${AUTH_URI}")
    private String authUri;
    @Value("${TOKEN_URI}")
    private String tokenUri;
    @Value("${AUTH_PROVIDER_X509_CERT_URL}")
    private String authProviderCertUrl;
    @Value("${CLIENT_X509_CERT_URL}")
    private String clientCertUrl;
    @Value("${UNIVERSE_DOMAIN}")
    private String universeDomain;

    private final List<String> keys = List.of("type", "project_id", "private_key_id", "private_key",
            "client_email", "client_id", "auth_uri", "token_uri", "auth_provider_x509_cert_url", "client_x509_cert_url", "universe_domain");
    private final List<String> values = List.of(type, projectId, privateKeyId, privateKey, clientEmail, clientId, authUri,
            tokenUri, authProviderCertUrl, clientCertUrl, universeDomain);

    @Bean
    public FirebaseMessaging initialize() throws IOException {
        InputStream refreshToken = convertYmlToJson();

        FirebaseApp firebaseApp = null;
        List<FirebaseApp> firebaseAppList = FirebaseApp.getApps();

        if (firebaseAppList != null && !firebaseAppList.isEmpty()) {
            for (FirebaseApp app : firebaseAppList) {
                if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
                    firebaseApp = app;
                    break;
                }
            }
        } else {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(refreshToken))
                    .setProjectId("stockoneq-fcm")
                    .build();
            firebaseApp = FirebaseApp.initializeApp(options);
        }

        return FirebaseMessaging.getInstance(firebaseApp);
    }

    private InputStream convertYmlToJson() {
        JsonObject jsonConfig = new JsonObject();
        for (int i = 0; i < keys.size(); i++)
            jsonConfig.addProperty(keys.get(i), values.get(i));
        return new ByteArrayInputStream(jsonConfig.toString().getBytes());
    }
}
