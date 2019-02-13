package com.example.ISA_AMA_projekat.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.OsobaIzRez;
import com.example.ISA_AMA_projekat.model.Rezervacija;
import com.example.ISA_AMA_projekat.model.RezervacijaHotel;
import com.example.ISA_AMA_projekat.model.RezervacijaVozila;
import com.example.ISA_AMA_projekat.repository.OsobaIzRezRepository;
import com.example.ISA_AMA_projekat.repository.RezervacijaHotelRepository;
import com.example.ISA_AMA_projekat.repository.RezervacijaRepository;
import com.example.ISA_AMA_projekat.repository.RezervacijaVozilaRepository;

@Service
public class RezervacijaService 
{
	@Autowired
	private RezervacijaRepository rezervacijaRepository;
	
	@Autowired
	private RezervacijaHotelRepository rezervacijaHotelRepository;
	
	@Autowired
	private RezervacijaVozilaRepository rezervacijaVozilaRepository;
	
	@Autowired
	private OsobaIzRezRepository osobaIzRezRepository;
	
	@Transactional
	public Rezervacija save(Rezervacija rez)
	{
		return rezervacijaRepository.save(rez);
	}
	
	public Optional<Rezervacija> findById(Integer id)
	{
		Optional<Rezervacija> rez = rezervacijaRepository.findById(id);
		
		try
		{
			Rezervacija r = rez.get();
			System.out.println("ID rez: " + r.getId());
		}
		catch(Exception e)
		{
			System.err.println("* * * * * * * * Erorcina * * * * * * * ");
		}
		
		return rez;
	}
	
	@Transactional
	public void updateRezVozila(Integer id_rezVozila, Integer id_rez)
	{
		RezervacijaVozila rezervacijaVozila = rezervacijaVozilaRepository.findById(id_rezVozila).get();
		Rezervacija rezervacija = rezervacijaRepository.findById(id_rez).get();
		rezervacija.setRezervacijaVozila(rezervacijaVozila);
		
		rezervacijaRepository.save(rezervacija);
		//rezervacijaRepository.updateRezVozila(id_rezVozila, id_rez);
	}
	
	@Transactional
	public void updateRezHotel(Integer id_rezHotel, Integer id_rez){
		RezervacijaHotel rezervacijaHotel = rezervacijaHotelRepository.findById(id_rezHotel).get();
		Rezervacija rezervacija = rezervacijaRepository.findById(id_rez).get();
		rezervacija.setRezevacijaHotel(rezervacijaHotel);
		
		rezervacijaRepository.save(rezervacija);
		//rezervacijaRepository.updateRezHotel(id_rezHotel, id_rez);
	}
	
	@Transactional
	public void updateCenaRez(double cena, Integer id_rez){
		Rezervacija rezervacija = rezervacijaRepository.findById(id_rez).get();
		rezervacija.setCena(cena);
		
		rezervacijaRepository.save(rezervacija);
		//rezervacijaRepository.updateCenaRez(cena, id_rez);
	}
	
	public void deleteRez(Rezervacija rez)
	{
		rezervacijaRepository.delete(rez);
	}

	@Transactional
	public void updateVoziloId(Integer id_rez)
	{
		Rezervacija rezervacija = rezervacijaRepository.findById(id_rez).get();
		rezervacija.setRezervacijaVozila(null);
		
		rezervacijaRepository.save(rezervacija);
		//rezervacijaRepository.updateVoziloId(id_rez);
	}
	

	@Transactional
	public void updateHotelId(Integer id_rez){
		Rezervacija rezervacija = rezervacijaRepository.findById(id_rez).get();
		rezervacija.setRezevacijaHotel(null);
		
		rezervacijaRepository.save(rezervacija);
		//rezervacijaRepository.updateHotelId(id_rez);
	}

	@Transactional
	public void zavrsiRez(Integer id_rez){
		Rezervacija rezervacija = rezervacijaRepository.findById(id_rez).get();
		rezervacija.setZavrsena(true);
		
		rezervacijaRepository.save(rezervacija);
		//rezervacijaRepository.zavrsiRez(id_rez);
	}
	

	@Transactional
	public void obrisiOsobu(Integer osoba_id, Integer rez_id)
	{
	
		rezervacijaRepository.obrisiOsobu(osoba_id, rez_id);
	}

	@Transactional
	public void potvrdiRez(Integer osoba_id, Integer rez_id)
	{
		Rezervacija r = rezervacijaRepository.findById(rez_id).get();
		OsobaIzRez o = osobaIzRezRepository.findById(osoba_id).get();
		o.setPotvrdjeno(true);
		osobaIzRezRepository.save(o);
		//rezervacijaRepository.potvrdiRez(osoba_id, rez_id);
	}

	@Transactional
	public void updateCena(double cena, Integer id)
	{
		Rezervacija rezervacija = rezervacijaRepository.findById(id).get();
		rezervacija.setCena(cena);
		
		rezervacijaRepository.save(rezervacija);
		//rezervacijaRepository.updateCena(cena, id);
	}

	@Transactional
	public void obrisiSveOsobe(Integer rez_id)
	{
		
		rezervacijaRepository.obrisiSveOsobe(rez_id);
	}
	
	
	
	
}
