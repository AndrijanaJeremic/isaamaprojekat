package com.example.ISA_AMA_projekat.controller;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.example.ISA_AMA_projekat.constants.VoziloConstants;
import com.example.ISA_AMA_projekat.model.Adresa;
import com.example.ISA_AMA_projekat.model.Filijala;
import com.example.ISA_AMA_projekat.model.Grad;
import com.example.ISA_AMA_projekat.model.Vozilo;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VoziloControllerTest {

	private static final String URL_PREFIX = "/api/vozila";

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void testGetVozilo() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/" + VoziloConstants.DB_ID_V)).andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$.id").value(VoziloConstants.DB_ID_V))
		.andExpect(jsonPath("$.naziv").value(VoziloConstants.DB_NAZIV_V))
		.andExpect(jsonPath("$.marka").value(VoziloConstants.DB_MARKA_V))
		.andExpect(jsonPath("$.model").value(VoziloConstants.DB_MODEL_V))
		.andExpect(jsonPath("$.tip").value(VoziloConstants.DB_TIP_V))
		.andExpect(jsonPath("$.cena_dan").value(VoziloConstants.DB_CENA_V))
		.andExpect(jsonPath("$.broj_sedista").value(VoziloConstants.DB_BR_SEDISTA_V))
		.andExpect(jsonPath("$.godina_proizvodnje").value(VoziloConstants.DB_GOD_PROIZ_V))
		.andExpect(jsonPath("$.prosecna_ocena").value(VoziloConstants.DB_PROSECNA_V));
	}
	
	@Test
	@WithMockUser(roles="RENTADMIN")
	@Transactional
	@Rollback(true)
	public void testEditVozilo() throws Exception {
		Vozilo vozilo= new Vozilo();
		vozilo.setId(2);
		vozilo.setNaziv("Vozilo2");
		vozilo.setMarka("V2");
		vozilo.setModel("Izmena");
		vozilo.setGodina_proizvodnje(2015);
		vozilo.setBroj_sedista(5);
		vozilo.setTip("tip");
		vozilo.setCena_dan(500);
		vozilo.setProsecna_ocena(4.5);
		vozilo.setCena_popust(0);
		vozilo.setZauzeto(false);
		mockMvc.perform(post(URL_PREFIX + "/admin/izmenaVozila/2/Vozilo2/V2/Izmena/2015/5/tip/500")).equals(vozilo);
	}
	
	@Test
	@WithMockUser(roles="RENTADMIN")
	@Transactional
	@Rollback(true)
	public void testAddVozilo() throws Exception {
		Vozilo vozilo= new Vozilo();
		vozilo.setId(3);
		vozilo.setNaziv("Novo");
		vozilo.setMarka("Vozilo");
		vozilo.setModel("Dodato");
		vozilo.setGodina_proizvodnje(2019);
		vozilo.setBroj_sedista(5);
		vozilo.setTip("tip");
		vozilo.setCena_dan(505);
		vozilo.setProsecna_ocena(0);
		vozilo.setZauzeto(false);
		
		Adresa a = new Adresa();
		a.setBroj("1");
		a.setGrad(new Grad("Grad1"));
		a.setUlica("Ulica1");
		a.setLatitude(1.1);
		a.setLongitude(1.1);
		Filijala filijala = new Filijala();
		filijala.setAdresa(a);
		filijala.setId(1);
		
		vozilo.setFilijala(filijala);
		mockMvc.perform(post(URL_PREFIX + "/admin/dodajVozilo/1/Novo/Vozilo/Dodato/2019/5/tip/505")).equals(vozilo);
	}
	
	@Test
	@WithMockUser(roles="RENTADMIN")
	@Transactional
	@Rollback(true)
	public void testDeleteVozilo() throws Exception {
		Vozilo vozilo= new Vozilo();
		vozilo.setId(2);
		vozilo.setNaziv("Vozilo2");
		vozilo.setMarka("V2");
		vozilo.setModel("VV");
		vozilo.setGodina_proizvodnje(2011);
		vozilo.setBroj_sedista(5);
		vozilo.setTip("Tip vozila 2");
		vozilo.setCena_dan(400);
		vozilo.setProsecna_ocena(4.5);
		vozilo.setCena_popust(0);
		vozilo.setZauzeto(false);
		
		Adresa a = new Adresa();
		a.setBroj("1");
		a.setGrad(new Grad("Grad1"));
		a.setUlica("Ulica1");
		a.setLatitude(1.1);
		a.setLongitude(1.1);
		Filijala filijala = new Filijala();
		filijala.setAdresa(a);
		filijala.setId(1);
		
		vozilo.setFilijala(filijala);
		mockMvc.perform(post(URL_PREFIX + "admin/delete/" + VoziloConstants.DB_ID_V)).equals(vozilo);
	}
	
	

}
