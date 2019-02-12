package com.example.ISA_AMA_projekat.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity
public class Rating implements Serializable
{

	private static final long serialVersionUID = 918061230652109647L;

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Integer id;
	
	
	@Column(nullable = false)
	private int ocena;
	
	@ManyToOne
	@JoinColumn(referencedColumnName="id", nullable=true)
	private Korisnik korisnik;
	
	@ManyToOne
	@JoinColumn(referencedColumnName="id", nullable=true)
	private Let let;
	
	@ManyToOne
	@JoinColumn(referencedColumnName="id", nullable=true)
	private Aviokompanija aviokompanija;

	
	@ManyToOne
	@JoinColumn(referencedColumnName="id", nullable=true)
	private Hotel hotel;
	
	@ManyToOne
	@JoinColumn(referencedColumnName="id", nullable=true)
	private Soba soba;
	
	@ManyToOne
	@JoinColumn(referencedColumnName="id", nullable=true)
	private RentacarServis rentacar;
	
	@ManyToOne
	@JoinColumn(referencedColumnName="id", nullable=true)
	private Vozilo vozilo;
	
	
	public Rating() {
		super();
	}

	//GET & SET:


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getOcena() {
		return ocena;
	}

	public void setOcena(int ocena) {
		this.ocena = ocena;
	}

	public Korisnik getKorisnik() {
		return korisnik;
	}

	public void setKorisnik(Korisnik korisnik) {
		this.korisnik = korisnik;
	}

	public Let getLet() {
		return let;
	}

	public void setLet(Let let) {
		this.let = let;
	}

	public Aviokompanija getAviokompanija() {
		return aviokompanija;
	}

	public void setAviokompanija(Aviokompanija aviokompanija) {
		this.aviokompanija = aviokompanija;
	}

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Soba getSoba() {
		return soba;
	}

	public void setSoba(Soba soba) {
		this.soba = soba;
	}

	public RentacarServis getRentacar() {
		return rentacar;
	}

	public void setRentacar(RentacarServis rentacar) {
		this.rentacar = rentacar;
	}

	public Vozilo getVozilo() {
		return vozilo;
	}

	public void setVozilo(Vozilo vozilo) {
		this.vozilo = vozilo;
	}

	
	
}
