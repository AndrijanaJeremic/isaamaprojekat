package com.example.ISA_AMA_projekat.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.RezervacijaVozila;

public interface RezervacijaVozilaRepository extends JpaRepository<RezervacijaVozila, Integer>{

	@Modifying
	@Query(value = "insert into vozilo_rezervacije (rezervacije_id, vozilo_id) VALUES (?1, ?2)", nativeQuery = true)
	void insertRezervacijaVozila(Integer rezervacija_id, Integer vozilo_id);
	
	@Modifying
	@Query("update RezervacijaVozila rv set rv.datum_rezervacije = ?1 where rv.id = ?2")
	public void updateDatumRez(Date datum_rez,  Integer id);
	
	@Modifying
	@Query(value = "delete from rezervacija_vozila where rezervacija_vozila.id=?1", nativeQuery = true)
	void deleteRezV(Integer id);
	
	@Modifying
	@Query(value = "delete from vozilo_rezervacije where vozilo_rezervacije.rezervacije_id=?1", nativeQuery = true)
	void deleteVoziloRez(Integer id);
	
	@Modifying
	@Query(value = "update rezervacija_vozila rez set rez.aktivirana = ?2 where rez.id = ?1", nativeQuery = true)
	void updateAktivirana(Integer id, boolean aktivirana);
}
