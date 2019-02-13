package com.example.ISA_AMA_projekat.controller;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.ISA_AMA_projekat.model.Filijala;
import com.example.ISA_AMA_projekat.model.RentacarServis;
import com.example.ISA_AMA_projekat.model.Vozilo;
import com.example.ISA_AMA_projekat.service.VoziloService;

@RestController
@RequestMapping(value="api/vozila")
public class VoziloController {

	@Autowired
	private VoziloService voziloService;
	
	@RequestMapping(
			value = "/{id}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Vozilo> getVozilo(@PathVariable("id") Integer id)
	{
		try
		{
			Vozilo found = voziloService.findById(id).get();
			return new ResponseEntity<Vozilo>(found, HttpStatus.OK);
		}
		catch(NoSuchElementException e)
		{
			System.out.println("Ne postoji vozilo sa id: " + id);
			return null; 	
		}
	}
	
	
	
	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(
			value = "/admin/izmenaVozila/{id}/{naziv}/{marka}/{model}/{godina}/{sedista}/{tip}/{cena}",
			method = RequestMethod.POST,
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Vozilo editVozilo(@PathVariable("id") Integer id,
			@PathVariable("naziv") String naziv, @PathVariable("marka") String marka, @PathVariable("model") String model,
			@PathVariable("godina") int godina, @PathVariable("sedista") int sedista, @PathVariable("tip") String tip,
			@PathVariable("cena") double cena)
	{
		System.out.println("[VoziloController]: editVozilo");
		Vozilo auto = voziloService.findById(id).get();
		System.out.println("[VoziloController]: vozilo_id: " + auto.getId());
		
		
		voziloService.updateVozilo(naziv, marka, model, godina, sedista, tip, cena, auto.getId());
		return auto;
	}
	
	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(
			value = "/admin/dodajVozilo/{filijala}/{naziv}/{marka}/{model}/{godina}/{sedista}/{tip}/{cena}",
			method = RequestMethod.POST,
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Vozilo addVozilo(@PathVariable("filijala") Integer filijala,
			@PathVariable("naziv") String naziv, @PathVariable("marka") String marka, @PathVariable("model") String model,
			@PathVariable("godina") int godina, @PathVariable("sedista") int sedista, @PathVariable("tip") String tip,
			@PathVariable("cena") double cena)
	{
		
		Vozilo found = voziloService.postoji(marka, naziv, model, godina, filijala);
		if(found==null)
		{
		System.out.println("[VoziloController]: addVozilo");
		Vozilo auto = new Vozilo();
		auto.setNaziv(naziv);
		auto.setMarka(marka);
		auto.setModel(model);
		auto.setGodina_proizvodnje(godina);
		auto.setBroj_sedista(sedista);
		auto.setTip(tip);
		auto.setCena_dan(cena);
		auto.setProsecna_ocena(0);
		auto.setZauzeto(false);
		Vozilo v = voziloService.save(auto);
		voziloService.updateFil(filijala, v.getId());
		return v;
		}
		else
			return null;
	}
	
	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(
			value = "admin/delete/{car_id}",
			method = RequestMethod.DELETE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Vozilo deleteVozilo(@PathVariable("car_id") Integer car_id)
	{
		try
		{
			Vozilo found = voziloService.findById(car_id).get();
			voziloService.deleteVozilo(found);
			return found;
		}
		catch(NoSuchElementException e)
		{
			System.out.println("Ne postoji vozilo sa id: " + car_id);
			return null; 	
		}
	}
	
}
