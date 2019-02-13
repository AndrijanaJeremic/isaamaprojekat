package com.example.ISA_AMA_projekat.controller;

import java.util.List;

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
import com.example.ISA_AMA_projekat.model.Grad;
import com.example.ISA_AMA_projekat.service.AddressService;
import com.example.ISA_AMA_projekat.service.GradService;

@RestController
@RequestMapping(value="api/address")
public class AddressController {
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private GradService gradService;
	
	@PreAuthorize("hasRole('SYSADMIN')")
	@RequestMapping(
			value = "/checkCity/{city}/{street}/{number}/{longitude}/{latitude}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Adresa> checkCity(@PathVariable("city") String city, @PathVariable("street") String street, @PathVariable("number") String number, @PathVariable("longitude") double longitude, @PathVariable("latitude") double latitude){
		
		Grad postoji = gradService.findByNaziv(city);
		
		if(postoji != null) {
			
			System.out.println("Grad sa ovim nazivom vec postoji u bazi");
			//return new ResponseEntity<Grad>(postoji, HttpStatus.OK);
		}else {
			
			postoji = new Grad(city);
			postoji = gradService.save(postoji);

			System.out.println("Grad sacuvan u bazi sa id: " + postoji.getId());
			//return new ResponseEntity<Grad>(result, HttpStatus.CREATED);
		}
		
		List<Adresa> adrese = addressService.checkAddress(postoji.getId(), street, number);
		
		Adresa novaAdresa = null;
		
		if(adrese.isEmpty()) {
			Adresa newAddress = new Adresa();
			newAddress.setGrad(postoji);
			newAddress.setUlica(street);
			newAddress.setBroj(number);
			newAddress.setLongitude(longitude);
			newAddress.setLatitude(latitude);
			
			novaAdresa = addressService.save(newAddress);
		}else {
			novaAdresa = adrese.get(0);
		}
		System.out.println("[AddessController: checkAddress] adresa.id: " + novaAdresa.getId());
		
		return new ResponseEntity<Adresa>(novaAdresa, HttpStatus.OK);
	}
	
	/*
	@RequestMapping(
			value = "/checkAddress/{street}/{number}/{longitude}/{latitude}/{city_id}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Adresa> checkAddress(@PathVariable("street") String street, @PathVariable("number") String number, @PathVariable("longitude") double longitude, @PathVariable("latitude") double latitude, @PathVariable("city_id") Integer city_id){
		
		System.out.println("[AddessController: checkAddress] grad.id: " + city_id);
		List<Adresa> adrese = addressService.checkAddress(city_id, street, number);
		
		Grad grad = gradService.findById(city_id);
		System.out.println("[AddessController: checkAddress] grad.naziv: " + grad.getNaziv());
		
		Adresa adresa = null;
		
		if(adrese.isEmpty()) {
			Adresa newAddress = new Adresa();
			newAddress.setGrad(grad);
			newAddress.setUlica(street);
			newAddress.setBroj(number);
			newAddress.setLongitude(longitude);
			newAddress.setLatitude(latitude);
			
			adresa = addressService.save(newAddress);
		}else {
			adresa = adrese.get(0);
		}
		System.out.println("[AddessController: checkAddress] adresa.id: " + adresa.getId());
		return new ResponseEntity<Adresa>(adresa, HttpStatus.OK);
	}
	*/
	
	@RequestMapping(
			value = "/check/{city}/{street}/{number}/{latitude}/{longitude}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Adresa> checkWholeAddress(@PathVariable("city") String city, @PathVariable("street") String street, @PathVariable("number") String number, @PathVariable("longitude") double longitude, @PathVariable("latitude") double latitude){
		
		System.out.println("[AddessController: checkWholeAddress] grad: " + city + "; street: " + street + "; number: " + number +
							"; latitude: " + latitude + "; longitude: " + longitude);
		
		Grad grad = gradService.findByNaziv(city);
		if(grad == null) {
			grad = new Grad(city);
			grad = gradService.save(grad);
		}
		
		List<Adresa> adrese = addressService.checkAddress(grad.getId(), street,number);
		
		Adresa adresa = null;
		
		if(adrese.isEmpty()) {
			Adresa newAddress = new Adresa();
			newAddress.setGrad(grad);
			newAddress.setUlica(street);
			newAddress.setBroj(number);
			newAddress.setLongitude(longitude);
			newAddress.setLatitude(latitude);
			
			adresa = addressService.save(newAddress);
		}else {
			adresa = adrese.get(0);
		}
		
		System.out.println("[AddessController: checkAddress] adresa.id: " + adresa.getId());
		return new ResponseEntity<Adresa>(adresa, HttpStatus.OK);
	}
	
	static class CheckAddress {
		public String City;
		public String Street;
		public String Number;
		public String Longitude;
		public String Latitude;
	}

}
