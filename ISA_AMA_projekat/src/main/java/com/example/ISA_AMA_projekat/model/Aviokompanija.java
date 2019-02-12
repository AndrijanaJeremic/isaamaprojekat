package com.example.ISA_AMA_projekat.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.example.ISA_AMA_projekat.controller.LuggageInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Aviokompanija implements Serializable {
	
	private static final long serialVersionUID = 1L;

	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Integer id;
	
	@Column(unique = true, nullable = false)
	private String naziv;
	
	@Column(nullable = true)
	private String opis;

	@Column(nullable = true)
	private Double ocena;
	
	@Column(nullable = true)
	private String info;
	
	@OneToOne
	private Adresa adresa;
	
	@OneToOne
	private LuggageInfo luggageInfo;
	
	@Column(nullable = true)
	private Integer id_admin;
	
	@Column(nullable = true)
	private double prosecna_ocena;
	
	//SLOZENI ATRIBUTI:
	@OneToMany(cascade={ALL}, fetch=LAZY, mappedBy="aviokompanija")
	@JsonManagedReference(value = "airline-flight")
	private List<Let> letovi = new ArrayList<Let>();
	 
	
	@OneToMany(cascade={ALL}, fetch=LAZY)
	private List<Rating> rejtinzi = new ArrayList<Rating>();
	
	
	@OneToMany(fetch=LAZY)
	private List<Let> brziLetovi = new ArrayList<Let>();

	
	
	
	
	public Aviokompanija() {
		super();
	}


	//GET & SET:
	
	public List<Let> getBrziLetovi() {
		return brziLetovi;
	}


	public void setBrziLetovi(List<Let> brziLetovi) {
		this.brziLetovi = brziLetovi;
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


	public Adresa getAdresa() {
		return adresa;
	}


	public void setAdresa(Adresa adresa) {
		this.adresa = adresa;
	}


	public String getOpis() {
		return opis;
	}


	public void setOpis(String opis) {
		this.opis = opis;
	}


	public Double getOcena() {
		return ocena;
	}


	public void setOcena(Double ocena) {
		this.ocena = ocena;
	}


	public String getInfo() {
		return info;
	}


	public void setInfo(String info) {
		this.info = info;
	}

	
	public List<Let> getLetovi() {
		return letovi;
	}


	public LuggageInfo getLuggageInfo() {
		return luggageInfo;
	}


	public void setLuggageInfo(LuggageInfo luggageInfo) {
		this.luggageInfo = luggageInfo;
	}
	
	
	public void setLetovi(List<Let> letovi) {
		this.letovi = letovi;
	}
	
	
	public List<Rating> getRejtinzi() {
		return rejtinzi;
	}


	public void setRejtinzi(List<Rating> rejtinzi) {
		this.rejtinzi = rejtinzi;
	}


	public Integer getId_admin() {
		return id_admin;
	}


	public void setId_admin(Integer id_admin) {
		this.id_admin = id_admin;
	}


	public double getProsecna_ocena() {
		return prosecna_ocena;
	}


	public void setProsecna_ocena(double prosecna_ocena) {
		this.prosecna_ocena = prosecna_ocena;
	}
	
	
}
