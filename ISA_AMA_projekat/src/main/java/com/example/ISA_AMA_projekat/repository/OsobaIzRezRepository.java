package com.example.ISA_AMA_projekat.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.OsobaIzRez;
import com.example.ISA_AMA_projekat.model.Rezervacija;


public interface OsobaIzRezRepository extends JpaRepository<OsobaIzRez,Integer>
{
	@Modifying
	@Transactional
	@Query("select osobaIzRez.rezervacija from OsobaIzRez osobaIzRez where osobaIzRez.korisnikUcesnik.id = ?1")
	public List<Rezervacija> searchByUser(Integer userID);
}
