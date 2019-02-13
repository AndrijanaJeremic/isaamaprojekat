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

import com.example.ISA_AMA_projekat.constants.VoziloConstants;
import com.example.ISA_AMA_projekat.model.Adresa;
import com.example.ISA_AMA_projekat.model.Filijala;
import com.example.ISA_AMA_projekat.model.Grad;
import com.example.ISA_AMA_projekat.model.Vozilo;
import com.example.ISA_AMA_projekat.repository.VoziloRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VoziloServiceTest {

	@Mock
	private VoziloRepository carRepositoryMock;
	
	@Mock
	private Vozilo carMock;
	
	@InjectMocks
	private VoziloService carService;
	
	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateRent() {
		Vozilo v = new Vozilo();
		v.setId(VoziloConstants.DB_ID_V);
		Filijala fil = new Filijala();
		fil.setId(3);
		Adresa adresa = new Adresa();
		adresa.setUlica("Ulica3");
		adresa.setBroj("3");
		adresa.setGrad(new Grad("Grad3"));
		adresa.setId(3);
		adresa.setLatitude(3.3);
		adresa.setLongitude(3.3);
		
		v.setFilijala(fil);
		
		when(carRepositoryMock.findById(VoziloConstants.DB_ID_V)).thenReturn(Optional.of(v));
		
		Vozilo dbCar = carService.findById(VoziloConstants.DB_ID_V).get();
		dbCar.setFilijala(fil);
	    
		doNothing().when(carRepositoryMock).updateFilVozilo(fil.getId(), VoziloConstants.DB_ID_V);
		carService.updateFil(fil.getId(), VoziloConstants.DB_ID_V);
		
		when(carRepositoryMock.findById(VoziloConstants.DB_ID_V)).thenReturn(Optional.of(dbCar));
		
		Vozilo carChanged = carService.findById(VoziloConstants.DB_ID_V).get();
		
		assertEquals(dbCar, carChanged);
	    verify(carRepositoryMock, times(2)).findById(VoziloConstants.DB_ID_V);
	    verify(carRepositoryMock, times(1)).updateFilVozilo(fil.getId(), VoziloConstants.DB_ID_V);
	    verifyNoMoreInteractions(carRepositoryMock);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testDelete() {
		
		Vozilo v1=new Vozilo();
		v1.setId(VoziloConstants.DB_ID_V);
		v1.setNaziv(VoziloConstants.DB_NAZIV_V);
		v1.setMarka(VoziloConstants.DB_MARKA_V);
		v1.setModel(VoziloConstants.DB_MODEL_V);
		v1.setTip(VoziloConstants.DB_TIP_V);
		v1.setBroj_sedista(VoziloConstants.DB_BR_SEDISTA_V);
		v1.setGodina_proizvodnje(VoziloConstants.DB_GOD_PROIZ_V);
		v1.setCena_dan(VoziloConstants.DB_CENA_V);
		v1.setProsecna_ocena(VoziloConstants.DB_PROSECNA_V);
		
		Vozilo v3=new Vozilo();
		v3.setId(2);
		v3.setNaziv("Vozilo2");
		v3.setMarka("V2");
		v3.setModel("VV");
		v3.setTip("Tip vozila 2");
		v3.setBroj_sedista(5);
		v3.setGodina_proizvodnje(2011);
		v3.setCena_dan(400);
		v3.setProsecna_ocena(4.5);
		v3.setCena_popust(0);
		v3.setZauzeto(false);
		
		
		when(carRepositoryMock.findAll()).thenReturn(Arrays.asList(v1, v3));
		int dbSizeBeforeRemove = carService.findAll().size();
		
		
		doNothing().when(carRepositoryMock).delete(v3);
		carService.deleteVozilo(v3);;
		
		when(carRepositoryMock.findAll()).thenReturn(Arrays.asList(v1));
		List<Vozilo> cars = carService.findAll();
		assertThat(cars).hasSize(dbSizeBeforeRemove - 1);
		
		when(carRepositoryMock.findById(v3.getId())).thenReturn(null);
		Optional<Vozilo> dbVozilo = carService.findById(v3.getId());
		assertThat(dbVozilo).isNull();
		verify(carRepositoryMock, times(1)).delete(v3);
		verify(carRepositoryMock, times(2)).findAll();
        verify(carRepositoryMock, times(1)).findById(v3.getId());
        verifyNoMoreInteractions(carRepositoryMock);
	}

}
