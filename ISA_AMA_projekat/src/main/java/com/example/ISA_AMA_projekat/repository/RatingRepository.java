package com.example.ISA_AMA_projekat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.Rating;

public interface RatingRepository extends JpaRepository<Rating, Integer>{
	
	@Transactional
	@Query("select r.ocena from Rating r where r.let.id = ?1")
	public List<Integer> oceneLeta(Integer let_id);
	
	@Transactional
	@Query("select r.ocena from Rating r where r.aviokompanija.id = ?1")
	public List<Integer> oceneAvio(Integer avio_id);
	
	@Transactional
	@Query("select r.ocena from Rating r where r.hotel.id = ?1")
	public List<Integer> oceneHotel(Integer hotel_id);
	
	@Transactional
	@Query("select r.ocena from Rating r where r.soba.id = ?1")
	public List<Integer> oceneSoba(Integer soba_id);
	
	@Transactional
	@Query("select r.ocena from Rating r where r.rentacar.id = ?1")
	public List<Integer> oceneRent(Integer rent_id);
	
	@Transactional
	@Query("select r.ocena from Rating r where r.vozilo.id = ?1")
	public List<Integer> oceneVozilo(Integer vozilo_id);

}
