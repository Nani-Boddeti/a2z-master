package com.a2z.dao;

import java.time.LocalDate;
import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PrimeUser extends RootEntity {

	@Enumerated(EnumType.ORDINAL)
	PrimeStatus status;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "price_id", referencedColumnName = "id")
	Price PrimeAmount;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "customer_userName", referencedColumnName = "userName")
	Customer customer;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "primeGroup_id", referencedColumnName = "id")
	UserGroup primeGroup;
	LocalDate primedDate;
	Boolean isActive;
	LocalDate primeExpireDate;

	public PrimeStatus getStatus() {
		return status;
	}

	public void setStatus(PrimeStatus status) {
		this.status = status;
	}

	public Price getPrimeAmount() {
		return PrimeAmount;
	}

	public void setPrimeAmount(Price primeAmount) {
		PrimeAmount = primeAmount;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public UserGroup getPrimeGroup() {
		return primeGroup;
	}

	public void setPrimeGroup(UserGroup primeGroup) {
		this.primeGroup = primeGroup;
	}

	public LocalDate getPrimedDate() {
		return primedDate;
	}

	public void setPrimedDate(LocalDate primedDate) {
		this.primedDate = primedDate;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public LocalDate getPrimeExpireDate() {
		return primeExpireDate;
	}

	public void setPrimeExpireDate(LocalDate primeExpireDate) {
		this.primeExpireDate = primeExpireDate;
	}
}
