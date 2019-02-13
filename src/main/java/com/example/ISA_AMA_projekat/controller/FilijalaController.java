package com.example.ISA_AMA_projekat.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.ISA_AMA_projekat.model.Adresa;
import com.example.ISA_AMA_projekat.model.Filijala;
import com.example.ISA_AMA_projekat.model.Grad;
import com.example.ISA_AMA_projekat.model.RezervacijaVozila;
import com.example.ISA_AMA_projekat.model.Vozilo;
import com.example.ISA_AMA_projekat.service.AddressService;
import com.example.ISA_AMA_projekat.service.FilijalaService;
import com.example.ISA_AMA_projekat.service.GradService;
import com.example.ISA_AMA_projekat.service.RezervacijaVozilaService;

@RestController
@RequestMapping(value="api/filijale")
public class FilijalaController {
	
	@Autowired
	private FilijalaService filijalaService;
	
	@Autowired
	private GradService gradService; 
	
	@Autowired
	private AddressService addressService; 
	
	@Autowired
	private RezervacijaVozilaService rezervacijaVozilaService;
	
	
	@RequestMapping(
			value = "/{id}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Filijala> getFilijala(@PathVariable("id") Integer id)
	{
		try
		{
			Filijala found = filijalaService.findById(id).get();
			return new ResponseEntity<Filijala>(found, HttpStatus.OK);
		}
		catch(NoSuchElementException e)
		{
			System.out.println("Ne postoji filijala sa id: " + id);
			return null; 	
		}
	}
	
	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(
			value = "/admin/izmenaFil/{id}/{ulica}/{broj}/{grad}/{latitude}/{longitude}",
			method = RequestMethod.POST,
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Filijala editFilijala(@PathVariable("id") Integer id,
			@PathVariable("ulica") String ulica, @PathVariable("broj") String broj, @PathVariable("grad") String grad_str,
			@PathVariable("latitude") double latitude, @PathVariable("longitude") double longitude)
	{
		System.out.println("[FilijalaController]: editFilijala");
		Filijala fil = filijalaService.findById(id).get();
		System.out.println("[FilijalaController]: filijala_id: " + fil.getId());
		Grad grad = null;
		grad = gradService.findByNaziv(grad_str);
		if(grad==null)
		{
			grad=new Grad();
			grad.setNaziv(grad_str);
			gradService.save(grad);
		}
		System.out.println("[FilijalaController]: grad: " + grad.getNaziv());
		Adresa adr = null;
		List<Adresa> adrese = addressService.checkAddress(grad.getId(), ulica, broj);
		
		if(adrese.isEmpty())
		{
			adr = new Adresa();
			adr.setGrad(grad);
			adr.setUlica(ulica);
			adr.setBroj(broj);
			adr.setLatitude(latitude);
			adr.setLongitude(longitude);
			addressService.save(adr);
		}
		else
		{
			adr = adrese.get(0);
			addressService.updateAdresa(ulica, broj, grad.getId(), adr.getId());
		}
		System.out.println("[FilijalaController]: adr: " + adr.getId() + " " + adr.getUlica() + " " + adr.getBroj());
		filijalaService.updateFilijala(adr.getId(), fil.getId());
		return fil;
	}
	
	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(
			value = "/admin/dodajFil/{ulica}/{broj}/{grad}/{idr}/{latitude}/{longitude}",
			method = RequestMethod.POST,
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Filijala addFilijala(@PathVariable("ulica") String ulica, @PathVariable("broj") String broj, @PathVariable("grad") String grad_str, @PathVariable("idr") Integer idr,
			@PathVariable("latitude") double latitude, @PathVariable("longitude") double longitude)
	{
		System.out.println("[FilijalaController]:addFilijala");
		
		Grad grad = null;
		grad = gradService.findByNaziv(grad_str);
		if(grad==null)
		{
			grad=new Grad();
			grad.setNaziv(grad_str);
			gradService.save(grad);
		}
		System.out.println("[FilijalaController]: grad: " + grad.getNaziv());
		Adresa adr = null;
		List<Adresa> adrese = addressService.checkAddress(grad.getId(), ulica, broj);
		
		if(adrese.isEmpty())
		{
			adr = new Adresa();
			adr.setGrad(grad);
			adr.setUlica(ulica);
			adr.setBroj(broj);
			adr.setLatitude(latitude);
			adr.setLongitude(longitude);
			addressService.save(adr);
		}
		else
		{
			return null;
		}
		System.out.println("[FilijalaController]: adr: " + adr.getId() + " " + adr.getUlica() + " " + adr.getBroj());
		Filijala fil = new Filijala();
		fil.setAdresa(adr);
		Filijala f =filijalaService.save(fil);
		filijalaService.updateRent(idr, f.getId());
		return f;
	}
	
	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(
			value = "admin/delete/{filijala_id}",
			method = RequestMethod.DELETE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Filijala deleteFilijala(@PathVariable("filijala_id") Integer	filijala_id)
	{
		try
		{
			Filijala found = filijalaService.findById(filijala_id).get();
			filijalaService.deleteFilijala(found);
			return found;
		}
		catch(NoSuchElementException e)
		{
			System.out.println("Ne postoji fil sa id: " + filijala_id);
			return null; 	
		}
	}
	



}
