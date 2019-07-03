package com.cynosure.service;

import com.cynosure.dto.ContactUsDto;
import com.cynosure.dto.UserDto;
import com.cynosure.model.EmailNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.Map;

public interface IEmailNotificationService {
  void sendTextMail(String to, String subject, String body);

  boolean sendHtmlMailWithTemplate(
      String to,
      String cc,
      String bcc,
      String subject,
      String template,
      Map<String, Object> parameters);

  boolean sendNotification(EmailNotification notification);

  List<EmailNotification> getPendingNotificationsAndUpdateStatusInProgress();

  void updateNotificationStatus(boolean checkIsSent, EmailNotification notification);

  void createNotificationForForgetPassword(UserDto userDto) throws JsonProcessingException;

  void createNotificationForVerification(UserDto userDto) throws JsonProcessingException;

  void createNotificationForContactusFeedback(ContactUsDto contactUsDto)
      throws JsonProcessingException;
}
