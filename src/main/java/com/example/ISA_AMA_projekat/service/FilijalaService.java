package com.example.ISA_AMA_projekat.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.Adresa;
import com.example.ISA_AMA_projekat.model.Filijala;
import com.example.ISA_AMA_projekat.model.RentacarServis;
import com.example.ISA_AMA_projekat.repository.AddressRepository;
import com.example.ISA_AMA_projekat.repository.FilijalaRepository;
import com.example.ISA_AMA_projekat.repository.RentacarRepository;

@Service
public class FilijalaService {

	@Autowired
	private FilijalaRepository filijalaRepository;
	
	@Autowired
	private AddressRepository adresaRepository;
	
	@Autowired
	private RentacarRepository rentRepository;
	
	public List<Filijala> findAll()
	{
		return filijalaRepository.findAll();
	}
	
	public Optional<Filijala> findById(Integer id)
	{
		return filijalaRepository.findById(id);
	}
	
	@Transactional
	public void updateFilijala(Integer adresa_id, Integer id)
	{
		Filijala fil = filijalaRepository.findById(id).get();
		Adresa adresa = adresaRepository.findById(adresa_id).get();
		fil.setAdresa(adresa);
		filijalaRepository.save(fil);
		//filijalaRepository.updateFilijala(adresa_id, id);
	}
	
	@Transactional
	public Filijala save(Filijala fil){
		return filijalaRepository.save(fil);
	}
	
	@Transactional
	public void updateRent(Integer idr, Integer id)
	{
		
		Filijala fil = filijalaRepository.findById(id).get();
		RentacarServis rs = rentRepository.findById(idr).get();
		fil.setRentacar(rs);
		filijalaRepository.save(fil);
		//filijalaRepository.updateRentacarFil(idr, id);
	}
	
	@Transactional
	public void deleteFilijala(Filijala filijala)
	{
		filijalaRepository.delete(filijala);
	}
}
