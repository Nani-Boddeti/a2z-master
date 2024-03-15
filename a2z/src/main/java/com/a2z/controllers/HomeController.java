package com.a2z.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.a2z.data.CustomerData;
import com.a2z.data.GPS;
import com.a2z.data.OTPFormData;
import com.a2z.persistence.impl.DefaultCustomerService;
import com.a2z.services.GeometryUtils;
import com.a2z.services.OTPGenerator;
import com.a2z.services.PasswordGenerator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@RestController
@ResponseBody
 @Validated 
public class HomeController extends RootController {

	@Autowired
	private DefaultCustomerService customerService;
	
	@Autowired
	private PasswordGenerator passwordGenerator;
	
	@Autowired
	private OTPGenerator otpGenerator;
	
	@Autowired
	GeometryUtils geoUtils;
	
	@GetMapping("/{name}")
	@Secured(value = { "ADMIN" })
	public String greeting(@PathVariable final String name) {
		
		return "hello "+name;
	}
	@GetMapping("/")
	public String home(Authentication authentication) {
		
		return "hello Bro!!";
	}
	
	@PostMapping("/customerSubmit")
	public String saveCustomer(@RequestBody @Valid CustomerData customerData,HttpServletRequest request) {
		customerService.saveCustomer(customerData);
		request.getSession().setAttribute("currentUser", customerData.getUserName());
		return "200/OK";
	}
	
	@GetMapping("/suggest/password")
	public String suggestPassword() {
		return passwordGenerator.generatePassword();
	}
	
	@GetMapping("/generate/otp/{mobile}")
	public String generateOTP(@PathVariable final String mobile) {
		return otpGenerator.generateOTP(mobile);
	}
	
	@PostMapping("/validateOTP")
	public CustomerData validateOTP(@RequestBody @Valid OTPFormData otpFormData,HttpServletRequest request) {
		CustomerData customerData = customerService.validateOTP(otpFormData);
		if(customerData.getUserName()!= null) request.getSession().setAttribute("currentUser", customerData.getUserName());
		return customerData;
	}
	
	@GetMapping("/api/hello")
    public String hello(Principal principal) {
        return "Hello " +principal.getName()+", Welcome to Daily Code Buffer!!";
    }
	
	@GetMapping("/test/currentUser")
    @ResponseBody
    public String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return authentication.getClass() + ": " + currentUserName;
        }
        return "anonymous";
    }
	
	  @GetMapping("/api/test/protected")
	    public String apiProtectedEndpoint() {
	        return "JWT PROTECTED STRING";
	    }

	    @GetMapping("/api/test/unprotected")
	    public String apiUnprotectedEndpoint() {
	        return "JWT UNPROTECTED STRING";
	    }
	    
	    @GetMapping("/getLocation")
	    public void getLocationData(@RequestParam double latitude , @RequestParam double longitude, @RequestParam double radius) {
	    	GPS gps = new GPS();
	    	gps.setDecimalLatitude(Double.valueOf(latitude));
	    	gps.setDecimalLongitude(Double.valueOf(longitude));
	    	geoUtils.getSquareOfTolerance(gps, radius);
	    }
}
