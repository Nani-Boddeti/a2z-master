package com.a2z.persistence;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.a2z.dao.OTP;

import jakarta.transaction.Transactional;

@Transactional
@Repository
public interface PODOTPRepository extends CrudRepository<OTP, String>{

	Optional<OTP> findById(String uid);
}
