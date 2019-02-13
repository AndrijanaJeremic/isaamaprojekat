package com.example.ISA_AMA_projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ISA_AMA_projekat.model.Grad;

public interface GradRepository extends JpaRepository<Grad, Integer>{
	
	Grad findOneByNaziv(String naziv);
	
	Grad findOneById(Integer id);
	

}
