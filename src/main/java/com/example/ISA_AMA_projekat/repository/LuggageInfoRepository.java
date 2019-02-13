package com.example.ISA_AMA_projekat.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.ISA_AMA_projekat.controller.LuggageInfo;


public interface LuggageInfoRepository extends JpaRepository<LuggageInfo, Integer> 
{
	@Modifying
	@Transactional
	@Query(value = "update luggage_info li set li.standard_max_kila = ?2, li.cena_standard = ?3, li.extra_max_kila = ?4, li.cena_extra = ?5, li.opis = ?6 where li.id = ?1", nativeQuery=true)
	public void updateInfo(Integer id, Integer standardKila, Double cenaStandard, Integer extraKila, Double cenaExtra, String opis);
	
}
