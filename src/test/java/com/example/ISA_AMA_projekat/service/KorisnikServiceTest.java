package com.example.ISA_AMA_projekat.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.constants.FilijalaConstants;
import com.example.ISA_AMA_projekat.constants.KorisnikConstants;
import com.example.ISA_AMA_projekat.model.Adresa;
import com.example.ISA_AMA_projekat.model.Filijala;
import com.example.ISA_AMA_projekat.model.Grad;
import com.example.ISA_AMA_projekat.model.Korisnik;
import com.example.ISA_AMA_projekat.model.RentacarServis;
import com.example.ISA_AMA_projekat.repository.KorisnikRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KorisnikServiceTest {

	@Mock
	private KorisnikRepository userRepositoryMock;
	
	@Mock
	private Korisnik userMock;
	
	@InjectMocks
	private KorisnikService userService;
	
	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateAkt() {
		
		Korisnik user = new Korisnik();
		user.setId(KorisnikConstants.DB_ID_K);
		user.setEmail(KorisnikConstants.DB_EMAIL_K);
		user.setLozinka(KorisnikConstants.DB_LOZINKA_K);
		user.setIme(KorisnikConstants.DB_IME_K);
		user.setPrezime(KorisnikConstants.DB_PREZIME_K);
		user.setTelefon(KorisnikConstants.NEW_TELEFON_K);
		user.setBonus_poeni(KorisnikConstants.DB_BONUS_K);
		user.setAktiviran(true);
		
		when(userRepositoryMock.findById(KorisnikConstants.DB_ID_K)).thenReturn(Optional.of(user));
		
		Korisnik dbUser = userService.findById(KorisnikConstants.DB_ID_K).get();
		dbUser.setAktiviran(true);
	    
		doNothing().when(userRepositoryMock).updateAktiviran(true, KorisnikConstants.DB_ID_K);
		userService.updateAkt(true, KorisnikConstants.DB_ID_K);
		
		when(userRepositoryMock.findById(KorisnikConstants.DB_ID_K)).thenReturn(Optional.of(dbUser));
		
		Korisnik userChanged = userService.findById(KorisnikConstants.DB_ID_K).get();
		
		assertEquals(dbUser, userChanged);
	    verify(userRepositoryMock, times(2)).findById(KorisnikConstants.DB_ID_K);
	    verify(userRepositoryMock, times(1)).updateAktiviran(true, KorisnikConstants.DB_ID_K);
	    verifyNoMoreInteractions(userRepositoryMock);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testSetBonus() {
		
		Korisnik user = new Korisnik();
		user.setId(KorisnikConstants.DB_ID_K);
		user.setEmail(KorisnikConstants.DB_EMAIL_K);
		user.setLozinka(KorisnikConstants.DB_LOZINKA_K);
		user.setIme(KorisnikConstants.DB_IME_K);
		user.setPrezime(KorisnikConstants.DB_PREZIME_K);
		user.setTelefon(KorisnikConstants.NEW_TELEFON_K);
		user.setBonus_poeni(20);
		user.setAktiviran(false);
		
		when(userRepositoryMock.findById(KorisnikConstants.DB_ID_K)).thenReturn(Optional.of(user));
		
		Korisnik dbUser = userService.findById(KorisnikConstants.DB_ID_K).get();
		dbUser.setBonus_poeni(20);;
	    
		doNothing().when(userRepositoryMock).updateBonusPoints(user.getBonus_poeni(), KorisnikConstants.DB_ID_K);
		userService.updateBonusPoints(user.getBonus_poeni(), KorisnikConstants.DB_ID_K);
		
		when(userRepositoryMock.findById(KorisnikConstants.DB_ID_K)).thenReturn(Optional.of(dbUser));
		
		Korisnik userChanged = userService.findById(KorisnikConstants.DB_ID_K).get();
		
		assertEquals(dbUser, userChanged);
	    verify(userRepositoryMock, times(2)).findById(KorisnikConstants.DB_ID_K);
	    verify(userRepositoryMock, times(1)).updateBonusPoints(user.getBonus_poeni(), KorisnikConstants.DB_ID_K);
	    verifyNoMoreInteractions(userRepositoryMock);
	}
	
}
