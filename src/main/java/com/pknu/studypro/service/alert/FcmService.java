package com.pknu.studypro.service.alert;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

@Service
public class FcmService {

    /*
        특정 기기 타겟팅
        header : Bearer FCM SDK Access Token
        {
           "message":{
              "token":"bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...",
              "notification":{
                "body":"This is an FCM notification message!",
                "title":"FCM Message"
              }
           }
        }
     */

    // FCM 전송
    public void sendMessageTo(String fcmToken, String title, String body) {
        // 여러 메세지를 보낼 때 참고
        // Create a list containing up to 500 messages.
//        List<Message> messages = Arrays.asList(
//                Message.builder()
//                        .setNotification(Notification.builder()
//                                .setTitle("Price drop")
//                                .setBody("5% off all electronics")
//                                .build())
//                        .setToken(registrationToken)
//                        .build(),
//                // ...
//                Message.builder()
//                        .setNotification(Notification.builder()
//                                .setTitle("Price drop")
//                                .setBody("2% off all books")
//                                .build())
//                        .setTopic("readers-club")
//                        .build()
//        );
        //        String responses = FirebaseMessaging.getInstance().sendAll(messages);

        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setAndroidConfig(AndroidConfig.builder() // 어플 설정
                            .setPriority(AndroidConfig.Priority.HIGH) // 우선 순위를 HIGH로 설정
                            .setNotification(AndroidNotification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build())
                            .build())
                    .setApnsConfig(ApnsConfig.builder() // iOS 설정
                            .setAps(Aps.builder()
                                    .setAlert(ApsAlert.builder()
                                            .setTitle(title)
                                            .setBody(body)
                                            .build())
                                    .setSound("default") // 사운드 설정
                                    .build())
                            .build())
                    .setWebpushConfig(WebpushConfig.builder()
                            .setNotification(WebpushNotification.builder()
                                    .setTitle(title) // 알림 제목
                                    .setBody(body) // 알림 본문
                                    .build())
                            .setFcmOptions(WebpushFcmOptions.builder()
                                    .setLink("https://localhost:8080/login") // 클릭 시 이동할 특정 링크
                                    .build())
                            .build())
                    .build();

            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }
}