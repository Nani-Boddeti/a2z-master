package com.a2z.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.a2z.dao.AdPost;

import jakarta.transaction.Transactional;
import com.a2z.dao.Customer;


@Transactional
@Repository
public interface A2zAdPostRepository extends JpaRepository<AdPost, Long> {
/*	@EntityGraph(attributePaths = {"sourceAddress", "categories", "customer",
			"Price", "mediaContainer","mediaContainer.medias"})*/
	Optional<AdPost> findById(Long id);

	/*@EntityGraph(attributePaths = {"sourceAddress", "categories", "customer",
			"Price", "mediaContainer","mediaContainer.medias"})*/
	@Query("SELECT ad FROM AdPost ad WHERE ad.isIndexed=:isIndexed AND ad.active=:isActive")
	List<AdPost> findNotIndexedAds(@Param("isIndexed") boolean isIndexed ,@Param("isActive") boolean isActive);
	/*@EntityGraph(attributePaths = {"sourceAddress", "categories", "customer",
			"Price", "mediaContainer","mediaContainer.medias"})*/
	List<AdPost> findByActive(boolean active);
	/*@EntityGraph(attributePaths = {"sourceAddress", "categories", "customer",
			"Price", "mediaContainer","mediaContainer.medias"})*/
	List<AdPost> findByActiveAndCustomer(boolean active, Customer customer);
	/*@EntityGraph(attributePaths = {"sourceAddress", "categories", "customer",
			"Price", "mediaContainer","mediaContainer.medias"})*/
	Page<AdPost> findByCustomer(Customer customer, PageRequest pageRequest);
	
}
