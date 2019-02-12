package com.example.ISA_AMA_projekat.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.Adresa;
import com.example.ISA_AMA_projekat.model.RentacarServis;
import com.example.ISA_AMA_projekat.repository.AddressRepository;
import com.example.ISA_AMA_projekat.repository.RentacarRepository;

@Service
public class RentacarService {
	
	@Autowired
	RentacarRepository rentRepository;
	
	@Autowired
	AddressRepository adresaRepository;
	
	public RentacarServis findByNaziv(String naziv) {
		return rentRepository.findOneByNaziv(naziv);
	}

	public List<RentacarServis> findAll() {
		return rentRepository.findAll();
	}
	
	public Optional<RentacarServis> findById(Integer id){
		return rentRepository.findById(id);
	}
	
	@Transactional
	public RentacarServis save(RentacarServis rent){
		return rentRepository.save(rent);
	}
	
	public List<RentacarServis> search(String name_location) {
		return rentRepository.search(name_location);
	}
	
	@Transactional
	public void updateAdmin(Integer rentalID, Integer adminID) {
		RentacarServis rent = rentRepository.findById(rentalID).get();
		rent.setId_admin(adminID);
		rentRepository.save(rent);
		//rentRepository.updateAdmin(rentalID, adminID);
	}

	@Transactional
	public void updateServis(String naziv, Integer adresa_id, String promotivni_opis, Integer id){
		RentacarServis rent = rentRepository.findById(id).get();
		rent.setNaziv(naziv);
		rent.setPromotivni_opis(promotivni_opis);
		Adresa adr = adresaRepository.findById(adresa_id).get();
		rent.setAdresa(adr);
		rentRepository.save(rent);
		//rentRepository.updateServis(naziv, adresa_id, promotivni_opis, id);
	}
	
	@Transactional
	public void updateProsecnaRent(double prosecna, Integer rent_id)
	{
		RentacarServis rent = rentRepository.findById(rent_id).get();
		rent.setProsecna_ocena(prosecna);
		rentRepository.save(rent);
		//rentRepository.updateProsecnaRent(prosecna, rent_id);
	}

}
