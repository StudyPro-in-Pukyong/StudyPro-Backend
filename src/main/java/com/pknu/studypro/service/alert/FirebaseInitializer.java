package com.pknu.studypro.service.alert;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class FirebaseInitializer {
    @PostConstruct
    public void initialize() {
        // firebase config
        try (InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase/studypro-firebase-adminsdk.json")) {

            if (serviceAccount == null) {
                throw new FileNotFoundException("Firebase service account file not found in classpath: firebase/studypro-firebase-adminsdk.json");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
