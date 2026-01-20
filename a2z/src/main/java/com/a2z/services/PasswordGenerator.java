package com.a2z.services;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class PasswordGenerator {

	public String generatePassword() {
		String pass = generateRandomSpecialChars(3).concat(generateRandomCharsNumbers(4));
		
		List<Character> listOfChar = pass.chars()  
                .mapToObj(data -> (char) data)  
                .collect(Collectors.toList());  
		Collections.shuffle(listOfChar);  
		String password = listOfChar.stream()  
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)  
                .toString();  
		return password;
	}
	
	private String generateRandomSpecialChars(int length) {
		return RandomStringUtils.randomAscii(33,45).substring(0,length);
	}
	private String generateRandomCharsNumbers(int length) {
		return RandomStringUtils.randomAlphanumeric(length);
	}
	
}
