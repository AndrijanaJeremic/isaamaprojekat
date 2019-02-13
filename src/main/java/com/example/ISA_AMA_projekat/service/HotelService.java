package com.example.ISA_AMA_projekat.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.Adresa;
import com.example.ISA_AMA_projekat.model.Hotel;
import com.example.ISA_AMA_projekat.repository.AddressRepository;
import com.example.ISA_AMA_projekat.repository.HotelRepository;

@Service
public class HotelService {

	@Autowired
	private HotelRepository hotelRepository;
	
	@Autowired
	private AddressRepository adresaRepository;
	
	public Hotel findByNaziv(String naziv) {
		return hotelRepository.findOneByNaziv(naziv);
	}

	public List<Hotel> findAll() {
		return hotelRepository.findAll();
	}
	
	public Optional<Hotel> findById(Integer id){
		return hotelRepository.findById(id);
	}
	
	@Transactional
	public Hotel save(Hotel hotel){
		return hotelRepository.save(hotel);
	}
	
	public List<Hotel> search(String name_location) {
		return hotelRepository.search(name_location);
	}
	
	@Transactional
	public void updateAdmin(Integer hotelID, Integer adminID) {
		Hotel hotel = hotelRepository.findById(hotelID).get();
		hotel.setId_admin(adminID);
		hotelRepository.save(hotel);
		//hotelRepository.updateAdmin(hotelID, adminID);
	}
	
	@Transactional
	public void updateHotel(Integer id, String naziv, String opis, Integer adresa_id) {

		Adresa adresa = adresaRepository.findById(adresa_id).get();
		Hotel hotel = hotelRepository.findById(id).get();
		hotel.setNaziv(naziv);
		hotel.setPromotivni_opis(opis);
		hotel.setAdresa(adresa);
		
		hotelRepository.save(hotel);
		//hotelRepository.updateHotel(hotel.getId(), naziv, opis, adresa, hotel.getVerzija());
	}
	
	@Transactional
	public void updateProsecnaHotel(double prosecna, Integer hotel_id){
		
		Hotel hotel = hotelRepository.findById(hotel_id).get();
		hotel.setProsecna_ocena(prosecna);
		hotelRepository.save(hotel);
		//hotelRepository.updateProsecnaHotel(prosecna, hotel_id);
	}
}
