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

import com.example.ISA_AMA_projekat.model.Hotel;
import com.example.ISA_AMA_projekat.model.Korisnik;
import com.example.ISA_AMA_projekat.model.Popust;
import com.example.ISA_AMA_projekat.model.Rezervacija;
import com.example.ISA_AMA_projekat.model.RezervacijaHotel;
import com.example.ISA_AMA_projekat.model.Soba;
import com.example.ISA_AMA_projekat.model.Usluga;
import com.example.ISA_AMA_projekat.service.HotelService;
import com.example.ISA_AMA_projekat.service.KorisnikService;
import com.example.ISA_AMA_projekat.service.PopustService;
import com.example.ISA_AMA_projekat.service.RezervacijaHotelService;
import com.example.ISA_AMA_projekat.service.RezervacijaService;
import com.example.ISA_AMA_projekat.service.SobaService;
import com.example.ISA_AMA_projekat.service.UslugaService;


@RestController
@RequestMapping(value="api/hotels")
public class HotelController {

	@Autowired
	private HotelService hotelService;
	
	@Autowired
	private RezervacijaHotelService rezervacijaHotelService;
	
	@Autowired
	private UslugaService uslugaService;
	
	@Autowired
	private SobaService sobaService;
	
	@Autowired
	private RezervacijaService rezervacijaService; 
	
	@Autowired
	private PopustService popustService;
	
	@Autowired
	private KorisnikService korisnikService;
	
