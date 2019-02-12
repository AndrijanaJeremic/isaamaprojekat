package com.example.ISA_AMA_projekat.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class RezervacijaVozila {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Version
	private int verzija;
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonBackReference(value = "RezervacijaVozila-Vozilo")
	private Vozilo vozilo;
	
	@Column
	private Date datum_rezervacije;
	
	@Column
	private Date datum_preuzimanja;
	
	@Column
	private Date datum_vracanja;
	
	@OneToOne
	private Grad mesto_preuzimanja;
	
	@OneToOne
	private Grad mesto_vracanja;
	
	@Column
	private double ukupna_cena;
	
	@Column
	private int broj_putnika;
	
	@Column
	private boolean brza;

	@Column
	private boolean aktivirana;
	
	@Column
	private double popust;
	
	public RezervacijaVozila() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public Date getDatum_rezervacije() {
		return datum_rezervacije;
	}


	public void setDatum_rezervacije(Date datum_rezervacije) {
		this.datum_rezervacije = datum_rezervacije;
	}


	public boolean isAktivirana() {
		return aktivirana;
	}


	public void setAktivirana(boolean aktivirana) {
		this.aktivirana = aktivirana;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public Vozilo getVozilo() {
		return vozilo;
	}

	public void setVozilo(Vozilo vozilo) {
		this.vozilo = vozilo;
	}

	public Date getDatum_preuzimanja() {
		return datum_preuzimanja;
	}

	public void setDatum_preuzimanja(Date datum_preuzimanja) {
		this.datum_preuzimanja = datum_preuzimanja;
	}

	public Date getDatum_vracanja() {
		return datum_vracanja;
	}

	public void setDatum_vracanja(Date datum_vracanja) {
		this.datum_vracanja = datum_vracanja;
	}

	public Grad getMesto_preuzimanja() {
		return mesto_preuzimanja;
	}

	public void setMesto_preuzimanja(Grad mesto_preuzimanja) {
		this.mesto_preuzimanja = mesto_preuzimanja;
	}

	public Grad getMesto_vracanja() {
		return mesto_vracanja;
	}

	public void setMesto_vracanja(Grad mesto_vracanja) {
		this.mesto_vracanja = mesto_vracanja;
	}

	public double getUkupna_cena() {
		return ukupna_cena;
	}

	public void setUkupna_cena(double ukupna_cena) {
		this.ukupna_cena = ukupna_cena;
	}

	public int getBroj_putnika() {
		return broj_putnika;
	}

	public void setBroj_putnika(int broj_putnika) {
		this.broj_putnika = broj_putnika;
	}

	public boolean isBrza() {
		return brza;
	}

	public void setBrza(boolean brza) {
		this.brza = brza;
	}
	
	
	public double getPopust() {
		return popust;
	}


	public void setPopust(double popust) {
		this.popust = popust;
	}


	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RezervacijaVozila rh = (RezervacijaVozila) o;
        if(rh.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, rh.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

	@Override
	public String toString() {
		return "RezervacijaVozila [id=" + id  + ", ukupna_cena=" + ukupna_cena + "]";
	}


	public int getVerzija() {
		return verzija;
	}


	public void setVerzija(int verzija) {
		this.verzija = verzija;
	}
	
	
	

}
