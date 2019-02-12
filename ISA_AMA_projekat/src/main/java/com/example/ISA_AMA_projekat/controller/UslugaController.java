package com.example.ISA_AMA_projekat.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.ISA_AMA_projekat.model.Usluga;
import com.example.ISA_AMA_projekat.service.UslugaService;

@RestController
@RequestMapping(value="api/usluge")
public class UslugaController {
	
	@Autowired
	private UslugaService uslugaService;

	@PreAuthorize("hasRole('RENTADMIN') or hasRole('HOTELADMIN')")
	@RequestMapping(
			value = "/admin/izmenaUsluge/{izabrano}/{cena}",
			method = RequestMethod.POST,
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Usluga editUsluga(@PathVariable("izabrano") Integer izabrano,
			@PathVariable("cena") double cena)
	{
	
		Usluga u = uslugaService.findById(izabrano).get();
		System.out.println("[UslugaController]: usluga_id: " + u.getId());
		
		uslugaService.updateCena(cena, u.getId());
		return u;
	}
	
	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(
			value = "/admin/izmenaPopustaUsluge/{izabrano}/{popust}",
			method = RequestMethod.POST,
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Usluga editPopustUsluga(@PathVariable("izabrano") Integer izabrano,
			@PathVariable("popust") double popust)
	{
	
		Usluga u = uslugaService.findById(izabrano).get();
		System.out.println("[UslugaController]: usluga_id: " + u.getId());
		
		uslugaService.updatePopust(popust, u.getId());
		return u;
	}
	
	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(
			value = "/dodaj_uslugu/{naziv}/{cena}/{popust}/{id_hotel}",
			method = RequestMethod.POST,
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Usluga dodajUsluga(@PathVariable("naziv") String naziv, @PathVariable("cena") double cena, @PathVariable("popust") double popust,  @PathVariable("id_hotel") Integer id_hotel){
	
		Usluga u = new Usluga();
		u.setNaziv(naziv);
		u.setCena(cena);
		u.setPopust(popust);
		
		u = uslugaService.save(u, id_hotel);
		
		System.out.println("[UslugaController]: usluga_id: " + u.getId());
		return u;
	}
	
	@RequestMapping(
			value = "/get/{id}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUsluga(@PathVariable("id") Integer id){
	
		Usluga u = uslugaService.findById(id).get();
		System.out.println("[UslugaController]: usluga_id: " + u.getId());
		
		Map<String, Double> result = new HashMap<>();
		
		result.put("cena", u.getCena());
		result.put("popust", u.getPopust());
		
		return ResponseEntity.accepted().body(result);
	}
	
}
