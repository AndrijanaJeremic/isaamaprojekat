package com.example.ISA_AMA_projekat.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.Adresa;
import com.example.ISA_AMA_projekat.model.Grad;
import com.example.ISA_AMA_projekat.repository.AddressRepository;
import com.example.ISA_AMA_projekat.repository.GradRepository;

@Service
public class AddressService {
	
	@Autowired
	private AddressRepository addressRepozitory;
	
	@Autowired
	private GradRepository gradRepozitory;

	public List<Adresa> gimmieAll() {
		return addressRepozitory.findAll();
	}
	
	public Optional<Adresa> findById(Integer id){
		return addressRepozitory.findById(id);
	}
	
	public List<Adresa> checkAddress(Integer city_id, String street, String number) {
		return addressRepozitory.checkAddress(city_id, street, number);
	}
	
	@Transactional
	public Adresa save(Adresa adresa) {
		return addressRepozitory.save(adresa);
	}
	
	@Transactional
	public void updateAdresa(String ulica, String broj, Integer grad_id, Integer id){
		Grad grad = gradRepozitory.findById(grad_id).get();
		Adresa adresa = addressRepozitory.findById(id).get();
		adresa.setBroj(broj);
		adresa.setUlica(ulica);
		adresa.setGrad(grad);
		
		addressRepozitory.save(adresa);
		//addressRepozitory.updateAdresa(ulica, broj, grad_id, id);
	}
}
