package com.cynosure.model;

import com.cynosure.util.NotificationStatusEnum;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "notification")
public class EmailNotification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @Column(nullable = false)
  @NotNull
  private String notificationTemplate;

  @Column(name = "email_to", nullable = false)
  private String emailTo;

  @Column(name = "email_cc")
  private String emailCC;

  @Column(name = "email_bcc")
  private String emailBCC;

  @Column(name = "subject", nullable = false)
  private String subject;

  @Column(nullable = false)
  private int totalAttempts;

  @Column(name = "params")
  private String parameterValues;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationStatusEnum status;

  @Column(nullable = false)
  private Instant notificationDate;
}
