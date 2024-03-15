package com.a2z.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.a2z.dao.Country;

import jakarta.transaction.Transactional;

@Transactional
@Repository
public interface A2zCountryRepository extends JpaRepository<Country, String>{

	Optional<Country> findById(String id);
}
