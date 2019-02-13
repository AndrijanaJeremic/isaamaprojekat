package com.example.ISA_AMA_projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.ISA_AMA_projekat.model.Popust;

public interface PopustRepository extends JpaRepository<Popust, Integer> {

	@Modifying
	@Query(value = "insert into soba_popusti (soba_id, popusti_id) VALUES (?1, ?2)", nativeQuery = true)
	public void updateSoba(Integer soba_id, Integer popust_id);
	
	@Modifying
	@Query(value = "insert into vozilo_popusti (vozilo_id, popusti_id) VALUES (?1, ?2)", nativeQuery = true)
	public void updateVozilo(Integer vozilo_id, Integer popust_id);
	
	@Modifying
	@Query(value = "insert into popust_usluge (popust_id, usluge_id) VALUES (?1, ?2)", nativeQuery = true)
	public void updateUsluga(Integer popust_id, Integer usluga_id);
}
