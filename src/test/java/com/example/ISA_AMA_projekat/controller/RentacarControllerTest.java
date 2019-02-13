package com.example.ISA_AMA_projekat.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.example.ISA_AMA_projekat.TestUtil;
import com.example.ISA_AMA_projekat.constants.RentacarServisConstants;
import com.example.ISA_AMA_projekat.constants.RezervacijaVozilaConstants;
import com.example.ISA_AMA_projekat.constants.VoziloConstants;
import com.example.ISA_AMA_projekat.model.Adresa;
import com.example.ISA_AMA_projekat.model.Grad;
import com.example.ISA_AMA_projekat.model.RentacarServis;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RentacarControllerTest {

	private static final String URL_PREFIX = "/api/rents";

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
	public void testGetRentacarServis() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/all")).andExpect(status().isOk())
		.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", hasSize(RentacarServisConstants.DB_COUNT_R)))
		.andExpect(jsonPath("$.[*].id").value(hasItem(RentacarServisConstants.DB_ID_R.intValue())))
		.andExpect(jsonPath("$.[*].naziv").value(hasItem(RentacarServisConstants.DB_NAZIV_R)))
		.andExpect(jsonPath("$.[*].promotivni_opis").value(hasItem(RentacarServisConstants.DB_PROMOTIVNI_OPIS_R)))
		.andExpect(jsonPath("$.[*].prosecna_ocena").value(hasItem(RentacarServisConstants.DB_PROSECNA_OCENA_R)));
	}
	
	
	@Test
	@WithMockUser(roles="SYSADMIN")
	public void testGetRentacarServisForAdmin() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/all_admin")).andExpect(status().isOk())
		.andExpect(content().contentType(contentType)).andExpect(jsonPath("$", hasSize(RentacarServisConstants.DB_RENT_ADMIN_1_R)))
		.andExpect(jsonPath("$.[*].id").value(hasItem(RentacarServisConstants.DB_ID_R.intValue())))
		.andExpect(jsonPath("$.[*].naziv").value(hasItem(RentacarServisConstants.DB_NAZIV_R)))
		.andExpect(jsonPath("$.[*].promotivni_opis").value(hasItem(RentacarServisConstants.DB_PROMOTIVNI_OPIS_R)))
		.andExpect(jsonPath("$.[*].prosecna_ocena").value(hasItem(RentacarServisConstants.DB_PROSECNA_OCENA_R)));
	}
	
	@Test
	@WithMockUser(roles="RENTADMIN")
	public void testGetRentacarServisAdm() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/admin/1")).andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$.id").value(RentacarServisConstants.DB_ID_R.intValue()))
		.andExpect(jsonPath("$.naziv").value(RentacarServisConstants.DB_NAZIV_R))
		.andExpect(jsonPath("$.promotivni_opis").value(RentacarServisConstants.DB_PROMOTIVNI_OPIS_R))
		.andExpect(jsonPath("$.prosecna_ocena").value(RentacarServisConstants.DB_PROSECNA_OCENA_R));
	}
	
	@Test
	@WithMockUser(roles="SYSADMIN")
	@Transactional
	@Rollback(true)
	public void testSave() throws Exception {
		RentacarServis rentacar = new RentacarServis();
		rentacar.setNaziv(RentacarServisConstants.NEW_NAZIV_R);
		rentacar.setPromotivni_opis(RentacarServisConstants.NEW_PROMOTIVNI_OPIS_R);
		rentacar.setProsecna_ocena(RentacarServisConstants.NEW_PROSECNA_OCENA_R);

		String json = TestUtil.json(rentacar);
		this.mockMvc.perform(post(URL_PREFIX + "/save").contentType(contentType).content(json)).andExpect(status().isCreated());
	}
	
	@Test
	public void testGetNumberOfDaysCar() throws Exception {
		mockMvc.perform(post(URL_PREFIX + "/get_number_of_days/2019-02-05/2019-02-10")).andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$").value(Integer.valueOf(6)));
	}
	
	@Test
	public void testGetCarFromRez() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/getVozilo/" + RezervacijaVozilaConstants.DB_ID_RV)).andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$.id").value(VoziloConstants.DB_ID_V))
		.andExpect(jsonPath("$.naziv").value(VoziloConstants.DB_NAZIV_V))
		.andExpect(jsonPath("$.model").value(VoziloConstants.DB_MODEL_V))
		.andExpect(jsonPath("$.cena_dan").value(VoziloConstants.DB_CENA_V))
		.andExpect(jsonPath("$.broj_sedista").value(VoziloConstants.DB_BR_SEDISTA_V))
		.andExpect(jsonPath("$.marka").value(VoziloConstants.DB_MARKA_V));
	}
	
	@Test
	public void testGetRentFromRez() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/getRent/" + RezervacijaVozilaConstants.DB_ID_RV)).andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$.id").value(RentacarServisConstants.DB_ID_R))
		.andExpect(jsonPath("$.naziv").value(RentacarServisConstants.DB_NAZIV_R))
		.andExpect(jsonPath("$.promotivni_opis").value(RentacarServisConstants.DB_PROMOTIVNI_OPIS_R))
		.andExpect(jsonPath("$.prosecna_ocena").value(RentacarServisConstants.DB_PROSECNA_OCENA_R));
	}
	
	@Test
	public void testGetServis() throws Exception {
		mockMvc.perform(get(URL_PREFIX + "/1/2019-07-10/Grad1/2019-07-20/Grad1/2")).andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$.id").value(RentacarServisConstants.DB_ID_R))
		.andExpect(jsonPath("$.naziv").value(RentacarServisConstants.DB_NAZIV_R))
		.andExpect(jsonPath("$.promotivni_opis").value(RentacarServisConstants.DB_PROMOTIVNI_OPIS_R))
		.andExpect(jsonPath("$.prosecna_ocena").value(RentacarServisConstants.DB_PROSECNA_OCENA_R));
		
		
	}
	
	
	@Test
	@WithMockUser(roles="RENTADMIN")
	public void testGetAllRezAndTotal() throws Exception {
		double total = 0;
		mockMvc.perform(get(URL_PREFIX + "/sveRezervacijeVozila/total/1")).equals(total);
		
		
	}
	
	@Test
	@WithMockUser(roles="RENTADMIN")
	public void testLast7Days() throws Exception {
		int dani[] = {0,0,0,0,0,0,0};
		mockMvc.perform(get(URL_PREFIX + "/last7days/1")).equals(dani);
		
		
	}
	
	
	
	
	

	

}
