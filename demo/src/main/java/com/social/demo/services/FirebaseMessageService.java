package com.social.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

@Service
public class FirebaseMessageService {

  @Autowired
  private FirebaseMessaging firebaseMessaging;

  public String sendNotificationByToken(String recipientToken, String title, String body, String image){

    Notification notification = Notification
    .builder()
    .setTitle(title)
    .setBody(body)
    .setImage(image)
    .build();


    Message message = Message
    .builder()
    .setToken(recipientToken)
    .setNotification(notification)
    .build();

    try {
      firebaseMessaging.send(message);
      return "SUCCESS SENDING NOTIFICATION";
    } catch (Exception e) {
      e.printStackTrace();
      return "ERROR SENDING NOTIFICATION";
    }
  }
}
