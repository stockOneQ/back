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
    @Value("${firebase.type}")
    private String type;
    @Value("${firebase.project_id}")
    private String projectId;
    @Value("${firebase.private_key_id}")
    private String privateKeyId;
    @Value("${firebase.private_key}")
    private String privateKey;
    @Value("${firebase.client_email}")
    private String clientEmail;
    @Value("${firebase.client_id}")
    private String clientId;
    @Value("${firebase.auth_uri}")
    private String authUri;
    @Value("${firebase.token_uri}")
    private String tokenUri;
    @Value("${firebase.auth_provider_x509_cert_url}")
    private String authProviderCertUrl;
    @Value("${firebase.client_x509_cert_url}")
    private String clientCertUrl;
    @Value("${firebase.universe_domain}")
    private String universeDomain;

    @Bean
    public FirebaseMessaging initialize() throws IOException {
        privateKey = "-----BEGIN PRIVATE KEY-----\\n" + privateKey + "-----END PRIVATE KEY-----\\n";

        final List<String> keys = List.of("type", "project_id", "private_key_id", "private_key",
                "client_email", "client_id", "auth_uri", "token_uri", "auth_provider_x509_cert_url", "client_x509_cert_url", "universe_domain");
        final List<String> values = List.of(type, projectId, privateKeyId, privateKey.replace("\\n", "\n"), clientEmail, clientId, authUri,
                tokenUri, authProviderCertUrl, clientCertUrl, universeDomain);

        InputStream refreshToken = convertYmlToJson(keys, values);

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

    private InputStream convertYmlToJson(List<String> keys, List<String> values) {
        JsonObject jsonConfig = new JsonObject();
        for (int i = 0; i < keys.size(); i++)
            jsonConfig.addProperty(keys.get(i), values.get(i));

        return new ByteArrayInputStream(jsonConfig.toString().getBytes());
    }
}
