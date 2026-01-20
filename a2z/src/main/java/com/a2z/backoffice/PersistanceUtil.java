package com.a2z.backoffice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.a2z.dao.Customer;
import com.a2z.data.CustomerData;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.populators.CustomerPopulator;

@RestController
@ResponseBody
@PreAuthorize("isAuthenticated()")
public class PersistanceUtil {

	@Autowired
	private PODCustomerRepository customerRepo;
	
	@Autowired
	private CustomerPopulator customerPopulator;
	
	/* @PreAuthorize("isAuthenticated() and #oauth2.hasScope('app.write')") */
	@GetMapping("/users")
	@Secured("ROLE_ADMIN")
	public List<CustomerData> getUsersList(Authentication authentication){
		Iterable<Customer> customerItr = customerRepo.findAll();
		List<CustomerData> customerList = new ArrayList<CustomerData>();
		customerItr.forEach(cus -> {
			CustomerData customerData = new CustomerData();
			customerPopulator.populate(cus, customerData);
			customerList.add(customerData);
		});
		return customerList;
	}
	@GetMapping("/users/{userID}")
	public CustomerData getUsersList(@PathVariable final String userID){
		Optional<Customer> customer = customerRepo.findById(userID);
		CustomerData customerData = new CustomerData();
		if(customer.isPresent()) customerPopulator.populate(customer.get(), customerData);
		return customerData;
	}
	
	
}
