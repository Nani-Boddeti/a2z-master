package com.a2z.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.a2z.dao.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a2z.enums.OrderStatus;
import com.a2z.enums.PrimeStatus;

import jakarta.transaction.Transactional;

@Transactional
@Repository
public interface RootRepository extends CrudRepository<RootEntity, Long> {


	@Query("SELECT add FROM A2zAddress add WHERE add.customer=:customer")
	List<A2zAddress> getMyAddresses(@Param("customer") Customer customer);

	Optional<A2zOrder> findByApprovalRequest(ApprovalRequest approvalRequest);

	@Query("SELECT add FROM A2zAddress add WHERE add.id=:id AND add.customer=:customer")
	Optional<A2zAddress> getAddressById(@Param("id") Long id , @Param("customer") Customer customer);
	
	@Query("SELECT ord FROM A2zOrder ord WHERE ord.customer=:customer")
	Page<A2zOrder> getMyOrders(@Param("customer") Customer customer, PageRequest pageRequest);

	@Query("SELECT ord FROM A2zOrder ord WHERE ord.customer=:customer")
	Optional<List<A2zOrder>> getMyOrders(@Param("customer") Customer customer);
	
	@Query("SELECT ord FROM A2zOrder ord WHERE ord.id=:id AND ord.customer=:customer")
	Optional<A2zOrder> getOrderDetails(@Param("id") Long id , @Param("customer") Customer customer);
	
	@Query("SELECT ord FROM A2zOrder ord WHERE ord.status!=:status AND ord.customer=:customer")
	List<A2zOrder> getOrdersByCustomerAndNotInStatus(@Param("status") OrderStatus status , @Param("customer") Customer customer);

	@Query("SELECT ord FROM A2zOrder ord WHERE ord.status=:status AND ord.customer=:customer")
	Page<A2zOrder> getOrdersByCustomerAndInStatus(@Param("status") OrderStatus status , @Param("customer") Customer customer, PageRequest pageRequest);


	@Query("SELECT cat FROM A2zCategory cat WHERE cat.isVisible=:isVisible")
	List<A2zCategory> getAllCategories(@Param("isVisible") boolean isVisible);

	@Query("SELECT cat.categoryName FROM A2zCategory cat WHERE cat.isVisible=:isVisible")
	List<String> getAllCategorieNames(@Param("isVisible") boolean isVisible);

	@Query("SELECT cat FROM A2zCategory cat WHERE cat.id=:id")
	Optional<A2zCategory> getCategory(@Param("id") Long id);

	@Query("SELECT cat FROM A2zCategory cat WHERE cat.categoryCode=:code")
	Optional<A2zCategory> getCategoryByCode(@Param("code") String code);

	@Query("SELECT u FROM UserGroup u")
	List<UserGroup> getAllUserGroups();
	
	@Query("SELECT u FROM UserGroup u WHERE u.id=:id")
	Optional<UserGroup> getUserGroup(@Param("id") Long id);
	
	@Query("SELECT ar FROM ApprovalRequest ar WHERE ar.customer=:customer")
	Page<ApprovalRequest> getAllApprovalRequests(@Param("customer") Customer customer , PageRequest pageRequest);
	
	@Query("SELECT ar FROM ApprovalRequest ar WHERE ar.id=:id AND ar.customer=:customer")
	Optional<ApprovalRequest> getApprovalRequestDetails(@Param("id") Long id, @Param("customer") Customer customer);
	
	@Query("SELECT wish FROM A2zWishlist wish WHERE wish.customer=:customer")
	List<A2zWishlist> getWishlistForCustomer(@Param("customer") Customer customer);
	
	@Query("SELECT wish FROM A2zWishlist wish WHERE wish.id=:id AND wish.customer=:customer")
	Optional<A2zWishlist> getWishlistDetails(@Param("id") Long id, @Param("customer") Customer customer);
	
	@Query("SELECT ug from UserGroup ug WHERE ug.userGroupName=:name")
	Optional<UserGroup> getUserGroupByName(@Param("name") String name);
	
	
	@Query("SELECT pu FROM PrimeUser pu WHERE pu.isActive=:isActive AND pu.customer=:customer")
	List<PrimeUser> getPrimeUserByCustomerAndActive(@Param("isActive") boolean isActive , @Param("customer") Customer customer);

	@Query("SELECT pu FROM PrimeUser pu WHERE pu.status=:status AND pu.customer=:customer")
	List<PrimeUser> getPrimeUserByCustomerAndStatus(@Param("status") PrimeStatus status , @Param("customer") Customer customer);

	@Query("SELECT pu FROM PrimeUser pu WHERE pu.isActive=:isActive AND pu.primeExpireDate <:date")
	List<PrimeUser> getActivePrimeUserAndExpired(@Param("isActive") boolean isActive , @Param("date") LocalDate date);
	
	@Query("SELECT pu FROM PrimeUser pu WHERE  pu.customer=:customer")
	List<PrimeUser> getPrimeUserByCustomer(@Param("customer") Customer customer);

	@Query("SELECT add FROM A2zAddress add WHERE add.customer=:customer and add.isDefaultAddress=:isDefault")
	Optional<A2zAddress> getMyDefaultAddress(@Param("customer") Customer customer,@Param("isDefault") boolean isDefault);
}
