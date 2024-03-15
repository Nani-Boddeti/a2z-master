package com.a2z.persistence;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.a2z.dao.Price;

import jakarta.transaction.Transactional;

@Transactional
@Repository
public interface A2zPriceRepository extends CrudRepository<Price,Long>{

	Optional<Price> findById(Long id);
	
}
