package com.example.ISA_AMA_projekat.controller;

import static com.example.ISA_AMA_projekat.constants.HotelConstants.DB_COUNT_HOTELS;
import static com.example.ISA_AMA_projekat.constants.HotelConstants.DB_NAZIV;
import static com.example.ISA_AMA_projekat.constants.HotelConstants.DB_PROMOTIVNI_OPIS;
import static com.example.ISA_AMA_projekat.constants.HotelConstants.DB_PROSECNA_OCENA;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import com.example.ISA_AMA_projekat.TestUtil;
import com.example.ISA_AMA_projekat.constants.HotelConstants;
import com.example.ISA_AMA_projekat.constants.RezervacijaHotelConstants;
import com.example.ISA_AMA_projekat.constants.SobaConstants;
import com.example.ISA_AMA_projekat.model.Adresa;
import com.example.ISA_AMA_projekat.model.Hotel;
import com.example.ISA_AMA_projekat.model.RezervacijaHotel;
import com.example.ISA_AMA_projekat.model.Soba;
import com.example.ISA_AMA_projekat.model.Usluga;

@RunWith(SpringRunner.class)
@ContextConfiguration
@WebAppConfiguration
@SpringBootTest
public class HotelControllerTest {

	private static final String URL_PREFIX = "/api/hotels";
	
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Before	
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
	}
	
	@Test
	public void testGetHotels() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/all")).andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$", hasSize(DB_COUNT_HOTELS)))
		.andExpect(jsonPath("$.[*].id").value(hasItem(HotelConstants.DB_ID.intValue())))
		.andExpect(jsonPath("$.[*].naziv").value(hasItem(DB_NAZIV)))
		.andExpect(jsonPath("$.[*].promotivni_opis").value(hasItem(DB_PROMOTIVNI_OPIS)))
		.andExpect(jsonPath("$.[*].prosecna_ocena").value(hasItem(DB_PROSECNA_OCENA)));
	}
	
	@WithMockUser(roles="SYSADMIN")
	@Test
	public void testGetHotelsForAdmin() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/all_admin")).andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$", hasSize(HotelConstants.DB_COUNT_HOTELS_ADMINS)))
		.andExpect(jsonPath("$.[*].naziv").value(hasItem(HotelConstants.NEW_NAZIV_NEG)));
	}
	
	@Test
	public void testGetHotelsSearch() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/search/" + HotelConstants.DB_NAZIV_SEARCH + "/" + HotelConstants.DB_DATE_CHECK_IN + "/" + HotelConstants.DB_DATE_CHECK_OUT + "/" + HotelConstants.DB_ADULTS))
		.andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$", hasSize(HotelConstants.DB_COUNT_SEARCH)));
		
	}
	
	@Test
	public void testGetRezervacija() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/rezervacija/" + RezervacijaHotelConstants.DB_DATE_CHECK_IN + "/" + RezervacijaHotelConstants.DB_DATE_CHECK_IN))
		.andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$.brza").value(RezervacijaHotelConstants.DB_BRZA))
		.andExpect(jsonPath("$.broj_nocenja").value(RezervacijaHotelConstants.DB_BROJ_NOCENJA))
		.andExpect(jsonPath("$.aktivirana").value(RezervacijaHotelConstants.DB_AKTIVIRANA));;
	}
	
	@Test
	public void testGetHotel() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/"+ HotelConstants.DB_ID +  "/" + HotelConstants.DB_DATE_CHECK_IN + "/" + HotelConstants.DB_DATE_CHECK_IN + "/" + HotelConstants.DB_ADULTS))
		.andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$.id").value(HotelConstants.DB_ID.intValue()))
		.andExpect(jsonPath("$.naziv").value(DB_NAZIV))
		.andExpect(jsonPath("$.promotivni_opis").value(DB_PROMOTIVNI_OPIS))
		.andExpect(jsonPath("$.prosecna_ocena").value(DB_PROSECNA_OCENA))
		.andExpect(jsonPath("$.prosecna_ocena").value(DB_PROSECNA_OCENA))
		.andExpect(jsonPath("$.sobe").isNotEmpty());
		
	}
	
	@WithMockUser(roles="HOTELADMIN")
	@Test
	public void testGetOneHotel() throws Exception {
		mockMvc.perform(get(URL_PREFIX  + "/admin/"+ HotelConstants.DB_ID + "/" + HotelConstants.DB_ADMIN_ID))
		.andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$.id").value(HotelConstants.DB_ID.intValue()))
		.andExpect(jsonPath("$.naziv").value(DB_NAZIV))
		.andExpect(jsonPath("$.promotivni_opis").value(DB_PROMOTIVNI_OPIS))
		.andExpect(jsonPath("$.prosecna_ocena").value(DB_PROSECNA_OCENA))
		.andExpect(jsonPath("$.id_admin").value(HotelConstants.DB_ADMIN_ID));
		
	}

	@WithMockUser(roles="HOTELADMIN")
	@Test(expected = NestedServletException.class)
	public void testGetOneHotelNegative() throws Exception {
		mockMvc.perform(get(URL_PREFIX  + "/admin/"+ HotelConstants.DB_ID + "/" + HotelConstants.DB_ADMIN_ID_FALSE))
		.andExpect(content().contentType(contentType));
		
	}
	
	@Test
	public void testGetHotelSpecialPrice() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/specialPrice/" + HotelConstants.DB_ID + "/" + HotelConstants.DB_POPUST_CHECK_IN + "/" + HotelConstants.DB_POPUST_CHECK_OUT + "/" + HotelConstants.DB_ADULTS))
		.andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$", hasSize(HotelConstants.DB_COUNT_POPUST)))
		.andExpect(jsonPath("$.[*].id").value(hasItem(SobaConstants.DB_ID.intValue())))
		.andExpect(jsonPath("$.[*].cena_nocenja").value(hasItem(SobaConstants.DB_CENA_NOCENJA)))
		.andExpect(jsonPath("$.[*].opis").value(hasItem(SobaConstants.DB_OPIS)))
		.andExpect(jsonPath("$.[*].prosecna_ocena").value(hasItem(SobaConstants.DB_PROSECNA_OCENA)))
		.andExpect(jsonPath("$.[*].broj_kreveta").value(hasItem(SobaConstants.DB_BROJ_KREVETA)))
		.andExpect(jsonPath("$.[*].cena_popust").value(hasItem(SobaConstants.DB_CENA_POPUST)));
		
	}
	
	@Test
	public void testGetHotelSpecialPriceNegative() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/specialPrice/" + HotelConstants.DB_ID + "/" + HotelConstants.DB_POPUST_CHECK_IN_NEG + "/" + HotelConstants.DB_POPUST_CHECK_OUT_NEG + "/" + HotelConstants.DB_ADULTS))
		.andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$", hasSize(HotelConstants.DB_COUNT_POPUST_NEG)));
		
	}
	
	@WithMockUser(roles="SYSADMIN")
	@Test
	@Transactional
	@Rollback(true)
	public void testSaveHotel() throws Exception {
		Hotel hotel = new Hotel();
		hotel.setNaziv(HotelConstants.NEW_NAZIV);
		hotel.setPromotivni_opis(HotelConstants.NEW_PROMOTIVNI_OPIS);
		hotel.setProsecna_ocena(HotelConstants.NEW_PROSECNA_OCENA);

		String json = TestUtil.json(hotel);
		this.mockMvc.perform(post(URL_PREFIX + "/save").contentType(contentType).content(json)).andExpect(status().isCreated());
	}
	
	@WithMockUser(roles="SYSADMIN")
	@Test
	@Transactional
	@Rollback(true)
	public void testSaveHotelNegative() throws Exception {
		Hotel hotel = new Hotel();
		hotel.setNaziv(HotelConstants.NEW_NAZIV_NEG);
		hotel.setPromotivni_opis(HotelConstants.NEW_PROMOTIVNI_OPIS);
		hotel.setProsecna_ocena(HotelConstants.NEW_PROSECNA_OCENA);

		String json = TestUtil.json(hotel);
		this.mockMvc.perform(post(URL_PREFIX + "/save").contentType(contentType).content(json)).andExpect(status().isConflict());
	}
	
	@WithMockUser(roles="USER")
	@Test
	public void testAddUsluga() throws Exception {
		RezervacijaHotel rezervacijaHotel = new RezervacijaHotel();
		rezervacijaHotel.setBroj_nocenja(RezervacijaHotelConstants.DB_BROJ_NOCENJA);
		rezervacijaHotel.setDatum_rezervacije(RezervacijaHotelConstants.DB_DATE);
		rezervacijaHotel.setDatum_dolaska(RezervacijaHotelConstants.DB_DATE);
		rezervacijaHotel.setDatum_odlaska(RezervacijaHotelConstants.DB_DATE);
		rezervacijaHotel.setUkupna_cena(RezervacijaHotelConstants.DB_UKUPNA_CENA_PRE);
		rezervacijaHotel.setBrza(RezervacijaHotelConstants.DB_BRZA);
		rezervacijaHotel.setPopust(RezervacijaHotelConstants.DB_POPUST);
		rezervacijaHotel.setAktivirana(RezervacijaHotelConstants.DB_AKTIVIRANA);

		String json = TestUtil.json(rezervacijaHotel);
		this.mockMvc.perform(post(URL_PREFIX + "/add_usluga/" + RezervacijaHotelConstants.DB_USLUGA_ID).contentType(contentType).content(json))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.usluge").isNotEmpty());
	}
	
	@WithMockUser(roles="USER")
	@Test
	public void testRemoveUsluga() throws Exception {
		
		Usluga usluga = new Usluga();
		usluga.setId(1);
		usluga.setNaziv("Wi-fi");
		usluga.setCena(10);
		
		RezervacijaHotel rezervacijaHotel = new RezervacijaHotel();
		rezervacijaHotel.setBroj_nocenja(RezervacijaHotelConstants.DB_BROJ_NOCENJA);
		rezervacijaHotel.setDatum_rezervacije(RezervacijaHotelConstants.DB_DATE);
		rezervacijaHotel.setDatum_dolaska(RezervacijaHotelConstants.DB_DATE);
		rezervacijaHotel.setDatum_odlaska(RezervacijaHotelConstants.DB_DATE);
		rezervacijaHotel.setUkupna_cena(RezervacijaHotelConstants.DB_UKUPNA_CENA_PRE);
		rezervacijaHotel.setBrza(RezervacijaHotelConstants.DB_BRZA);
		rezervacijaHotel.setPopust(RezervacijaHotelConstants.DB_POPUST);
		rezervacijaHotel.setAktivirana(RezervacijaHotelConstants.DB_AKTIVIRANA);
		rezervacijaHotel.getUsluge().add(usluga);

		String json = TestUtil.json(rezervacijaHotel);
		this.mockMvc.perform(post(URL_PREFIX + "/remove_usluga/" + RezervacijaHotelConstants.DB_USLUGA_ID).contentType(contentType).content(json))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.usluge").isEmpty());
	}
	
	@Test
	public void testGetNumberOfDays() throws Exception {
		mockMvc.perform(post(URL_PREFIX + "/get_number_of_days/" + HotelConstants.DB_DATE_CHECK_IN + "/" + HotelConstants.DB_DATE_CHECK_OUT))
		.andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$").value(HotelConstants.DB_NUMBER_OF_DAYS));
		
	}
	
	@Test
	public void testGetSoba() throws Exception {
		mockMvc.perform(post(URL_PREFIX + "/get_soba/" + SobaConstants.DB_ID))
		.andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$.cena_nocenja").value(SobaConstants.DB_CENA_NOCENJA))
		.andExpect(jsonPath("$.opis").value(SobaConstants.DB_OPIS))
		.andExpect(jsonPath("$.prosecna_ocena").value(SobaConstants.DB_PROSECNA_OCENA))
		.andExpect(jsonPath("$.broj_kreveta").value(SobaConstants.DB_BROJ_KREVETA))
		.andExpect(jsonPath("$.cena_popust").value(SobaConstants.DB_CENA_POPUST));
	}
	/*
	@WithMockUser(roles="USER")
	@Test
	@Transactional
	@Rollback(true)
	public void testBookRoom() throws Exception {
		
		Usluga usluga = new Usluga();
		usluga.setId(1);
		usluga.setNaziv("Wi-fi");
		usluga.setCena(10);
		
		RezervacijaHotel rezervacijaHotel = new RezervacijaHotel();
		rezervacijaHotel.setId(RezervacijaHotelConstants.DB_ID_3);
		rezervacijaHotel.setDatum_dolaska(RezervacijaHotelConstants.DB_DATE);
		rezervacijaHotel.setDatum_odlaska(RezervacijaHotelConstants.DB_DATE);
		rezervacijaHotel.setUkupna_cena(RezervacijaHotelConstants.DB_UKUPNA_CENA_PRE);
		rezervacijaHotel.setBrza(RezervacijaHotelConstants.DB_BRZA);
		rezervacijaHotel.setPopust(RezervacijaHotelConstants.DB_POPUST);
		rezervacijaHotel.setAktivirana(RezervacijaHotelConstants.DB_AKTIVIRANA);
		rezervacijaHotel.getUsluge().add(usluga);

		String json = TestUtil.json(rezervacijaHotel);
		this.mockMvc.perform(put(URL_PREFIX + "/book_room/" + + SobaConstants.DB_ID + "/" + RezervacijaHotelConstants.DB_ID).contentType(contentType).content(json))
		.andExpect(status().isOk())
		.andExpect(content().contentType(contentType));
	}
	*/
	
	@Test
	public void testGetPopust() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/popust/" + SobaConstants.DB_ID + "/" + SobaConstants.DB_POPUST_CHECK_IN + "/" + SobaConstants.DB_POPUST_CHECK_OUT))
		.andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$.id").value(SobaConstants.DB_POPUST_ID));
		
	}
	
	@WithMockUser(roles="HOTELADMIN")
	@Test
	public void testEditHotel() throws Exception {
		
		Hotel hotel = new Hotel();
		hotel.setNaziv("Novi naziv");
		hotel.setPromotivni_opis("Novi promotivni opis");
		
		Adresa adresa = new Adresa();
		adresa.setId(1);
		
		hotel.setAdresa(adresa);

		String json = TestUtil.json(hotel);
		this.mockMvc.perform(post(URL_PREFIX + "/editHotel/" + HotelConstants.DB_ID_EDIT).contentType(contentType).content(json))
		.andExpect(status().isAccepted());
	}
	
	@WithMockUser(roles="HOTELADMIN")
	@Test
	public void testAddRoom() throws Exception {
		
		Soba soba = new Soba();
		soba.setBroj_kreveta(3);
		soba.setCena_nocenja(140);

		String json = TestUtil.json(soba);
		this.mockMvc.perform(post(URL_PREFIX + "/add_room/" + HotelConstants.DB_ID_EDIT).contentType(contentType).content(json))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.prosecna_ocena").value(SobaConstants.DB_PROSECNA_ADD))
		.andExpect(jsonPath("$.zauzeta").value(SobaConstants.DB_ZAUZETA));
	}
}
