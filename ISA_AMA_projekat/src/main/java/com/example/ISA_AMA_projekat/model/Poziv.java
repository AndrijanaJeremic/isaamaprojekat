package com.example.ISA_AMA_projekat.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Poziv implements Serializable {

	private static final long serialVersionUID = 6901581624741089050L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Integer id;
	
	@Column(nullable = false)
	private Date datum; 
	
	@ManyToOne
	@JoinColumn(name="rezervacija_id", referencedColumnName="id", nullable=false)
	private Rezervacija rezervacija;
	
	@ManyToOne
	@JoinColumn(name="osoba_id", referencedColumnName="id", nullable=false)
	@JsonBackReference
	private Korisnik korisnik;

	public Poziv() {
		super();
	}


	//GET & SET:
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public Rezervacija getRezervacija() {
		return rezervacija;
	}

	public void setRezervacija(Rezervacija rezervacija) {
		this.rezervacija = rezervacija;
	}

	public Korisnik getKorisnik() {
		return korisnik;
	}

	public void setKorisnik(Korisnik korisnik) {
		this.korisnik = korisnik;
	}
		
	
	
}
