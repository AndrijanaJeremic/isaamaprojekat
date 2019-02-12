package com.example.ISA_AMA_projekat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.Hotel;
import com.example.ISA_AMA_projekat.model.Vozilo;


public interface VoziloRepository extends JpaRepository<Vozilo, Integer>{

	@Modifying
	@Transactional
	@Query("update Vozilo v set v.naziv = ?1 , v.marka = ?2 , v.model = ?3 , v.godina_proizvodnje = ?4 , v.broj_sedista = ?5 , v.tip = ?6, v.cena_dan = ?7 where v.id = ?8")
	public void updateVozilo(String naziv, String marka, String model, int godina_proizvodnje, int broj_sedista, String tip, double cena_dan,  Integer id);

	@Modifying
	@Transactional
	@Query("update Vozilo v set v.filijala.id = ?1 where v.id = ?2")
	public void updateFilVozilo(Integer filijala,  Integer id);
	
	
	@Modifying
	@Transactional
	@Query(value = "update vozilo set vozilo.prosecna_ocena=?1 where vozilo.id=?2", nativeQuery = true)
	public void updateProsecnaVozilo(double prosecna_ocena, Integer vozilo_id);

	@Transactional
	@Query("select vozilo from Vozilo vozilo where vozilo.marka=?1 and vozilo.naziv=?2 and vozilo.model=?3 and vozilo.godina_proizvodnje=?4 and vozilo.filijala.id=?5")
	public Vozilo postoji(String marka, String naziv, String model, int godina_proizvodnje, Integer filijala);
	

}
