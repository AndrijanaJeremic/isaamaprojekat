package com.example.ISA_AMA_projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.Grad;
import com.example.ISA_AMA_projekat.model.Korisnik;


public interface KorisnikRepository extends JpaRepository<Korisnik, Integer>{
	
	Korisnik findOneByEmail(String email);
	
	@Modifying
	@Query("update Korisnik u set u.aktiviran = ?1 where u.id = ?2")
	public void updateAktiviran(boolean aktiviran, Integer id);
	
	@Modifying
	@Query("update Korisnik u set u.aktiviran = ?1, u.lozinka = ?2 where u.id = ?3")
	public void updateAktiviranPass(boolean aktiviran, String new_pass, Integer id);
	
	@Modifying
	@Query("update Korisnik u set u.ime = ?2, u.prezime = ?3, u.email = ?4, u.telefon = ?5, u.grad = ?6 where u.id = ?1")
	public void updateKorisnik(Integer id, String ime, String prezime, String email, String telefon, Grad grad);
	
	@Modifying
	@Query("update Korisnik u set u.bonuspoeni = ?1 where u.id = ?2")
	public void updateBonusPoints(int points, Integer id);
	
}
