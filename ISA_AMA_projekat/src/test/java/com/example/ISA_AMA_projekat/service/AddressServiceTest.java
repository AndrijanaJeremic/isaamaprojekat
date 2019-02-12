package com.example.ISA_AMA_projekat.service;

import static org.junit.Assert.assertEquals;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.constants.AddressConstants;
import com.example.ISA_AMA_projekat.constants.HotelConstants;
import com.example.ISA_AMA_projekat.model.Adresa;
import com.example.ISA_AMA_projekat.model.Grad;
import com.example.ISA_AMA_projekat.repository.AddressRepository;
import com.example.ISA_AMA_projekat.repository.GradRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AddressServiceTest {
	
	@Mock
	private AddressRepository addressRepositoryMock;
	
	@Mock
	private GradRepository gradRepositoryMock;
	
	@Mock
	private Adresa addressMock;
	
	@InjectMocks
	private AddressService addressService;
	
	@Test
	public void testCheckAddress() {
		Grad grad = new Grad();
		grad.setId(AddressConstants.DB_ID_GR);
		grad.setNaziv(AddressConstants.DB_GRAD);
		
		Adresa adresa = new Adresa();
		adresa.setUlica(AddressConstants.DB_ULICA);
		adresa.setBroj(AddressConstants.DB_BROJ);
		adresa.setGrad(grad);
		adresa.setId(AddressConstants.DB_ID_AD);
		adresa.setLatitude(AddressConstants.DB_LAT);
		adresa.setLongitude(AddressConstants.DB_LONG);
		
		when(addressRepositoryMock.checkAddress(AddressConstants.DB_ID_GR, AddressConstants.DB_ULICA, AddressConstants.DB_BROJ)).thenReturn(Arrays.asList(adresa));
		
		List<Adresa> adrese = addressService.checkAddress(AddressConstants.DB_ID_GR, AddressConstants.DB_ULICA, AddressConstants.DB_BROJ);
		
		assertEquals(1, adrese.size());
	    verify(addressRepositoryMock, times(1)).checkAddress(AddressConstants.DB_ID_GR, AddressConstants.DB_ULICA, AddressConstants.DB_BROJ);
	    verifyNoMoreInteractions(addressRepositoryMock);
	}
	
	@Test
	@Transactional
	public void testUpdateAdresa() {
		
		Grad grad = new Grad();
		grad.setId(AddressConstants.DB_ID_GR);
		grad.setNaziv(AddressConstants.DB_GRAD);
		
		Adresa adresa = new Adresa();
		adresa.setUlica(AddressConstants.DB_ULICA);
		adresa.setBroj(AddressConstants.DB_BROJ);
		adresa.setGrad(grad);
		adresa.setId(AddressConstants.DB_ID_AD);
		adresa.setLatitude(AddressConstants.DB_LAT);
		adresa.setLongitude(AddressConstants.DB_LONG);
		
		when(addressRepositoryMock.findById(AddressConstants.DB_ID_AD)).thenReturn(Optional.of(adresa));
		
		Adresa dbAdresa = addressService.findById(AddressConstants.DB_ID_AD).get();
		dbAdresa.setBroj(AddressConstants.DB_BROJ);
		dbAdresa.setUlica(AddressConstants.DB_ULICA);
		
		Grad grad1 = new Grad();
		grad1.setId(AddressConstants.DB_ID_GR1);
		grad1.setNaziv(AddressConstants.DB_GRAD1);
		
		dbAdresa.setGrad(grad1);
        
		when(addressRepositoryMock.findById(AddressConstants.DB_ID_AD)).thenReturn(Optional.of(dbAdresa));
		when(gradRepositoryMock.findById(AddressConstants.DB_ID_GR1)).thenReturn(Optional.of(grad1));
		addressService.updateAdresa(AddressConstants.DB_ULICA1, AddressConstants.DB_BROJ1, AddressConstants.DB_ID_GR1, AddressConstants.DB_ID_AD);
		
		when(addressRepositoryMock.findById(AddressConstants.DB_ID_AD)).thenReturn(Optional.of(dbAdresa));
		
		Adresa adresaChanged = addressService.findById(AddressConstants.DB_ID_AD).get();
		
		assertEquals(dbAdresa, adresaChanged);
        verify(addressRepositoryMock, times(3)).findById(AddressConstants.DB_ID_AD);
        verify(addressRepositoryMock, times(1)).save(dbAdresa);
        verifyNoMoreInteractions(addressRepositoryMock);
        
        verify(gradRepositoryMock, times(1)).findById(AddressConstants.DB_ID_GR1);
        verifyNoMoreInteractions(gradRepositoryMock);
	}

}
