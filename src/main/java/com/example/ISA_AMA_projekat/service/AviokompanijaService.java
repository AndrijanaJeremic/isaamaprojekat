package com.example.ISA_AMA_projekat.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ISA_AMA_projekat.model.Adresa;
import com.example.ISA_AMA_projekat.model.Aviokompanija;
import com.example.ISA_AMA_projekat.repository.AviokompanijaRepository;

@Service
public class AviokompanijaService 
{
	@Autowired
	private AviokompanijaRepository avioRepo;
	
	public Optional<Aviokompanija> findById(Integer id)
	{
		return avioRepo.findById(id);
	}
	
	public Aviokompanija findByNaziv(String naziv)
	{
		return avioRepo.findOneByNaziv(naziv);
	}
	
	public Collection<Aviokompanija> findAll() {
		return avioRepo.findAll();
	}
	
	public Aviokompanija save(Aviokompanija aviokompanija) {
		return avioRepo.save(aviokompanija);
	}
	
	public void updateAdmin(Integer avioID, Integer adminID) {
		avioRepo.updateAdmin(avioID, adminID);
	}
	
	public void updateProsecnaAvio(double prosecna, Integer avio_id)
	{
		avioRepo.updateProsecnaAvio(prosecna, avio_id);
	}
	
	public void updateAirline(Integer id, String naziv, Adresa adresa, String opis)
	{
		avioRepo.updateAirline(id, naziv, adresa.getId(), opis);
	}
	
}
