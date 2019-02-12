package com.example.ISA_AMA_projekat.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.RezervacijaVozila;
import com.example.ISA_AMA_projekat.model.Vozilo;
import com.example.ISA_AMA_projekat.repository.RezervacijaVozilaRepository;
import com.example.ISA_AMA_projekat.repository.VoziloRepository;

@Service
public class RezervacijaVozilaService {
	
	@Autowired
	private RezervacijaVozilaRepository rezervacijaVozilaRepository;
	
	@Autowired
	private VoziloRepository voziloRepository;

	@Transactional
	public RezervacijaVozila save(RezervacijaVozila rezervacijaVozila) {
		return rezervacijaVozilaRepository.save(rezervacijaVozila);
	}
	
	@Transactional
	public void insertRezervacijaVozila(Integer rezervacija_id, Integer vozilo_id) {
		RezervacijaVozila rv = rezervacijaVozilaRepository.findById(rezervacija_id).get();
		Vozilo v =voziloRepository.findById(vozilo_id).get();
		v.getRezervacije().add(rv);
		
		//rezervacijaVozilaRepository.insertRezervacijaVozila(rezervacija_id, vozilo_id);
	}
	
	public List<RezervacijaVozila> getAll()
	{
		return rezervacijaVozilaRepository.findAll();
	}
	
	@Transactional
	public void updateDatumRez(Date datum_rez, Integer id)
	{
		RezervacijaVozila rv = rezervacijaVozilaRepository.findById(id).get();
		rv.setDatum_rezervacije(datum_rez);
		rezervacijaVozilaRepository.save(rv);
		//rezervacijaVozilaRepository.updateDatumRez(datum_rez, id);
	}
	
	public Optional<RezervacijaVozila> findById(Integer id){
		return rezervacijaVozilaRepository.findById(id);
	}
	
	@Transactional
	public void deleteRezV(RezervacijaVozila rv)
	{
		rezervacijaVozilaRepository.delete(rv);
	}
	
	@Transactional
	public void deleteRezV2(Integer id)
	{
		rezervacijaVozilaRepository.deleteRezV(id);
	}
	
	@Transactional
	public void deleteVoziloRez(Integer id)
	{
		rezervacijaVozilaRepository.deleteVoziloRez(id);
	}
	
	@Transactional
	public void updateAktivirana(Integer id, boolean aktivirana)
	{
		RezervacijaVozila rv = rezervacijaVozilaRepository.findById(id).get();
		rv.setAktivirana(aktivirana);
		rezervacijaVozilaRepository.save(rv);
		//rezervacijaVozilaRepository.updateAktivirana(id, aktivirana);
	}
}
