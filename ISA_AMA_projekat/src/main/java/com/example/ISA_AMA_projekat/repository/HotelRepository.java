package com.example.ISA_AMA_projekat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.ISA_AMA_projekat.model.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Integer>{

	Hotel findOneByNaziv(String naziv);
	
	@Modifying
	@Query("select hotel from Hotel hotel where hotel.adresa.grad.naziv like ?1 or hotel.adresa.ulica like ?1 or hotel.adresa.broj like ?1 or hotel.naziv like ?1")
	List<Hotel> search(String name_location);
	
	@Modifying
	@Query(value = "update hotel h set h.id_admin = ?2 where h.id = ?1", nativeQuery = true)
	public void updateAdmin(Integer hotelID, Integer adminID);
	
	@Modifying
	@Query("update Hotel h set h.naziv = ?2, h.promotivni_opis = ?3, h.adresa.id = ?4 where h.id = ?1 and h.verzija = ?5")
	public void updateHotel(Integer id, String naziv, String opis, Integer adresa, int verzija);
	
	@Modifying
	@Query(value = "update hotel set hotel.prosecna_ocena=?1 where hotel.id=?2", nativeQuery = true)
	public void updateProsecnaHotel(double prosecna_ocena, Integer hotel_id);
}
