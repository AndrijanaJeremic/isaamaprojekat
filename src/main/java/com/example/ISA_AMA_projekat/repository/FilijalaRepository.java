package com.example.ISA_AMA_projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.Filijala;

public interface FilijalaRepository extends JpaRepository<Filijala, Integer>{

	@Modifying
	@Query("update Filijala f set f.adresa.id = ?1 where f.id = ?2")
	public void updateFilijala(Integer adresa_id,  Integer id);
	
	@Modifying
	@Query("update Filijala f set f.rentacar.id = ?1 where f.id = ?2")
	public void updateRentacarFil(Integer idr,  Integer id);

}
