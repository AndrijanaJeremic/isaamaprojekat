 package com.example.ISA_AMA_projekat.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.ISA_AMA_projekat.model.Aviokompanija;
import com.example.ISA_AMA_projekat.model.Hotel;
import com.example.ISA_AMA_projekat.model.Korisnik;
import com.example.ISA_AMA_projekat.model.Let;
import com.example.ISA_AMA_projekat.model.OsobaIzRez;
import com.example.ISA_AMA_projekat.model.Rating;
import com.example.ISA_AMA_projekat.model.RentacarServis;
import com.example.ISA_AMA_projekat.model.Rezervacija;
import com.example.ISA_AMA_projekat.model.RezervacijaHotel;
import com.example.ISA_AMA_projekat.model.RezervacijaVozila;
import com.example.ISA_AMA_projekat.model.Soba;
import com.example.ISA_AMA_projekat.model.Vozilo;
import com.example.ISA_AMA_projekat.security.TokenUtils;
import com.example.ISA_AMA_projekat.service.AviokompanijaService;
import com.example.ISA_AMA_projekat.service.EmailService;
import com.example.ISA_AMA_projekat.service.HotelService;
import com.example.ISA_AMA_projekat.service.KorisnikService;
import com.example.ISA_AMA_projekat.service.LetService;
import com.example.ISA_AMA_projekat.service.OsobaIzRezService;
import com.example.ISA_AMA_projekat.service.RatingService;
import com.example.ISA_AMA_projekat.service.RentacarService;
import com.example.ISA_AMA_projekat.service.RezervacijaHotelService;
import com.example.ISA_AMA_projekat.service.RezervacijaService;
import com.example.ISA_AMA_projekat.service.RezervacijaVozilaService;
import com.example.ISA_AMA_projekat.service.SobaService;
import com.example.ISA_AMA_projekat.service.VoziloService;

@RestController
@RequestMapping(value="api/rezervacija")
public class RezervacijaController 
{

	@Autowired
	private TokenUtils tokenUtils;
	
	@Autowired
	private KorisnikService userDetailsService;
	
	@Autowired
	private LetService letService;
	
	@Autowired
	private RezervacijaService rezervacijaService;
	
	@Autowired
	private RezervacijaHotelService rezervacijaHotelService;
	
	@Autowired
	private RezervacijaVozilaService rezervacijaVozilaService;
	
	@Autowired
	private KorisnikService korisnikService;
	
	@Autowired
	private RatingService ratingService;
	

	@Autowired
	private HotelService hotelService;
	
	@Autowired
	private VoziloService voziloService;
	
	@Autowired
	private RentacarService rentacarService;
	
	@Autowired
	private SobaService sobaService;
	
