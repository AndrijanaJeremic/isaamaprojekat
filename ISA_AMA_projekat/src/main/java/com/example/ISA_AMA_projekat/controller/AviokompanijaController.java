package com.example.ISA_AMA_projekat.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.ISA_AMA_projekat.model.Adresa;
import com.example.ISA_AMA_projekat.model.Aviokompanija;
import com.example.ISA_AMA_projekat.model.Grad;
import com.example.ISA_AMA_projekat.model.Korisnik;
import com.example.ISA_AMA_projekat.security.TokenUtils;
import com.example.ISA_AMA_projekat.service.AddressService;
import com.example.ISA_AMA_projekat.model.Hotel;
import com.example.ISA_AMA_projekat.service.AviokompanijaService;
import com.example.ISA_AMA_projekat.service.GradService;
import com.example.ISA_AMA_projekat.service.KorisnikService;
import com.example.ISA_AMA_projekat.service.LuggageInfoService;

@RestController
@RequestMapping(value="rest/airline")
public class AviokompanijaController 
{
	@Autowired
	private AviokompanijaService avioServis;
	
	@Autowired
	private TokenUtils tokenUtils;
	
	@Autowired
	private KorisnikService userDetailsService;
	
	
	@Autowired
	private AddressService addressService;
	
	
	@Autowired
	private GradService gradService;
	
