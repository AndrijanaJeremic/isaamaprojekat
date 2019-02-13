package com.example.ISA_AMA_projekat.controller;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class LuggageInfo
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String opis;
	
	@Column(nullable = false)
	private Integer standardMaxKila;
	
	@Column(nullable = false)
	private Integer extraMaxKila;
	
	@Column(nullable = false)
	private double cenaStandard;
	
	@Column(nullable = false)
	private double cenaExtra;
	
	public LuggageInfo()
	{
		super();
	}

	public Integer getId() {
		return id;
	}

	public String getOpis() {
		return opis;
	}

	public Integer getStandardMaxKila() {
		return standardMaxKila;
	}

	public Integer getExtraMaxKila() {
		return extraMaxKila;
	}

	public double getCenaStandard() {
		return cenaStandard;
	}

	public double getCenaExtra() {
		return cenaExtra;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setOpis(String opis) {
		this.opis = opis;
	}

	public void setStandardMaxKila(Integer standardMaxKila) {
		this.standardMaxKila = standardMaxKila;
	}

	public void setExtraMaxKila(Integer extraMaxKila) {
		this.extraMaxKila = extraMaxKila;
	}

	public void setCenaStandard(double cenaStandard) {
		this.cenaStandard = cenaStandard;
	}

	public void setCenaExtra(double cenaExtra) {
		this.cenaExtra = cenaExtra;
	}

	
}
