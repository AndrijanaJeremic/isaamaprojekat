package com.example.ISA_AMA_projekat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.constants.SobaConstants;
import com.example.ISA_AMA_projekat.model.Soba;
import com.example.ISA_AMA_projekat.repository.SobaRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SobaServiceTest {
	
	@Mock
	private SobaRepository sobaRepositoryMock;
	
	@Mock
	private Soba sobaMock;
	
	@InjectMocks
	private SobaService sobaService;
	
	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteRoom() {
		
		Soba soba = new Soba();
		soba.setId(SobaConstants.DB_ID);
		soba.setBroj_kreveta(SobaConstants.DB_BROJ_KREVETA);
		soba.setCena_nocenja(SobaConstants.DB_CENA_NOCENJA);
		soba.setCena_popust(SobaConstants.DB_CENA_POPUST);
		soba.setProsecna_ocena(SobaConstants.DB_PROSECNA_OCENA);
		soba.setOpis(SobaConstants.DB_OPIS);
		
		Soba soba1 = new Soba();
		soba.setId(SobaConstants.DB_ID1);
		soba.setBroj_kreveta(SobaConstants.DB_BROJ_KREVETA1);
		soba.setCena_nocenja(SobaConstants.DB_CENA_NOCENJA1);
		soba.setCena_popust(SobaConstants.DB_CENA_POPUST1);
		soba.setProsecna_ocena(SobaConstants.DB_PROSECNA_OCENA1);
		soba.setOpis(SobaConstants.DB_OPIS1);
		
		
		when(sobaRepositoryMock.findAll()).thenReturn(Arrays.asList(soba, soba1));
		int dbSizeBeforeRemove = sobaService.findAll().size();
		
		
		doNothing().when(sobaRepositoryMock).delete(soba1);
		sobaService.deleteRoom(soba1);
		
		when(sobaRepositoryMock.findAll()).thenReturn(Arrays.asList(soba));
		Collection<Soba> sobe = sobaService.findAll();
		assertThat(sobe).hasSize(dbSizeBeforeRemove - 1);
		
		when(sobaRepositoryMock.findById(soba1.getId())).thenReturn(null);
		Optional<Soba> dbSoba = sobaService.findById(soba1.getId());
		assertThat(dbSoba).isNull();
		
		verify(sobaRepositoryMock, times(1)).delete(soba1);
		verify(sobaRepositoryMock, times(2)).findAll();
	    verify(sobaRepositoryMock, times(1)).findById(soba1.getId());
	    verifyNoMoreInteractions(sobaRepositoryMock);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateProsecnaSoba() {
		
		Soba soba = new Soba();
		soba.setId(SobaConstants.DB_ID);
		soba.setProsecna_ocena(5);
		
		when(sobaRepositoryMock.findById(SobaConstants.DB_ID)).thenReturn(Optional.of(soba));
		
		Soba dbSoba = sobaService.findById(SobaConstants.DB_ID).get();
		dbSoba.setProsecna_ocena(5);
        
		doNothing().when(sobaRepositoryMock).updateProsecnaSoba(5, SobaConstants.DB_ID);
		sobaService.updateProsecnaSoba(5, SobaConstants.DB_ID);
		
		when(sobaRepositoryMock.findById(SobaConstants.DB_ID)).thenReturn(Optional.of(dbSoba));
		
		Soba sobaChanged = sobaService.findById(SobaConstants.DB_ID).get();
		
		assertEquals(dbSoba, sobaChanged);
        verify(sobaRepositoryMock, times(3)).findById(SobaConstants.DB_ID);
        verify(sobaRepositoryMock, times(1)).save(sobaChanged);
        verifyNoMoreInteractions(sobaRepositoryMock);
	}

}
