package com.example.ISA_AMA_projekat.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.Let;

public interface LetRepository extends JpaRepository<Let,Integer>
{
	Optional<Let> findById(Integer id);
	
	public Let save(Let let);
	
	@Modifying
	@Query(value = "delete from zauzeta_sedista where zauzeta_sedista.let_id=?1 and zauzeta_sedista.zauzeta_sedista=?2", nativeQuery = true)
	void deleteZauzetoSediste(Integer let_id, int sediste);
	
	
	@Modifying
	@Query(value = "update let set let.prosecna_ocena=?1 where let.id=?2", nativeQuery = true)
	public void updateProsecna(double prosecna_ocena, Integer let_id);
	
	
	

	@Modifying
	@Transactional
	@Query(value = "update let set let.odakle=?2, let.dokle=?3, let.vreme_poletanja=?4, let.vreme_sletanja=?5, let.trajanje=?6, let.udaljenost=?7, let.cena=?8, let.max_kapacitet=?9, let.popust=?10 where let.id=?1", nativeQuery = true)
	public void updateFlight(Integer id, String odakle, String dokle, Date vremePoletanja, Date vremeSletanja, int trajanje,
			double udaljenost, double cena, int maxKapacitet, int popust);
	

	
	@Modifying
	@Transactional
	@Query(value = "delete from aviokompanija_brzi_letovi where aviokompanija_brzi_letovi.brzi_letovi_id = ?1", nativeQuery = true)
	public void deleteFromAction(Integer flightID);
	
	
	@Modifying
	@Transactional
	@Query(value = "insert into aviokompanija_brzi_letovi (aviokompanija_id, brzi_letovi_id) VALUES (?1, ?2)", nativeQuery = true)
	public void addToAcion(Integer airlineID, Integer flightID);
	
	
}
