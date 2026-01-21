package com.a2z.services;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.a2z.dao.Customer;
import com.a2z.dao.OTP;
import com.a2z.persistence.PODOTPRepository;
import com.a2z.services.impl.DefaultCustomerService;

@Service
public class OTPGenerator {

	@Autowired
	private DefaultCustomerService customerService;

	@Autowired
	private PODOTPRepository otpRepo;

	public String generateOTP(String mobile) {
		String genOtp = RandomStringUtils.randomNumeric(6);
		List<Character> listOfChar = genOtp.chars().mapToObj(data -> (char) data).collect(Collectors.toList());
		Collections.shuffle(listOfChar);
		String finalOTP = listOfChar.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
				.toString();
		Customer customer = customerService.getUserByEmailORPhone(mobile, mobile);
		boolean isOTPSaved = false;
		if (Objects.nonNull(customer)) {
			Optional<OTP> otpOptional = otpRepo.findById(customer.getUserName());
			OTP otp = new OTP();
			if (otpOptional.isEmpty()) {
				otp.setUid(customer.getUserName());
				otp.setOtp(finalOTP);
				otp.setTimeCreated(new Date());
				otp.setValidated(false);
				otpRepo.save(otp);
				isOTPSaved = true;
			} else {
				otp = otpOptional.get();
				otp.setOtp(finalOTP);
				otp.setTimeCreated(new Date());
				otp.setValidated(false);
				otpRepo.save(otp);
				isOTPSaved = true;
			}
		}
		return isOTPSaved ? finalOTP : "User Not Found!";
	}
}
