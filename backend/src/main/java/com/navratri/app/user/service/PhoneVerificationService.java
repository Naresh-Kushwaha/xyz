package com.navratri.app.user.service;

import com.navratri.app.common.exceptions.BadRequestException;
import com.navratri.app.user.entity.OtpVerification;
import com.navratri.app.user.entity.User;
import com.navratri.app.user.repository.OtpVerificationRepository;
import com.navratri.app.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * MVP phone verification: generates a 6-digit OTP, stores it, and logs it to the
 * console instead of sending a real SMS. Swap sendViaSms() for a real provider
 * (Twilio, MSG91, etc.) before using this outside development.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PhoneVerificationService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final OtpVerificationRepository otpRepository;
    private final UserRepository userRepository;

    @Transactional
    public void sendOtp(String phoneNumber) {
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));

        OtpVerification otp = OtpVerification.builder()
                .phoneNumber(phoneNumber)
                .code(code)
                .expiresAt(Instant.now().plus(10, ChronoUnit.MINUTES))
                .build();
        otpRepository.save(otp);

        sendViaSms(phoneNumber, code);
    }

    @Transactional
    public void verifyOtp(Long userId, String phoneNumber, String code) {
        OtpVerification otp = otpRepository
                .findTopByPhoneNumberAndConsumedFalseOrderByCreatedAtDesc(phoneNumber)
                .orElseThrow(() -> new BadRequestException("No pending verification for this number"));

        if (otp.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("This code has expired, request a new one");
        }
        if (!otp.getCode().equals(code)) {
            throw new BadRequestException("Incorrect code");
        }

        otp.setConsumed(true);
        otpRepository.save(otp);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Account not found"));
        user.setPhoneNumber(phoneNumber);
        user.setPhoneVerified(true);
        userRepository.save(user);
    }

    private void sendViaSms(String phoneNumber, String code) {
        // TODO: replace with a real SMS provider integration.
        log.info("[DEV ONLY] OTP for {} is {}", phoneNumber, code);
    }
}
