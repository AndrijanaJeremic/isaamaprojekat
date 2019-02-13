package com.example.ISA_AMA_projekat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
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





import com.example.ISA_AMA_projekat.constants.HotelConstants;
import com.example.ISA_AMA_projekat.constants.RentacarServisConstants;
import com.example.ISA_AMA_projekat.model.Hotel;
import com.example.ISA_AMA_projekat.model.RentacarServis;
import com.example.ISA_AMA_projekat.repository.RentacarRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RentacarServiceTest {

	@Mock
	private RentacarRepository rentRepositoryMock;
	
	@Mock
	private RentacarServis rentMock;
	
	@InjectMocks
	private RentacarService rentService;
	
	
	@Test
	public void testFindAll() {
		
		RentacarServis rent = new RentacarServis();
		rent.setId(RentacarServisConstants.DB_ID_R);
		rent.setPromotivni_opis(RentacarServisConstants.DB_PROMOTIVNI_OPIS_R);
		rent.setNaziv(RentacarServisConstants.DB_NAZIV_R);
		rent.setProsecna_ocena(RentacarServisConstants.DB_PROSECNA_OCENA_R);
	
		when(rentRepositoryMock.findAll()).thenReturn(Arrays.asList(rent));
		Collection<RentacarServis> rents = rentService.findAll();
		assertThat(rents).hasSize(1);
		verify(rentRepositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(rentRepositoryMock);
	}
	
	@Test
	public void testFindByNaziv() {
        
        when(rentRepositoryMock.findOneByNaziv(RentacarServisConstants.DB_NAZIV_R)).thenReturn(rentMock);
        RentacarServis rent = rentService.findByNaziv(RentacarServisConstants.DB_NAZIV_R);
		assertEquals(rentMock, rent);
        verify(rentRepositoryMock, times(1)).findOneByNaziv(RentacarServisConstants.DB_NAZIV_R);
        verifyNoMoreInteractions(rentRepositoryMock);
	}
	
	@Test
	public void testFindById() {
        
        when(rentRepositoryMock.findById(RentacarServisConstants.DB_ID_R)).thenReturn(Optional.of(rentMock));
        Optional<RentacarServis> rent = rentService.findById(RentacarServisConstants.DB_ID_R);
		assertEquals(Optional.of(rentMock), rent);
        verify(rentRepositoryMock, times(1)).findById(RentacarServisConstants.DB_ID_R);
        verifyNoMoreInteractions(rentRepositoryMock);
	}
	
	@Test
    @Transactional
	public void testSave() {
		
		    
        when(rentRepositoryMock.save(rentMock)).thenReturn(rentMock);
         
        RentacarServis savedRent = rentService.save(rentMock);
           
        assertThat(savedRent, is(equalTo(rentMock)));
	}
	
	@Test
	public void testSearch() {
		
		RentacarServis rent1 = new RentacarServis();
		RentacarServis rent2 = new RentacarServis();
		RentacarServis rent3 = new RentacarServis();
		
        
        when(rentRepositoryMock.search(RentacarServisConstants.SEARCH_R)).thenReturn(Arrays.asList(rent1, rent2, rent3));
        List<RentacarServis> rents = rentService.search(RentacarServisConstants.SEARCH_R);
		assertEquals(Arrays.asList(rent1, rent2, rent3), rents);
		
		int size = rents.size();
		assertEquals(RentacarServisConstants.SEARCH_SIZE_R, size);
        verify(rentRepositoryMock, times(1)).search(RentacarServisConstants.SEARCH_R);
        verifyNoMoreInteractions(rentRepositoryMock);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateAdmin() {
		
		RentacarServis rent = new RentacarServis();
		rent.setId(RentacarServisConstants.ID_R);
		rent.setId_admin(RentacarServisConstants.ADMIN_ID_R);
		
		when(rentRepositoryMock.findById(RentacarServisConstants.ID_R)).thenReturn(Optional.of(rent));
		
		RentacarServis dbRent = rentService.findById(RentacarServisConstants.ID_R).get();
		dbRent.setId_admin(RentacarServisConstants.ADMIN_ID_CHANGED_R);
        
		doNothing().when(rentRepositoryMock).updateAdmin(RentacarServisConstants.ID_R, RentacarServisConstants.ADMIN_ID_R);
		rentService.updateAdmin(RentacarServisConstants.ID_R, RentacarServisConstants.ADMIN_ID_R);
		
		when(rentRepositoryMock.findById(RentacarServisConstants.ID_R)).thenReturn(Optional.of(dbRent));
		
		RentacarServis rentChanged = rentService.findById(RentacarServisConstants.ID_R).get();
		
		assertEquals(dbRent, rentChanged);
        verify(rentRepositoryMock, times(2)).findById(RentacarServisConstants.ID_R);
        verify(rentRepositoryMock, times(1)).updateAdmin(RentacarServisConstants.ID_R, RentacarServisConstants.ADMIN_ID_R);
        verifyNoMoreInteractions(rentRepositoryMock);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testUpdateProsecna() {
		
		RentacarServis rent = new RentacarServis();
		rent.setId(RentacarServisConstants.DB_ID_R);
		rent.setProsecna_ocena(5);
		
		when(rentRepositoryMock.findById(RentacarServisConstants.DB_ID_R)).thenReturn(Optional.of(rent));
		
		RentacarServis dbRent = rentService.findById(RentacarServisConstants.DB_ID_R).get();
		dbRent.setProsecna_ocena(5);;
        
		doNothing().when(rentRepositoryMock).updateProsecnaRent(5, RentacarServisConstants.DB_ID_R);
		rentService.updateProsecnaRent(5, RentacarServisConstants.DB_ID_R);
		
		when(rentRepositoryMock.findById(RentacarServisConstants.DB_ID_R)).thenReturn(Optional.of(dbRent));
		
		RentacarServis rentChanged = rentService.findById(RentacarServisConstants.DB_ID_R).get();
		
		assertEquals(dbRent, rentChanged);
        verify(rentRepositoryMock, times(2)).findById(RentacarServisConstants.DB_ID_R);
        verify(rentRepositoryMock, times(1)).updateProsecnaRent(5, RentacarServisConstants.DB_ID_R);
        verifyNoMoreInteractions(rentRepositoryMock);
	}
	
	
}
