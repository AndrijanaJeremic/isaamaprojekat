package com.example.ISA_AMA_projekat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
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
import com.example.ISA_AMA_projekat.constants.RentacarServisConstants;
import com.example.ISA_AMA_projekat.constants.VoziloConstants;
import com.example.ISA_AMA_projekat.model.Adresa;
import com.example.ISA_AMA_projekat.model.Filijala;
import com.example.ISA_AMA_projekat.model.Grad;
import com.example.ISA_AMA_projekat.model.RentacarServis;
import com.example.ISA_AMA_projekat.model.Vozilo;
import com.example.ISA_AMA_projekat.repository.FilijalaRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FilijalaServiceTest {

	@Mock
	private FilijalaRepository filRepositoryMock;
	
	@Mock
	private Filijala filMock;
	
	@InjectMocks
	private FilijalaService filService;
	
	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateFilijala() {
		
		Filijala fil = new Filijala();
		fil.setId(FilijalaConstants.DB_ID_F);
		Adresa adresa = new Adresa();
		adresa.setUlica("Ulica2");
		adresa.setBroj("2");
		adresa.setGrad(new Grad("Grad2"));
		adresa.setId(2);
		adresa.setLatitude(2.2);
		adresa.setLongitude(2.2);
		fil.setAdresa(adresa);
		
		when(filRepositoryMock.findById(FilijalaConstants.DB_ID_F)).thenReturn(Optional.of(fil));
		
		Filijala dbFil = filService.findById(FilijalaConstants.DB_ID_F).get();
		dbFil.setAdresa(adresa);
        
		doNothing().when(filRepositoryMock).updateFilijala(adresa.getId(), FilijalaConstants.DB_ID_F);
		filService.updateFilijala(adresa.getId(), FilijalaConstants.DB_ID_F);
		
		when(filRepositoryMock.findById(FilijalaConstants.DB_ID_F)).thenReturn(Optional.of(dbFil));
		
		Filijala filChanged = filService.findById(FilijalaConstants.DB_ID_F).get();
		
		assertEquals(dbFil, filChanged);
        verify(filRepositoryMock, times(2)).findById(FilijalaConstants.DB_ID_F);
        verify(filRepositoryMock, times(1)).updateFilijala(adresa.getId(), FilijalaConstants.DB_ID_F);
        verifyNoMoreInteractions(filRepositoryMock);
	}



@Test
@Transactional
@Rollback(true)
public void testUpdateRent() {
	
	Filijala fil = new Filijala();
	fil.setId(FilijalaConstants.DB_ID_F);
	Adresa adresa = new Adresa();
	adresa.setUlica("Ulica2");
	adresa.setBroj("2");
	adresa.setGrad(new Grad("Grad2"));
	adresa.setId(2);
	adresa.setLatitude(2.2);
	adresa.setLongitude(2.2);
	RentacarServis rent = new RentacarServis(2, "Rent2", adresa, "Rent opis 2", 4.2);
	fil.setRentacar(rent);
	
	when(filRepositoryMock.findById(FilijalaConstants.DB_ID_F)).thenReturn(Optional.of(fil));
	
	Filijala dbFil = filService.findById(FilijalaConstants.DB_ID_F).get();
	dbFil.setRentacar(rent);
    
	doNothing().when(filRepositoryMock).updateRentacarFil(rent.getId(), FilijalaConstants.DB_ID_F);
	filService.updateRent(rent.getId(), FilijalaConstants.DB_ID_F);
	
	when(filRepositoryMock.findById(FilijalaConstants.DB_ID_F)).thenReturn(Optional.of(dbFil));
	
	Filijala filChanged = filService.findById(FilijalaConstants.DB_ID_F).get();
	
	assertEquals(dbFil, filChanged);
    verify(filRepositoryMock, times(2)).findById(FilijalaConstants.DB_ID_F);
    verify(filRepositoryMock, times(1)).updateRentacarFil(rent.getId(), FilijalaConstants.DB_ID_F);
    verifyNoMoreInteractions(filRepositoryMock);
}

@Test
@Transactional
@Rollback(true)
public void testDelete() {
	
	Filijala fil = new Filijala();
	fil.setId(FilijalaConstants.DB_ID_F);
	Adresa adresa = new Adresa();
	adresa.setUlica("Ulica1");
	adresa.setBroj("1");
	adresa.setGrad(new Grad("Grad1"));
	adresa.setId(1);
	adresa.setLatitude(1.1);
	adresa.setLongitude(1.1);
	fil.setAdresa(adresa);
	
	Filijala fil2 = new Filijala();
	fil2.setId(2);
	Adresa adresa1 = new Adresa();
	adresa1.setUlica("Ulica2");
	adresa1.setBroj("2");
	adresa1.setGrad(new Grad("Grad2"));
	adresa1.setId(2);
	adresa1.setLatitude(2.2);
	adresa1.setLongitude(2.2);
	fil2.setAdresa(adresa1);
	
	when(filRepositoryMock.findAll()).thenReturn(Arrays.asList(fil, fil2));
	int dbSizeBeforeRemove = filService.findAll().size();
	
	
	doNothing().when(filRepositoryMock).delete(fil2);
	filService.deleteFilijala(fil2);
	
	when(filRepositoryMock.findAll()).thenReturn(Arrays.asList(fil));
	List<Filijala> fils = filService.findAll();
	assertThat(fils).hasSize(dbSizeBeforeRemove - 1);
	
	when(filRepositoryMock.findById(fil2.getId())).thenReturn(null);
	Optional<Filijala> dbFil = filService.findById(fil2.getId());
	assertThat(dbFil).isNull();
	verify(filRepositoryMock, times(1)).delete(fil2);
	verify(filRepositoryMock, times(2)).findAll();
    verify(filRepositoryMock, times(1)).findById(fil2.getId());
    verifyNoMoreInteractions(filRepositoryMock);
}

}

