package com.example.ISA_AMA_projekat.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.Grad;
import com.example.ISA_AMA_projekat.repository.GradRepository;

@Service
public class GradService {
	
	@Autowired
	GradRepository gradRepository;
	

	public List<Grad> gimmieAll()
	{
		return gradRepository.findAll();
	}
	
	public Grad findByNaziv(String naziv)
	{

		return gradRepository.findOneByNaziv(naziv);
	}
	
	public Grad findById(Integer id){
		return gradRepository.findOneById(id);
	}
	
	@Transactional
	public Grad save(Grad grad){
		return gradRepository.save(grad);
	}

}