	@Autowired
	private OsobaIzRezService osobaIzRezService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private AviokompanijaService avioServis;
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/create/{id}/{token}",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public String saveKorisnik(@RequestBody List<OsobaIzRez> osobe, @PathVariable("id") Integer flightID, @PathVariable("token") String token)
	{
		String email = tokenUtils.getUsernameFromToken(token);
		Korisnik korisnik = (Korisnik) this.userDetailsService.loadUserByUsername(email);
				
		//* * * BEKEND VALIDACIJA INFO O OSOBAMA U REZERVACIJI START* * * 
		Optional<Let> letOp = letService.findById(flightID);
		
		List<OsobaIzRez> new_osobe = new ArrayList<OsobaIzRez>(); 
		
		System.out.println("Size of the list: " + osobe.size());
		
		
		Let let = null;
		try
		{
			let = letOp.get();
		}
		catch(Exception e)
		{
			System.err.println("NO FLIGHT FOUND: " + flightID);
			return null;
		}
		
		
		Date today = new Date();
		
		if(today.compareTo(let.getVremePoletanja()) > 0)
			return null;	
		
		
		for(int i = 0 ; i < osobe.size() ; i ++ )
		{
			OsobaIzRez osoba = osobe.get(i);
			System.out.println("ID OSOBE:" + osoba.getId() + " " + osoba.getEmail());
			
			if( !Pattern.matches("[a-zA-Z]{1,}", osoba.getIme() ) )
			{
				System.err.println("BEKEND VALIDACIJA IZBACILA ZBOG PASOSA: " + osoba.getIme());
				return null;
			}
			if( !Pattern.matches("[a-zA-Z]{1,}", osoba.getPrezime() ) )
			{
				System.err.println("BEKEND VALIDACIJA IZBACILA ZBOG PASOSA: " + osoba.getPrezime());
				return null;
			}
			if( !Pattern.matches("[0-9]{2,}", osoba.getBrojPasosa() ) )
			{
				System.err.println("BEKEND VALIDACIJA IZBACILA ZBOG PASOSA: " + osoba.getBrojPasosa());
				return null;
			}
			if( !Pattern.matches("[a-zA-Z0-9_-[.]]{2,}@[a-zA-Z0-9_-]{2,}[.][a-zA-Z]{2,4}", osoba.getEmail() ) )
			{
				System.err.println("BEKEND VALIDACIJA IZBACILA ZBOG EMAIL-A: " + osoba.getEmail());
				return null;
			}
			

			if(osoba.getPrtljag() > 2 || osoba.getPrtljag() < 0)
			{
				System.err.println("BEKEND VALIDACIJA IZBACILA ZBOG PTRLJAGA");
				return null;
			}
			
			//provera sedista
			if(osoba.getSediste() < 1 || osoba.getSediste() > let.getMaxKapacitet())
			{
				System.err.println("Nevalidno numerisano sediste.");
				return null;
			}
			if(let.getZauzetaSedista().contains(osoba.getSediste()))
			{
				System.err.println("Pokusaj rezervacije zauzetog sedista.");
				return null;
			}
			//ubacivanje sedista
			let.getZauzetaSedista().add(osoba.getSediste());
			
		}	
		
		System.out.println("Korisnik: " + korisnik.getId() + " Let: " + let.getId());		
		//* * * BEKEND VALIDACIJA INFO O OSOBAMA U REZERVACIJI END* * *
		
		Rezervacija rezervacija = new Rezervacija();
		rezervacija.setLet(let);
		rezervacija.setCena(let.getCena() * osobe.size());
		rezervacija.setBrza(false);
		rezervacija.setDatumRezervacije(new Date());
		rezervacija.setKorisnik(korisnik);
		
		//settovanje ostalih rezervacija u okviru ove na null
		rezervacija.setRezervacijaVozila(null);
		rezervacija.setRezevacijaHotel(null);
		rezervacija.setZavrsena(false);
		
		Rezervacija rez = rezervacijaService.save(rezervacija);
		System.out.println("Upisana rezervacija id: " + rez.getId());
		
		for(int i = 0 ; i < osobe.size() ; i ++ )
		{
			OsobaIzRez osoba = osobe.get(i);	
			osoba.setRezervacija(rez);
			
			
			if(osoba.getEmail().equals(korisnik.getEmail()))
			{
				osoba.setPotvrdjeno(true);
				osoba.setKorisnikUcesnik(korisnik);
				boolean poslatMejl= mejlKorisniku(korisnik, rez);
			}
			else
			{	
				Korisnik tempKorisnik = korisnikService.findByEmail(osoba.getEmail());
				if(tempKorisnik == null) //ako ne postoji korisnik nase aplikacije sa ovim mejlom
				{
					//potvrdjeno zanci korisnik placa
					osoba.setPotvrdjeno(true);
				}
				else
				{
					if(korisnikService.areFriends(korisnik, tempKorisnik))
					{
						//ukoliko se informacije ne poklapaju
						if(!osoba.getIme().equals(tempKorisnik.getIme()))
						{
							return null;
						}
						if(!osoba.getPrezime().equals(tempKorisnik.getPrezime()))
						{
							return null;
						}
						//salje se takav mejl koji govori da prijatelj moze da prihvati svoj deo troskova 
						
						
						boolean poslatMejl= sendRezervationInfo(tempKorisnik, rez, true);
						osoba.setPotvrdjeno(false);	
						osoba.setKorisnikUcesnik(tempKorisnik);
					}
					else
					{
						//salje se mejl da je rezevisao i da su troskovi na korisnika koji je rezervisao
						boolean poslatMejl= sendRezervationInfo(tempKorisnik, rez, false);
						osoba.setPotvrdjeno(true);	
					}
				}
			}
			
			
			OsobaIzRez new_osoba = osobaIzRezService.save(osoba);
			new_osobe.add(new_osoba);
			System.out.println("UPISANA NOVA OSOBA:" + new_osoba.getId() + " " + new_osoba.getEmail());
		}
		//sacuvaj let
		Let l = letService.save(let);
				
		return rez.getId().toString();
	}
	
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/{id}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Rezervacija> getRezervacija(@PathVariable("id") Integer id)	
	{
		System.out.println("[RezervacijaContorller: getRezervacija] id: " + id);
		try
		{
			return new ResponseEntity<Rezervacija>(rezervacijaService.findById(id).get(), HttpStatus.OK);
		}
		catch(NoSuchElementException e)
		{
			System.out.println("ispao");
			return null; 	
		}
	}
	
	

	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/getAllReservations/{id}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Rezervacija>> getRerzervationsForUser(@PathVariable("id") Integer userID)	
	{
		try
		{
			return new ResponseEntity<List<Rezervacija>>(osobaIzRezService.searchByUser(userID), HttpStatus.OK);
		}
		catch(NoSuchElementException e)
		{
			System.out.println("ispao");
			return null; 	
		}
	}
	
	
	public boolean sendRezervationInfo(Korisnik korisnik, Rezervacija rez, boolean friend)
	{
		//slanje emaila
		try 
		{	
			if(friend)
				emailService.sendReservationExpensesConformation(korisnik, rez);
			else
				emailService.sendReservationConformation(korisnik, rez);
		}catch( Exception e )
		{
			System.out.println("Greska prilikom slanja emaila: " + e.getMessage());
			return false;
		}

		return true;
	}
	
