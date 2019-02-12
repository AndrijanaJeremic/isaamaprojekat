package com.example.ISA_AMA_projekat.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.ISA_AMA_projekat.model.Rezervacija;

public interface RezervacijaRepository extends JpaRepository<Rezervacija, Integer>
{
	public Rezervacija save(Rezervacija rez);
	
	public Optional<Rezervacija> findById(Integer id);
	
	@Modifying
	@Query(value = "update Rezervacija r set r.rezervacija_vozila_id = ?1 where r.id = ?2", nativeQuery = true)
	public void updateRezVozila(Integer id_rezVozila, Integer id_rez);
	
	@Modifying
	@Query(value = "update Rezervacija r set r.rezevacija_hotel_id = ?1 where r.id = ?2", nativeQuery = true)
	public void updateRezHotel(Integer id_rezHotel, Integer id_rez);
	
	@Modifying
	@Query(value = "update Rezervacija r set r.cena = ?1 where r.id = ?2", nativeQuery = true)
	public void updateCenaRez(double cena, Integer id_rez);
	

	@Modifying
	@Query(value = "update Rezervacija r set r.rezevacija_hotel_id = null where r.id = ?1", nativeQuery = true)
	public void updateHotelId( Integer id_rez);
	
	@Modifying
	@Query(value = "update Rezervacija r set r.rezervacija_vozila_id = null where r.id = ?1", nativeQuery = true)
	public void updateVoziloId( Integer id_rez);
	
	@Modifying
	@Query(value = "update Rezervacija r set r.zavrsena = true where r.id = ?1", nativeQuery = true)
	public void zavrsiRez( Integer id_rez);
	
	@Modifying
	@Query(value = "delete from osoba_iz_rez where osoba_iz_rez.korisnik_id=?1 and osoba_iz_rez.rezervacija_id=?2", nativeQuery = true)
	public void obrisiOsobu( Integer osoba_id, Integer rez_id);
	
	@Modifying
	@Query(value = "update osoba_iz_rez set osoba_iz_rez.potvrdjeno = true where osoba_iz_rez.korisnik_id=?1 and osoba_iz_rez.rezervacija_id=?2", nativeQuery = true)
	public void potvrdiRez( Integer osoba_id, Integer rez_id);
	
	@Modifying
	@Query(value = "update Rezervacija r set r.cena = ?1 where r.id = ?2", nativeQuery = true)
	public void updateCena( double cena, Integer id);
	
	@Modifying
	@Query(value = "delete from osoba_iz_rez where osoba_iz_rez.rezervacija_id=?1", nativeQuery = true)
	public void obrisiSveOsobe( Integer rez_id);

}
