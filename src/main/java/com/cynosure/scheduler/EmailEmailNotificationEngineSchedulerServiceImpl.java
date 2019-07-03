package com.cynosure.scheduler;

import com.cynosure.model.EmailNotification;
import com.cynosure.service.IEmailNotificationService;
import com.cynosure.util.NotificationStatusEnum;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service(" notificationEngineSchedulerService")
public class EmailEmailNotificationEngineSchedulerServiceImpl {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(EmailEmailNotificationEngineSchedulerServiceImpl.class);

  @Autowired IEmailNotificationService emailNotificationService;

  @Value("${notificationengine.scheduledJob.enabled:false}")
  private boolean notificationEngineScheduledJobEnabled;

  @Scheduled(fixedRateString = "${notificationtask.fixedRate.in.milliseconds}")
  public void mailScheduler() {
    if (!notificationEngineScheduledJobEnabled) {
      return;
    }
    // pick up the list of max 10 email notifications which are new / error but cnt < 3
    // update status to IN_PROGESS so that next scheduer thread does not pick up
    List<EmailNotification> allNotifications =
        emailNotificationService.getPendingNotificationsAndUpdateStatusInProgress();
    // loop through list
    for (EmailNotification notification : allNotifications) {
      if (notification.getStatus().equals(NotificationStatusEnum.IN_PROGRESS)) {
        // update notification status as per checkIsSent flag
        boolean checkIsSent = emailNotificationService.sendNotification(notification);
        LOGGER.debug("Email sentrsult {}", checkIsSent);
        // transaction is set currently at individual record level
        emailNotificationService.updateNotificationStatus(checkIsSent, notification);
      }
    }
  }
}