	public boolean mejlKorisniku(Korisnik korisnik, Rezervacija rez)
	{
		//slanje emaila
		try 
		{	
			emailService.toUser(korisnik, rez);
		}catch( Exception e )
		{
			System.out.println("Greska prilikom slanja emaila: " + e.getMessage());
			return false;
		}

		return true;
	}
	
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/otkaziLet/{rez_id}/{sediste}",
			method = RequestMethod.DELETE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Rezervacija deleteRez(@PathVariable("rez_id") Integer rez_id, @PathVariable("sediste") Integer sediste)
	{
		Rezervacija rez = rezervacijaService.findById(rez_id).get();
		rezervacijaService.obrisiSveOsobe(rez_id);
		rezervacijaService.deleteRez(rez);
		
		letService.deleteZauzetoSediste(rez.getLet().getId(), sediste);
		System.out.println("HOTEL: " + rez.getRezevacijaHotel());
		if(rez.getRezevacijaHotel()!=null)
		{
			System.out.println("Ima hotel rez");
			RezervacijaHotel rh = rez.getRezevacijaHotel();
			rezervacijaHotelService.deleteSobaRez(rh.getId());
			rezervacijaHotelService.deleteRezH2(rh.getId());;
		
		}
		
		if(rez.getRezervacijaVozila()!=null)
		{
			System.out.println("Ima vozilo rez");
		RezervacijaVozila rv = rez.getRezervacijaVozila();
		rezervacijaVozilaService.deleteVoziloRez(rv.getId());
		rezervacijaVozilaService.deleteRezV2(rv.getId());
		
		}
		
	
		return rez;
		
	}
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/otkaziHotel/{rez_id}",
			method = RequestMethod.DELETE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public RezervacijaHotel deleteRezHotel(@PathVariable("rez_id") Integer rez_id)
	{
		Rezervacija rez = rezervacijaService.findById(rez_id).get();
		RezervacijaHotel rh = rez.getRezevacijaHotel();
		double cena_rez = rez.getCena();
		double cena_rh=rh.getUkupna_cena();
		double nova_cena = cena_rez-cena_rh;
		rezervacijaService.updateCenaRez(nova_cena, rez.getId());
		rezervacijaService.updateHotelId(rez.getId());
		rezervacijaHotelService.deleteSobaRez(rh.getId());
		rezervacijaHotelService.deleteRezHotelUsluge(rh.getId());
		rezervacijaHotelService.deleteRezH2(rh.getId());
		
	
		return rh;
		
	}
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")	
	@RequestMapping(
			value = "/otkaziAuto/{rez_id}",
			method = RequestMethod.DELETE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public RezervacijaVozila deleteRezAuto(@PathVariable("rez_id") Integer rez_id)
	{
		Rezervacija rez = rezervacijaService.findById(rez_id).get();
		RezervacijaVozila rv = rez.getRezervacijaVozila();
		double cena_rez = rez.getCena();
		double cena_rv=rv.getUkupna_cena();
		double nova_cena = cena_rez-cena_rv;
		rezervacijaService.updateCenaRez(nova_cena, rez.getId());
		rezervacijaService.updateVoziloId(rez.getId());
		rezervacijaVozilaService.deleteVoziloRez(rv.getId());
		rezervacijaVozilaService.deleteRezV2(rv.getId());
		
		
	
		return rv;
		
	}
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")	
	@RequestMapping(
			value = "/zavrsi/{rez_id}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Rezervacija zavrsiRez(@PathVariable("rez_id") Integer rez_id)
	{
		Rezervacija rez = rezervacijaService.findById(rez_id).get();
		
		rezervacijaService.zavrsiRez(rez.getId());
		
		RezervacijaHotel rezHotel = rez.getRezevacijaHotel();
		
		if(rezHotel != null) {

			System.out.println("RezervacijaController: zavrsiRez]: aktivirana rezervacija hotela");
			rezervacijaHotelService.updateAktivirana(rezHotel.getId(), true);
		}
		
		RezervacijaVozila rezVozila = rez.getRezervacijaVozila();
		
		if(rezVozila != null) {
			
			System.out.println("RezervacijaController: zavrsiRez]: aktivirana rezervacija vozila");
			rezervacijaVozilaService.updateAktivirana(rezVozila.getId(), true);
		}
		
		return rez;
		
	}
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")	
	@RequestMapping(
			value = "/ocenaLeta/{aktivne}/{let_id}/{korisnik_id}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public double oceniAvio(@PathVariable("aktivne") int ocena, @PathVariable("let_id") Integer let_id, @PathVariable("korisnik_id") Integer korisnik_id)
	{
		Rating rating = new Rating();
		rating.setOcena(ocena);
		Korisnik korisnik = korisnikService.findById(korisnik_id).get();
		rating.setKorisnik(korisnik);
		Let l = letService.findById(let_id).get();
		rating.setLet(l);
		Rating sacuvan = ratingService.save(rating);
		
		List<Integer> ocene = ratingService.oceneLeta(let_id);
		int zbir = 0;
		for(int i=0; i<ocene.size();i++)
			zbir += ocene.get(i);
		
		double prosecna = (double) zbir/ocene.size();
		
		letService.updateProsecna(prosecna, let_id);
		
		return prosecna;
	}
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")	
	@RequestMapping(
			value = "/ocenaAvio/{aktivne}/{let_id}/{korisnik_id}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public double oceniLet(@PathVariable("aktivne") int ocena, @PathVariable("let_id") Integer let_id, @PathVariable("korisnik_id") Integer korisnik_id)
	{
		Rating rating = new Rating();
		rating.setOcena(ocena);
		Korisnik korisnik = korisnikService.findById(korisnik_id).get();
		rating.setKorisnik(korisnik);
		Let l = letService.findById(let_id).get();
		Aviokompanija avio = l.getAviokompanija();
		rating.setAviokompanija(avio);
		Rating sacuvan = ratingService.save(rating);
		
		List<Integer> ocene = ratingService.oceneAvio(avio.getId());
		int zbir = 0;
		for(int i=0; i<ocene.size();i++)
			zbir += ocene.get(i);
		
		double prosecna = (double) zbir/ocene.size();
		
		avioServis.updateProsecnaAvio(prosecna, avio.getId());
		
		return prosecna;
	}
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")	
	@RequestMapping(
			value = "/ocenaHotel/{aktivne}/{hotel_id}/{korisnik_id}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public double oceniHotel(@PathVariable("aktivne") int ocena, @PathVariable("hotel_id") Integer hotel_id, @PathVariable("korisnik_id") Integer korisnik_id)
	{
		Rating rating = new Rating();
		rating.setOcena(ocena);
		Korisnik korisnik = korisnikService.findById(korisnik_id).get();
		rating.setKorisnik(korisnik);
		Hotel h = hotelService.findById(hotel_id).get();
		rating.setHotel(h);
		Rating sacuvan = ratingService.save(rating);
		
		List<Integer> ocene = ratingService.oceneHotel(h.getId());
		int zbir = 0;
		for(int i=0; i<ocene.size();i++)
			zbir += ocene.get(i);
		
		double prosecna = (double) zbir/ocene.size();
		
		hotelService.updateProsecnaHotel(prosecna, h.getId());
		
		return prosecna;
	}
	
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")	
	@RequestMapping(
			value = "/ocenaSoba/{aktivne}/{soba_id}/{korisnik_id}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public double oceniSobu(@PathVariable("aktivne") int ocena, @PathVariable("soba_id") Integer soba_id, @PathVariable("korisnik_id") Integer korisnik_id)
	{
		Rating rating = new Rating();
		rating.setOcena(ocena);
		Korisnik korisnik = korisnikService.findById(korisnik_id).get();
		rating.setKorisnik(korisnik);
		Soba s =sobaService.findById(soba_id).get();
		rating.setSoba(s);
		Rating sacuvan = ratingService.save(rating);
		
		List<Integer> ocene = ratingService.oceneSoba(s.getId());
		int zbir = 0;
		for(int i=0; i<ocene.size();i++)
			zbir += ocene.get(i);
		
		double prosecna = (double) zbir/ocene.size();
		
		sobaService.updateProsecnaSoba(prosecna, s.getId());
		
		return prosecna;
	}
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")	
	@RequestMapping(
			value = "/ocenaRent/{aktivne}/{rent_id}/{korisnik_id}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public double oceniRent(@PathVariable("aktivne") int ocena, @PathVariable("rent_id") Integer rent_id, @PathVariable("korisnik_id") Integer korisnik_id)
	{
		Rating rating = new Rating();
		rating.setOcena(ocena);
		Korisnik korisnik = korisnikService.findById(korisnik_id).get();
		rating.setKorisnik(korisnik);
		RentacarServis rs =rentacarService.findById(rent_id).get();
		rating.setRentacar(rs);
		Rating sacuvan = ratingService.save(rating);
		
		List<Integer> ocene = ratingService.oceneRent(rs.getId());
		int zbir = 0;
		for(int i=0; i<ocene.size();i++)
			zbir += ocene.get(i);
		
		double prosecna = (double) zbir/ocene.size();
		
		rentacarService.updateProsecnaRent(prosecna, rs.getId());
		
		return prosecna;
	}
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")	
	@RequestMapping(
			value = "/ocenaVozilo/{aktivne}/{car_id}/{korisnik_id}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public double oceniVozilo(@PathVariable("aktivne") int ocena, @PathVariable("car_id") Integer car_id, @PathVariable("korisnik_id") Integer korisnik_id)
	{
		Rating rating = new Rating();
		rating.setOcena(ocena);
		Korisnik korisnik = korisnikService.findById(korisnik_id).get();
		rating.setKorisnik(korisnik);
		Vozilo v =voziloService.findById(car_id).get();
		rating.setVozilo(v);
		Rating sacuvan = ratingService.save(rating);
		
		List<Integer> ocene = ratingService.oceneVozilo(v.getId());
		int zbir = 0;
		for(int i=0; i<ocene.size();i++)
			zbir += ocene.get(i);
		
		double prosecna = (double) zbir/ocene.size();
		
		voziloService.updateProsecnaVozilo(prosecna, v.getId());
		
		return prosecna;
	}
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/updateCena/{id}/{cena}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public void updateCena(@PathVariable("id") Integer id_rez, @PathVariable("cena") double cena){
		
		rezervacijaService.updateCenaRez(cena, id_rez);
	}
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/obrisiOsobu/{osoba_id}/{rez_id}",
			method = RequestMethod.DELETE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Rezervacija deleteOsoba(@PathVariable("osoba_id") Integer osoba_id, @PathVariable("rez_id") Integer rez_id)
	{
		Rezervacija rez = rezervacijaService.findById(rez_id).get();
		Let let = letService.findById(rez.getLet().getId()).get();
		double cena_rez=rez.getCena();
		System.out.println("CENA REZ " + cena_rez);
		double cena_let = let.getCena();
		System.out.println("CENA LET " + cena_let);
		double nova_cena = cena_rez-cena_let;
		
		System.out.println("NOVA " + nova_cena);
		rezervacijaService.updateCena(nova_cena, rez_id);
		
		rezervacijaService.obrisiOsobu(osoba_id, rez_id);
		return rez;
		
	}
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/prihvatiRez/{osoba_id}/{rez_id}",
			method = RequestMethod.PUT,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Rezervacija prihvatiRez(@PathVariable("osoba_id") Integer osoba_id, @PathVariable("rez_id") Integer rez_id)
	{
		Rezervacija rez = rezervacijaService.findById(rez_id).get();
		
		rezervacijaService.potvrdiRez(osoba_id, rez_id);
		return rez;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/createBrza/{id}/{token}",
			method = RequestMethod.POST)
	public String brrrrzza( @PathVariable("id") Integer flightID, @PathVariable("token") String token)
	{
		String email = tokenUtils.getUsernameFromToken(token);
		Korisnik korisnik = (Korisnik) this.userDetailsService.loadUserByUsername(email);
				
		//* * * BEKEND VALIDACIJA INFO O OSOBAMA U REZERVACIJI START* * * 
		Optional<Let> letOp = letService.findById(flightID);
		

		Let let = null;
		try
		{
			let = letOp.get();
		}
		catch(Exception e)
		{
			System.err.println("************NO FLIGHT FOUND: " + flightID);
			return null;
		}
		
		
		Date today = new Date();
		
		if(today.compareTo(let.getVremePoletanja()) > 0)
			return null;	
		
		
		if(!let.getAviokompanija().getBrziLetovi().contains(let))
		{
			System.err.println("***********Aviokompanija ne sadzi ovaj let u svojim akcijskim ponudama.");
			return null;
		}
		
		Rezervacija rezervacija = new Rezervacija();
		rezervacija.setLet(let);
		
		double cenaPopust = let.getCena() -  let.getCena()*((double)let.getPopust()/100);
		
		
		rezervacija.setCena(cenaPopust);
		rezervacija.setBrza(true);
		rezervacija.setDatumRezervacije(new Date());
		rezervacija.setKorisnik(korisnik);
		
		//settovanje ostalih rezervacija u okviru ove na null
		rezervacija.setRezervacijaVozila(null);
		rezervacija.setRezevacijaHotel(null);
		rezervacija.setZavrsena(true);
		
		Rezervacija rez = rezervacijaService.save(rezervacija);
		System.out.println("Upisana rezervacija id: " + rez.getId());
		
		
		OsobaIzRez osoba = new OsobaIzRez();
		osoba.setEmail(korisnik.getEmail());
		osoba.setPotvrdjeno(true);
		osoba.setRezervacija(rez);
		osoba.setKorisnikUcesnik(korisnik);
		osoba.setIme(korisnik.getIme());
		osoba.setPrezime(korisnik.getPrezime());
		osoba.setBrojPasosa("is not needed");
		
		int seatNum = let.getFirstFreeSeat();
		if(seatNum == -1)
			return "-1"; //nema vise slobodnih mesta 

		osoba.setSediste(seatNum);
		let.getZauzetaSedista().add(seatNum); //zauzmi sediste
		osoba.setPrtljag(0); //nosi CARRYON uvek
		OsobaIzRez new_osoba = osobaIzRezService.save(osoba);
		
		
		
		boolean poslatMejl= mejlKorisniku(korisnik, rez);
		
		
		//zbog sedista?
		Let l = letService.save(let);
				
		return rez.getId().toString();
	}
	
}
