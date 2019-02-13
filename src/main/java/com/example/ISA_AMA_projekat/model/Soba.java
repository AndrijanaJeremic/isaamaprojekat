package com.example.ISA_AMA_projekat.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Soba {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Version
	private int verzija;
	
	@Column(nullable = true)
	private double prosecna_ocena;
	
	@Column
	private double cena_nocenja;
	
	@Column
	private int broj_kreveta;
	
	@Column
	private boolean zauzeta;
	
	@Column
	private String opis;
	
	@Column
	private double cena_popust;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER)
	@JsonBackReference(value = "Hotel-Soba")
	private Hotel hotel;
	
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	private Set<Popust> popusti = new HashSet<Popust>();
	
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@JsonManagedReference(value = "RezervacijaHotel-Soba")
	private Set<RezervacijaHotel> rezervacije = new HashSet<RezervacijaHotel>();
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<Rating> rejtinzi = new HashSet<Rating>();
	
	public Soba() {
		super();
		this.zauzeta = false;
	}
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public double getProsecna_ocena() {
		return prosecna_ocena;
	}

	public void setProsecna_ocena(double prosecna_ocena) {
		this.prosecna_ocena = prosecna_ocena;
	}

	public double getCena_nocenja() {
		return cena_nocenja;
	}

	public void setCena_nocenja(double cena_nocenja) {
		this.cena_nocenja = cena_nocenja;
	}

	public int getBroj_kreveta() {
		return broj_kreveta;
	}

	public void setBroj_kreveta(int broj_kreveta) {
		this.broj_kreveta = broj_kreveta;
	}

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Set<RezervacijaHotel> getRezervacije() {
		return rezervacije;
	}

	public void setRezervacije(Set<RezervacijaHotel> rezervacije) {
		this.rezervacije = rezervacije;
	}

	public Set<Rating> getRejtinzi() {
		return rejtinzi;
	}

	public void setRejtinzi(Set<Rating> ocene) {
		this.rejtinzi = ocene;
	}

	public boolean isZauzeta() {
		return zauzeta;
	}

	public void setZauzeta(boolean zauzeta) {
		this.zauzeta = zauzeta;
	}

	public String getOpis() {
		return opis;
	}

	public void setOpis(String opis) {
		this.opis = opis;
	}
	
	public Set<Popust> getPopusti() {
		return popusti;
	}
	
	public void setPopusti(Set<Popust> popusti) {
		this.popusti = popusti;
	}


	public double getCena_popust() {
		return cena_popust;
	}


	public void setCena_popust(double cena_popust) {
		this.cena_popust = cena_popust;
	}


	public int getVerzija() {
		return verzija;
	}


	public void setVerzija(int verzija) {
		this.verzija = verzija;
	}
	
}
