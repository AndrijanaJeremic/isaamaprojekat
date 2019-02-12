package com.example.ISA_AMA_projekat.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ISA_AMA_projekat.model.Rating;
import com.example.ISA_AMA_projekat.repository.RatingRepository;

@Service
public class RatingService {
	
	@Autowired
	RatingRepository ratingRepository;
	
	public Rating save(Rating ocena) {
		return ratingRepository.save(ocena);
	}
	
	public List<Integer> oceneLeta(Integer let_id)
	{
		return ratingRepository.oceneLeta(let_id);
	}
	
	public List<Integer> oceneAvio(Integer avio_id)
	{
		return ratingRepository.oceneAvio(avio_id);
	}
	
	public List<Integer> oceneHotel(Integer hotel_id)
	{
		return ratingRepository.oceneHotel(hotel_id);
	}
	
	public List<Integer> oceneSoba(Integer soba_id)
	{
		return ratingRepository.oceneSoba(soba_id);
	}
	
	public List<Integer> oceneRent(Integer rent_id)
	{
		return ratingRepository.oceneRent(rent_id);
	}
	
	public List<Integer> oceneVozilo(Integer vozilo_id)
	{
		return ratingRepository.oceneVozilo(vozilo_id);
	}
}
