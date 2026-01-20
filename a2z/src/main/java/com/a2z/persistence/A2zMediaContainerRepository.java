package com.a2z.persistence;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.a2z.dao.MediaContainer;

import jakarta.transaction.Transactional;
import java.util.List;


@Transactional
@Repository
public interface A2zMediaContainerRepository extends CrudRepository<MediaContainer, String>{

	Optional<MediaContainer> findById(String code);
	
	/*
	 * @Query("SELECT ad FROM MediaContainer ad WHERE ad.code=:code")
	 * Optional<MediaContainer> findByCode(@Param("code") String code);
	 */

	List<MediaContainer> findAllByUserId(String userId);
}
