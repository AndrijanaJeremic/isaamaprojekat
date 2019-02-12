package com.example.ISA_AMA_projekat.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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


import com.example.ISA_AMA_projekat.constants.FilijalaConstants;
import com.example.ISA_AMA_projekat.model.Adresa;
import com.example.ISA_AMA_projekat.model.Filijala;
import com.example.ISA_AMA_projekat.model.Grad;
import com.example.ISA_AMA_projekat.model.RentacarServis;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FilijalaControllerTest {

	private static final String URL_PREFIX = "/api/filijale";

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
	public void testGetFilijala() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/" + FilijalaConstants.DB_ID_F)).andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$.id").value(FilijalaConstants.DB_ID_F))
		.andExpect(jsonPath("$.adresa.id").value(FilijalaConstants.DB_ADRESA_ID));
	}
	
	@Test
	@WithMockUser(roles="RENTADMIN")
	@Transactional
	@Rollback(true)
	public void testEditFilijala() throws Exception {
		Adresa a = new Adresa();
		a.setBroj("1");
		a.setGrad(new Grad("Grad2"));
		a.setUlica("Ulica1");
		a.setLatitude(1.3);
		a.setLongitude(1.3);
		Filijala filijala = new Filijala();
		filijala.setAdresa(a);
		filijala.setId(1);
		mockMvc.perform(post(URL_PREFIX + "/admin/izmenaFil/1/Ulica1/1/Grad2/1.3/1.3")).equals(filijala);
	}
	
	
	@Test
	@WithMockUser(roles="RENTADMIN")
	@Transactional
	@Rollback(true)
	public void testAddFilijala() throws Exception {
		Adresa a = new Adresa();
		a.setBroj("1");
		a.setGrad(new Grad("Grad7"));
		a.setUlica("Ulica8");
		a.setLatitude(1.5);
		a.setLongitude(1.5);
		
		Adresa a1 = new Adresa();
		a1.setBroj("1");
		a1.setGrad(new Grad("Grad1"));
		a1.setUlica("Ulica1");
		a1.setLatitude(1.1);
		a1.setLongitude(1.1);
		
		Filijala filijala = new Filijala();
		filijala.setAdresa(a);
		filijala.setRentacar(new RentacarServis(1, "Rent1", a1, "Rent opis 1", 3.2 ));
		filijala.setId(4);
		mockMvc.perform(post(URL_PREFIX + "/dodajFil/Ulica8/1/Grad7/1/1.5/1.5")).equals(filijala);
	}
	
	@Test
	@WithMockUser(roles="RENTADMIN")
	@Transactional
	@Rollback(true)
	public void testDeleteFilijala() throws Exception {
		Adresa a = new Adresa();
		a.setBroj("1");
		a.setGrad(new Grad("Grad1"));
		a.setUlica("Ulica1");
		a.setLatitude(1.1);
		a.setLongitude(1.1);
		Filijala filijala = new Filijala();
		filijala.setAdresa(a);
		filijala.setId(1);
		this.mockMvc.perform(delete(URL_PREFIX + "admin/delete/" + FilijalaConstants.DB_ID_F)).equals(filijala);
	}
	
}
