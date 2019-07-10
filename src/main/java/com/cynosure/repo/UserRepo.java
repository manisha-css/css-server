package com.cynosure.repo;

import com.cynosure.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("update User u set u.accountLocked = false where u.id = :id")
  int verifyUser(@Param("id") long userId);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("update User u set u.verificationCode = :verificationCode where u.userName = :userName")
  int updateVerificationCode(
      @Param("userName") String userName, @Param("verificationCode") String verificationCode);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query("update User u set u.password = :password where u.id = :id")
  int updateNewPassword(@Param("id") long userId, @Param("password") String encodedPassword);

  @Modifying(flushAutomatically = true, clearAutomatically = true)
  @Query(
      "update User u set u.givenName = :givenName, u.publicProfile = :publicProfile where u.id = :id")
  int updateUserProfile(
      @Param("id") long userId,
      @Param("givenName") String givenName,
      @Param("publicProfile") String publicProfile);

  Optional<User> findByUserName(String userName);

  Optional<User> findByUserNameAndVerificationCode(String userName, String verificationCode);
}
