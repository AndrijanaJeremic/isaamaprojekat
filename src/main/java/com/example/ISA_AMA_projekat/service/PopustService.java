package com.example.ISA_AMA_projekat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.Popust;
import com.example.ISA_AMA_projekat.model.Soba;
import com.example.ISA_AMA_projekat.model.Usluga;
import com.example.ISA_AMA_projekat.model.Vozilo;
import com.example.ISA_AMA_projekat.repository.PopustRepository;
import com.example.ISA_AMA_projekat.repository.SobaRepository;
import com.example.ISA_AMA_projekat.repository.UslugaRepository;
import com.example.ISA_AMA_projekat.repository.VoziloRepository;

@Service
public class PopustService {

	@Autowired
	private PopustRepository popustRepozitory;
	
	@Autowired
	private UslugaRepository uslugaRepozitory;
	
	@Autowired
	private VoziloRepository voziloRepozitory;
	
	@Autowired
	private SobaRepository sobaRepozitory;
	
	@Transactional
	public Popust save(Popust popust) {
		return popustRepozitory.save(popust);
	}

	@Transactional
	public void updateSoba(Integer soba_id, Integer popust_id) {
		Soba soba = sobaRepozitory.findById(soba_id).get();
		Popust popust = popustRepozitory.findById(popust_id).get();
		soba.getPopusti().add(popust);
		
		sobaRepozitory.save(soba);
		//popustRepozitory.updateSoba(soba_id, popust_id);
	}

	@Transactional
	public void updateVozilo(Integer vozilo_id, Integer popust_id) {
		Vozilo vozilo = voziloRepozitory.findById(vozilo_id).get();
		Popust popust = popustRepozitory.findById(popust_id).get();
		vozilo.getPopusti().add(popust);
		
		voziloRepozitory.save(vozilo);
		//popustRepozitory.updateVozilo(vozilo_id, popust_id);
	}

	@Transactional
	public void updateUsluga(Integer popust_id, Integer usluga_id) {
		Usluga usluga = uslugaRepozitory.findById(usluga_id).get();
		Popust popust = popustRepozitory.findById(popust_id).get();
		popust.getUsluge().add(usluga);
		
		popustRepozitory.save(popust);
		System.out.println("[PopustService: updateUsluga] popust_id:" + popust_id + "; usluga_id: " + usluga_id);
		//popustRepozitory.updateUsluga(popust_id, usluga_id);
	}
	
	public Popust findById(Integer id){
		return popustRepozitory.findById(id).get();
	}
}
