package com.example.ISA_AMA_projekat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import com.example.ISA_AMA_projekat.model.Bonus;

public interface BonusRepository extends JpaRepository<Bonus, Integer> {

	
	Bonus findOneByBonusPoeni(int poeni);
	
	@Modifying
	@Query("update Bonus b set b.popust = ?1 where b.bonusPoeni = ?2")
	public void updateBonus(int popust, int poeni);
}
