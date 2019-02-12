package com.example.ISA_AMA_projekat.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.ISA_AMA_projekat.model.Adresa;
import com.example.ISA_AMA_projekat.model.Filijala;
import com.example.ISA_AMA_projekat.model.Grad;
import com.example.ISA_AMA_projekat.model.Hotel;
import com.example.ISA_AMA_projekat.model.Popust;
import com.example.ISA_AMA_projekat.model.RentacarServis;
import com.example.ISA_AMA_projekat.model.Rezervacija;
import com.example.ISA_AMA_projekat.model.RezervacijaVozila;
import com.example.ISA_AMA_projekat.model.Soba;
import com.example.ISA_AMA_projekat.model.Vozilo;
import com.example.ISA_AMA_projekat.service.AddressService;
import com.example.ISA_AMA_projekat.service.GradService;
import com.example.ISA_AMA_projekat.service.PopustService;
import com.example.ISA_AMA_projekat.service.RentacarService;
import com.example.ISA_AMA_projekat.service.RezervacijaService;
import com.example.ISA_AMA_projekat.service.RezervacijaVozilaService;
import com.example.ISA_AMA_projekat.service.VoziloService;

@RestController
@RequestMapping(value="api/rents")
public class RentacarController {

	@Autowired
	private RentacarService rentService;
	
	@Autowired
	private VoziloService voziloService;
	
	@Autowired
	private GradService gradService; 
	
	@Autowired
	private AddressService addressService; 
	
	@Autowired
	private RezervacijaService rezervacijaService; 
	 
	
	@Autowired
	private RezervacijaVozilaService rezervacijaVozilaService;
	
	@Autowired
	private PopustService popustService;
	
