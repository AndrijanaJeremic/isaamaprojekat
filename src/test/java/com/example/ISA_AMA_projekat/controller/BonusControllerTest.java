package com.example.ISA_AMA_projekat.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.example.ISA_AMA_projekat.TestUtil;
import com.example.ISA_AMA_projekat.constants.BonusConstants;
import com.example.ISA_AMA_projekat.model.Bonus;

@RunWith(SpringRunner.class)
@ContextConfiguration
@WebAppConfiguration
@SpringBootTest
public class BonusControllerTest {
	
	private static final String URL_PREFIX = "/api/bonus";
	
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
	@WithMockUser(roles="SYSADMIN")
	@Transactional
	@Rollback(true)
	public void testSaveBonus() throws Exception {
		Bonus bonus = new Bonus();
		bonus.setBonusPoeni(BonusConstants.DB_BONUS_POENI);
		bonus.setPopust(BonusConstants.DB_POPUST);

		String json = TestUtil.json(bonus);
		this.mockMvc.perform(post(URL_PREFIX + "/save").contentType(contentType).content(json)).andExpect(status().isAccepted());
	}

}
