package com.example.ISA_AMA_projekat.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class RentacarServis {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer id;
	
	@Column(nullable = false)
	private String naziv;
	
	@OneToOne
	private Adresa adresa;
	
	@Version
	private int verzija;
	
	@Column
	private String promotivni_opis;
	
	@Column(nullable = true)
	private Integer id_admin;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "rentacar_id", referencedColumnName = "id")
	@JsonManagedReference
	private Set<Filijala> filijale;
	
	
	@ManyToMany
	private Set<Usluga> usluge = new HashSet<Usluga>();
	
	@Column(nullable = true)
	private double prosecna_ocena;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Rating> rejtinzi = new HashSet<Rating>();
	
	
	
	public RentacarServis() {
		super();
	}
	
	public RentacarServis(Integer id, String naziv, String adresa, String broj, String grad, String opis) {
	
		this.id=id;
		this.naziv=naziv;
		this.adresa = new Adresa();
		this.adresa.setUlica(adresa);
		this.adresa.setBroj(broj);
		this.adresa.getGrad().setNaziv(grad);
		this.promotivni_opis=opis;
	}
	
	public RentacarServis(Integer id,String naziv, Adresa adresa, String opis, double prosecna_ocena) {
		
		this.id=id;
		this.naziv=naziv;
		this.adresa = adresa;
		this.prosecna_ocena=prosecna_ocena;
		this.promotivni_opis=opis;
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

	public String getPromotivni_opis() {
		return promotivni_opis;
	}

	public void setPromotivni_opis(String promotivni_opis) {
		this.promotivni_opis = promotivni_opis;
	}

	public double getProsecna_ocena() {
		return prosecna_ocena;
	}

	public void setProsecna_ocena(double prosecna_ocena) {
		this.prosecna_ocena = prosecna_ocena;
	}

	public Set<Usluga> getUsluge() {
		return usluge;
	}

	public void setUsluge(Set<Usluga> usluge) {
		this.usluge = usluge;
	}
	

	public Set<Filijala> getFilijale() {
		return filijale;
	}

	public void setFilijale(Set<Filijala> filijale) {
		this.filijale = filijale;
	}

	public Set<Rating> getRejtinzi() {
		return rejtinzi;
	}

	public void setRejtinzi(Set<Rating> ocene) {
		this.rejtinzi = ocene;
	}
	
	public Integer getId_admin() {
		return id_admin;
	}

	public void setId_admin(Integer id_admin) {
		this.id_admin = id_admin;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RentacarServis s = (RentacarServis) o;
        if(s.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, s.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

	@Override
	public String toString() {
		return "Rent-a-car servis [id=" + id + ", naziv=" + naziv + ", adresa="
				+ adresa + ", promotivni opis=" + promotivni_opis + "]";
	}

	public int getVerzija() {
		return verzija;
	}

	public void setVerzija(int verzija) {
		this.verzija = verzija;
	}
	
	
}
	
	

