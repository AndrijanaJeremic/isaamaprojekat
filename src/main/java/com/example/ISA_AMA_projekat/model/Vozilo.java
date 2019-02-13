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
public class Vozilo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private String naziv;
	
	@Column
	private String marka;
	
	@Column
	private String model;
	
	@Column
	private int godina_proizvodnje;
	
	@Column
	private int broj_sedista;
	
	@Column
	private String tip;
	
	@Column
	private double cena_dan;
	
	@Version
	private int verzija;
	
	@Column(nullable = true)
	private double prosecna_ocena;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER)
	@JsonBackReference
	private Filijala filijala;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@JsonManagedReference(value = "RezervacijaVozila-Vozilo")
	private Set<RezervacijaVozila> rezervacije = new HashSet<RezervacijaVozila>();
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<Rating> rejtinzi = new HashSet<Rating>();
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	private Set<Popust> popusti = new HashSet<Popust>();
	
	@Column
	private boolean zauzeto; 
	
	@Column
	private double cena_popust;
	
	public Vozilo() {
		super();
	}

	
	

	public Set<Popust> getPopusti() {
		return popusti;
	}




	public void setPopusti(Set<Popust> popusti) {
		this.popusti = popusti;
	}




	public boolean isZauzeto() {
		return zauzeto;
	}




	public void setZauzeto(boolean zauzeto) {
		this.zauzeto = zauzeto;
	}




	public double getCena_popust() {
		return cena_popust;
	}




	public void setCena_popust(double cena_popust) {
		this.cena_popust = cena_popust;
	}




	public Set<RezervacijaVozila> getRezervacije() {
		return rezervacije;
	}




	public void setRezervacije(Set<RezervacijaVozila> rezervacije) {
		this.rezervacije = rezervacije;
	}




	public Set<Rating> getRejtinzi() {
		return rejtinzi;
	}




	public void setRejtinzi(Set<Rating> ocene) {
		this.rejtinzi = ocene;
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

	public String getMarka() {
		return marka;
	}

	public void setMarka(String marka) {
		this.marka = marka;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public int getGodina_proizvodnje() {
		return godina_proizvodnje;
	}

	public void setGodina_proizvodnje(int godina_proizvodnje) {
		this.godina_proizvodnje = godina_proizvodnje;
	}

	public int getBroj_sedista() {
		return broj_sedista;
	}

	public void setBroj_sedista(int broj_sedista) {
		this.broj_sedista = broj_sedista;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public double getCena_dan() {
		return cena_dan;
	}

	public void setCena_dan(double cena_dan) {
		this.cena_dan = cena_dan;
	}

	public double getProsecna_ocena() {
		return prosecna_ocena;
	}

	public void setProsecna_ocena(double prosecna_ocena) {
		this.prosecna_ocena = prosecna_ocena;
	}

	public Filijala getFilijala() {
		return filijala;
	}

	public void setFilijala(Filijala filijala) {
		this.filijala = filijala;
	}




	public int getVerzija() {
		return verzija;
	}




	public void setVerzija(int verzija) {
		this.verzija = verzija;
	}
	
	
	
	

}
