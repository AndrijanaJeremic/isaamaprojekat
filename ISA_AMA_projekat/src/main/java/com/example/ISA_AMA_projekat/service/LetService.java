package com.example.ISA_AMA_projekat.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.Let;
import com.example.ISA_AMA_projekat.repository.LetRepository;

@Service
public class LetService 
{
	@Autowired
	LetRepository letRepository;
	
	public Optional<Let> findById(Integer id)
	{
		return letRepository.findById(id);
	}
	
	public Let save(Let let)
	{
		return letRepository.save(let);
	}
	
	@Transactional
	public void deleteZauzetoSediste(Integer let_id, int sediste)
	{
		
		letRepository.deleteZauzetoSediste(let_id, sediste);
	}
	
	@Transactional
	public void updateProsecna(double prosecna_ocena, Integer let_id)
	{
		Let l = letRepository.findById(let_id).get();
		l.setProsecna_ocena(prosecna_ocena);
		letRepository.save(l);
		//letRepository.updateProsecna(prosecna_ocena, let_id);
	}
	
	public void updateFlight(Let let)
	{
		letRepository.updateFlight(let.getId(), let.getOdakle(), let.getDokle(), let.getVremePoletanja(), let.getVremeSletanja(), let.getTrajanje(), let.getUdaljenost(), let.getCena(), let.getMaxKapacitet(), let.getPopust());
	}
	
	
	public void deleteFlight(Let let)
	{
		letRepository.deleteById(let.getId());
	}
	
	
	public void deleteFlightAction(Let let)
	{
		letRepository.deleteFromAction(let.getId());
	}
	
	public void addToActions(Integer airlineID, Integer flightID)
	{
		letRepository.addToAcion(airlineID, flightID);
	}
	
	public List<Let> findAll()
	{
		return letRepository.findAll();
	}
	
}
