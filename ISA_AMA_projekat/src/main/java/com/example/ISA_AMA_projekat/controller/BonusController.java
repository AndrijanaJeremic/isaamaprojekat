package com.example.ISA_AMA_projekat.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.ISA_AMA_projekat.model.Bonus;
import com.example.ISA_AMA_projekat.service.BonusService;

@RestController
@RequestMapping(value="api/bonus")
public class BonusController {

	@Autowired
	private BonusService bonusService;
	
	@PreAuthorize("hasRole('SYSADMIN')")
	@RequestMapping(
			value = "/save",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> saveBonus(@RequestBody Bonus bonus){
		
		Bonus postoji = bonusService.findByPoeni(bonus.getBonusPoeni());
		Map<String, String> result = new HashMap<>();
		
		
		if(postoji != null) {
			System.out.println("Bonus sa ovim brojem poena vec postoji u bazi");
			bonusService.updateBonus(bonus.getPopust(), bonus.getBonusPoeni());
			result.put("result", "error");
			
			return ResponseEntity.accepted().body(result);
		}else {
			bonusService.save(bonus);
			
			result.put("result", "success");
			
			return ResponseEntity.accepted().body(result);
		}
	}
	
}
