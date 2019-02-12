package com.example.ISA_AMA_projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ISA_AMA_projekat.model.Authority;


public interface AuthorityRepository extends JpaRepository<Authority, Integer>{
	
	Authority findOneByName(String naziv);

}
