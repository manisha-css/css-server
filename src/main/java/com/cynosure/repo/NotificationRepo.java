package com.cynosure.repo;

import com.cynosure.model.EmailNotification;
import com.cynosure.util.NotificationStatusEnum;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepo extends JpaRepository<EmailNotification, Long> {

  List<EmailNotification> findTopTenByStatusInAndTotalAttemptsLessThan(
      List<NotificationStatusEnum> notificationStatusEnums, int minCnt);
}
