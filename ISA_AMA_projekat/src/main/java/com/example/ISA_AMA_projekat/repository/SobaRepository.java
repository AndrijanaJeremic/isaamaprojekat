package com.example.ISA_AMA_projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.ISA_AMA_projekat.model.Soba;

public interface SobaRepository extends JpaRepository<Soba, Integer>{

	@Modifying
	@Query(value = "update soba set soba.prosecna_ocena=?1 where soba.id=?2", nativeQuery = true)
	public void updateProsecnaSoba(double prosecna_ocena, Integer soba_id);
}
