package com.example.ISA_AMA_projekat.controller;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.example.ISA_AMA_projekat.TestUtil;
import com.example.ISA_AMA_projekat.constants.KorisnikConstants;
import com.example.ISA_AMA_projekat.model.Grad;
import com.example.ISA_AMA_projekat.model.Korisnik;


@RunWith(SpringRunner.class)
@SpringBootTest
public class KorisnikControllerTest {

	private static final String URL_PREFIX = "/api/users";

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	
	
	@WithMockUser(roles="SYSADMIN")
	@Test
	@Transactional
	@Rollback(true)
	public void testRegistrujAdmina() throws Exception {
		Korisnik korisnik = new Korisnik();
		korisnik.setEmail(KorisnikConstants.NEW_EMAIL_K);
		korisnik.setLozinka(KorisnikConstants.NEW_LOZINKA_K);
		korisnik.setIme(KorisnikConstants.NEW_IME_K);
		korisnik.setPrezime(KorisnikConstants.NEW_PREZIME_K);
		korisnik.setTelefon(KorisnikConstants.NEW_TELEFON_K);
		korisnik.setBonuspoeni(KorisnikConstants.NEW_BONUS_K);
		korisnik.setAktiviran(KorisnikConstants.NEW_AKTIVIRAN_K);
		
		Grad grad=new Grad("Novi Grad");
		korisnik.setGrad(grad);
		korisnik.setAdmin_id(1);
		

		String json = TestUtil.json(korisnik);
		this.mockMvc.perform(post(URL_PREFIX + "/registruj_admina/3").contentType(contentType).content(json))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.email").value(KorisnikConstants.NEW_EMAIL_K));
	}
	
	
}
