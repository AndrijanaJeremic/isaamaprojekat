package com.example.ISA_AMA_projekat.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.Entity;
import javax.persistence.FetchType;

@Entity
public class Let 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Integer id;
	
	@Column(nullable = false)
	private double cena;
	
	@Column(nullable = false)
	private String odakle;
	
	@Column(nullable = false)
	private String dokle;
	
	@Column(nullable = false)
	private Date vremePoletanja;
	
	@Column(nullable = false)
	private int trajanje;
	
	@Column(nullable = false)
	private Date vremeSletanja;
	
	@Column(nullable = false)
	private double udaljenost; //u km
	
	@Column(nullable = true)
	private int popust; //u %
	
	@Column(nullable = false)
	private int maxKapacitet;
	
	@Column(nullable = true)
	private double prosecna_ocena;
	
	@Version
	private int verzija;
	
	//SLOZENI ATRIBUTI:

	@ElementCollection
	@CollectionTable(name ="presedanja")
	private List<String> presedanja = new ArrayList<String>();
	
	
	@ElementCollection
	@CollectionTable(name ="zauzetaSedista")
	private List<Integer> zauzetaSedista = new ArrayList<Integer>();
	
	


	@OneToMany(cascade={ALL}, fetch=LAZY)
	private List<Rating> rejtinzi = new ArrayList<Rating>();

	@JsonIgnore
	@OneToMany(cascade={ALL}, fetch=LAZY, mappedBy="let")
	private List<Rezervacija> rezervacije = new ArrayList<Rezervacija>();

	
	
	//FOREIGN KEYS:
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(referencedColumnName="id", nullable=false)
    @JsonBackReference (value = "airline-flight")
	private Aviokompanija aviokompanija;
	
	


	
	
	public Let() {
		super();
	}
	
	//GET & SET:


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public double getCena() {
		return cena;
	}

	public void setCena(double cena) {
		this.cena = cena;
	}

	public String getOdakle() {
		return odakle;
	}

	public void setOdakle(String odakle) {
		this.odakle = odakle;
	}

	public String getDokle() {
		return dokle;
	}

	public void setDokle(String dokle) {
		this.dokle = dokle;
	}

	public Date getVremePoletanja() {
		return vremePoletanja;
	}

	public void setVremePoletanja(Date vremePoletanja) {
		this.vremePoletanja = vremePoletanja;
	}

	public int getTrajanje() {
		return trajanje;
	}

	public void setTrajanje(int trajanje) {
		this.trajanje = trajanje;
	}

	public Date getVremeSletanja() {
		return vremeSletanja;
	}

	public void setVremeSletanja(Date vremeSletanja) {
		this.vremeSletanja = vremeSletanja;
	}

	public double getUdaljenost() {
		return udaljenost;
	}

	public void setUdaljenost(double udaljenost) {
		this.udaljenost = udaljenost;
	}

	public int getPopust() {
		return popust;
	}

	public void setPopust(int popust) {
		this.popust = popust;
	}

	public List<String> getPresedanja() {
		return presedanja;
	}

	public void setPresedanja(List<String> presedanja) {
		this.presedanja = presedanja;
	}

	public List<Rating> getRejtinzi() {
		return rejtinzi;
	}

	public void setRejtinzi(List<Rating> rejtinzi) {
		this.rejtinzi = rejtinzi;
	}

	public List<Rezervacija> getRezervacije() {
		return rezervacije;
	}

	public void setRezervacije(List<Rezervacija> rezervacije) {
		this.rezervacije = rezervacije;
	}

	public Aviokompanija getAviokompanija() {
		return aviokompanija;
	}

	public void setAviokompanija(Aviokompanija aviokompanija) {
		this.aviokompanija = aviokompanija;
	}
	
	public int getMaxKapacitet() {
		return maxKapacitet;
	}


	public void setMaxKapacitet(int maxKapacitet) {
		this.maxKapacitet = maxKapacitet;
	}

	public List<Integer> getZauzetaSedista() {
		return zauzetaSedista;
	}

	public void setZauzetaSedista(List<Integer> zauzetaSedista) {
		this.zauzetaSedista = zauzetaSedista;
	}

	public double getProsecna_ocena() {
		return prosecna_ocena;
	}

	public void setProsecna_ocena(double prosecna_ocena) {
		this.prosecna_ocena = prosecna_ocena;
	}

	public int getVerzija() {
		return verzija;
	}

	public void setVerzija(int verzija) {
		this.verzija = verzija;
	}

	
	public int getFirstFreeSeat()
	{
		for(int i = 1 ; i <= maxKapacitet ; i ++)
		{
			if(!zauzetaSedista.contains(i))
				return i;
		}
		
		return -1;
	}
	
		
}
