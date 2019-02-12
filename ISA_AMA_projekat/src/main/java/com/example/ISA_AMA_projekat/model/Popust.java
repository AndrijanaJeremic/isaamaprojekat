package com.example.ISA_AMA_projekat.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Version;

@Entity
public class Popust {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Version
	private int verzija;
	
	@Column
	private Date pocetak_vazenja;

	@Column
	private Date kraj_vazenja;
	
	@Column
	private double popust;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	private Set<Usluga> usluge = new HashSet<Usluga>();
	
	public Popust() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getPocetak_vazenja() {
		return pocetak_vazenja;
	}

	public void setPocetak_vazenja(Date pocetak_vazenja) {
		this.pocetak_vazenja = pocetak_vazenja;
	}

	public Date getKraj_vazenja() {
		return kraj_vazenja;
	}

	public void setKraj_vazenja(Date kraj_vazenja) {
		this.kraj_vazenja = kraj_vazenja;
	}

	public double getPopust() {
		return popust;
	}

	public void setPopust(double popust) {
		this.popust = popust;
	}

	public Set<Usluga> getUsluge() {
		return usluge;
	}

	public void setUsluge(Set<Usluga> usluge) {
		this.usluge = usluge;
	}

	public int getVerzija() {
		return verzija;
	}

	public void setVerzija(int verzija) {
		this.verzija = verzija;
	}
	
}
