package com.example.ISA_AMA_projekat.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.Filijala;
import com.example.ISA_AMA_projekat.model.Vozilo;
import com.example.ISA_AMA_projekat.repository.FilijalaRepository;
import com.example.ISA_AMA_projekat.repository.VoziloRepository;

@Service
public class VoziloService {
	
	@Autowired
	VoziloRepository voziloRepository;
	
	@Autowired
	FilijalaRepository filRepository;
	
	public Optional<Vozilo> findById(Integer id){
		return voziloRepository.findById(id);
	}
	
	public List<Vozilo> findAll() {
		return voziloRepository.findAll();
	}
	
	@Transactional
	public void updateVozilo(String naziv, String marka, String model, int godina_proizvodnje, int broj_sedista, String tip, double cena_dan,  Integer id)
	{
		Vozilo v =voziloRepository.findById(id).get();
		v.setNaziv(naziv);
		v.setMarka(marka);
		v.setModel(model);
		v.setBroj_sedista(broj_sedista);
		v.setGodina_proizvodnje(godina_proizvodnje);
		v.setTip(tip);
		v.setCena_dan(cena_dan);
		voziloRepository.save(v);
		// voziloRepository.updateVozilo(naziv, marka, model, godina_proizvodnje, broj_sedista, tip, cena_dan, id);
	}
	
	@Transactional
	public Vozilo save(Vozilo vozilo){
		return voziloRepository.save(vozilo);
	}
	
	@Transactional
	public void updateFil(Integer filijala, Integer id)
	{
		Vozilo v =voziloRepository.findById(id).get();
		Filijala f =filRepository.findById(filijala).get();
		v.setFilijala(f);
		voziloRepository.save(v);
		//voziloRepository.updateFilVozilo(filijala, id);
	}
	
	@Transactional
	public void deleteVozilo(Vozilo vozilo)
	{
		voziloRepository.delete(vozilo);
	}
	
	@Transactional
	public void updateProsecnaVozilo(double prosecna_ocena, Integer vozilo_id)
	{
		Vozilo v = voziloRepository.findById(vozilo_id).get();
		v.setProsecna_ocena(prosecna_ocena);
		voziloRepository.save(v);
		//voziloRepository.updateProsecnaVozilo(prosecna_ocena, vozilo_id);
	}
	
	public Vozilo postoji(String marka, String naziv, String model, int godina_proizvodnje, Integer filijala)
	{
		return voziloRepository.postoji(marka, naziv, model, godina_proizvodnje, filijala);
	}

}