	@RequestMapping(
			value = "/all",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<Hotel>> getHotels(){
		
		Collection<Hotel> hotels = hotelService.findAll();
		ArrayList<Hotel> sortirani = (ArrayList<Hotel>) hotels;
		sortirani.sort(Comparator.comparing(Hotel::getNaziv));
		hotels = sortirani;
		return new ResponseEntity<Collection<Hotel>>(hotels, HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('SYSADMIN')")
	@RequestMapping(
			value = "/all_admin",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<Hotel>> getHotelsForAdmin(){
		
		Collection<Hotel> hotels = hotelService.findAll();
		Collection<Hotel> result = new ArrayList<Hotel>();
		
		for(Iterator<Hotel> iteratorHotel = hotels.iterator(); iteratorHotel.hasNext();) {
			Hotel hotel = iteratorHotel.next();
			if(hotel.getId_admin() == null) {
				result.add(hotel);
			}
		}
		
		return new ResponseEntity<Collection<Hotel>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(
			value = "/search/{name_location}/{check_in}/{check_out}/{adults}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Hotel>> getHotelsSearch(@PathVariable("name_location") String name_location, @PathVariable("check_in") String check_in, @PathVariable("check_out") String check_out, @PathVariable("adults") int adults) throws ParseException{
		
		System.out.println("[HotelController: getHotelsSearch]: name_location-" + name_location + ", check_in-" + check_in + ", check_out-" + check_out + ", adults-" + adults);
		name_location = name_location.toLowerCase();
		String[] search = name_location.split(" ");
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date_check_in = format.parse(check_in);
		Date date_check_out = format.parse(check_out);
		
		List<Hotel> hotels = hotelService.search("%" + search[0] + "%");
		List<Hotel> hoteli = pretraga(hotels, search, date_check_in, date_check_out, adults);
		
			
		return new ResponseEntity<List<Hotel>>(hoteli, HttpStatus.OK);
	}
	
	@RequestMapping(
			value = "/rezervacija/{check_in}/{check_out}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RezervacijaHotel> getRezervacija(@PathVariable("check_in") String check_in, @PathVariable("check_out") String check_out) throws ParseException{
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date_check_in = format.parse(check_in);
		Date date_check_out = format.parse(check_out);
		
		RezervacijaHotel rezervacijaHotel = new RezervacijaHotel();
		rezervacijaHotel.setDatum_dolaska(date_check_in);
		rezervacijaHotel.setDatum_odlaska(date_check_out);
		rezervacijaHotel.setBrza(false);
		int broj_nocenja = (int) Math.round((date_check_out.getTime() - date_check_in.getTime()) / (double) 86400000) + 1;
		rezervacijaHotel.setBroj_nocenja(broj_nocenja);
		rezervacijaHotel.setAktivirana(false);
			
		return new ResponseEntity<RezervacijaHotel>(rezervacijaHotel, HttpStatus.OK);
	}
	
	@RequestMapping(
			value = "/{id}/{check_in}/{check_out}/{adults}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Hotel> getHotel(@PathVariable("id") Integer id, @PathVariable("check_in") String check_in, @PathVariable("check_out") String check_out, @PathVariable("adults") int adults) throws ParseException
	{
		System.out.println("usao u metodu api/hotels/{id}; adults: " + adults);
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date_check_in = format.parse(check_in);
		Date date_check_out = format.parse(check_out);
		
		Hotel result = null;
		
		try{
			result = hotelService.findById(id).get();
			System.out.println("Nasao hotel sa id:" + result.getId());
		}catch(NoSuchElementException e)
		{
			System.out.println("Ne postoji hotel sa id: " + id);
			return null; 	
		}
		
		result = pretraga_hotel(result, date_check_in, date_check_out, adults);
		
		System.out.println("[HotelController: getHotel]: id pronadjenog hotela: " + result.getId());
		
		return new ResponseEntity<Hotel>(result, HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(
			value = "/admin/{id_hotel}/{id_korisnik}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Hotel> getOneHotel(@PathVariable("id_hotel") Integer id_hotel, @PathVariable("id_korisnik") Integer id_korisnik){
		
		System.out.println("usao u metodu api/hotels/hotelAdmin/{id}; id: " + id_hotel);
		Hotel result = null;
		
		try{
			result = hotelService.findById(id_hotel).get();
			System.out.println("Nasao hotel sa id:" + result.getId());
		}catch(NoSuchElementException e)
		{
			System.out.println("Ne postoji hotel sa id: " + id_hotel);
			return null; 	
		}
		
		System.out.println("[HotelController: getHotel]: id pronadjenog hotela: " + result.getId());
		
		Korisnik korisnik = korisnikService.findById(id_korisnik).get();
		
		if(korisnik.getAdmin_id() != id_hotel) {
			System.out.println("Korisnik: " + korisnik.getId() + " nije admin ovog hotela: " + id_hotel);
			return null; 
		}
		
		return new ResponseEntity<Hotel>(result, HttpStatus.OK);
	}
	
	@RequestMapping(
			value = "specialPrice/{id}/{check_in}/{check_out}/{adults}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<Soba>> getHotelSpecialPrice(@PathVariable("id") Integer id, @PathVariable("check_in") String check_in, @PathVariable("check_out") String check_out, @PathVariable("adults") int adults) throws ParseException
	{
		System.out.println("usao u metodu api/hotels/specialPrice/{id}, id: " + id);
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date_check_in = format.parse(check_in);
		Date date_check_out = format.parse(check_out);
		
		Hotel hotel = null;
		
		try{
			hotel = hotelService.findById(id).get();
			System.out.println("[HotelController: getHotelSpecialPrice]: Nasao hotel sa id:" + hotel.getId());
		}catch(NoSuchElementException e)
		{
			System.out.println("Ne postoji hotel sa id: " + id);
			return null; 	
		}
		
		Collection<Soba> result = new ArrayList<Soba>();
		boolean postojiPopust, postojiTerminSoba;
		
		for(Iterator<Soba> iteratorSoba = hotel.getSobe().iterator(); iteratorSoba.hasNext();) {
			postojiPopust = false;
			postojiTerminSoba = true;
			Soba soba = iteratorSoba.next();
			for(Iterator<Popust> iteratorPopust = soba.getPopusti().iterator(); iteratorPopust.hasNext();) {
				Popust popust = iteratorPopust.next();
				if((date_check_in.equals(popust.getPocetak_vazenja()) || date_check_in.after(popust.getPocetak_vazenja())) && (date_check_out.equals(popust.getKraj_vazenja()) || date_check_out.before(popust.getKraj_vazenja()))) {
					System.out.println("[HotelController: getHotelSpecialPrice]: soba sa id: " + soba.getId() + " je na popustu u trazenom periodu.");
					postojiPopust = true;
					break;
				}else {
					System.out.println("[HotelController: getHotelSpecialPrice]: soba sa id: " + soba.getId() + " nije na popustu u trazenom periodu.");
				}
			}
			if(postojiPopust) {
				for(Iterator<RezervacijaHotel> iteratorRezervacija = soba.getRezervacije().iterator(); iteratorRezervacija.hasNext();) {
					RezervacijaHotel rezervacijaHotel = iteratorRezervacija.next();
					System.out.println("[HotelController: getHotelSpecialPrice]: zahtev:  datum dolaska: " + check_in + ", datum odlaska: " + check_out + ".");
					System.out.println("[HotelController: getHotelSpecialPrice]: rezervacija: " + rezervacijaHotel.getId() + ", datum dolaska: " + rezervacijaHotel.getDatum_dolaska() + ", datum odlaska: " + rezervacijaHotel.getDatum_odlaska() + ".");

					if(rezervacijaHotel.isAktivirana() && (date_check_in.equals(rezervacijaHotel.getDatum_dolaska()) || date_check_in.equals(rezervacijaHotel.getDatum_odlaska()) || date_check_out.equals(rezervacijaHotel.getDatum_dolaska()) || ((rezervacijaHotel.getDatum_dolaska()).after(date_check_in) && (rezervacijaHotel.getDatum_dolaska()).before(date_check_out)) || (date_check_in.after(rezervacijaHotel.getDatum_dolaska()) && date_check_in.before(rezervacijaHotel.getDatum_odlaska())) || (date_check_out.after(rezervacijaHotel.getDatum_dolaska()) && date_check_out.before(rezervacijaHotel.getDatum_odlaska())))) {
						System.out.println("[HotelController: getHotelSpecialPrice]: soba je zauzeta u trazenom periodu.");
						postojiTerminSoba = false;
						break;
					}else {
						System.out.println("[HotelController: getHotelSpecialPrice]: soba je slobodna u trazenom periodu.");
					}
				}
				
				if(postojiTerminSoba) {
					if(soba.getBroj_kreveta() <= adults) {
						System.out.println("[HotelController: getHotelSpecialPrice]: odgovara zbog broja osoba; dodata soba sa id:" + soba.getId());
						result.add(soba);
					}else {
						System.out.println("[HotelController: getHotelSpecialPrice]: soba sa id:" + soba.getId() + " ne odgovara zbog broja osoba");
					}
					
				}
			}
		}
		
		return new ResponseEntity<Collection<Soba>>(result, HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('SYSADMIN')")
	@RequestMapping(
			value = "/save",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Hotel> saveHotel(@RequestBody Hotel hotel) throws Exception{
		
		Hotel postoji = hotelService.findByNaziv(hotel.getNaziv());
		
		if(postoji != null) {
			System.out.println("Hotel sa ovim nazivom vec postoji u bazi");
			return new ResponseEntity<Hotel>(postoji, HttpStatus.CONFLICT);
		}else {
			Hotel saved = hotelService.save(hotel);
			return new ResponseEntity<Hotel>(saved, HttpStatus.CREATED);
		}
	}
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/add_usluga/{usluga_id}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<RezervacijaHotel> addUsluga(@PathVariable("usluga_id") Integer id, @RequestBody RezervacijaHotel rezervacijaHotela) throws java.sql.SQLIntegrityConstraintViolationException{
		
		Usluga usluga =  uslugaService.findById(id).get();
		rezervacijaHotela.getUsluge().add(usluga);
		
		return new ResponseEntity<RezervacijaHotel>(rezervacijaHotela, HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/remove_usluga/{usluga_id}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<RezervacijaHotel> removeUsluga(@PathVariable("usluga_id") Integer id, @RequestBody RezervacijaHotel rezervacijaHotela){
		
		Usluga usluga =  uslugaService.findById(id).get();
		rezervacijaHotela.getUsluge().remove(usluga);
		
		return new ResponseEntity<RezervacijaHotel>(rezervacijaHotela, HttpStatus.OK);
	}
	
	@RequestMapping(
			value = "/get_number_of_days/{check_in}/{check_out}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> getNumberOfDays(@PathVariable("check_in") String check_in, @PathVariable("check_out") String check_out) throws ParseException
	{
		System.out.println("[HotelController: getNumberOfDays]: usao u metodu getnumberOfDays");
		int result;
		
		if(check_in.equals("0001-01-01") || check_out.equals("0001-01-01")) {
			result = 0;
		}else {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date_check_in = format.parse(check_in);
			Date date_check_out = format.parse(check_out);
			result = (int) Math.round((date_check_out.getTime() - date_check_in.getTime()) / (double) 86400000) + 1;
		}
		System.out.println("[HotelController: getNumberOfDays]: numberOfDays: " + result);
		
		return new ResponseEntity<Integer>(result, HttpStatus.OK);
		
	}
	
	@RequestMapping(
			value = "/get_soba/{soba_id}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Soba> getSoba(@PathVariable("soba_id") Integer soba_id)
	{
		System.out.println("[HotelController: get_soba]: usao u metodu get_soba");
		Soba result = sobaService.findById(soba_id).get();
		
		
		return new ResponseEntity<Soba>(result, HttpStatus.OK);
		
	}
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/book_room/{soba_id}/{id_rez}",
			method = RequestMethod.PUT,
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Soba> bookRoom(@PathVariable("soba_id") Integer soba_id, @PathVariable("id_rez") Integer id_rez, @RequestBody RezervacijaHotel rezervacijaHotel){
		
		Soba soba =  sobaService.findById(soba_id).get();
		rezervacijaHotel.setSoba(soba);
		rezervacijaHotel.setAktivirana(false);
		
		Date date_check_in = rezervacijaHotel.getDatum_dolaska();
		Date date_check_out = rezervacijaHotel.getDatum_odlaska();
		int broj_dana = (int) Math.round((date_check_out.getTime() - date_check_in.getTime()) / (double) 86400000) + 1;
		
		double cena_rez = broj_dana * soba.getCena_nocenja();
		
		ArrayList<Usluga> usluge = new ArrayList<Usluga>();
		System.out.println("[HotelController: bookRoom]: U rezervaciji se nalaze sledece usluge: ");
		
		double popustUsluga = 0;
		
		for(Iterator<Usluga> iteratorUsluga = rezervacijaHotel.getUsluge().iterator(); iteratorUsluga.hasNext();) {
			Usluga usluga = iteratorUsluga.next();
			cena_rez += broj_dana * usluga.getCena();
			popustUsluga += usluga.getPopust();
			System.out.println("usluga: " + usluga.getNaziv());
			usluge.add(usluga);
			iteratorUsluga.remove();
		}
		
		cena_rez = cena_rez - (popustUsluga/100)*cena_rez;
		rezervacijaHotel.setUkupna_cena(cena_rez);
		rezervacijaHotel = rezervacijaHotelService.save(rezervacijaHotel);
		rezervacijaHotelService.insertRezervacijaSoba(rezervacijaHotel.getId(), soba.getId());
		
		rezervacijaService.updateRezHotel(rezervacijaHotel.getId(), id_rez);
		Rezervacija rezervacija = rezervacijaService.findById(id_rez).get();
		double cena = rezervacija.getCena() + rezervacijaHotel.getUkupna_cena() - (rezervacija.getCena() + rezervacijaHotel.getUkupna_cena())*0.05;
		rezervacijaService.updateCenaRez(cena, id_rez);
		Date datum_rez = rezervacija.getDatumRezervacije();
		rezervacijaHotelService.updateDatumRez(datum_rez, rezervacijaHotel.getId());
		
		for(int i=0; i<usluge.size(); i++) {
			Usluga usluga = usluge.get(i);
			System.out.println("[HotelController: bookRoom]: dodavanje usluge: " + usluga.getNaziv());
			try {
				rezervacijaHotelService.updateUsluga(usluga.getId(), rezervacijaHotel.getId());
				System.out.println("[HotelController: bookRoom]: usluga: " + usluga.getNaziv() + " dodata.");
			}catch(Exception e) {
				System.out.println("[HotelController: bookRoom]: exception kod dodavanja usluge: " + usluga.getNaziv());
			}
		}
		
		
		return new ResponseEntity<Soba>(soba, HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/book_room_special/{soba_id}/{check_in}/{check_out}/{rez_id}",
			method = RequestMethod.PUT,
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Soba> bookRoomSpecial(@PathVariable("soba_id") Integer soba_id, @PathVariable("check_in") String check_in_string, @PathVariable("check_out") String check_out_string, @PathVariable("rez_id") Integer rez_id, @RequestBody Popust popust) throws ParseException {
		
		Soba soba =  sobaService.findById(soba_id).get();
		
		System.out.println("[HotelController: bookRoomSpecial]: popust id: " + popust.getId());
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date check_in = format.parse(check_in_string);
		Date check_out = format.parse(check_out_string);
		
		RezervacijaHotel rezervacijaHotel = new RezervacijaHotel();
		rezervacijaHotel.setDatum_dolaska(check_in);
		rezervacijaHotel.setDatum_odlaska(check_out);
		int broj_nocenja = (int) Math.round((check_out.getTime() - check_in.getTime()) / (double) 86400000) + 1;
		rezervacijaHotel.setBroj_nocenja(broj_nocenja);
		rezervacijaHotel.setBrza(true);
		rezervacijaHotel.setPopust(popust.getPopust());
		rezervacijaHotel.setAktivirana(false);
		rezervacijaHotel.setSoba(soba);
		double cena_rez = broj_nocenja * (soba.getCena_nocenja() * 0.01 * (100 - popust.getPopust()));
		
		System.out.println("[HotelController: bookRoomSpecial]: U popustu se nalaze sledece usluge: ");
		for(Iterator<Usluga> iteratorUsluga = popust.getUsluge().iterator(); iteratorUsluga.hasNext();) {
			Usluga usluga = iteratorUsluga.next();
			cena_rez += broj_nocenja * usluga.getCena();
			System.out.println("usluga: " + usluga.getNaziv());
		}
		
		rezervacijaHotel.setUkupna_cena(cena_rez);
		rezervacijaHotel = rezervacijaHotelService.save(rezervacijaHotel);
		rezervacijaHotelService.insertRezervacijaSoba(rezervacijaHotel.getId(), soba.getId());
		
		rezervacijaService.updateRezHotel(rezervacijaHotel.getId(), rez_id);
		Rezervacija rezervacija = rezervacijaService.findById(rez_id).get();
		double cena = rezervacija.getCena() + rezervacijaHotel.getUkupna_cena() - (rezervacija.getCena() + rezervacijaHotel.getUkupna_cena())*0.05;
		rezervacijaService.updateCenaRez(cena, rez_id);
		Date datum_rez = rezervacija.getDatumRezervacije();
		rezervacijaHotelService.updateDatumRez(datum_rez, rezervacijaHotel.getId());
		
		for(Iterator<Usluga> iteratorUsluga = popust.getUsluge().iterator(); iteratorUsluga.hasNext();) {
			Usluga usluga = iteratorUsluga.next();
			System.out.println("[HotelController: bookRoomSpecial]: dodavanje usluge: " + usluga.getNaziv());
			try {
				rezervacijaHotelService.updateUsluga(usluga.getId(), rezervacijaHotel.getId());
				System.out.println("[HotelController: bookRoomSpecial]: usluga: " + usluga.getNaziv() + " dodata.");
			}catch(Exception e) {
				System.out.println("[HotelController: bookRoomSpecial]: exception kod dodavanja usluge: " + usluga.getNaziv());
			}
		}
		
		return new ResponseEntity<Soba>(soba, HttpStatus.OK);
	}
	
	@RequestMapping(
			value = "/popust/{soba_id}/{check_in}/{check_out}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Popust> getPopust(@PathVariable("soba_id") Integer soba_id, @PathVariable("check_in") String check_in_string, @PathVariable("check_out") String check_out_string) throws ParseException {
		System.out.println("[HotelController: getPopust]: usao u metodu getPopust");
		
		Soba soba =  sobaService.findById(soba_id).get();
		Popust result = null;
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date check_in = format.parse(check_in_string);
		Date check_out = format.parse(check_out_string);
		
		for(Iterator<Popust> iteratorPopusta = soba.getPopusti().iterator(); iteratorPopusta.hasNext();) {
			Popust popust = iteratorPopusta.next();
			if((check_in.equals(popust.getPocetak_vazenja()) || check_in.after(popust.getPocetak_vazenja())) && (check_out.equals(popust.getKraj_vazenja()) || check_out.before(popust.getKraj_vazenja()))) {
			//if(check_in.equals(popust.getPocetak_vazenja()) || check_in.equals(popust.getKraj_vazenja()) || check_out.equals(popust.getPocetak_vazenja()) || ((popust.getPocetak_vazenja()).after(check_in) && (popust.getPocetak_vazenja()).before(check_out)) || (check_in.after(popust.getPocetak_vazenja()) && check_in.before(popust.getKraj_vazenja())) || (check_out.after(popust.getPocetak_vazenja()) && check_out.before(popust.getKraj_vazenja()))) {
				result = popust;
				System.out.println("[HotelController: getPopust]: soba je na popustu u trazenom periodu, popust id: " + popust.getId());
				break;
			}else {
				System.out.println("[HotelController: isBrzaSoba]: soba nije na popustu u trazenom periodu.");
			}
		}

		
		return new ResponseEntity<Popust>(result, HttpStatus.OK);
		
	}
	
	public List<Hotel> pretraga(List<Hotel> hoteli, String[] search, Date check_in, Date check_out, int adults){
		List<Hotel> result = new ArrayList<Hotel>();
		
		for(int i=0; i<hoteli.size(); i++) {
			result.add(hoteli.get(i));
		}
		
		for(int j=0; j<hoteli.size(); j++) {
			System.out.println("[HotelController: pretraga]: naziv hotela: " + hoteli.get(j).getNaziv() + ", adresa hotela: " + hoteli.get(j).getAdresa().getGrad().getNaziv() + " " + hoteli.get(j).getAdresa().getUlica() + " " + hoteli.get(j).getAdresa().getBroj());
			for(int i=0; i<search.length; i++) {
				if(!(hoteli.get(j).getAdresa().getGrad().getNaziv().toLowerCase().contains(search[i]) || hoteli.get(j).getAdresa().getUlica().toLowerCase().contains(search[i]) || hoteli.get(j).getAdresa().getBroj().toLowerCase().contains(search[i]) || hoteli.get(j).getNaziv().toLowerCase().contains(search[i]))) {
					result.remove(hoteli.get(j));
					System.out.println("[HotelController: pretraga]: obrisan hotel sa nazivom - " + hoteli.get(j).getNaziv());
				}
			}
		}
		
		boolean postojiTerminSoba;
		boolean postojiSoba;
		
		for (int i=0; i<result.size(); i++) {
			Hotel hotel = result.get(i);
			postojiSoba = false;
			System.out.println("[HotelController: pretraga]: iteracija pomocu iteratora; naziv hotela: " + hotel.getNaziv());
			if(hotel.getSobe().size() != 0) {
				for(Iterator<Soba> iteratorSoba = hotel.getSobe().iterator(); iteratorSoba.hasNext();) {
					System.out.println("[HotelController: pretraga]: usao u for za prolazak kroz sobe; ima " + hotel.getSobe().size() + " soba.");
					Soba soba = (Soba) iteratorSoba.next();
					postojiTerminSoba = true;
					/*
					if(isBrzaSoba(soba, check_in, check_out)) {
						System.out.println("[HotelController: pretraga]: soba sa id: " + soba.getId() + " je brza soba.");
						iteratorSoba.remove();
					}else {*/
						if(soba.getRezervacije().isEmpty()) {
							System.out.println("[HotelController: pretraga]: soba nema rezervacija.");
							postojiTerminSoba = true;
							postojiSoba = true;
							//break;
						}else {
							System.out.println("[HotelController: pretraga]: postoje rezervacije za sobu; ima ih " + soba.getRezervacije().size() + ".");
							for(Iterator<RezervacijaHotel> iteratorRezervacija = soba.getRezervacije().iterator(); iteratorRezervacija.hasNext();) {
								RezervacijaHotel rezervacijaHotel = iteratorRezervacija.next();
								System.out.println("[HotelController: pretraga]: zahtev:  datum dolaska: " + check_in + ", datum odlaska: " + check_out + ".");
								System.out.println("[HotelController: pretraga]: rezervacija: " + rezervacijaHotel.getId() + ", datum dolaska: " + rezervacijaHotel.getDatum_dolaska() + ", datum odlaska: " + rezervacijaHotel.getDatum_odlaska() + ".");

								if(rezervacijaHotel.isAktivirana() && (check_in.equals(rezervacijaHotel.getDatum_dolaska()) || check_in.equals(rezervacijaHotel.getDatum_odlaska()) || check_out.equals(rezervacijaHotel.getDatum_dolaska()) || ((rezervacijaHotel.getDatum_dolaska()).after(check_in) && (rezervacijaHotel.getDatum_dolaska()).before(check_out)) || (check_in.after(rezervacijaHotel.getDatum_dolaska()) && check_in.before(rezervacijaHotel.getDatum_odlaska())) || (check_out.after(rezervacijaHotel.getDatum_dolaska()) && check_out.before(rezervacijaHotel.getDatum_odlaska())))) {
									System.out.println("[HotelController: pretraga]: soba je zauzeta u trazenom periodu.");
									iteratorSoba.remove();
									postojiTerminSoba = false;
								}else {
									System.out.println("[HotelController: pretraga]: soba je slobodna u trazenom periodu.");
								}
							}
							
							if(postojiTerminSoba) {
								postojiSoba = true;
							}
						}
					//}
					
				}
				
				if(!postojiSoba) {
					result.remove(hotel);
					i--;
				}
			}
			
		}
		
		
		if(adults != 0) {
			boolean postoji;
			
			for(int i=0; i<result.size(); i++) {
				postoji = false;
				Hotel hotel = result.get(i);
				System.out.println("[HotelController: pretraga]: hotel " + hotel.getNaziv() + " ima " + hotel.getSobe().size() + " soba.");
				for(Iterator<Soba> iteratorSoba = hotel.getSobe().iterator(); iteratorSoba.hasNext();) {
					Soba soba = (Soba) iteratorSoba.next();
					if(soba.getBroj_kreveta() <= adults) {
						postoji = true;
						System.out.println("[HotelController: pretraga]: soba.getBroj_kreveta() <= adults; soba.getBroj_kreveta(): " + soba.getBroj_kreveta() + ".");
						//break;
					}else {
						iteratorSoba.remove();
						System.out.println("[HotelController: pretraga]: uklanjanje sobe.");
						System.out.println("[HotelController: pretraga]: soba.getBroj_kreveta() > adults; soba.getBroj_kreveta(): " + soba.getBroj_kreveta() + ".");
					}
				}
				if(!postoji) {
					result.remove(hotel);
					i--;
				}
			}
		}
		
		return result;
	}
	
	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(
			value = "/editHotel/{hotel_id}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> editHotel(@PathVariable("hotel_id") Integer id, @RequestBody Hotel hotel) {
		
		Hotel stari =  hotelService.findById(id).get();
		
		System.out.println("[HotelController: editHotel] id hotela: " + stari.getId());
		hotelService.updateHotel(stari.getId(), hotel.getNaziv(), hotel.getPromotivni_opis(), hotel.getAdresa().getId());
		
		Map<String, String> result = new HashMap<>();
		result.put("result", "success");
	
		return ResponseEntity.accepted().body(result);
	}
	
	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(
			value = "/add_room/{hotel_id}",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Soba> addSoba(@PathVariable("hotel_id") Integer hotel_id, @RequestBody Soba soba){
		System.out.println("[HotelController: addSoba]: usao u metodu add_room");
		
		Hotel hotel = hotelService.findById(hotel_id).get();
		
		soba.setHotel(hotel);
		soba.setProsecna_ocena(0);
		soba.setZauzeta(false);
		
		System.out.println("[HotelController: addSoba] PRE CUVANJA SOBE U BAZU - hotel: " + hotel.getNaziv() + " ima " + hotel.getSobe().size() + " soba.");
		
		Soba result = sobaService.save(soba);
		
		System.out.println("[HotelController: addSoba] PRE CUVANJA HOTELA U BAZU - hotel: " + hotel.getNaziv() + " ima " + hotel.getSobe().size() + " soba.");

		hotel.getSobe().add(result);
		Hotel novi_hotel = hotelService.save(hotel);
		System.out.println("[HotelController: addSoba] POSLE CUVANJA U BAZU - hotel: " + novi_hotel.getNaziv() + " ima " + novi_hotel.getSobe().size() + " soba.");
		
		return new ResponseEntity<Soba>(result, HttpStatus.OK);
		
	}
	
	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(
			value = "/edit_room/{soba_id}",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Soba> editSoba(@PathVariable("soba_id") Integer soba_id, @RequestBody Soba soba){
		System.out.println("[HotelController: addSoba]: usao u metodu add_room");
		
		Soba pronadjena = sobaService.findById(soba_id).get();
		
		pronadjena.setBroj_kreveta(soba.getBroj_kreveta());
		pronadjena.setCena_nocenja(soba.getCena_nocenja());
		pronadjena.setOpis(soba.getOpis());
		
		Soba result = sobaService.save(pronadjena);
		
		return new ResponseEntity<Soba>(result, HttpStatus.OK);
		
	}
	
	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(
			value = "/delete_room/{soba_id}",
			method = RequestMethod.DELETE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Soba> deleteSoba(@PathVariable("soba_id") Integer soba_id){
		System.out.println("[HotelController: deleteSoba]: usao u metodu delete_room");
		
		Soba pronadjena = sobaService.findById(soba_id).get();
		sobaService.deleteRoom(pronadjena);
		
		return new ResponseEntity<Soba>(pronadjena, HttpStatus.OK);
		
	}
	
	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(
			value = "/total_earnings/{hotel_id}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> totalEarnings(@PathVariable("hotel_id") Integer hotel_id) throws ParseException{
		System.out.println("[HotelController: totalEarnings]: usao u metodu totalEarnings");
		
		Hotel hotel = hotelService.findById(hotel_id).get();
		
		Map<String, Double> result = new HashMap<>();
		
		ArrayList<Integer> sobeID = new ArrayList<Integer>();
		for (Iterator<Soba> iteratorSoba = hotel.getSobe().iterator(); iteratorSoba.hasNext();){
			
			Soba soba = iteratorSoba.next();
			sobeID.add(soba.getId());
		}
		
		List<RezervacijaHotel> sve_rez = rezervacijaHotelService.getAll();
		List<RezervacijaHotel> rez = new ArrayList<RezervacijaHotel>();
		
		for(Iterator<RezervacijaHotel> iteratorSveRez = sve_rez.iterator(); iteratorSveRez.hasNext();){
			
			RezervacijaHotel rezHotel = iteratorSveRez.next();
			for(int i = 0; i<sobeID.size();i++){
				if(rezHotel.getSoba().getId() == sobeID.get(i)){
					
					rez.add(rezHotel);
				}
			}
		}
		
		System.out.println("[HotelController: totalEarnings]: Rezervacija ima: " + rez.size());
		
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today_str = format.format(new Date());
		Date today = format.parse(today_str);
		System.out.println("[HotelController: totalEarnings]: danasnji datum: " + today);
		
		Calendar cal = new GregorianCalendar();
		
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		String today1_str = format.format(cal.getTime());
		Date today1 = format.parse(today1_str);
		System.out.println("[HotelController: totalEarnings]: datum pre 1 dan: " + today1);

		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -7);
		String today7_str = format.format(cal.getTime());
		Date today7 = format.parse(today7_str);
		System.out.println("[HotelController: totalEarnings]: datum pre 7 dana: " + today7);

		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -30);
		String today30_str = format.format(cal.getTime());
		Date today30 = format.parse(today30_str);
		System.out.println("[HotelController: totalEarnings]: datum pre mesec dana: " + today30);
		
		double totalDay=0, totalWeek=0, totalMonth=0;
		
		for(Iterator<RezervacijaHotel> iteratorRez = rez.iterator(); iteratorRez.hasNext();){
			
			RezervacijaHotel rezervacija = iteratorRez.next();
			
			Calendar start = Calendar.getInstance();
			start.setTime(rezervacija.getDatum_dolaska());
			Calendar end = Calendar.getInstance();
			end.setTime(rezervacija.getDatum_odlaska());
			
			System.out.println("[HotelController: totalEarnings]: rezervacija " + rezervacija.getId() + " ima ukupnu cenu: " + rezervacija.getUkupna_cena());
			System.out.println("[HotelController: totalEarnings]: datum odlaska: " + rezervacija.getDatum_odlaska() + "; datum dolaska: " + rezervacija.getDatum_dolaska());
			
			for(Date date = start.getTime(); !start.after(end); start.add(Calendar.DATE, 1), date = start.getTime()){
				
				if(today.equals(date)){
					
					totalDay += rezervacija.getUkupna_cena()/rezervacija.getBroj_nocenja();
				}
				
				if(today7.equals(date) || (today7.before(date) && today1.after(date))
						|| today1.equals(date)){
					
					totalWeek += rezervacija.getUkupna_cena()/rezervacija.getBroj_nocenja();
				}
				
				if(today30.equals(date) || (today30.before(date) && today.after(date))
						|| today.equals(date)){
					
					totalMonth += rezervacija.getUkupna_cena()/rezervacija.getBroj_nocenja();
				}
				
				/*
				if(today.equals(rezervacija.getDatum_odlaska())){
					
					System.out.println("Datum vracanja je danas za rez: " + rezervacija.getId());
					totalDay += rezervacija.getUkupna_cena();
				}
				
				if(today7.equals(rezervacija.getDatum_odlaska()) || (today7.before(rezervacija.getDatum_odlaska()) && today.after(rezervacija.getDatum_odlaska()))
						|| today.equals(rezervacija.getDatum_odlaska())){
					
					System.out.println("Datum vracanja je u toku prethodne nedelje za rez: " + rezervacija.getId());
					totalWeek += rezervacija.getUkupna_cena();
				}
				
				if(today30.equals(rezervacija.getDatum_odlaska()) || (today30.before(rezervacija.getDatum_odlaska()) && today.after(rezervacija.getDatum_odlaska()))
						|| today.equals(rezervacija.getDatum_odlaska())){
					
					System.out.println("Datum vracanja je u toku prethodnog meseca za rez: " + rezervacija.getId());
					totalMonth += rezervacija.getUkupna_cena();
				}
				*/
			}
		}
		
		result.put("day", totalDay);
		result.put("week", totalWeek);
		result.put("month", totalMonth);
		
		return ResponseEntity.accepted().body(result);
		
	}
	
	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(
			value = "/daily_report/{hotel_id}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> dailyReport(@PathVariable("hotel_id") Integer hotel_id) throws ParseException{
		System.out.println("[HotelController: dailyReport]: usao u metodu dailyReport");
		
		Hotel hotel = hotelService.findById(hotel_id).get();
		
		Map<String, Double> result = new HashMap<>();
		double ret = 0;
		
		ArrayList<Integer> sobeID = new ArrayList<Integer>();
		for (Iterator<Soba> iteratorSoba = hotel.getSobe().iterator(); iteratorSoba.hasNext();){
			
			Soba soba = iteratorSoba.next();
			sobeID.add(soba.getId());
		}
		
		List<RezervacijaHotel> sve_rez = rezervacijaHotelService.getAll();
		List<RezervacijaHotel> rez = new ArrayList<RezervacijaHotel>();
		
		for(Iterator<RezervacijaHotel> iteratorSveRez = sve_rez.iterator(); iteratorSveRez.hasNext();){
			
			RezervacijaHotel rezHotel = iteratorSveRez.next();
			for(int i = 0; i<sobeID.size();i++){
				if(rezHotel.getSoba().getId() == sobeID.get(i)){
					
					rez.add(rezHotel);
				}
			}
		}
		
		System.out.println("[HotelController: dailyReport]: Rezervacija ima: " + rez.size());
		
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today_str = format.format(new Date());
		Date today = format.parse(today_str);
		System.out.println("[HotelController: dailyReport]: danasnji datum: " + today);
		
		int broj_rez=0;
		
		for(Iterator<RezervacijaHotel> iteratorRez = rez.iterator(); iteratorRez.hasNext();){
			
			RezervacijaHotel rezervacija = iteratorRez.next();
			
			if(today.equals(rezervacija.getDatum_dolaska()) || (rezervacija.getDatum_odlaska().after(today) && rezervacija.getDatum_dolaska().before(today))
					|| today.equals(rezervacija.getDatum_odlaska())){
				
				broj_rez+=1;
			}
			
		}
		System.out.println("[HotelController: dailyReport]: broj_rez: " + broj_rez + " broj_soba: " + sobeID.size());
		
		
		ret = (double) broj_rez / sobeID.size();
		ret = ret * 100;
		System.out.println("[HotelController: dailyReport]: ret: " + ret);
		
		result.put("ret", ret);
		
		return ResponseEntity.accepted().body(result);
		
	}
	
	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(
			value = "/weekly_report/{hotel_id}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> weeklyReport(@PathVariable("hotel_id") Integer hotel_id) throws ParseException{
		
		Hotel hotel = hotelService.findById(hotel_id).get();
		
		Map<String, Integer> result = new HashMap<>();
		
		ArrayList<Integer> sobeID = new ArrayList<Integer>();
		for (Iterator<Soba> iteratorSoba = hotel.getSobe().iterator(); iteratorSoba.hasNext();){
			
			Soba soba = iteratorSoba.next();
			sobeID.add(soba.getId());
		}
		
		List<RezervacijaHotel> sve_rez = rezervacijaHotelService.getAll();
		List<RezervacijaHotel> rez = new ArrayList<RezervacijaHotel>();
		
		for(Iterator<RezervacijaHotel> iteratorSveRez = sve_rez.iterator(); iteratorSveRez.hasNext();){
			
			RezervacijaHotel rezHotel = iteratorSveRez.next();
			for(int i = 0; i<sobeID.size();i++){
				if(rezHotel.getSoba().getId() == sobeID.get(i)){
					
					rez.add(rezHotel);
				}
			}
		}
		
		System.out.println("[HotelController: weeklyReport]: Rezervacija ima: " + rez.size());
		
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today_str = format.format(new Date());
		Date today = format.parse(today_str);
		System.out.println("[HotelController: weeklyReport]: danasnji datum: " + today);
		
		
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -7);
		String today6_str = format.format(cal.getTime());
		Date today6 = format.parse(today6_str);
		System.out.println("[HotelController: weeklyReport]: datum6: " + today6);
		
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -6);
		String today5_str = format.format(cal.getTime());
		Date today5 = format.parse(today5_str);
		System.out.println("[HotelController: weeklyReport]: datum5: " + today5);
		
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -5);
		String today4_str = format.format(cal.getTime());
		Date today4 = format.parse(today4_str);
		System.out.println("[HotelController: weeklyReport]: datum4: " + today4);
		
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -4);
		String today3_str = format.format(cal.getTime());
		Date today3 = format.parse(today3_str);
		System.out.println("[HotelController: weeklyReport]: datum3: " + today3);
		
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -3);
		String today2_str = format.format(cal.getTime());
		Date today2 = format.parse(today2_str);
		System.out.println("[HotelController: weeklyReport]: datum2: " + today2);
		
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -2);
		String today1_str = format.format(cal.getTime());
		Date today1 = format.parse(today1_str);
		System.out.println("[HotelController: weeklyReport]: datum1: " + today1);
		
		cal.setTime(today);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		String today0_str = format.format(cal.getTime());
		Date today0 = format.parse(today0_str);
		System.out.println("[HotelController: weeklyReport]: datum0: " + today0);
		
		
		int day7 = 0, day6 = 0 , day5 = 0, day4 = 0, day3 = 0, day2 = 0, day1 = 0;
		
		for(Iterator<RezervacijaHotel> iteratorRez = rez.iterator(); iteratorRez.hasNext();){
			
			RezervacijaHotel rezervacija = iteratorRez.next();
			
			if(today6.equals(rezervacija.getDatum_odlaska()) || (rezervacija.getDatum_odlaska().after(today6) && rezervacija.getDatum_dolaska().before(today6))
					|| today6.equals(rezervacija.getDatum_dolaska()))
			{
				day7 += 1;
			}
			
			if(today5.equals(rezervacija.getDatum_odlaska()) || (rezervacija.getDatum_odlaska().after(today5) && rezervacija.getDatum_dolaska().before(today5))
					|| today5.equals(rezervacija.getDatum_dolaska()))
			{
				day6 += 1;
			}
			
			if(today4.equals(rezervacija.getDatum_odlaska()) || (rezervacija.getDatum_odlaska().after(today4) && rezervacija.getDatum_dolaska().before(today4))
					|| today4.equals(rezervacija.getDatum_dolaska()))
			{
				day5 += 1;
			}
			
			if(today3.equals(rezervacija.getDatum_odlaska()) || (rezervacija.getDatum_odlaska().after(today3) && rezervacija.getDatum_dolaska().before(today3))
					|| today3.equals(rezervacija.getDatum_dolaska()))
			{
				day4 += 1;
			}
			
			if(today2.equals(rezervacija.getDatum_odlaska()) || (rezervacija.getDatum_odlaska().after(today2) && rezervacija.getDatum_dolaska().before(today2))
					|| today2.equals(rezervacija.getDatum_dolaska()))
			{
				day3 += 1;
			}
			
			if(today1.equals(rezervacija.getDatum_odlaska()) || (rezervacija.getDatum_odlaska().after(today1) && rezervacija.getDatum_dolaska().before(today1))
					|| today1.equals(rezervacija.getDatum_dolaska()))
			{
				day2 += 1;
			}
			
			if(today0.equals(rezervacija.getDatum_odlaska()) || (rezervacija.getDatum_odlaska().after(today0) && rezervacija.getDatum_dolaska().before(today0))
					|| today0.equals(rezervacija.getDatum_dolaska()))
			{
				day1 += 1;
			}
			
		}
		
		System.out.println("[HotelController: weeklyReport]: day7 " + day7);
		System.out.println("[HotelController: weeklyReport]: day6 " + day6);
		System.out.println("[HotelController: weeklyReport]: day5 " + day5);
		System.out.println("[HotelController: weeklyReport]: day4 " + day4);
		System.out.println("[HotelController: weeklyReport]: day3 " + day3);
		System.out.println("[HotelController: weeklyReport]: day2 " + day2);
		System.out.println("[HotelController: weeklyReport]: day1 " + day1);
		
		result.put("day7", day7);
		result.put("day6", day6);
		result.put("day5", day5);
		result.put("day4", day4);
		result.put("day3", day3);
		result.put("day2", day2);
		result.put("day1", day1);
		
		return ResponseEntity.accepted().body(result);
	}
	
	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(
			value = "/monthly_report/{hotel_id}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> monthlyReport(@PathVariable("hotel_id") Integer hotel_id) throws ParseException{
		
		Hotel hotel = hotelService.findById(hotel_id).get();
		
		Map<String, Integer> result = new HashMap<>();
		
		ArrayList<Integer> sobeID = new ArrayList<Integer>();
		for (Iterator<Soba> iteratorSoba = hotel.getSobe().iterator(); iteratorSoba.hasNext();){
			
			Soba soba = iteratorSoba.next();
			sobeID.add(soba.getId());
		}
		
		List<RezervacijaHotel> sve_rez = rezervacijaHotelService.getAll();
		List<RezervacijaHotel> rez = new ArrayList<RezervacijaHotel>();
		
		for(Iterator<RezervacijaHotel> iteratorSveRez = sve_rez.iterator(); iteratorSveRez.hasNext();){
			
			RezervacijaHotel rezHotel = iteratorSveRez.next();
			for(int i = 0; i<sobeID.size();i++){
				if(rezHotel.getSoba().getId() == sobeID.get(i)){
					
					rez.add(rezHotel);
				}
			}
		}
		
		System.out.println("[HotelController: monthlyReport]: Rezervacija ima: " + rez.size());
		
		
		int year = Year.now().getValue();
		boolean prestupna = false;
		
		if(year % 4 == 0){
			prestupna=true;
		}
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = new GregorianCalendar();
		
		cal.set(year, 0, 1);
		String jan1_str = format.format(cal.getTime());
		Date jan1 = format.parse(jan1_str);
		
		cal.set(year, 0, 31);
		String jan31_str = format.format(cal.getTime());
		Date jan31 = format.parse(jan31_str);
		
		cal.set(year, 1, 1);
		String feb1_str = format.format(cal.getTime());
		Date feb1 = format.parse(feb1_str);
		
		String febkraj_str="";
		if(prestupna){
			cal.set(year, 1, 29);
			febkraj_str=format.format(cal.getTime());
		}
		else{
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
	
		int jan = 0, feb = 0 , mart = 0, apr = 0, maj = 0, jun = 0, jul = 0, avg = 0, sep = 0, okt = 0, nov = 0, dec =0;
		
		for(Iterator<RezervacijaHotel> iteratorRez = rez.iterator(); iteratorRez.hasNext();){
			
			RezervacijaHotel rv2 = iteratorRez.next();
			Calendar start = Calendar.getInstance();
			start.setTime(rv2.getDatum_dolaska());
			Calendar end = Calendar.getInstance();
			end.setTime(rv2.getDatum_odlaska());
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
		meseci[1]=feb;
		meseci[2]=mart;
		meseci[3]=apr;
		meseci[4]=maj;
		meseci[5]=jun;
		meseci[6]=jul;
		meseci[7]=avg;
		meseci[8]=sep;
		meseci[9]=okt;
		meseci[10]=nov;
		meseci[11]=dec;
		
		result.put("jan", jan);
		result.put("feb", feb);
		result.put("mar", mart);
		result.put("apr", apr);
		result.put("may", maj);
		result.put("jun", jun);
		result.put("jul", jul);
		result.put("aug", avg);
		result.put("sep", sep);
		result.put("oct", okt);
		result.put("nov", nov);
		result.put("dec", dec);
		
		return ResponseEntity.accepted().body(result);
	}
	
	@RequestMapping(
			value = "/get_room/{id_rez_hotel}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getRezervacija(@PathVariable("id_rez_hotel") Integer id_rez_hotel) throws ParseException{
		
		RezervacijaHotel rezervacijaHotel = rezervacijaHotelService.findById(id_rez_hotel).get();
		Soba soba = rezervacijaHotel.getSoba();
		Hotel hotel = soba.getHotel();
		
		Map<String, Object> result = new HashMap<>();
		
		result.put("soba", soba);
		result.put("hotel", hotel);
			
		return ResponseEntity.accepted().body(result);
	}
	
	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(
			value = "/add_special_price/{id_soba}",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addSpecialPrice(@PathVariable("id_soba") Integer id_soba, @RequestBody Popust popust){
		
		Soba soba = sobaService.findById(id_soba).get();
		
		Date pocetak = popust.getPocetak_vazenja();
		Date kraj = popust.getKraj_vazenja();
		
		System.out.println("[HotelController: addSpecialPrice] popust - pocetak: " + pocetak + "; kraj: " + kraj);
		boolean postoji = false;
		
		for(Iterator<Popust> iteratorPopust = soba.getPopusti().iterator(); iteratorPopust.hasNext();) {
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
			popustService.updateSoba(id_soba, saved.getId());
			
			result.put("result", "success");
			result.put("popust_id", saved.getId());
		}else {
			result.put("result", "error");
		}
		
		return ResponseEntity.accepted().body(result);
	}
	
	@PreAuthorize("hasRole('HOTELADMIN')")
	@RequestMapping(
			value = "/add_usluga_special_price/{usluge}/{size}/{popust_id}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> addUslugaSpecialPrice(@PathVariable("usluge") int[] usluge, @PathVariable("size") int size, @PathVariable("popust_id") Integer popust_id){
		
		Popust popust = popustService.findById(popust_id);
		Usluga usluga;
		for(int i=0; i<size; i++) {
			System.out.println("[HotelController: addUslugaSpecialPrice] usluga: " + usluge[i]);
			Integer usluga_id = usluge[i];
			usluga = uslugaService.findById(usluga_id).get();
			popust.getUsluge().add(usluga);
		}
		System.out.println("[HotelController: addUslugaSpecialPrice] kraj sa dodavanjem usluga; size: " + popust.getUsluge().size());
		popustService.save(popust);
		
		Map<String, Object> result = new HashMap<>();
		
		result.put("result", "success");
			
		return ResponseEntity.accepted().body(result);
	}
	
	
	public Hotel pretraga_hotel(Hotel hotel, Date check_in, Date check_out, int adults){
		Hotel result = hotel;
		
		System.out.println("[HotelController: pretraga_hotel]: iteracija pomocu iteratora; naziv hotela: " + hotel.getNaziv());
		
		for(Iterator<Soba> iteratorSoba = hotel.getSobe().iterator(); iteratorSoba.hasNext();) {
			System.out.println("[HotelController: pretraga_hotel]: usao u for za prolazak kroz sobe; ima " + hotel.getSobe().size() + " soba.");
			Soba soba = (Soba) iteratorSoba.next();
			
			if(isBrzaSoba(soba, check_in, check_out)) {
				System.out.println("[HotelController: pretraga_hotel]: soba sa id: " + soba.getId() + " je brza soba.");
				iteratorSoba.remove();
			}else {
				if(soba.getRezervacije().isEmpty()) {
					System.out.println("[HotelController: pretraga_hotel]: soba nema rezervacija.");
				}else {
					System.out.println("[HotelController: pretraga_hotel]: postoje rezervacije za sobu; ima ih " + soba.getRezervacije().size() + ".");
					for(Iterator<RezervacijaHotel> iteratorRezervacija = soba.getRezervacije().iterator(); iteratorRezervacija.hasNext();) {
						RezervacijaHotel rezervacijaHotel = iteratorRezervacija.next();
						System.out.println("[HotelController: pretraga_hotel]: zahtev:  datum dolaska: " + check_in + ", datum odlaska: " + check_out + ".");
						System.out.println("[HotelController: pretraga_hotel]: rezervacija: " + rezervacijaHotel.getId() + ", datum dolaska: " + rezervacijaHotel.getDatum_dolaska() + ", datum odlaska: " + rezervacijaHotel.getDatum_odlaska() + ".");

						if(rezervacijaHotel.isAktivirana() && (check_in.equals(rezervacijaHotel.getDatum_dolaska()) || check_in.equals(rezervacijaHotel.getDatum_odlaska()) || check_out.equals(rezervacijaHotel.getDatum_dolaska()) || ((rezervacijaHotel.getDatum_dolaska()).after(check_in) && (rezervacijaHotel.getDatum_dolaska()).before(check_out)) || (check_in.after(rezervacijaHotel.getDatum_dolaska()) && check_in.before(rezervacijaHotel.getDatum_odlaska())) || (check_out.after(rezervacijaHotel.getDatum_dolaska()) && check_out.before(rezervacijaHotel.getDatum_odlaska())))) {
							System.out.println("[HotelController: pretraga_hotel]: soba je zauzeta u trazenom periodu.");
							iteratorSoba.remove();
						}else {
							System.out.println("[HotelController: pretraga_hotel]: soba je slobodna u trazenom periodu.");
						}
					}
				}
			}
		}
		
		if(adults != 0) {
			System.out.println("[HotelController: pretraga_hotel]: hotel " + hotel.getNaziv() + " ima " + hotel.getSobe().size() + " soba.");
			
			for(Iterator<Soba> iteratorSoba = hotel.getSobe().iterator(); iteratorSoba.hasNext();) {
				Soba soba = (Soba) iteratorSoba.next();
				if(soba.getBroj_kreveta() <= adults) {
					System.out.println("[HotelController: pretraga_hotel]: soba.getBroj_kreveta() <= adults; soba.getBroj_kreveta(): " + soba.getBroj_kreveta() + ".");
				}else {
					iteratorSoba.remove();
					System.out.println("[HotelController: pretraga_hotel]: uklanjanje sobe.");
					System.out.println("[HotelController: pretraga_hotel]: soba.getBroj_kreveta() > adults; soba.getBroj_kreveta(): " + soba.getBroj_kreveta() + ".");
				}
			}
		}
		
		return result;
	}
	
	public boolean isBrzaSoba(Soba soba, Date check_in, Date check_out) {
		
		boolean result = false;
		
		System.out.println("[HotelController: isBrzaSoba]: postoje popusti za sobu; ima ih " + soba.getPopusti().size() + ".");
		for(Iterator<Popust> iteratorPopusta = soba.getPopusti().iterator(); iteratorPopusta.hasNext();) {
			Popust popust = iteratorPopusta.next();

			if(check_in.equals(popust.getPocetak_vazenja()) || check_in.equals(popust.getKraj_vazenja()) || check_out.equals(popust.getPocetak_vazenja()) || ((popust.getPocetak_vazenja()).after(check_in) && (popust.getPocetak_vazenja()).before(check_out)) || (check_in.after(popust.getPocetak_vazenja()) && check_in.before(popust.getKraj_vazenja())) || (check_out.after(popust.getPocetak_vazenja()) && check_out.before(popust.getKraj_vazenja()))) {

				System.out.println("[HotelController: isBrzaSoba]: soba je na popustu u trazenom periodu.");
				//soba.setCena_popust(soba.getCena_nocenja() * 0.01 * (100 - popust.getPopust()));
				result = true;
				break;
			}else {
				System.out.println("[HotelController: isBrzaSoba]: soba nije na popustu u trazenom periodu.");
			}
		}
		
		return result;
	}
}
