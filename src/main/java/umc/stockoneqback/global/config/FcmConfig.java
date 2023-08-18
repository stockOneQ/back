package umc.stockoneqback.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Configuration
public class FcmConfig {
    @Bean
    public FirebaseMessaging initialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("/firebase/firebase_service_secret.yml");
        InputStream inputStream = resource.getInputStream();
        InputStream refreshToken = convertYmlToJson(inputStream);

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

    private InputStream convertYmlToJson(InputStream inputStream) {
        Yaml yaml = new Yaml();
        Map<String, Object> configMap = yaml.load(inputStream);

        JsonObject jsonConfig = new JsonObject();
        for (Map.Entry<String, Object> entry : configMap.entrySet()) {
            jsonConfig.addProperty(entry.getKey(), entry.getValue().toString());
        }

        return new ByteArrayInputStream(jsonConfig.toString().getBytes());
    }
}
