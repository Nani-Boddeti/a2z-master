package com.a2z.persistence;

import java.util.List;
import java.util.Optional;

import com.a2z.dao.AdPost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.a2z.dao.OrderEntry;

import jakarta.transaction.Transactional;

@Transactional
@Repository
public interface OrderEntryRepository extends CrudRepository<OrderEntry, Long> {

	 Optional<OrderEntry> findById(Long id);

	 List<OrderEntry> findByAdPost(AdPost adPost);
}
