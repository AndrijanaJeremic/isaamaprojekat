package com.example.ISA_AMA_projekat.model;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.joda.time.DateTime;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Korisnik implements Serializable, UserDetails 
{

	private static final long serialVersionUID = 7284235902041908178L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Version
	private int verzija;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String lozinka;
	
	@Column(nullable = false)
	private String ime;
	
	@Column(nullable = false)
	private String prezime;
	
	@OneToOne
	private Grad grad;
	
	@Column(nullable = false)
	private String telefon;
	
	@Column(nullable = true)
	private int bonuspoeni;
	
	@Column(nullable = true)
	private boolean aktiviran;
	
	
	@Column(name = "last_password_reset_date")
	private Timestamp lastPasswordResetDate;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    private Authority authority;
	
	//SLOZENI ATRIBUTI:

	@OneToMany(fetch=FetchType.EAGER, mappedBy="prima")
	@JsonManagedReference
	private Set<FriendRequest> prijateljstva = new HashSet<FriendRequest>();
	
	
	@OneToMany(fetch=LAZY, mappedBy="korisnik")
	@JsonManagedReference
	private Set<Poziv> pozivi = new HashSet<Poziv>();
	

	@OneToMany(cascade={ALL}, fetch=FetchType.EAGER, mappedBy="korisnikUcesnik")
	@JsonManagedReference (value = "korisnik_ucestvovanje")
	private List<OsobaIzRez> rezervacijeUcestvovanje = new ArrayList<OsobaIzRez>();

	
	@Column(nullable=true)
	private Integer admin_id;
	
	
	public Korisnik() 
	{
		super();
		this.bonuspoeni=0;
	}
	
	public Korisnik(String email, String lozinka, String ime, String prezime, Grad grad, String telefon)
	{
		this.email=email;
		this.lozinka=lozinka;
		this.ime=ime;
		this.prezime=prezime;
		this.grad=grad;
		this.telefon=telefon;
		this.bonuspoeni=0;
	}

	public Korisnik(String email, String lozinka, String ime, String prezime, String grad, String telefon)
	{
		this.email=email;
		this.lozinka=lozinka;
		this.ime=ime;
		this.prezime=prezime;
		this.grad.setNaziv(grad);
		this.telefon=telefon;
		this.bonuspoeni=0;
	}
	 
	public Korisnik(String email, String lozinka)
	{
		this.email=email;
		this.lozinka=lozinka;
	}

	

	//GET & SET:

	public Integer getId() 
	{
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


	public String getLozinka() {
		return lozinka;
	}


	public void setLozinka(String lozinka) {
		Timestamp now = new Timestamp(DateTime.now().getMillis());
	    this.setLastPasswordResetDate( now );
		this.lozinka = lozinka;
	}
	
	 public Timestamp getLastPasswordResetDate() {
	        return lastPasswordResetDate;
	    }

	    public void setLastPasswordResetDate(Timestamp lastPasswordResetDate) {
	        this.lastPasswordResetDate = lastPasswordResetDate;
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


	public Grad getGrad() {
		return grad;
	}


	public void setGrad(Grad grad) {
		this.grad = grad;
	}


	public String getTelefon() {
		return telefon;
	}


	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}


	public int getBonus_poeni() {
		return bonuspoeni;
	}


	public void setBonus_poeni(int bonus_poeni) {
		this.bonuspoeni = bonus_poeni;
	}


	public Set<FriendRequest> getPrijateljstva() {
		return prijateljstva;
	}


	public void setPrijateljstva(Set<FriendRequest> prijateljstva) {
		this.prijateljstva = prijateljstva;
	}


	public Set<Poziv> getPozivi() {
		return pozivi;
	}


	public void setPozivi(Set<Poziv> poziviZaRezervacije) {
		this.pozivi = poziviZaRezervacije;
	}


	    public Authority getAuthority() {
		return authority;
	}

	public void setAuthority(Authority authority) {
		this.authority = authority;
	}

		@JsonIgnore
	    @Override
	    public boolean isAccountNonExpired() {
	        return true;
	    }

	    @JsonIgnore
	    @Override
	    public boolean isAccountNonLocked() {
	        return true;
	    }

	    @JsonIgnore
	    @Override
	    public boolean isCredentialsNonExpired() {
	        return true;
	    }

	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Korisnik k = (Korisnik) o;
        if(k.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, k.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

	public int getBonuspoeni() {
		return bonuspoeni;
	}

	public void setBonuspoeni(int bonuspoeni) {
		this.bonuspoeni = bonuspoeni;
	}

	public boolean getAktiviran() {
		return aktiviran;
	}

	public void setAktiviran(boolean aktiviran) {
		this.aktiviran = aktiviran;
	}

	

	public Integer getAdmin_id() {
		return admin_id;
	}

	public void setAdmin_id(Integer admin_id) {
		this.admin_id = admin_id;
	}

	@Override
	public String getPassword() {
		return lozinka;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isEnabled() {
		return aktiviran;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<Authority> authorities= new ArrayList<Authority>();
		authorities.add(this.authority);
		return authorities;
	}

	public List<OsobaIzRez> getRezervacijeUcestvovanje() {
		return rezervacijeUcestvovanje;
	}

	public void setRezervacijeUcestvovanje(List<OsobaIzRez> rezervacijeUcestvovanje) {
		this.rezervacijeUcestvovanje = rezervacijeUcestvovanje;
	}

	public int getVerzija() {
		return verzija;
	}

	public void setVerzija(int verzija) {
		this.verzija = verzija;
	}
    
}
