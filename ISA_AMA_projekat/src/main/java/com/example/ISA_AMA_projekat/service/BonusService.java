package com.example.ISA_AMA_projekat.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.Bonus;
import com.example.ISA_AMA_projekat.repository.BonusRepository;

@Service
public class BonusService {
	
	@Autowired
	private BonusRepository bonusRepository;
	
	@Transactional
	public Bonus save(Bonus bonus) {
		return bonusRepository.save(bonus);
	}
	
	public Bonus findByPoeni(int poeni){
		return bonusRepository.findOneByBonusPoeni(poeni);
	}

	public Collection<Bonus> findAll() {
		return bonusRepository.findAll();
	}
	
	@Transactional
	public void updateBonus(int popust, int poeni) {
		Bonus bonus = bonusRepository.findOneByBonusPoeni(poeni);
		bonus.setPopust(popust);
		bonusRepository.save(bonus);
		//bonusRepository.updateBonus(popust, poeni);
	}
}
