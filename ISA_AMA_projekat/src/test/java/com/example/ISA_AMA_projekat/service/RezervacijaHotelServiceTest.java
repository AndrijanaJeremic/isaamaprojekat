package com.example.ISA_AMA_projekat.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.constants.RezervacijaHotelConstants;
import com.example.ISA_AMA_projekat.constants.UslugaConstants;
import com.example.ISA_AMA_projekat.model.RezervacijaHotel;
import com.example.ISA_AMA_projekat.model.Usluga;
import com.example.ISA_AMA_projekat.repository.RezervacijaHotelRepository;
import com.example.ISA_AMA_projekat.repository.UslugaRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RezervacijaHotelServiceTest {

	@Mock
	private RezervacijaHotelRepository rezervacijaHotelRepositoryMock;
	
	@Mock
	private UslugaRepository uslugaRepositoryMock;
	
	@Mock
	private RezervacijaHotel rezervacijaHotelMock;
	
	@InjectMocks
	private RezervacijaHotelService rezervacijaHotelService;
	
	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateUsluga() {
		
		RezervacijaHotel rez = new RezervacijaHotel();
		rez.setId(RezervacijaHotelConstants.DB_ID);
		rez.setBroj_nocenja(RezervacijaHotelConstants.DB_BROJ_NOCENJA);
		rez.setBrza(RezervacijaHotelConstants.DB_BRZA);
		rez.setAktivirana(RezervacijaHotelConstants.DB_AKTIVIRANA);
		
		Usluga usluga = new Usluga();
		usluga.setId(UslugaConstants.DB_ID);
		usluga.setCena(UslugaConstants.DB_CENA);
		usluga.setNaziv(UslugaConstants.DB_NAZIV);
		usluga.setPopust(UslugaConstants.DB_POPUST);

		Set<Usluga> usluge = new HashSet<Usluga>();
		usluge.add(usluga);
		rez.setUsluge(new HashSet<Usluga>());
		
		when(rezervacijaHotelRepositoryMock.findById(RezervacijaHotelConstants.DB_ID)).thenReturn(Optional.of(rez));
		
		RezervacijaHotel dbRez = rezervacijaHotelService.findById(RezervacijaHotelConstants.DB_ID).get();
		dbRez.getUsluge().add(usluga);
		
		when(uslugaRepositoryMock.findById(UslugaConstants.DB_ID)).thenReturn(Optional.of(usluga));
		when(rezervacijaHotelRepositoryMock.findById(RezervacijaHotelConstants.DB_ID)).thenReturn(Optional.of(dbRez));
		rezervacijaHotelService.updateUsluga(UslugaConstants.DB_ID, RezervacijaHotelConstants.DB_ID);
		
		when(rezervacijaHotelRepositoryMock.findById(RezervacijaHotelConstants.DB_ID)).thenReturn(Optional.of(dbRez));
		
		RezervacijaHotel rezChanged = rezervacijaHotelService.findById(RezervacijaHotelConstants.DB_ID).get();
		
		assertEquals(dbRez, rezChanged);
		assertEquals(1, rezChanged.getUsluge().size());
        verify(rezervacijaHotelRepositoryMock, times(3)).findById(RezervacijaHotelConstants.DB_ID);
        verify(rezervacijaHotelRepositoryMock, times(1)).save(rezChanged);
        verifyNoMoreInteractions(rezervacijaHotelRepositoryMock);
        
        verify(uslugaRepositoryMock, times(1)).findById(UslugaConstants.DB_ID);
        verifyNoMoreInteractions(uslugaRepositoryMock);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testRemoveUsluga() {
		
		RezervacijaHotel rez = new RezervacijaHotel();
		rez.setId(RezervacijaHotelConstants.DB_ID);
		rez.setBroj_nocenja(RezervacijaHotelConstants.DB_BROJ_NOCENJA);
		rez.setBrza(RezervacijaHotelConstants.DB_BRZA);
		rez.setAktivirana(RezervacijaHotelConstants.DB_AKTIVIRANA);
		
		Usluga usluga = new Usluga();
		usluga.setId(UslugaConstants.DB_ID);
		usluga.setCena(UslugaConstants.DB_CENA);
		usluga.setNaziv(UslugaConstants.DB_NAZIV);
		usluga.setPopust(UslugaConstants.DB_POPUST);

		Set<Usluga> usluge = new HashSet<Usluga>();
		usluge.add(usluga);
		rez.setUsluge(usluge);
		
		when(rezervacijaHotelRepositoryMock.findById(RezervacijaHotelConstants.DB_ID)).thenReturn(Optional.of(rez));
		
		RezervacijaHotel dbRez = rezervacijaHotelService.findById(RezervacijaHotelConstants.DB_ID).get();
		dbRez.getUsluge().remove(usluga);
		
		when(uslugaRepositoryMock.findById(UslugaConstants.DB_ID)).thenReturn(Optional.of(usluga));
		when(rezervacijaHotelRepositoryMock.findById(RezervacijaHotelConstants.DB_ID)).thenReturn(Optional.of(dbRez));
		rezervacijaHotelService.removeUsluga(UslugaConstants.DB_ID, RezervacijaHotelConstants.DB_ID);
		
		when(rezervacijaHotelRepositoryMock.findById(RezervacijaHotelConstants.DB_ID)).thenReturn(Optional.of(dbRez));
		
		RezervacijaHotel rezChanged = rezervacijaHotelService.findById(RezervacijaHotelConstants.DB_ID).get();
		
		assertEquals(dbRez, rezChanged);
		assertEquals(0, rezChanged.getUsluge().size());
        verify(rezervacijaHotelRepositoryMock, times(3)).findById(RezervacijaHotelConstants.DB_ID);
        verify(rezervacijaHotelRepositoryMock, times(1)).save(rezChanged);
        verifyNoMoreInteractions(rezervacijaHotelRepositoryMock);
        
        verify(uslugaRepositoryMock, times(1)).findById(UslugaConstants.DB_ID);
        verifyNoMoreInteractions(uslugaRepositoryMock);
	}
}
