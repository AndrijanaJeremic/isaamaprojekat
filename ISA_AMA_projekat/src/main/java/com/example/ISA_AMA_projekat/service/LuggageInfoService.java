package com.example.ISA_AMA_projekat.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ISA_AMA_projekat.controller.LuggageInfo;
import com.example.ISA_AMA_projekat.repository.LuggageInfoRepository;

@Service
public class LuggageInfoService 
{
	@Autowired
	private LuggageInfoRepository luggageInfoRepository; 
	
	public void updateInfo(LuggageInfo li)
	{
		luggageInfoRepository.updateInfo(li.getId(), li.getStandardMaxKila(), li.getCenaStandard(), li.getExtraMaxKila(), li.getCenaExtra(), li.getOpis());
	}
	
	public Optional<LuggageInfo> findById(Integer id)
	{
		return luggageInfoRepository.findById(id);
	}
}
