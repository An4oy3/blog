package main.model.repositories;

import main.model.CaptchaCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public interface CaptchaRepository extends JpaRepository<CaptchaCodes, Integer> {
    @Modifying
    @Query(value = "INSERT INTO captcha_codes(code, secret_code, time) values (:code, :secret, now())", nativeQuery = true)
    void addCaptcha(@Param("secret") String secret, @Param("code") String code);

    @Query(value = "select c FROM CaptchaCodes c WHERE secret_code = :secret")
    CaptchaCodes findOneBySecretCode(@Param("secret") String secretCode);

    @Modifying
    @Query(value = "delete FROM captcha_codes WHERE time < :timeDelete", nativeQuery = true)
    int deleteCaptcha(@Param("timeDelete") LocalDateTime timeDelete);
}
