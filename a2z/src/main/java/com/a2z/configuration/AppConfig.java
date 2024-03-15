package com.a2z.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.a2z.populators.AdPostPopulator;
import com.a2z.populators.AddressPopulator;
import com.a2z.populators.CountryPopulator;
import com.a2z.populators.CustomerPopulator;
import com.a2z.populators.MediaContainerPopulator;
import com.a2z.populators.MediaPopulator;
import com.a2z.populators.OrderEntryPopulator;
import com.a2z.populators.OrderPopulator;
import com.a2z.populators.PaymentInfoBasicPopulator;
import com.a2z.populators.PaymentInfoPopulator;
import com.a2z.populators.PricePopulator;
import com.a2z.populators.WishlistPopulator;
import com.a2z.populators.reverse.AdPostReversePopulator;
import com.a2z.populators.reverse.AddressReversePopulator;
import com.a2z.populators.reverse.CountryReversePopulator;
import com.a2z.populators.reverse.CustomerReversePopulator;
import com.a2z.populators.reverse.OrderEntryReversePopulator;
import com.a2z.populators.reverse.OrderReversePopulator;
import com.a2z.populators.reverse.PaymentInfoBasicReversePopulator;
import com.a2z.populators.reverse.PaymentInfoReversePopulator;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
@CrossOrigin("http://localhost:4200")
public class AppConfig {

	@Bean
	public AddressPopulator addressPopulator() {
		return new AddressPopulator();
	}
	
	@Bean
	public AdPostPopulator adPostPopulator() {
		return new AdPostPopulator();
	}
	@Bean
	public MediaContainerPopulator mediaContainerPopulator() {
		return new MediaContainerPopulator();
	}
	@Bean
	public MediaPopulator mediaPopulator() {
		return new MediaPopulator();
	}
	@Bean
	public OrderPopulator orderPopulator() {
		return new OrderPopulator();
	}
	@Bean
	public OrderEntryPopulator orderEntryPopulator() {
		return new OrderEntryPopulator();
	}
	
	@Bean
	public CustomerPopulator customerPopulator() {
		return new CustomerPopulator();
	}
	
	@Bean
	public CustomerReversePopulator customerReversePopulator() {
		return new CustomerReversePopulator();
	}
	
	@Bean
	public CountryPopulator countryPopulator() {
		return new CountryPopulator();
	}
	
	@Bean
	public CountryReversePopulator countryReversePopulator() {
		return new CountryReversePopulator();
	}
	
	@Bean
	public PaymentInfoPopulator paymentInfoPopulator() {
		return new PaymentInfoPopulator();
	}
	
	@Bean
	public PaymentInfoReversePopulator paymentInfoReversePopulator() {
		return new PaymentInfoReversePopulator();
	}
	
	@Bean
	public PricePopulator pricePopulator() {
		return new PricePopulator();
	}
	
	@Bean
	public AdPostReversePopulator adPostReversePopulator() {
		return new AdPostReversePopulator();
	}
	
	@Bean
	public OrderReversePopulator orderReversePopulator() {
		return new OrderReversePopulator();
	}
	
	@Bean
	public AddressReversePopulator addressReversePopulator() {
		return new AddressReversePopulator();
	}
	
	@Bean
	public OrderEntryReversePopulator orderEntryReversePopulator() {
		return new OrderEntryReversePopulator();
	}
	
	@Bean
	public WishlistPopulator wishlistPopulator() {
		return new WishlistPopulator();
	}
	
	@Bean
	public PaymentInfoBasicReversePopulator paymentInfoBasicReversePopulator() {
		return new PaymentInfoBasicReversePopulator();
	}
	
	@Bean
	public PaymentInfoBasicPopulator paymentInfoBasicPopulator() {
		return new PaymentInfoBasicPopulator();
	}
	
}
