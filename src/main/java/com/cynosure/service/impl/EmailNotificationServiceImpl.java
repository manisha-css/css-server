package com.cynosure.service.impl;

import com.cynosure.dto.ContactUsDto;
import com.cynosure.dto.UserDto;
import com.cynosure.model.EmailNotification;
import com.cynosure.repo.NotificationRepo;
import com.cynosure.service.IEmailNotificationService;
import com.cynosure.util.NotificationStatusEnum;
import com.cynosure.util.NotificationTemplateCodeEnum;
import com.cynosure.util.Translator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

@Service("notificationService")
public class EmailNotificationServiceImpl implements IEmailNotificationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotificationServiceImpl.class);
  private static final String USERNAME_PARAM = "userName";

  @Autowired private Configuration freemarkerConfiguration; // from spring boot freemarker starter

  @Autowired private NotificationRepo notificationRepo;

  @Autowired private JavaMailSender javaMailSender; // from spring boot mail starter

  @Autowired private Environment environment;

  @Autowired private Translator translator;

  @Override
  public boolean sendHtmlMailWithTemplate(
      String to,
      String cc,
      String bcc,
      String subject,
      String template,
      Map<String, Object> parameters) {
    MimeMessagePreparator preparator =
        new MimeMessagePreparator() {
          @Override
          public void prepare(MimeMessage mimeMessage)
              throws MessagingException, TemplateException, IOException {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(environment.getProperty("email.from"));
            helper.setTo(to);
            if (cc != null) {
              helper.setCc(cc);
              helper.setBcc(bcc);
            }
            helper.setSubject(subject);

            StringBuilder content = new StringBuilder();
            String text = "";
            content.append(
                FreeMarkerTemplateUtils.processTemplateIntoString(
                    freemarkerConfiguration.getTemplate(template), parameters));
            text = content.toString();
            helper.setText(text, true);
          }
        };

    try {
      javaMailSender.send(preparator);
      LOGGER.debug("mail is sent successfully to the address..{}", to);
    } catch (Exception e) { // nothing much can be done here
      LOGGER.error("Error in sending notification: {} ", e);
      return false;
    }

    return true;
  }

  @Override
  public void sendTextMail(String to, String subject, String body) {
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setTo(to);
    simpleMailMessage.setSubject(subject);
    simpleMailMessage.setText(body);
    javaMailSender.send(simpleMailMessage);
  }

  @Override
  public boolean sendNotification(EmailNotification notification) {
    boolean checkIsSent = false;
    try {
      String strParameters = notification.getParameterValues();
      ObjectMapper mapper = new ObjectMapper();
      TypeReference<HashMap<String, Object>> typeRef =
          new TypeReference<HashMap<String, Object>>() {};
      InputStream inputStreamParameters =
          new ByteArrayInputStream(strParameters.getBytes(StandardCharsets.UTF_8));
      HashMap<String, Object> parameterModel = mapper.readValue(inputStreamParameters, typeRef);
      checkIsSent =
          this.sendHtmlMailWithTemplate(
              notification.getEmailTo(),
              notification.getEmailCC(),
              notification.getEmailBCC(),
              notification.getSubject(),
              notification.getNotificationTemplate(),
              parameterModel);
    } catch (Exception exception) {
      // nothing much can be done about exception
      checkIsSent = false;
      LOGGER.error("Mail sending failed due to exception ", exception);
    }
    return checkIsSent;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public synchronized List<EmailNotification> getPendingNotificationsAndUpdateStatusInProgress() {

    List<EmailNotification> notifications =
        notificationRepo.findTopTenByStatusInAndTotalAttemptsLessThan(
            Arrays.asList(NotificationStatusEnum.NEW, NotificationStatusEnum.ERROR), 4);

    for (EmailNotification notification : notifications) {
      // updating the notification status to the inProgress
      notification.setStatus(NotificationStatusEnum.IN_PROGRESS);
      notificationRepo.save(notification);
    }
    return notifications;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void updateNotificationStatus(boolean checkIsSent, EmailNotification notification) {

    if (checkIsSent) {
      notification.setStatus(NotificationStatusEnum.SUCCESS);

    } else {
      notification.setStatus(NotificationStatusEnum.ERROR);
      int noOfAttemptsCount = notification.getTotalAttempts();
      noOfAttemptsCount++;
      notification.setTotalAttempts(noOfAttemptsCount);
      LOGGER.error("Mail sending is failed due to invalid mailId or Other Error.!!");
    }
    // updating status of the notification...
    notificationRepo.save(notification);

    LOGGER.debug(
        "updateNotificationStatus for notification ["
            + notification.getId()
            + "] with status ["
            + notification.getStatus()
            + "]");
  }

  @Override
  public void createNotificationForForgetPassword(UserDto userDto) throws JsonProcessingException {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put(USERNAME_PARAM, userDto.getUserName());
    parameterMap.put("password", userDto.getUserPassword());
    String cssAppUrl = environment.getProperty("css.app.url");
    parameterMap.put("cssAppUrl", cssAppUrl + "/#/user/login");
    addEmailNotification(
        userDto.getUserName(),
        translator.toLocale("notification.forgetpassword", null),
        parameterMap,
        NotificationTemplateCodeEnum.FORGET_PASSWORD);
  }

  @Override
  public void createNotificationForVerification(UserDto userDto) throws JsonProcessingException {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put(USERNAME_PARAM, userDto.getUserName());
    parameterMap.put("verificationCode", userDto.getUserVerificationCode());
    addEmailNotification(
        userDto.getUserName(),
        translator.toLocale("notification.verify", null),
        parameterMap,
        NotificationTemplateCodeEnum.CREATE_USER);
  }

  @Override
  public void createNotificationForContactusFeedback(ContactUsDto contactUsDto)
      throws JsonProcessingException {
    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("givenName", contactUsDto.getGivenName());
    parameterMap.put("email", contactUsDto.getEmail());
    parameterMap.put("feedback", contactUsDto.getFeedback());
    addEmailNotification(
        environment.getProperty("email.feedback"),
        environment.getProperty("notification.feedback"),
        parameterMap,
        NotificationTemplateCodeEnum.CONTACTUS_FEEDBACK);
  }

  private void addEmailNotification(
      String email,
      String subject,
      Map<String, Object> parameterMap,
      NotificationTemplateCodeEnum notificationTemplateCodeEnum)
      throws JsonProcessingException {
    String jsonMap = new ObjectMapper().writeValueAsString(parameterMap);
    EmailNotification notification = new EmailNotification();
    notification.setStatus(NotificationStatusEnum.NEW);
    notification.setEmailTo(email);
    notification.setSubject(subject);
    notification.setNotificationDate(Instant.now());
    notification.setParameterValues(jsonMap);
    notification.setNotificationTemplate(notificationTemplateCodeEnum.getValue());
    notificationRepo.save(notification);
  }
}
