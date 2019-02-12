package com.example.ISA_AMA_projekat.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.RezervacijaHotel;
import com.example.ISA_AMA_projekat.model.Soba;
import com.example.ISA_AMA_projekat.repository.RezervacijaHotelRepository;
import com.example.ISA_AMA_projekat.repository.SobaRepository;

@Service
public class SobaService {

	@Autowired
	SobaRepository sobaRepository;

	@Autowired
	private RezervacijaHotelRepository rezervacijaHotelRepository;
	
	public Optional<Soba> findById(Integer id){
		return sobaRepository.findById(id);
	}
	
	public Collection<Soba> findAll(){
		return sobaRepository.findAll();
	}

	@Transactional
	public Soba save(Soba soba) {
		return sobaRepository.save(soba);
	}

	@Transactional
	public void deleteRoom(Soba soba) {
		for(Iterator<RezervacijaHotel> iteratorRezervacija = soba.getRezervacije().iterator(); iteratorRezervacija.hasNext();) {
			RezervacijaHotel rezervacija = iteratorRezervacija.next();
			iteratorRezervacija.remove();
			rezervacijaHotelRepository.delete(rezervacija);
		}
		sobaRepository.delete(soba);
	}

	@Transactional
	public void updateProsecnaSoba(double prosecna_ocena,Integer soba_id){
		Soba soba = sobaRepository.findById(soba_id).get();
		soba.setProsecna_ocena(prosecna_ocena);
		
		sobaRepository.save(soba);
		//sobaRepository.updateProsecnaSoba(prosecna_ocena, soba_id);
	}
}