	@RequestMapping(
			value = "/all",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<RentacarServis>> getRentacarServis(){
		
		Collection<RentacarServis> rents = rentService.findAll();
		for (RentacarServis rs : rents)
		{
			System.out.println("IMA LI FILIJALA: " + rs.getFilijale().size());
		}
		
		ArrayList<RentacarServis> sortirani = (ArrayList<RentacarServis>) rents;
		sortirani.sort(Comparator.comparing(RentacarServis::getNaziv));
		rents = sortirani;
		return new ResponseEntity<Collection<RentacarServis>>(rents, HttpStatus.OK);
	}
	
	@RequestMapping(
			value = "/getVozilo/{rez_id}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Vozilo> getCarFromRez(@PathVariable("rez_id") Integer rez_id){
		
			RezervacijaVozila rv = rezervacijaVozilaService.findById(rez_id).get();
			Vozilo v = rv.getVozilo();
			return new ResponseEntity<Vozilo>(v, HttpStatus.OK);
	}
	
	@RequestMapping(
			value = "/getRent/{rez_id}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RentacarServis> getRentFromRez(@PathVariable("rez_id") Integer rez_id){
		
			RezervacijaVozila rv = rezervacijaVozilaService.findById(rez_id).get();
			Vozilo v = rv.getVozilo();
			Filijala fil = v.getFilijala();
			RentacarServis rs = fil.getRentacar();
			return new ResponseEntity<RentacarServis>(rs, HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('SYSADMIN')")
	@RequestMapping(
			value = "/all_admin",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<RentacarServis>> getRentacarServisForAdmin(){
		
		Collection<RentacarServis> rents = rentService.findAll();
		Collection<RentacarServis> result = new ArrayList<RentacarServis>();
		
		for (RentacarServis rs : rents){
			if(rs.getId_admin() == null) {
				result.add(rs);
			}
		}
		return new ResponseEntity<Collection<RentacarServis>>(result, HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(
			value = "admin/{id}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RentacarServis> getRentacarServisAdm(@PathVariable("id") Integer id)
	{
		try
		{
			RentacarServis found = rentService.findById(id).get();
			return new ResponseEntity<RentacarServis>(found, HttpStatus.OK);
		}
		catch(NoSuchElementException e)
		{
			System.out.println("Ne postoji servis sa id: " + id);
			return null; 	
		}
	}
	
	@PreAuthorize("hasRole('SYSADMIN')")
	@RequestMapping(
			value = "/save",
			method = RequestMethod.POST,
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RentacarServis> saveRentacarServis(@RequestBody RentacarServis rentacarServis) throws Exception{
		
		RentacarServis postoji = rentService.findByNaziv(rentacarServis.getNaziv());
		
		if(postoji != null) {
			System.out.println("RentacarServis sa ovim nazivom vec postoji u bazi");
			return new ResponseEntity<RentacarServis>(postoji, HttpStatus.CONFLICT);
		}else {
			RentacarServis saved = rentService.save(rentacarServis);
			return new ResponseEntity<RentacarServis>(saved, HttpStatus.CREATED);
		}
	}
	
	@RequestMapping(
			value = "/search/{name_location}/{check_in}/{check_out}/{check_in_town}/{check_out_town}/{passengers}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<RentacarServis>> getRentacarSearch(HttpSession session, HttpServletRequest request, @PathVariable("name_location") String name_location, @PathVariable("check_in") String check_in, @PathVariable("check_out") String check_out, @PathVariable("check_in_town") String check_in_town, @PathVariable("check_out_town") String check_out_town, @PathVariable("passengers") int passengers) throws ParseException{
		
		System.out.println("[RentacarController: getRentacarSearch]: name_location-" + name_location + ", check_in-" + check_in + ", check_out-" + check_out + ", in_town-" + check_in_town + ", out_town-" + check_out_town + ", passengers-" + passengers);
		name_location = name_location.toLowerCase();
		String[] search = name_location.split(" ");
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date_check_in = format.parse(check_in);
		Date date_check_out = format.parse(check_out);
		
		List<RentacarServis> rents = rentService.search("%" + search[0] + "%");
		System.out.println("Search 0: " +search[0]);
		System.out.println("RENTS: " + rents.size());
		
		for(int i=0; i<rents.size(); i++) {
			System.out.println("[RentacarController: getRentacarSearch]: rentacar name: " + rents.get(i).getNaziv() + ", rentacar address: " + rents.get(i).getAdresa());
		}
		
		
		List<RentacarServis> servisi = pretragaServisa(rents, search, date_check_in, date_check_out, check_in_town, check_out_town, passengers);
			System.out.println("SERVISI VRACENI: " + servisi.size());
			
			request.getSession().setAttribute("servisiPretraga", servisi);
		return new ResponseEntity<List<RentacarServis>>(servisi, HttpStatus.OK);
		
	}
	
	public List<RentacarServis> pretragaServisa(List<RentacarServis> rents, String[] search, Date check_in, Date check_out, String check_in_town, String check_out_town, int passengers){
		check_in_town=check_in_town.toLowerCase();
		check_out_town=check_out_town.toLowerCase();
		
		List<RentacarServis> result = new ArrayList<RentacarServis>();
		
		for(int i=0; i<rents.size(); i++) {
			result.add(rents.get(i));
		}
		
		for(int j=0; j<rents.size(); j++) {
			System.out.println("[RentacarController: pretraga]: naziv servisa: " + rents.get(j).getNaziv() + ", adresa servisa: " + rents.get(j).getAdresa());
			for(int i=0; i<search.length; i++) {
				if(!(rents.get(j).getAdresa().getGrad().getNaziv().toLowerCase().contains(search[i]) || rents.get(j).getAdresa().getUlica().toLowerCase().contains(search[i]) || rents.get(j).getAdresa().getBroj().toLowerCase().contains(search[i]) || rents.get(j).getNaziv().toLowerCase().contains(search[i]))) {
					result.remove(rents.get(j));
					System.out.println("[RentacarController: pretraga]: obrisan rentacar sa nazivom - " + rents.get(j).getNaziv());
				}
			}
		}
		boolean adresaPreuzimanja = false;
		boolean adresaVracanja = false;
		for (int i=0; i<result.size(); i++) {
			RentacarServis rentacar = result.get(i);
			for (Iterator<Filijala> iteratorFilijala = rentacar.getFilijale().iterator(); iteratorFilijala.hasNext();)
			{
				Filijala filijala = (Filijala) iteratorFilijala.next();
				System.out.println("[RentacarController: pretraga] MESTO FILIJALE: " + filijala.getAdresa().getGrad().getNaziv());
				if(filijala.getAdresa().getGrad().getNaziv().toLowerCase().equals(check_in_town) ||
						rentacar.getAdresa().getGrad().getNaziv().toLowerCase().equals(check_in_town)  )
				{
					adresaPreuzimanja=true;
					break;
				}
			}
		}
		
		for (int i=0; i<result.size(); i++) {
			RentacarServis rentacar = result.get(i);
			for (Iterator<Filijala> iteratorFilijala = rentacar.getFilijale().iterator(); iteratorFilijala.hasNext();)
			{
				Filijala filijala = (Filijala) iteratorFilijala.next();
				System.out.println("[RentacarController: pretraga] MESTO FILIJALE: " + filijala.getAdresa().getGrad().getNaziv());
				if(filijala.getAdresa().getGrad().getNaziv().toLowerCase().equals(check_out_town) ||
						rentacar.getAdresa().getGrad().getNaziv().toLowerCase().equals(check_out_town) )
				{
					adresaVracanja=true;
					break;
				}
			}
		}
		
		System.out.println("[RentacarController: pretraga] mesto preuzimanja: " + adresaPreuzimanja + " mesto vracanja: " + adresaVracanja);
		boolean postojiTerminVozilo;
		boolean postojiVozilo;
		
		
	 if(adresaPreuzimanja==true && adresaVracanja== true)	{
		for (int i=0; i<result.size(); i++) {
			RentacarServis rentacar = result.get(i);
			postojiVozilo = false;
			System.out.println("[RentacarController: pretraga]: iteracija pomocu iteratora; naziv rentacara: " + rentacar.getNaziv());
		for (Iterator<Filijala> iteratorFilijala = rentacar.getFilijale().iterator(); iteratorFilijala.hasNext();)
		{
				Filijala filijala = (Filijala) iteratorFilijala.next();
			
					for(Iterator<Vozilo> iteratorVozilo = filijala.getVozila().iterator(); iteratorVozilo.hasNext();) {
						System.out.println("[RentacarController: pretraga]: usao u for za prolazak kroz vozila; ima " + filijala.getVozila().size() + " vozila u filijali: " + filijala.getAdresa());
						Vozilo vozilo = (Vozilo) iteratorVozilo.next();
						postojiTerminVozilo = true;
						
						if(vozilo.getRezervacije().isEmpty()) {
							System.out.println("[RentacarController: pretraga]: vozilo nema rezervacija.");
							postojiTerminVozilo = true;
							postojiVozilo = true;
							//break;
						}else {
							for(Iterator<RezervacijaVozila> iteratorRezervacija = vozilo.getRezervacije().iterator(); iteratorRezervacija.hasNext();) {
								System.out.println("[HotelController: pretraga]: postoje rezervacije za vozilo; ima ih " + vozilo.getRezervacije().size() + ".");
								RezervacijaVozila rezervacijaVozilo = iteratorRezervacija.next();
								System.out.println("[RentacarController: pretraga]: zahtev:  datum preuzimanja: " + check_in + ", datum vracanja: " + check_out + ".");
								System.out.println("[RentacarController: pretraga]: rezervacija: " + rezervacijaVozilo.getId() + ", datum preuzimanja: " + rezervacijaVozilo.getDatum_preuzimanja() + ", datum vracanja: " + rezervacijaVozilo.getDatum_vracanja() + ".");
								System.out.println("[RentacarController: pretraga]: " + check_in.equals(rezervacijaVozilo.getDatum_preuzimanja()));
								System.out.println("[RentacarController: pretraga]: " + check_in.after(rezervacijaVozilo.getDatum_vracanja()));
								System.out.println("[RentacarController: pretraga]: " + check_in.before(rezervacijaVozilo.getDatum_vracanja()));
								if(rezervacijaVozilo.isAktivirana() && (check_in.equals(rezervacijaVozilo.getDatum_preuzimanja()) || check_in.equals(rezervacijaVozilo.getDatum_vracanja())
										|| check_out.equals(rezervacijaVozilo.getDatum_preuzimanja()) || 
										((rezervacijaVozilo.getDatum_preuzimanja()).after(check_in) && (rezervacijaVozilo.getDatum_preuzimanja()).before(check_out)) || 
										(check_in.after(rezervacijaVozilo.getDatum_preuzimanja()) && check_in.before(rezervacijaVozilo.getDatum_vracanja())) || 
										(check_out.after(rezervacijaVozilo.getDatum_preuzimanja()) && check_out.before(rezervacijaVozilo.getDatum_vracanja())))) {
									System.out.println("[RentacarController: pretraga]: vozilo je zauzeto u trazenom periodu.");
									iteratorVozilo.remove();
									postojiTerminVozilo = false;
									//break;
								}else {
									System.out.println("[RentacarController: pretraga]: vozilo je slobodno u trazenom periodu.");
								}
							}
							
							if(postojiTerminVozilo) {
								postojiVozilo = true;
								//break;
							}
						}
					
			}
			
		}
			
			if(!postojiVozilo) {
				result.remove(rentacar);
				i--;
			}
		}
		
		if(passengers != 0) {
			boolean postoji;
			
			for(int i=0; i<result.size(); i++) {
				postoji = false;
				RentacarServis rent = result.get(i);
				for (Iterator<Filijala> iteratorFilijala = rent.getFilijale().iterator(); iteratorFilijala.hasNext();)
				{
						Filijala filijala = (Filijala) iteratorFilijala.next();
						System.out.println("[RentacarController: pretraga] MESTO FILIJALE: " + filijala.getAdresa().getGrad().getNaziv());
					if(filijala.getAdresa().getGrad().getNaziv().toLowerCase().equals(check_in_town) || filijala.getAdresa().getGrad().getNaziv().toLowerCase().equals(check_out_town))
					{
				for(Iterator<Vozilo> iteratorVozilo = filijala.getVozila().iterator(); iteratorVozilo.hasNext();) {
					Vozilo vozilo = (Vozilo) iteratorVozilo.next();
						
						if(vozilo.getBroj_sedista() >= passengers) {
							postoji = true;
							System.out.println("[RentacarController: pretraga]:vozilo.getBroj_sedista() <= passengers ; vozilo.getBroj_sedista(): " + vozilo.getBroj_sedista() + ".");
							//break;
						}else {
							iteratorVozilo.remove();
							System.out.println("[RentacarController: pretraga]: uklanjanje vozila.");
							System.out.println("[RentacarController: pretraga]:vozilo.getBroj_sedista() < passengers ; vozilo.getBroj_sedista(): " + vozilo.getBroj_sedista() + ".");
						}
				}
					}
				}
				if(!postoji) {
					result.remove(rent);
					i--;
				}
			}
		}
	}
	 else
	 {
		result.clear(); 
	 }
		
		
		return result;
	}
	
	@RequestMapping(
			value = "/{id}/{check_in}/{check_in_town}/{check_out}/{check_out_town}/{passengers}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RentacarServis> getServis(@PathVariable("id") Integer id, @PathVariable("check_in") String check_in, @PathVariable("check_in_town") String check_in_town,  @PathVariable("check_out") String check_out, @PathVariable("check_out_town") String check_out_town, @PathVariable("passengers") int passengers) throws ParseException
	{
		System.out.println("usao u metodu api/rents/{id}/checkin/checkout");
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date_check_in = format.parse(check_in);
		Date date_check_out = format.parse(check_out);
		
		RentacarServis result = null;
		
		try{
			result = rentService.findById(id).get();
			System.out.println("Nasao rentacar sa id:" + result.getId());
		}catch(NoSuchElementException e)
		{
			System.out.println("Ne postoji rentacar sa id: " + id);
			return null; 	
		}
		
		
		if(check_in_town.equals("prazan") && check_out_town.equals("prazan"))
		{
			check_in_town=result.getAdresa().getGrad().getNaziv();
			check_out_town=result.getAdresa().getGrad().getNaziv();
		}
		result = pretraga_servisa(result, date_check_in, date_check_out,check_in_town, check_out_town, passengers);
		
		
		System.out.println("[RentacarController: getRentacar]: id pronadjenog servisa: " + result.getId());
		
		return new ResponseEntity<RentacarServis>(result, HttpStatus.OK);
	}
	
	public RentacarServis pretraga_servisa(RentacarServis servis,
			Date check_in, Date check_out, String check_in_town,
			String check_out_town, int passengers) {
		RentacarServis result = servis;
		check_in_town=check_in_town.toLowerCase();
		check_out_town=check_out_town.toLowerCase();
		
		System.out.println("[RentacarController: pretraga_servisa]: iteracija pomocu iteratora; naziv servisa: " + servis.getNaziv());
		boolean adresaPreuzimanja = false;
		boolean adresaVracanja = false;
		
			for (Iterator<Filijala> iteratorFilijala = servis.getFilijale().iterator(); iteratorFilijala.hasNext();)
			{
				Filijala filijala = (Filijala) iteratorFilijala.next();
				System.out.println("[RentacarController: pretraga] MESTO FILIJALE: " + filijala.getAdresa().getGrad().getNaziv());
				if(filijala.getAdresa().getGrad().getNaziv().toLowerCase().equals(check_in_town) ||
						servis.getAdresa().getGrad().getNaziv().toLowerCase().equals(check_in_town)  )
				{
					adresaPreuzimanja=true;
					break;
				}
			}
		
		
		
			for (Iterator<Filijala> iteratorFilijala = servis.getFilijale().iterator(); iteratorFilijala.hasNext();)
			{
				Filijala filijala = (Filijala) iteratorFilijala.next();
				System.out.println("[RentacarController: pretraga] MESTO FILIJALE: " + filijala.getAdresa().getGrad().getNaziv());
				if(filijala.getAdresa().getGrad().getNaziv().toLowerCase().equals(check_out_town) ||
						servis.getAdresa().getGrad().getNaziv().toLowerCase().equals(check_out_town) )
				{
					adresaVracanja=true;
					break;
				}
			}
			
	if(adresaPreuzimanja==true && adresaVracanja==true)
	{
		for(Iterator<Filijala> iteratorFil = servis.getFilijale().iterator(); iteratorFil.hasNext();)
		{
			Filijala filijala = iteratorFil.next();
		for(Iterator<Vozilo> iteratorVozilo = filijala.getVozila().iterator(); iteratorVozilo.hasNext();) {
			System.out.println("[RentacarController: pretraga_servisa]: usao u for za prolazak kroz vozila; ima " + filijala.getVozila().size() + " vozila.");
			Vozilo vozilo = iteratorVozilo.next();
			
			if(isBrzaVozilo(vozilo, check_in, check_out)) {
				System.out.println("[RentacarController: pretraga_servisa]: vozilo sa id: " + vozilo.getId() + " je brza rez.");
				iteratorVozilo.remove();
			}else {
				if(vozilo.getRezervacije().isEmpty()) {
					System.out.println("[RentacarController: pretraga_servisa]: vozilo nema rezervacija.");
				}else {
					System.out.println("[RentacarController: pretraga_servisa]: postoje rezervacije za vozilo; ima ih " + vozilo.getRezervacije().size() + ".");
					for(Iterator<RezervacijaVozila> iteratorRezervacija = vozilo.getRezervacije().iterator(); iteratorRezervacija.hasNext();) {
						RezervacijaVozila rezervacijaVozila = iteratorRezervacija.next();
						System.out.println("RentacarController: pretraga_servisa]: zahtev:  datum dolaska: " + check_in + ", datum odlaska: " + check_out + ".");
						
						if(rezervacijaVozila.isAktivirana() && (check_in.equals(rezervacijaVozila.getDatum_preuzimanja()) || check_in.equals(rezervacijaVozila.getDatum_vracanja()) || check_out.equals(rezervacijaVozila.getDatum_preuzimanja()) || ((rezervacijaVozila.getDatum_preuzimanja()).after(check_in) && (rezervacijaVozila.getDatum_preuzimanja()).before(check_out)) || (check_in.after(rezervacijaVozila.getDatum_preuzimanja()) && check_in.before(rezervacijaVozila.getDatum_vracanja())) || (check_out.after(rezervacijaVozila.getDatum_preuzimanja()) && check_out.before(rezervacijaVozila.getDatum_vracanja())))) {
							System.out.println("[RentacarController: pretraga_servisa]: vozilo je zauzeto u trazenom periodu.");
							iteratorVozilo.remove();
						}else {
							System.out.println("[RentacarController: pretraga_servisa]: vozilo je slobodno u trazenom periodu.");
						}
					}
				}
			}
		}
		
		if(passengers != 0) {
			
			for(Iterator<Vozilo> iteratorVozilo = filijala.getVozila().iterator(); iteratorVozilo.hasNext();) {
				Vozilo vozilo = (Vozilo) iteratorVozilo.next();
				if(vozilo.getBroj_sedista() >= passengers) {
					System.out.println("[RentacarController: pretraga_servisa]: broj sedista: " + vozilo.getBroj_sedista() + ".");
				}else {
					iteratorVozilo.remove();
					System.out.println("[RentacarController: pretraga_servisa]: uklanjanje vozila.");
					}
			}
		}
		}
		
	}	
		
		return result;
	}

	@RequestMapping(
			value = "/get_number_of_days/{check_in}/{check_out}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> getNumberOfDaysCar(@PathVariable("check_in") String check_in, @PathVariable("check_out") String check_out) throws ParseException
	{
		System.out.println("RentacarController: getNumberOfDays]: usao u metodu getnumberOfDays");
		//RezervacijaHotel rezervacijaHotel = (RezervacijaHotel) request.getSession().getAttribute("rezervacijaHotel");
		int result;
		
		if(check_in.equals("0001-01-01") || check_out.equals("0001-01-01")) {
			System.out.println("REZULTAT JE NULA");
			result = 0;
		}else {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date_check_in = format.parse(check_in);
			Date date_check_out = format.parse(check_out);
			result = (int) Math.round((date_check_out.getTime() - date_check_in.getTime()) / (double) 86400000) + 1;
		}
		System.out.println("[RentacarController: getNumberOfDays]: numberOfDays: " + result);
		
		return new ResponseEntity<Integer>(result, HttpStatus.OK);
		
	}
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/book_car/{vozilo_id}/{id_rez}",
			method = RequestMethod.PUT,
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Vozilo> bookCar(@PathVariable("vozilo_id") Integer vozilo_id, @RequestBody RezervacijaVozila rezervacijaVozila, @PathVariable("id_rez") Integer id_rez) throws ParseException{
		
		//RezervacijaHotel rezervacijaHotel = (RezervacijaHotel) request.getSession().getAttribute("rezervacijaHotel");
		Vozilo vozilo =  voziloService.findById(vozilo_id).get();
		rezervacijaVozila.setVozilo(vozilo);
		
		rezervacijaVozila.setAktivirana(false);
		
		Date date_check_in = rezervacijaVozila.getDatum_preuzimanja();
		Date date_check_out = rezervacijaVozila.getDatum_vracanja();
		int broj_dana = (int) Math.round((date_check_out.getTime() - date_check_in.getTime()) / (double) 86400000) + 1;
		double cena_rez = broj_dana * vozilo.getCena_dan();
		
		rezervacijaVozila.setUkupna_cena(cena_rez);
		System.out.println("REZ: " + rezervacijaVozila.getId() + " " + rezervacijaVozila.getBroj_putnika() + " " + rezervacijaVozila.getUkupna_cena()
				+ " " + rezervacijaVozila.getDatum_preuzimanja() + " " + rezervacijaVozila.getDatum_rezervacije() + 
				" " + rezervacijaVozila.getDatum_vracanja() + " " + rezervacijaVozila.getMesto_preuzimanja().getNaziv() + " "
				+ rezervacijaVozila.getMesto_vracanja().getNaziv() + " " + rezervacijaVozila.getVozilo().getModel());
		
		rezervacijaVozila = rezervacijaVozilaService.save(rezervacijaVozila);
		rezervacijaVozilaService.insertRezervacijaVozila(rezervacijaVozila.getId(), vozilo.getId());
		rezervacijaService.updateRezVozila(rezervacijaVozila.getId(), id_rez);
		Rezervacija rez = rezervacijaService.findById(id_rez).get();
		Date datum_rez = rez.getDatumRezervacije();
		rezervacijaVozilaService.updateDatumRez(datum_rez, rezervacijaVozila.getId());
		double cena = rez.getCena();
		cena+=rezervacijaVozila.getUkupna_cena();
		cena = cena - (cena)*0.05;
		rezervacijaService.updateCenaRez(cena, rez.getId());
		
		return new ResponseEntity<Vozilo>(vozilo, HttpStatus.OK);
	}
	
	@RequestMapping(
			value = "/rezervacija/{check_in}/{check_in_town}/{check_out}/{check_out_town}/{passengers}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RezervacijaVozila> getRezervacija(@PathVariable("check_in") String check_in, @PathVariable("check_in_town") String check_in_town,@PathVariable("check_out") String check_out, @PathVariable("check_out_town") String check_out_town, @PathVariable("passengers") int passengers) throws ParseException{
		if(passengers <= 0)
			passengers=1;
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date_check_in = format.parse(check_in);
		Date date_check_out = format.parse(check_out);
		
		RezervacijaVozila rezervacijavozila = new RezervacijaVozila();
		rezervacijavozila.setDatum_preuzimanja(date_check_in);
		rezervacijavozila.setDatum_vracanja(date_check_out);
		rezervacijavozila.setBrza(false);
		Grad grad_preuzimanja = gradService.findByNaziv(check_in_town);
		Grad grad_vracanja = gradService.findByNaziv(check_out_town);
		
		rezervacijavozila.setMesto_preuzimanja(grad_preuzimanja);
		rezervacijavozila.setMesto_vracanja(grad_vracanja);
		rezervacijavozila.setBroj_putnika(passengers);
		
		rezervacijavozila.setAktivirana(false);
		
		//rezervacijaHotel = rezervacijaHotelService.save(rezervacijaHotel);
			
		return new ResponseEntity<RezervacijaVozila>(rezervacijavozila, HttpStatus.OK);
	}
	
	@RequestMapping(
			value = "specialPrice/{id}/{check_in}/{check_in_town}/{check_out}/{check_out_town}/{passengers}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<Vozilo>> getVoziloSpecialPrice(@PathVariable("id") Integer id, @PathVariable("check_in") String check_in, @PathVariable("check_in_town") String check_in_town, @PathVariable("check_out") String check_out, @PathVariable("check_out_town") String check_out_town, @PathVariable("passengers") int passengers) throws ParseException
	{
		System.out.println("usao u metodu api/rents/specialPrice/{id}, id: " + id + "mesto : " + check_in_town + " " + check_out_town );
		if(passengers <= 0)
			passengers=1;
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date_check_in = format.parse(check_in);
		Date date_check_out = format.parse(check_out);
		
		RentacarServis servis = null;
		
		try{
			servis = rentService.findById(id).get();
			System.out.println("[RentacarController: getVoziloSpecialPrice]: Nasao servis sa id:" + servis.getId());
		}catch(NoSuchElementException e)
		{
			System.out.println("Ne postoji hotel sa id: " + id);
			return null; 	
		}
		
		check_in_town=check_in_town.toLowerCase();
		check_out_town=check_out_town.toLowerCase();
		
		boolean adresaPreuzimanja = false;
		boolean adresaVracanja = false;
		
			for (Iterator<Filijala> iteratorFilijala = servis.getFilijale().iterator(); iteratorFilijala.hasNext();)
			{
				Filijala filijala = (Filijala) iteratorFilijala.next();
				System.out.println("[RentacarController: pretraga] MESTO FILIJALE: " + filijala.getAdresa().getGrad().getNaziv());
				if(filijala.getAdresa().getGrad().getNaziv().toLowerCase().equals(check_in_town) ||
						servis.getAdresa().getGrad().getNaziv().toLowerCase().equals(check_in_town)  )
				{
					adresaPreuzimanja=true;
					break;
				}
			}
		
		
		
			for (Iterator<Filijala> iteratorFilijala = servis.getFilijale().iterator(); iteratorFilijala.hasNext();)
			{
				Filijala filijala = (Filijala) iteratorFilijala.next();
				System.out.println("[RentacarController: pretraga] MESTO FILIJALE: " + filijala.getAdresa().getGrad().getNaziv());
				if(filijala.getAdresa().getGrad().getNaziv().toLowerCase().equals(check_out_town) ||
						servis.getAdresa().getGrad().getNaziv().toLowerCase().equals(check_out_town) )
				{
					adresaVracanja=true;
					break;
				}
			}
		
		
		System.out.println("[RentacarController: pretraga] mesto preuzimanja: " + adresaPreuzimanja + " mesto vracanja: " + adresaVracanja);
		
		Collection<Vozilo> result = new ArrayList<Vozilo>();
		boolean postojiPopust, postojiTerminVozilo;
		if(adresaPreuzimanja==true && adresaVracanja== true)
		{
			
				for(Iterator<Filijala> iteratorFil = servis.getFilijale().iterator(); iteratorFil.hasNext();)
				{
					Filijala filijala = iteratorFil.next();
					for(Iterator<Vozilo> iteratorVozilo = filijala.getVozila().iterator(); iteratorVozilo.hasNext();) {
					postojiPopust = false;
					postojiTerminVozilo = true;
					Vozilo vozilo = iteratorVozilo.next();
					for(Iterator<Popust> iteratorPopust = vozilo.getPopusti().iterator(); iteratorPopust.hasNext();) {
						Popust popust = iteratorPopust.next();
						if((date_check_in.equals(popust.getPocetak_vazenja()) || date_check_in.after(popust.getPocetak_vazenja())) && (date_check_out.equals(popust.getKraj_vazenja()) || date_check_out.before(popust.getKraj_vazenja()))) {
							System.out.println("[RentacarController: getVoziloSpecialPrice]: vozilo sa id: " + vozilo.getId() + " je na popustu u trazenom periodu.");
							postojiPopust = true;
							break;
						}else {
							System.out.println("[RentacarController: getVoziloSpecialPrice]: vozilo sa id: " + vozilo.getId() + " nije na popustu u trazenom periodu.");
						}
					}
					if(postojiPopust) {
						for(Iterator<RezervacijaVozila> iteratorRezervacija = vozilo.getRezervacije().iterator(); iteratorRezervacija.hasNext();) {
							RezervacijaVozila rezervacijaVozila = iteratorRezervacija.next();
							System.out.println("[RentacarController: getVoziloSpecialPrice]: zahtev:  datum dolaska: " + check_in + ", datum odlaska: " + check_out + ".");
							
							if(rezervacijaVozila.isAktivirana() && (date_check_in.equals(rezervacijaVozila.getDatum_preuzimanja()) || date_check_in.equals(rezervacijaVozila.getDatum_vracanja()) || date_check_out.equals(rezervacijaVozila.getDatum_preuzimanja()) || ((rezervacijaVozila.getDatum_preuzimanja()).after(date_check_in) && (rezervacijaVozila.getDatum_preuzimanja()).before(date_check_out)) || (date_check_in.after(rezervacijaVozila.getDatum_preuzimanja()) && date_check_in.before(rezervacijaVozila.getDatum_vracanja())) || (date_check_out.after(rezervacijaVozila.getDatum_preuzimanja()) && date_check_out.before(rezervacijaVozila.getDatum_vracanja())))) {
								System.out.println("[RentacarController: getVoziloSpecialPrice]: vozilo je zauzeto u trazenom periodu.");
								postojiTerminVozilo = false;
								break;
							}else {
								System.out.println("[RentacarController: getVoziloSpecialPrice]: vozilo je slobodno u trazenom periodu.");
							}
						}
						
						if(postojiTerminVozilo) {
							if(vozilo.getBroj_sedista() >= passengers) {
								System.out.println("[RentacarController: getVoziloSpecialPrice]: odgovara zbog broja osoba; dodato vozilo sa id:" + vozilo.getId());
								result.add(vozilo);
							}else {
								System.out.println("[RentacarController: getVoziloSpecialPrice]: vozilo sa id:" + vozilo.getId() + " ne odgovara zbog broja osoba");
							}
							
						}
					}
				}
				
				}
		}
		
		return new ResponseEntity<Collection<Vozilo>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(
			value = "/popust/{vozilo_id}/{check_in}/{check_out}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Popust> getPopustVozilo(@PathVariable("vozilo_id") Integer vozilo_id, @PathVariable("check_in") String check_in_string, @PathVariable("check_out") String check_out_string) throws ParseException {
		System.out.println("[RentacarController: getPopust]: usao u metodu getPopust");
		
		Vozilo vozilo =  voziloService.findById(vozilo_id).get();
		Popust result = null;
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date check_in = format.parse(check_in_string);
		Date check_out = format.parse(check_out_string);
		
		for(Iterator<Popust> iteratorPopusta = vozilo.getPopusti().iterator(); iteratorPopusta.hasNext();) {
			Popust popust = iteratorPopusta.next();
			if((check_in.equals(popust.getPocetak_vazenja()) || check_in.after(popust.getPocetak_vazenja())) && (check_out.equals(popust.getKraj_vazenja()) || check_out.before(popust.getKraj_vazenja()))) {
			//if(check_in.equals(popust.getPocetak_vazenja()) || check_in.equals(popust.getKraj_vazenja()) || check_out.equals(popust.getPocetak_vazenja()) || ((popust.getPocetak_vazenja()).after(check_in) && (popust.getPocetak_vazenja()).before(check_out)) || (check_in.after(popust.getPocetak_vazenja()) && check_in.before(popust.getKraj_vazenja())) || (check_out.after(popust.getPocetak_vazenja()) && check_out.before(popust.getKraj_vazenja()))) {
				result = popust;
				System.out.println("[RentacarController: getPopust]: vozilo je na popustu u trazenom periodu, popust id: " + popust.getId());
				break;
			}else {
				System.out.println("[RentacarController: isBrzaVozilo]: vozilo nije na popustu u trazenom periodu.");
			}
		}

		
		return new ResponseEntity<Popust>(result, HttpStatus.OK);
		
	}
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/book_car_special/{vozilo_id}/{check_in}/{check_in_town}/{check_out}/{check_out_town}/{passengers}/{id_rez}",
			method = RequestMethod.PUT,
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Vozilo> bookRoomSpecial(@PathVariable("vozilo_id") Integer vozilo_id, @PathVariable("check_in") String check_in_string, @PathVariable("check_in_town") String check_in_town, @PathVariable("check_out") String check_out_string,@PathVariable("check_out_town") String check_out_town, @PathVariable("passengers") int passengers, @RequestBody Popust popust, @PathVariable("id_rez") Integer id_rez) throws ParseException {
		
		
		Vozilo vozilo =  voziloService.findById(vozilo_id).get();
		
		System.out.println("[RentacarController: bookCarSpecial]: popust id: " + popust.getId());
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date check_in = format.parse(check_in_string);
		Date check_out = format.parse(check_out_string);
		
		RezervacijaVozila rezervacijaVozila = new RezervacijaVozila();
		rezervacijaVozila.setDatum_preuzimanja(check_in);;
		rezervacijaVozila.setDatum_vracanja(check_out);;
		int broj_dana = (int) Math.round((check_out.getTime() - check_in.getTime()) / (double) 86400000) + 1;
		Grad grad_preuzimanja = gradService.findByNaziv(check_in_town);
		Grad grad_vracanja = gradService.findByNaziv(check_out_town);
		
		rezervacijaVozila.setMesto_preuzimanja(grad_preuzimanja);
		rezervacijaVozila.setMesto_vracanja(grad_vracanja);
		rezervacijaVozila.setBroj_putnika(passengers);
		rezervacijaVozila.setBrza(true);
		rezervacijaVozila.setPopust(popust.getPopust());
		rezervacijaVozila.setAktivirana(false);
		rezervacijaVozila.setVozilo(vozilo);
		double cena_rez = broj_dana * (vozilo.getCena_dan() * 0.01 * (100 - popust.getPopust()));
		
		System.out.println("[HotelController: bookRoomSpecial]: U popustu se nalaze sledece usluge: ");
		
		
		rezervacijaVozila.setUkupna_cena(cena_rez);
		rezervacijaVozila = rezervacijaVozilaService.save(rezervacijaVozila);
		rezervacijaVozilaService.insertRezervacijaVozila(rezervacijaVozila.getId(), vozilo.getId());
		rezervacijaService.updateRezVozila(rezervacijaVozila.getId(), id_rez);
		Rezervacija rez = rezervacijaService.findById(id_rez).get();
		Date datum_rez = rez.getDatumRezervacije();
		rezervacijaVozilaService.updateDatumRez(datum_rez, rezervacijaVozila.getId());
		double cena = rez.getCena();
		cena+=rezervacijaVozila.getUkupna_cena();
		cena = cena - (cena)*0.05;
		rezervacijaService.updateCenaRez(cena, rez.getId());
		
		
		return new ResponseEntity<Vozilo>(vozilo, HttpStatus.OK);
	}

	public boolean isBrzaVozilo(Vozilo vozilo, Date check_in, Date check_out) {
		
		boolean result = false;
		
		System.out.println("[RentacarController: isBrzaVozilo]: postoje popusti za vozilo; ima ih " + vozilo.getPopusti().size() + ".");
		for(Iterator<Popust> iteratorPopusta = vozilo.getPopusti().iterator(); iteratorPopusta.hasNext();) {
			Popust popust = iteratorPopusta.next();

			if(check_in.equals(popust.getPocetak_vazenja()) || check_in.equals(popust.getKraj_vazenja()) || check_out.equals(popust.getPocetak_vazenja()) || ((popust.getPocetak_vazenja()).after(check_in) && (popust.getPocetak_vazenja()).before(check_out)) || (check_in.after(popust.getPocetak_vazenja()) && check_in.before(popust.getKraj_vazenja())) || (check_out.after(popust.getPocetak_vazenja()) && check_out.before(popust.getKraj_vazenja()))) {

				System.out.println("[RentacarController: isBrzaVozilo]: vozilo je na popustu u trazenom periodu.");
				result = true;
				break;
			}else {
				System.out.println("[RentacarController: isBrzaVozilo]: vozilo nije na popustu u trazenom periodu.");
			}
		}
		
		return result;
	}
	
	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(
			value = "/admin/izmenaRent/{id}/{naziv}/{ulica}/{broj}/{grad}/{opis}/{latitude}/{longitude}",
			method = RequestMethod.POST,
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RentacarServis> editServis(@PathVariable("id") Integer id, @PathVariable("naziv") String naziv,
			@PathVariable("ulica") String ulica, @PathVariable("broj") String broj, @PathVariable("grad") String grad_str, @PathVariable("opis") String opis,
			 @PathVariable("latitude") double latitude,  @PathVariable("longitude") double longitude)
	{
		System.out.println("[RentacarControler]: editServis");
		RentacarServis ser = rentService.findById(id).get();
		System.out.println("[RentacarControler]: servis_id: " + ser.getId());
		Grad grad = null;
		grad = gradService.findByNaziv(grad_str);
		if(grad==null)
		{
			grad=new Grad();
			grad.setNaziv(grad_str);
			gradService.save(grad);
		}
		System.out.println("[RentacarControler]: grad: " + grad.getNaziv());
		Adresa adr = null;
		List<Adresa> adrese = addressService.checkAddress(grad.getId(), ulica, broj);
		
		if(adrese.isEmpty())
		{
			adr = new Adresa();
			adr.setGrad(grad);
			adr.setUlica(ulica);
			adr.setBroj(broj);
			adr.setLatitude(latitude);
			adr.setLongitude(longitude);
			addressService.save(adr);
		}
		else
		{
			adr = adrese.get(0);
			addressService.updateAdresa(ulica, broj, grad.getId(), adr.getId());
		}
		System.out.println("[RentacarControler]: adr: " + adr.getId() + " " + adr.getUlica() + " " + adr.getBroj());
		rentService.updateServis(naziv, adr.getId(), opis, ser.getId());
		return  new ResponseEntity<RentacarServis>(ser, HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(
			value = "/sveRezervacijeVozila/total/{id_servisa}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public double getAllRezAndTotal(@PathVariable("id_servisa") Integer id) throws ParseException{
		
		RentacarServis rent = rentService.findById(id).get();
		
		ArrayList<Integer> vozilaSer = new ArrayList<Integer>();
		for (Iterator<Filijala> iteratorFil = rent.getFilijale().iterator(); iteratorFil.hasNext();)
		{
			Filijala filijala = iteratorFil.next();
			for(Iterator<Vozilo> iteratorVoz = filijala.getVozila().iterator(); iteratorVoz.hasNext();)
			{
				Vozilo v = iteratorVoz.next();
				vozilaSer.add(v.getId());
			}
		}
		
		List<RezervacijaVozila> sve_rez = rezervacijaVozilaService.getAll();
		List<RezervacijaVozila> rez = new ArrayList<RezervacijaVozila>();
		for(Iterator<RezervacijaVozila> iteratorSveRez = sve_rez.iterator(); iteratorSveRez.hasNext();)
		{
			RezervacijaVozila rv = iteratorSveRez.next();
			for(int i = 0; i<vozilaSer.size();i++)
			{
				if(rv.getVozilo().getId()==vozilaSer.get(i))
				{
					rez.add(rv);
				}
			}
		}
		
		System.out.println("Rezervacija ima: " + rez.size());
		
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today_str = format.format(new Date());
		Date today = format.parse(today_str);
		System.out.println("DANASNJI DATUM: " + today);
		
		
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -90);
		String today90_str = format.format(cal.getTime());
		Date today90 = format.parse(today90_str);
		System.out.println("DATUM PRE TRI MESECA: " + today90);
		double total=0;
		for(Iterator<RezervacijaVozila> iteratorRez = rez.iterator(); iteratorRez.hasNext();)
		{
			RezervacijaVozila rv2 = iteratorRez.next();
			System.out.println("REZ " + rv2.getId() + " ima ukupnu cenu: " + rv2.getUkupna_cena());
			System.out.println("DATUM Vracanja: " + rv2.getDatum_vracanja());
			System.out.println("DATUM VRACANJA JE PRE 3 MESECA " + today90.equals(rv2.getDatum_vracanja()));
			System.out.println("DATUM VRACANJA JE DANAS " + today.equals(rv2.getDatum_vracanja()));
			
			if(today90.equals(rv2.getDatum_vracanja()) || (today90.before(rv2.getDatum_vracanja()) && today.after(rv2.getDatum_vracanja()))
					|| today.equals(rv2.getDatum_vracanja()))
			{
				System.out.println("Datum vracanja je okej za rez: " + rv2.getId());
				total+=rv2.getUkupna_cena();
			}
			
		}
		
		return total;
	}
	
	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(
			value = "/last7days/{id_servisa}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public int[] getLast7days(@PathVariable("id_servisa") Integer id) throws ParseException{
		
		RentacarServis rent = rentService.findById(id).get();
		
		ArrayList<Integer> vozilaSer = new ArrayList<Integer>();
		for (Iterator<Filijala> iteratorFil = rent.getFilijale().iterator(); iteratorFil.hasNext();)
		{
			Filijala filijala = iteratorFil.next();
			for(Iterator<Vozilo> iteratorVoz = filijala.getVozila().iterator(); iteratorVoz.hasNext();)
			{
				Vozilo v = iteratorVoz.next();
				vozilaSer.add(v.getId());
			}
		}
		
		List<RezervacijaVozila> sve_rez = rezervacijaVozilaService.getAll();
		List<RezervacijaVozila> rez = new ArrayList<RezervacijaVozila>();
		for(Iterator<RezervacijaVozila> iteratorSveRez = sve_rez.iterator(); iteratorSveRez.hasNext();)
		{
			RezervacijaVozila rv = iteratorSveRez.next();
			for(int i = 0; i<vozilaSer.size();i++)
			{
				if(rv.getVozilo().getId()==vozilaSer.get(i))
				{
					rez.add(rv);
				}
			}
		}
		
		System.out.println("Rezervacija ima: " + rez.size());
		
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today_str = format.format(new Date());
		Date today = format.parse(today_str);
		System.out.println("DANASNJI DATUM: " + today);
		
		
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -7);
		String today6_str = format.format(cal.getTime());
		Date today6 = format.parse(today6_str);
		System.out.println("DATUM6: " + today6);
		
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -6);
		String today5_str = format.format(cal.getTime());
		Date today5 = format.parse(today5_str);
		System.out.println("DATUM5: " + today5);
		
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -5);
		String today4_str = format.format(cal.getTime());
		Date today4 = format.parse(today4_str);
		System.out.println("DATUM4: " + today4);
		
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -4);
		String today3_str = format.format(cal.getTime());
		Date today3 = format.parse(today3_str);
		System.out.println("DATUM3: " + today3);
		
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -3);
		String today2_str = format.format(cal.getTime());
		Date today2 = format.parse(today2_str);
		System.out.println("DATUM2: " + today2);
		
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -2);
		String today1_str = format.format(cal.getTime());
		Date today1 = format.parse(today1_str);
		System.out.println("DATUM1: " + today1);
		
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		String today0_str = format.format(cal.getTime());
		Date today0 = format.parse(today0_str);
		System.out.println("DATUM0: " + today0);
		
		
		int day7 = 0,day6 =0 ,day5 = 0,day4 = 0,day3 = 0,day2 = 0,day1 = 0;
		
		for(Iterator<RezervacijaVozila> iteratorRez = rez.iterator(); iteratorRez.hasNext();)
		{
			RezervacijaVozila rv2 = iteratorRez.next();
			
			if(today6.equals(rv2.getDatum_vracanja()) || (today6.before(rv2.getDatum_vracanja()) && today6.after(rv2.getDatum_preuzimanja()))
					|| today6.equals(rv2.getDatum_preuzimanja()))
			{
				System.out.println("DAY7 REZ" + rv2.getId());
				day7+=1;
			}
			
			if(today5.equals(rv2.getDatum_vracanja()) || (today5.before(rv2.getDatum_vracanja()) && today5.after(rv2.getDatum_preuzimanja()))
					|| today5.equals(rv2.getDatum_preuzimanja()))
			{
				System.out.println("DAY6 REZ" + rv2.getId());
				day6+=1;
			}
			
			if(today4.equals(rv2.getDatum_vracanja()) || (today4.before(rv2.getDatum_vracanja()) && today4.after(rv2.getDatum_preuzimanja()))
					|| today4.equals(rv2.getDatum_preuzimanja()))
			{
				System.out.println("DAY5 REZ" + rv2.getId());
				day5+=1;
			}
			
			if(today3.equals(rv2.getDatum_vracanja()) || (today3.before(rv2.getDatum_vracanja()) && today3.after(rv2.getDatum_preuzimanja()))
					|| today3.equals(rv2.getDatum_preuzimanja()))
			{
				System.out.println("DAY4 REZ" + rv2.getId());
				day4+=1;
			}
			
			if(today2.equals(rv2.getDatum_vracanja()) || (today2.before(rv2.getDatum_vracanja()) && today2.after(rv2.getDatum_preuzimanja()))
					|| today2.equals(rv2.getDatum_preuzimanja()))
			{
				System.out.println("DAY3 REZ" + rv2.getId());
				day3+=1;
			}
			
			if(today1.equals(rv2.getDatum_vracanja()) || (today1.before(rv2.getDatum_vracanja()) && today1.after(rv2.getDatum_preuzimanja()))
					|| today1.equals(rv2.getDatum_preuzimanja()))
			{
				System.out.println("DAY2 REZ" + rv2.getId());
				day2+=1;
			}
			
			if(today0.equals(rv2.getDatum_vracanja()) || (today0.before(rv2.getDatum_vracanja()) && today0.after(rv2.getDatum_preuzimanja()))
					|| today0.equals(rv2.getDatum_preuzimanja()))
			{
				System.out.println("DAY1 REZ" + rv2.getId());
				day1+=1;
			}
			
		}
		
		int[] dani = new int[7]; 
		dani[0]=day7;
		System.out.println(dani[0]);
		dani[1]=day6;
		System.out.println(dani[1]);
		dani[2]=day5;
		System.out.println(dani[2]);
		dani[3]=day4;
		System.out.println(dani[3]);
		dani[4]=day3;
		System.out.println(dani[4]);
		dani[5]=day2;
		System.out.println(dani[5]);
		dani[6]=day1;
		System.out.println(dani[6]);
		
		return dani;
	}
	
	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(
			value = "/months/{id_servisa}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public int[] getGodisnji(@PathVariable("id_servisa") Integer id) throws ParseException{
		
		RentacarServis rent = rentService.findById(id).get();
		
		ArrayList<Integer> vozilaSer = new ArrayList<Integer>();
		for (Iterator<Filijala> iteratorFil = rent.getFilijale().iterator(); iteratorFil.hasNext();)
		{
			Filijala filijala = iteratorFil.next();
			for(Iterator<Vozilo> iteratorVoz = filijala.getVozila().iterator(); iteratorVoz.hasNext();)
			{
				Vozilo v = iteratorVoz.next();
				vozilaSer.add(v.getId());
			}
		}
		
		List<RezervacijaVozila> sve_rez = rezervacijaVozilaService.getAll();
		List<RezervacijaVozila> rez = new ArrayList<RezervacijaVozila>();
		for(Iterator<RezervacijaVozila> iteratorSveRez = sve_rez.iterator(); iteratorSveRez.hasNext();)
		{
			RezervacijaVozila rv = iteratorSveRez.next();
			for(int i = 0; i<vozilaSer.size();i++)
			{
				if(rv.getVozilo().getId()==vozilaSer.get(i))
				{
					rez.add(rv);
				}
			}
		}
		
		System.out.println("Rezervacija ima: " + rez.size());
		
		int year = Year.now().getValue();
		boolean prestupna=false;
		if(year%4==0)
		{
			prestupna=true;
		}
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = new GregorianCalendar();
		
		
		cal.set(year, 0, 1);
		String jan1_str = format.format(cal.getTime());
		Date jan1 = format.parse(jan1_str);
		System.out.println("JAN1 " + jan1);
		
		cal.set(year, 0, 31);
		String jan31_str = format.format(cal.getTime());
		Date jan31 = format.parse(jan31_str);
		System.out.println("JAN31 " + jan31);
		
		cal.set(year, 1, 1);
		String feb1_str = format.format(cal.getTime());
		Date feb1 = format.parse(feb1_str);
		System.out.println("FEB1 " + feb1);
		
		String febkraj_str="";
		if(prestupna)
		{
			cal.set(year, 1, 29);
			febkraj_str=format.format(cal.getTime());
		}
		else
		{
			cal.set(year, 1, 28);
			febkraj_str=format.format(cal.getTime());
		}
		Date febkraj = format.parse(febkraj_str);
		
		cal.set(year, 2, 1);
		String mart1_str = format.format(cal.getTime());
		Date mart1 = format.parse(mart1_str);
		
		cal.set(year, 2, 31);
		String mart31_str = format.format(cal.getTime());
		Date mart31 = format.parse(mart31_str);
		
		cal.set(year, 3, 1);
		String apr1_str = format.format(cal.getTime());
		Date apr1 = format.parse(apr1_str);
		
		cal.set(year, 3, 30);
		String apr30_str = format.format(cal.getTime());
		Date apr30 = format.parse(apr30_str);
		
		cal.set(year, 4, 1);
		String maj1_str = format.format(cal.getTime());
		Date maj1 = format.parse(maj1_str);
		
		cal.set(year, 4, 31);
		String maj31_str = format.format(cal.getTime());
		Date maj31 = format.parse(maj31_str);
		
		cal.set(year, 5, 1);
		String jun1_str = format.format(cal.getTime());
		Date jun1 = format.parse(jun1_str);
		System.out.println("JUN1 " + jun1);
		
		cal.set(year, 5, 30);
		String jun30_str = format.format(cal.getTime());
		Date jun30 = format.parse(jun30_str);
		
		cal.set(year, 6, 1);
		String jul1_str = format.format(cal.getTime());
		Date jul1 = format.parse(jul1_str);
		
		cal.set(year, 6, 31);
		String jul31_str = format.format(cal.getTime());
		Date jul31 = format.parse(jul31_str);
		
		cal.set(year, 7, 1);
		String avg1_str = format.format(cal.getTime());
		Date avg1 = format.parse(avg1_str);
		
		cal.set(year, 7, 31);
		String avg31_str = format.format(cal.getTime());
		Date avg31 = format.parse(avg31_str);
		
		cal.set(year, 8, 1);
		String sep1_str = format.format(cal.getTime());
		Date sep1 = format.parse(sep1_str);
		
		cal.set(year, 8, 30);
		String sep30_str = format.format(cal.getTime());
		Date sep30 = format.parse(sep30_str);
		System.out.println("SEP30 " + sep30);
		
		cal.set(year, 9, 1);
		String okt1_str = format.format(cal.getTime());
		Date okt1 = format.parse(okt1_str);
		
		cal.set(year, 9, 31);
		String okt31_str = format.format(cal.getTime());
		Date okt31 = format.parse(okt31_str);
		
		cal.set(year, 10, 1);
		String nov1_str = format.format(cal.getTime());
		Date nov1 = format.parse(nov1_str);
		
		cal.set(year, 10, 30);
		String nov30_str = format.format(cal.getTime());
		Date nov30 = format.parse(nov30_str);
		
		cal.set(year, 11, 1);
		String dec1_str = format.format(cal.getTime());
		Date dec1 = format.parse(dec1_str);
		
		cal.set(year, 11, 31);
		String dec31_str = format.format(cal.getTime());
		Date dec31 = format.parse(dec31_str);
	
		int jan = 0,feb =0 ,mart = 0,apr = 0,maj = 0,jun = 0,jul = 0, avg = 0, sep = 0, okt = 0, nov = 0, dec =0;
		
		for(Iterator<RezervacijaVozila> iteratorRez = rez.iterator(); iteratorRez.hasNext();)
		{
			RezervacijaVozila rv2 = iteratorRez.next();
			Calendar start = Calendar.getInstance();
			start.setTime(rv2.getDatum_preuzimanja());
			Calendar end = Calendar.getInstance();
			end.setTime(rv2.getDatum_vracanja());
			System.out.println("START: " + start.getTime() + " END: " + end.getTime());
			for(Date date = start.getTime(); !start.after(end); start.add(Calendar.DATE, 1), date = start.getTime())
			{
				
				if(jan1.equals(date) || jan31.equals(date) || (jan1.before(date) && jan31.after(date)))
				{
					jan+=1;
				}
				
				if(feb1.equals(date) || febkraj.equals(date) || (feb1.before(date) && febkraj.after(date)))
				{
					feb+=1;
				}
				
				if(mart1.equals(date) || mart31.equals(date) || (mart1.before(date) && mart31.after(date)))
				{
					mart+=1;
				}
				
				if(apr1.equals(date) || apr30.equals(date) || (apr1.before(date) && apr30.after(date)))
				{
					apr+=1;
				}
				
				if(maj1.equals(date) || maj31.equals(date) || (maj1.before(date) && maj31.after(date)))
				{
					maj+=1;
				}
				
				if(jun1.equals(date) || jun30.equals(date) || (jun1.before(date) && jun30.after(date)))
				{
					jun+=1;
				}
				
				if(jul1.equals(date) || jul31.equals(date) || (jul1.before(date) && jul31.after(date)))
				{
					jul+=1;
				}
				
				if(avg1.equals(date) || avg31.equals(date) || (avg1.before(date) && avg31.after(date)))
				{
					avg+=1;
				}
				
				if(sep1.equals(date) || sep30.equals(date) || (sep1.before(date) && sep30.after(date)))
				{
					sep+=1;
				}
				
				if(okt1.equals(date) || okt31.equals(date) || (okt1.before(date) && okt31.after(date)))
				{
					okt+=1;
				}
				
				if(nov1.equals(date) || nov30.equals(date) || (nov1.before(date) && nov30.after(date)))
				{
					nov+=1;
				}
				
				if(dec1.equals(date) || dec31.equals(date) || (dec1.before(date) && dec31.after(date)))
				{
					dec+=1;
				}	
				
				
			}
			
			
		}
		
		int[] meseci = new int[12]; 
		meseci[0]=jan;
		System.out.println(meseci[0]);
		meseci[1]=feb;
		System.out.println(meseci[1]);
		meseci[2]=mart;
		System.out.println(meseci[2]);
		meseci[3]=apr;
		System.out.println(meseci[3]);
		meseci[4]=maj;
		System.out.println(meseci[4]);
		meseci[5]=jun;
		System.out.println(meseci[5]);
		meseci[6]=jul;
		System.out.println(meseci[6]);
		meseci[7]=avg;
		System.out.println(meseci[7]);
		meseci[8]=sep;
		System.out.println(meseci[8]);
		meseci[9]=okt;
		System.out.println(meseci[9]);
		meseci[10]=nov;
		System.out.println(meseci[10]);
		meseci[11]=dec;
		System.out.println(meseci[11]);
		
		
		return meseci;
	}
	
	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(
			value = "/dnevni/{id_servisa}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public double getDnevni(@PathVariable("id_servisa") Integer id) throws ParseException{
		
		double ret=0;
		RentacarServis rent = rentService.findById(id).get();
		
		ArrayList<Integer> vozilaSer = new ArrayList<Integer>();
		for (Iterator<Filijala> iteratorFil = rent.getFilijale().iterator(); iteratorFil.hasNext();)
		{
			Filijala filijala = iteratorFil.next();
			for(Iterator<Vozilo> iteratorVoz = filijala.getVozila().iterator(); iteratorVoz.hasNext();)
			{
				Vozilo v = iteratorVoz.next();
				vozilaSer.add(v.getId());
			}
		}
		
		List<RezervacijaVozila> sve_rez = rezervacijaVozilaService.getAll();
		List<RezervacijaVozila> rez = new ArrayList<RezervacijaVozila>();
		for(Iterator<RezervacijaVozila> iteratorSveRez = sve_rez.iterator(); iteratorSveRez.hasNext();)
		{
			RezervacijaVozila rv = iteratorSveRez.next();
			for(int i = 0; i<vozilaSer.size();i++)
			{
				if(rv.getVozilo().getId()==vozilaSer.get(i))
				{
					rez.add(rv);
				}
			}
		}
		
		System.out.println("Rezervacija ima: " + rez.size());
		
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today_str = format.format(new Date());
		Date today = format.parse(today_str);
		System.out.println("DANASNJI DATUM: " + today);
		
		
		int broj_rez=0;
		for(Iterator<RezervacijaVozila> iteratorRez = rez.iterator(); iteratorRez.hasNext();)
		{
			RezervacijaVozila rv2 = iteratorRez.next();
			
			if(today.equals(rv2.getDatum_preuzimanja()) || (today.before(rv2.getDatum_vracanja()) && today.after(rv2.getDatum_preuzimanja()))
					|| today.equals(rv2.getDatum_vracanja()))
			{
				broj_rez+=1;
			}
			
		}
		System.out.println("BROJ REZ: " + broj_rez + " BROJ_VOZILA: " + vozilaSer.size());
		
		
		ret = (double)broj_rez/vozilaSer.size();
		ret = ret *100;
		System.out.println("Povratna vrednost: " + ret);
		
		return ret;
	}
	
	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(
			value = "/add_special_price/{id_vozilo}",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addSpecialPrice(@PathVariable("id_vozilo") Integer id_vozilo, @RequestBody Popust popust){
		
		Vozilo vozilo = voziloService.findById(id_vozilo).get();
		
		Date pocetak = popust.getPocetak_vazenja();
		Date kraj = popust.getKraj_vazenja();
		
		System.out.println("[HotelController: addSpecialPrice] popust - pocetak: " + pocetak + "; kraj: " + kraj);
		boolean postoji = false;
		
		for(Iterator<Popust> iteratorPopust = vozilo.getPopusti().iterator(); iteratorPopust.hasNext();) {
			Popust pronadjen = iteratorPopust.next();
			System.out.println("[HotelController: addSpecialPrice] pronadjen - pocetak: " + pronadjen.getPocetak_vazenja() + "; kraj: " + pronadjen.getKraj_vazenja());
			Date pronadjen_pocetak = pronadjen.getPocetak_vazenja();
			Date pronadjen_kraj = pronadjen.getKraj_vazenja();
			
			if(pocetak.equals(pronadjen_pocetak) || pocetak.equals(pronadjen_kraj) || kraj.equals(pronadjen_pocetak) || (pronadjen_pocetak.after(pocetak) && pronadjen_pocetak.before(kraj)) || (pocetak.after(pronadjen_pocetak) && pocetak.before(pronadjen_kraj)) || (kraj.after(pronadjen_pocetak) && kraj.before(pronadjen_kraj))) {
				postoji = true;
				break;
			}
		}

		Map<String, Object> result = new HashMap<>();
		
		if(!postoji) {
			Popust saved = popustService.save(popust);
			popustService.updateVozilo(id_vozilo, saved.getId());
			
			result.put("result", "success");
			result.put("popust_id", saved.getId());
		}else {
			result.put("result", "error");
		}
		
		return ResponseEntity.accepted().body(result);
	}
	
	@PreAuthorize("hasRole('RENTADMIN')")
	@RequestMapping(
			value = "/add_usluga_special_price/{popust_id}/{usluga_id}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addUslugaSpecialPrice(@PathVariable("popust_id") Integer popust_id, @PathVariable("usluga_id") Integer usluga_id){
		
		popustService.updateUsluga(popust_id, usluga_id);
		
		Map<String, Object> result = new HashMap<>();
		
		result.put("result", "success");
			
		return ResponseEntity.accepted().body(result);
	}
}
