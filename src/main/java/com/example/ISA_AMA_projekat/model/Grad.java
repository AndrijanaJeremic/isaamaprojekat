package com.example.ISA_AMA_projekat.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;


@Entity
public class Grad {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Version
	private int verzija;
	
	@Column(nullable = false, unique = true)
	private String naziv;
	
	public Grad() {
		super();
	}

	
	public Grad(String naziv) {
		this.naziv=naziv;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNaziv() {
		return naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}


	public int getVerzija() {
		return verzija;
	}


	public void setVerzija(int verzija) {
		this.verzija = verzija;
	}

}
