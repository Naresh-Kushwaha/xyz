package com.navratri.app.user.repository;

import com.navratri.app.user.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findTopByPhoneNumberAndConsumedFalseOrderByCreatedAtDesc(String phoneNumber);
}