	@Autowired
	private LuggageInfoService luggageInfoService;
	
	
	@RequestMapping(
			value = "/{id}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Aviokompanija> getAviokompanija(@PathVariable("id") Integer id)
	{
		
		try
		{
			System.out.println("Nasao:" + avioServis.findById(id).get().getId());
			return new ResponseEntity<Aviokompanija>(avioServis.findById(id).get(), HttpStatus.OK);
		}
		catch(NoSuchElementException e)
		{
			System.out.println("ispao");
			return null; 	
		}
	}
	
	@RequestMapping(
			value = "all",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<Aviokompanija>> getAviokompanije(){
		
		Collection<Aviokompanija> los_avios = avioServis.findAll();
		ArrayList<Aviokompanija> sortirani = (ArrayList<Aviokompanija>)los_avios;
		sortirani.sort(Comparator.comparing(Aviokompanija::getNaziv));
		los_avios = sortirani;
		return new ResponseEntity<Collection<Aviokompanija>>(los_avios, HttpStatus.OK);
	}
	
	
	@PreAuthorize("hasRole('SYSADMIN')")
	@RequestMapping(
			value = "all_admin",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<Aviokompanija>> getAviokompanijeForAdmin(){
		
		Collection<Aviokompanija> los_avios = avioServis.findAll();
		Collection<Aviokompanija> result = new ArrayList<Aviokompanija>();
		
		for(Aviokompanija avio: los_avios) {
			if(avio.getId_admin() == null) {
				result.add(avio);
			}
		}
		
		return new ResponseEntity<Collection<Aviokompanija>>(result, HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('SYSADMIN')")
	@RequestMapping(
			value = "/save",
			method = RequestMethod.POST,
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Aviokompanija> saveAviokompanija(@RequestBody Aviokompanija aviokompanija){
		
		Aviokompanija postoji = avioServis.findByNaziv(aviokompanija.getNaziv());
		
		if(postoji != null) {
			System.out.println("Aviokompanija sa ovim nazivom vec postoji u bazi");
			return null;
		}else {
			Aviokompanija saved = avioServis.save(aviokompanija);
			return new ResponseEntity<Aviokompanija>(saved, HttpStatus.CREATED);
		}
		
	}
	
	@PreAuthorize("hasRole('AVIOADMIN')")
	@RequestMapping(
			value = "/admin/{id}/{token}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Aviokompanija> getTheAviokompanija(@PathVariable("id") Integer id, @PathVariable("token") String token)
	{
		
		String email = tokenUtils.getUsernameFromToken(token);
		Korisnik korisnik = (Korisnik) this.userDetailsService.loadUserByUsername(email);
		
		
		if(korisnik.getAdmin_id() != id)
		{
			System.err.println("This admin is not allowed to edit this airline.");
			return null;
		}
		
		System.out.println("usao u metodu api/avio/hotelAdmin/{id}; id: " + id);
		Aviokompanija result = null;
		
		try
		{
			result = avioServis.findById(id).get();
		}
		catch(NoSuchElementException e)
		{
			System.out.println("Ne postoji aviokompanija sa id: " + id);
			return null; 	
		}
		
		return new ResponseEntity<Aviokompanija>(result, HttpStatus.OK);
	}
	
	
	@PreAuthorize("hasRole('AVIOADMIN')")
	@RequestMapping(
			value = "/editAirline/{token}",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String editAirline(@PathVariable("token") String token, @RequestBody Aviokompanija aviokompanija) 
	{
		
		Adresa adresaRequest = aviokompanija.getAdresa();
		adresaRequest.setId(null);//sumnjivo?
		
		String email = tokenUtils.getUsernameFromToken(token);
		Korisnik korisnik = (Korisnik) this.userDetailsService.loadUserByUsername(email);
		
		if(korisnik.getAdmin_id() != aviokompanija.getId())
		{
			System.err.println("This admin is not allowed to edit this airline.");
			return null;
		}
		//trazenje grad - ako postoji sa istim nazivom uzimam taj, ako ne pravim novi
		List<Grad> allGrads = gradService.gimmieAll();
		Grad theOneGrad = null; 
		Grad gradRequest = aviokompanija.getAdresa().getGrad();
		for(Grad g : allGrads)
		{
			if(g.getNaziv().toLowerCase().equals(gradRequest.getNaziv().toLowerCase()))
			{
				theOneGrad = g;
				System.out.println("*******************UZIMAM STARI GRAD: " + g.getId());

			}
		}
		if(theOneGrad == null) //ako nije nadjen sacuvavam novi
		{
			theOneGrad = gradService.save(gradRequest);
		}
		adresaRequest.setGrad(theOneGrad);
		
		
		
		//trazenje adrese - ako postoji adresa sa istim param
		List<Adresa> allAddresses = addressService.gimmieAll();
		Adresa theOne = null;
		for(Adresa a : allAddresses)
		{
			if(a.getBroj().toString().equals(adresaRequest.getBroj()))
				if(((int)a.getLatitude()) == ((int)adresaRequest.getLatitude()))
					if(((int)a.getLongitude()) == ((int)adresaRequest.getLongitude()))
						if(a.getUlica().toLowerCase().equals(adresaRequest.getUlica().toLowerCase()))
							if(a.getGrad().getNaziv().toLowerCase().equals(adresaRequest.getGrad().getNaziv().toLowerCase()))
							{
								theOne = a;
								System.out.println("*******************UZIMAM STARU ADRESU: " + a.getId());
								break;
							}
		}
		if(theOne == null) //ako nije nadjena sa istim param pravim novu adresu
		{
			theOne = addressService.save(adresaRequest);
			
			System.out.println("***********************************ID NOVE ADRESE: " + theOne.getId());
			
		}
		
		aviokompanija.setAdresa(theOne);
		
		avioServis.updateAirline(aviokompanija.getId(), aviokompanija.getNaziv(), aviokompanija.getAdresa(), aviokompanija.getOpis());

		return "success";
		
	}
	
	
	@PreAuthorize("hasRole('AVIOADMIN')")
	@RequestMapping(
			value = "/editLuggageInfo/{token}/{id}",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String editLuggage(@PathVariable("token") String token, @PathVariable("id") Integer aviokompanijaID, @RequestBody LuggageInfo luggageRequest) 
	{
		
		String email = tokenUtils.getUsernameFromToken(token);
		Korisnik korisnik = (Korisnik) this.userDetailsService.loadUserByUsername(email);
		
		if(korisnik.getAdmin_id() != aviokompanijaID)
		{
			System.err.println("This admin is not allowed to edit this airline.");
			return null;
		}
		
		Aviokompanija airline = avioServis.findById(aviokompanijaID).get();
		if(airline.getLuggageInfo().getId() != luggageRequest.getId())
		{
			return null;
		}
		
			
		luggageInfoService.updateInfo(luggageRequest);
		
		
		return "success";
		
		
	}
	
	
	
}
