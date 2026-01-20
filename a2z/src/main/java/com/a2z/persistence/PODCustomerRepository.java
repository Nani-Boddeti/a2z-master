package com.a2z.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a2z.dao.Customer;

import jakarta.transaction.Transactional;
@Transactional
@Repository
public interface PODCustomerRepository extends CrudRepository<Customer, String> {

	List<Customer> findByLastName(String lastName);

	Optional<Customer> findById(String username);

	@Query("SELECT c FROM Customer c WHERE c.phoneNumber=:phone or c.email=:email")
	public Customer getUserByEmailORPhone(@Param("phone") String phone, @Param("email") String email);
	
}
