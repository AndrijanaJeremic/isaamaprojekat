package com.example.ISA_AMA_projekat.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.Hotel;
import com.example.ISA_AMA_projekat.model.Usluga;
import com.example.ISA_AMA_projekat.repository.HotelRepository;
import com.example.ISA_AMA_projekat.repository.UslugaRepository;

@Service
public class UslugaService {

	@Autowired
	private UslugaRepository uslugaRepository;
	
	@Autowired
	private HotelRepository hotelRepository;
	
	public List<Usluga> findAll(){
		return uslugaRepository.findAll();
	}
	
	public Optional<Usluga> findById(Integer id) {
		return uslugaRepository.findById(id);
	}

	@Transactional
	public void updateCena(double cena, Integer id){
		Usluga usluga = uslugaRepository.findById(id).get();
		usluga.setCena(cena);
		
		uslugaRepository.save(usluga);
		//uslugaRepository.updateUsluga(cena, id);
	}

	@Transactional
	public void updatePopust(double popust, Integer id){
		Usluga usluga = uslugaRepository.findById(id).get();
		usluga.setPopust(popust);
		
		uslugaRepository.save(usluga);
		//uslugaRepository.updatePopust(popust, id);
	}
	
	@Transactional
	public Usluga save(Usluga usluga, Integer id_hotel){
		Usluga u = uslugaRepository.save(usluga);
		Hotel hotel = hotelRepository.findById(id_hotel).get();
		hotel.getUsluge().add(usluga);
		
		hotelRepository.save(hotel);
		//uslugaRepository.addToHotel(id_hotel, u.getId());
		return u;
	}
}
