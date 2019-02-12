package com.example.ISA_AMA_projekat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.model.RentacarServis;

public interface RentacarRepository extends JpaRepository<RentacarServis, Integer>{

		RentacarServis findOneByNaziv(String naziv);
		
		@Modifying
		@Query("select rentacar_servis from RentacarServis rentacar_servis where rentacar_servis.adresa.grad.naziv like ?1 or rentacar_servis.adresa.ulica like ?1 or rentacar_servis.adresa.broj like ?1 or rentacar_servis.naziv like ?1")
		List<RentacarServis> search(String name_location);
		
		
		@Modifying
		@Query(value = "update RentacarServis rs set rs.naziv = ?1, rs.adresa_id=?2, rs.promotivni_opis=?3 where rs.id = ?4", nativeQuery = true)
		public void updateAktiviran(String naziv, Integer adresa_id, String promotivni_opis, Integer id);
		
		@Modifying
		@Query(value = "update rentacar_servis rs set rs.id_admin = ?2 where rs.id = ?1", nativeQuery = true)
		public void updateAdmin(Integer rentalID, Integer adminID);

		@Modifying
		@Query("update RentacarServis rs set rs.naziv = ?1 , rs.adresa.id = ?2 , rs.promotivni_opis = ?3 where rs.id = ?4")
		public void updateServis(String naziv, Integer adresa_id, String promotivni_opis,  Integer id);
		
		@Modifying
		@Query(value = "update rentacar_servis rs set rs.prosecna_ocena=?1 where rs.id=?2", nativeQuery = true)
		public void updateProsecnaRent(double prosecna_ocena, Integer rent_id);
}
