package com.example.ISA_AMA_projekat.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.RezervacijaHotel;
import com.example.ISA_AMA_projekat.model.Soba;
import com.example.ISA_AMA_projekat.model.Usluga;
import com.example.ISA_AMA_projekat.repository.RezervacijaHotelRepository;
import com.example.ISA_AMA_projekat.repository.SobaRepository;
import com.example.ISA_AMA_projekat.repository.UslugaRepository;

@Service
public class RezervacijaHotelService {

	@Autowired
	private RezervacijaHotelRepository rezervacijaHotelRepository;
	
	@Autowired
	private UslugaRepository uslugaRepozitory;
	
	@Autowired
	private SobaRepository sobaRepozitory;
	
	@Transactional
	public RezervacijaHotel save(RezervacijaHotel rezervacijaHotel) {
		return rezervacijaHotelRepository.save(rezervacijaHotel);
	}
	
	@Transactional
	public void updateUsluga(Integer id_usluga, Integer id_rezervacijaHotel) {
		Usluga usluga = uslugaRepozitory.findById(id_usluga).get();
		RezervacijaHotel rezervacijHotel = rezervacijaHotelRepository.findById(id_rezervacijaHotel).get();
		rezervacijHotel.getUsluge().add(usluga);
		
		rezervacijaHotelRepository.save(rezervacijHotel);
		//rezervacijaHotelRepository.updateUsluga(id_usluga, id_rezervacijaHotel);
	}

	@Transactional
	public void removeUsluga(Integer id_usluga, Integer id_rezervacijaHotel) {
		Usluga usluga = uslugaRepozitory.findById(id_usluga).get();
		RezervacijaHotel rezervacijaHotel = rezervacijaHotelRepository.findById(id_rezervacijaHotel).get();
		rezervacijaHotel.getUsluge().remove(usluga);
		
		rezervacijaHotelRepository.save(rezervacijaHotel);
		//rezervacijaHotelRepository.removeUsluga(id_usluga, id_rezervacijaHotel);
	}

	@Transactional
	public void updateSoba(Integer id_soba, boolean aktivirana, double cena_rez, Integer id_rezervacijaHotel) {
		Soba soba = sobaRepozitory.findById(id_soba).get();
		RezervacijaHotel rezervacijaHotel = rezervacijaHotelRepository.findById(id_rezervacijaHotel).get();
		rezervacijaHotel.setAktivirana(aktivirana);
		rezervacijaHotel.setUkupna_cena(cena_rez);
		rezervacijaHotel.setSoba(soba);
		
		rezervacijaHotelRepository.save(rezervacijaHotel);
		//rezervacijaHotelRepository.updateSoba(id_soba, aktivirana, cena_rez, id_rezervacijaHotel);
	}

	public Optional<RezervacijaHotel> findById(Integer id) {
		return rezervacijaHotelRepository.findById(id);
	}
	
	public Collection<RezervacijaHotel> findBrzeRez(Integer id, boolean brza) {
		return rezervacijaHotelRepository.findBrzeRez(id, brza);
	}

	@Transactional
	public void updateAktivirana(Integer id, boolean aktivirana) {
		RezervacijaHotel rezervacijaHotel = rezervacijaHotelRepository.findById(id).get();
		rezervacijaHotel.setAktivirana(aktivirana);
		
		rezervacijaHotelRepository.save(rezervacijaHotel);
		//rezervacijaHotelRepository.updateAktivirana(id, aktivirana);
	}

	@Transactional
	public void insertRezervacijaSoba(Integer rezervacija_id, Integer soba_id) {
		Soba soba = sobaRepozitory.findById(soba_id).get();
		RezervacijaHotel rezervacijaHotel = rezervacijaHotelRepository.findById(rezervacija_id).get();
		soba.getRezervacije().add(rezervacijaHotel);
		
		//sobaRepozitory.save(soba);
		//rezervacijaHotelRepository.insertRezervacijaSoba(rezervacija_id, soba_id);
	}
	
	public List<RezervacijaHotel> getAll(){
		return rezervacijaHotelRepository.findAll();
	}

	@Transactional
	public void updateDatumRez(Date datum_rez, Integer id){
		RezervacijaHotel rezervacijaHotel = rezervacijaHotelRepository.findById(id).get();
		rezervacijaHotel.setDatum_rezervacije(datum_rez);
		
		rezervacijaHotelRepository.save(rezervacijaHotel);
		//rezervacijaHotelRepository.updateDatumRez(datum_rez, id);
	}
	

	@Transactional
	public void deleteRezH(RezervacijaHotel rh){
		rezervacijaHotelRepository.delete(rh);
	}

	@Transactional
	public void deleteRezH2(Integer id){
		rezervacijaHotelRepository.deleteRezH(id);
	}
	

	@Transactional
	public void deleteSobaRez(Integer id){
		rezervacijaHotelRepository.deleteSobaRez(id);
	}

	@Transactional
	public void deleteRezHotelUsluge(Integer id){
		rezervacijaHotelRepository.deleteRezHotelUsluge(id);
	}
}
