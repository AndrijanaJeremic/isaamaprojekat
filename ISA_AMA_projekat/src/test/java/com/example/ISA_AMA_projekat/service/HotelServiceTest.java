package com.example.ISA_AMA_projekat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.constants.HotelConstants;
import com.example.ISA_AMA_projekat.model.Hotel;
import com.example.ISA_AMA_projekat.repository.HotelRepository;


@RunWith(SpringRunner.class)
@SpringBootTest
public class HotelServiceTest {
	
	@Mock
	private HotelRepository hotelRepositoryMock;
	
	@Mock
	private Hotel hotelMock;
	
	@InjectMocks
	private HotelService hotelService;
	
	@Test
	public void testFindAll() {
		
		Hotel hotel = new Hotel();
		hotel.setId(HotelConstants.DB_ID);
		hotel.setNaziv(HotelConstants.DB_NAZIV);
		hotel.setPromotivni_opis(HotelConstants.DB_PROMOTIVNI_OPIS);
		hotel.setProsecna_ocena(HotelConstants.DB_PROSECNA_OCENA);
	
		when(hotelRepositoryMock.findAll()).thenReturn(Arrays.asList(hotel));
		Collection<Hotel> hotels = hotelService.findAll();
		assertThat(hotels).hasSize(1);
		verify(hotelRepositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(hotelRepositoryMock);
	}
	
	@Test
	public void testFindByNaziv() {
        
        when(hotelRepositoryMock.findOneByNaziv(HotelConstants.DB_NAZIV)).thenReturn(hotelMock);
        Hotel hotel = hotelService.findByNaziv(HotelConstants.DB_NAZIV);
		assertEquals(hotelMock, hotel);
        verify(hotelRepositoryMock, times(1)).findOneByNaziv(HotelConstants.DB_NAZIV);
        verifyNoMoreInteractions(hotelRepositoryMock);
	}
	
	@Test
	public void testFindById() {
        
        when(hotelRepositoryMock.findById(HotelConstants.DB_ID)).thenReturn(Optional.of(hotelMock));
        Optional<Hotel> hotel = hotelService.findById(HotelConstants.DB_ID);
		assertEquals(Optional.of(hotelMock), hotel);
        verify(hotelRepositoryMock, times(1)).findById(HotelConstants.DB_ID);
        verifyNoMoreInteractions(hotelRepositoryMock);
	}
	
	@Test
	public void testSearch() {
		
		Hotel hotel1 = new Hotel();
		Hotel hotel2 = new Hotel();
		Hotel hotel3 = new Hotel();
        
        when(hotelRepositoryMock.search(HotelConstants.SEARCH)).thenReturn(Arrays.asList(hotel1, hotel2, hotel3));
        List<Hotel> hotels = hotelService.search(HotelConstants.SEARCH);
		assertEquals(Arrays.asList(hotel1, hotel2, hotel3), hotels);
		
		int size = hotels.size();
		assertEquals(HotelConstants.SEARCH_SIZE, size);
        verify(hotelRepositoryMock, times(1)).search(HotelConstants.SEARCH);
        verifyNoMoreInteractions(hotelRepositoryMock);
	}
	
	@Test
	public void testUpdateAdmin() {
		
		Hotel hotel = new Hotel();
		hotel.setId(HotelConstants.ID);
		hotel.setId_admin(HotelConstants.ADMIN_ID);
		
		when(hotelRepositoryMock.findById(HotelConstants.ID)).thenReturn(Optional.of(hotel));
		
		Hotel dbHotel = hotelService.findById(HotelConstants.ID).get();
		dbHotel.setId_admin(HotelConstants.ADMIN_ID_CHANGED);
        
		doNothing().when(hotelRepositoryMock).updateAdmin(HotelConstants.ID, HotelConstants.ADMIN_ID);
		hotelService.updateAdmin(HotelConstants.ID, HotelConstants.ADMIN_ID);
		
		when(hotelRepositoryMock.findById(HotelConstants.ID)).thenReturn(Optional.of(dbHotel));
		
		Hotel hotelChanged = hotelService.findById(HotelConstants.ID).get();
		
		assertEquals(dbHotel, hotelChanged);
        verify(hotelRepositoryMock, times(3)).findById(HotelConstants.ID);
        verify(hotelRepositoryMock, times(1)).save(dbHotel);
        verifyNoMoreInteractions(hotelRepositoryMock);
	}
	
	@Test
    @Transactional
	public void testSave() {
		when(hotelRepositoryMock.save(hotelMock)).thenReturn(hotelMock);
         
        Hotel savedHotel = hotelService.save(hotelMock);
           
        assertThat(savedHotel, is(equalTo(hotelMock)));
        verify(hotelRepositoryMock, times(1)).save(hotelMock);
        verifyNoMoreInteractions(hotelRepositoryMock);
	}

}
