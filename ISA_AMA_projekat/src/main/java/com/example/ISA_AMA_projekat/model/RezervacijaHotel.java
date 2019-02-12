package com.example.ISA_AMA_projekat.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class RezervacijaHotel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Version
	private int verzija;
	
	@Column
	private Date datum_rezervacije;
	
	@Column
	private Date datum_dolaska;
	
	@Column
	private Date datum_odlaska;

	@Column
	private int broj_nocenja;

	@Column
	private double ukupna_cena;

	@Column
	private boolean brza;
	
	@Column
	private double popust;
	
	@Column
	private boolean aktivirana;
	
	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER)
	private Set<Usluga> usluge = new HashSet<Usluga>();
	
	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JsonBackReference(value = "RezervacijaHotel-Soba")
	private Soba soba;
	
	public RezervacijaHotel() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDatum_rezervacije() {
		return datum_rezervacije;
	}

	public void setDatum_rezervacije(Date datum_rezervacije) {
		this.datum_rezervacije = datum_rezervacije;
	}

	public Date getDatum_dolaska() {
		return datum_dolaska;
	}

	public void setDatum_dolaska(Date datum_dolaska) {
		this.datum_dolaska = datum_dolaska;
	}

	public int getBroj_nocenja() {
		return broj_nocenja;
	}

	public void setBroj_nocenja(int broj_nocenja) {
		this.broj_nocenja = broj_nocenja;
	}

	public double getUkupna_cena() {
		return ukupna_cena;
	}

	public void setUkupna_cena(double ukupna_cena) {
		this.ukupna_cena = ukupna_cena;
	}

	public boolean isBrza() {
		return brza;
	}

	public void setBrza(boolean brza) {
		this.brza = brza;
	}
	
	public Soba getSoba() {
		return soba;
	}

	public void setSoba(Soba soba) {
		this.soba = soba;
	}

	public Date getDatum_odlaska() {
		return datum_odlaska;
	}

	public void setDatum_odlaska(Date datum_odlaska) {
		this.datum_odlaska = datum_odlaska;
	}

	public boolean isAktivirana() {
		return aktivirana;
	}

	public void setAktivirana(boolean aktivirana) {
		this.aktivirana = aktivirana;
	}

	public Set<Usluga> getUsluge() {
		return usluge;
	}

	public void setUsluge(Set<Usluga> usluge) {
		this.usluge = usluge;
	}
	
	public void removeUsluga(Usluga usluga) {
		for(Iterator<Usluga> iteratorUsluga = this.usluge.iterator(); iteratorUsluga.hasNext();) {
			if(usluga.getId().equals(iteratorUsluga.next().getId())) {
				this.usluge.remove(iteratorUsluga.next());
			}
		}
	}

	public double getPopust() {
		return popust;
	}

	public void setPopust(double popust) {
		this.popust = popust;
	}

	public int getVerzija() {
		return verzija;
	}

	public void setVerzija(int verzija) {
		this.verzija = verzija;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RezervacijaHotel rh = (RezervacijaHotel) o;
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
		return "RezervacijaHotel [id=" + id + ", broj_nocenja=" + broj_nocenja + ", ukupna_cena=" + ukupna_cena + "]";
	}
}
