package com.example.ISA_AMA_projekat.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
 
enum Prtljag 
{
   RUCNI, STANDARD, EXTRA
}
@Entity
public class OsobaIzRez implements Serializable{

	private static final long serialVersionUID = 7632373016632740844L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Integer id;
	
	
	@Column(nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String ime;
	
	@Column(nullable = false)
	private String prezime;
	
	@Column(nullable = false)
	private String brojPasosa;
	
	@Column(nullable = false)
	private int sediste;
	
	@Column(nullable = false)
	private boolean potvrdjeno;
	
	@Column(nullable = false)
	private Prtljag prtljag;
		

	//FOREIGN KEY:
	

	@ManyToOne
	@JoinColumn(referencedColumnName="id", nullable=false)
	@JsonBackReference (value = "rezervacija_osoba")
	private Rezervacija rezervacija;
	
	//nullable ako je pozvan korisnik koji nema acc
	@ManyToOne
	@JoinColumn(name="korisnik_id", referencedColumnName="id", nullable=true)
	@JsonBackReference (value = "korisnik_ucestvovanje")
	private Korisnik korisnikUcesnik;
	
	
	//GET & SET:

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIme() {
		return ime;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public String getPrezime() {
		return prezime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public String getBrojPasosa() {
		return brojPasosa;
	}

	public void setBrojPasosa(String brojPasosa) {
		this.brojPasosa = brojPasosa;
	}

	public int getSediste() {
		return sediste;
	}

	public void setSediste(int sediste) {
		this.sediste = sediste;
	}

	public boolean isPotvrdjeno() {
		return potvrdjeno;
	}

	public void setPotvrdjeno(boolean potvrdjeno) {
		this.potvrdjeno = potvrdjeno;
	}

	public Rezervacija getRezervacija() {
		return rezervacija;
	}

	public void setRezervacija(Rezervacija rezervacija) {
		this.rezervacija = rezervacija;
	}

	public Korisnik getKorisnikUcesnik() {
		return korisnikUcesnik;
	}

	public void setKorisnikUcesnik(Korisnik korisnik) {
		this.korisnikUcesnik = korisnik;
	}
	
	
	public int getPrtljag() {
		return prtljag.ordinal();
	}

	public void setPrtljag(int prtljag) {
		this.prtljag = Prtljag.values()[prtljag];
	}
	

}
