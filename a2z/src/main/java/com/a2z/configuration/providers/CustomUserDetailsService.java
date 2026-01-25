
package com.a2z.configuration.providers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.a2z.dao.Customer;
import com.a2z.persistence.PODCustomerRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private PODCustomerRepository userRepository;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(11);
	}

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		Optional<Customer> customerOpt = userRepository.getUserByPhoneOrUserName(userName);
		if (customerOpt.isEmpty()) {
			throw new UsernameNotFoundException("No User Found");
		}
		Customer customer = customerOpt.get();
		return new org.springframework.security.core.userdetails.User(customer.getUserName(), customer.getPassword(),
				!customer.isDisabled(), true, true, true, getAuthorities(List.of(customer.getRole())));
	}

	private Collection<? extends GrantedAuthority> getAuthorities(List<String> roles) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		return authorities;
	}
}
