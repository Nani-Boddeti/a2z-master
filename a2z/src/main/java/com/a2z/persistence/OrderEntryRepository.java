package com.a2z.persistence;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.a2z.dao.OrderEntry;

import jakarta.transaction.Transactional;

@Transactional
@Repository
public interface OrderEntryRepository extends CrudRepository<OrderEntry, Long> {

	 Optional<OrderEntry> findById(Long id);
}
