package com.example.ISA_AMA_projekat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ISA_AMA_projekat.controller.AuthenticationController;
import com.example.ISA_AMA_projekat.model.FriendRequest;
import com.example.ISA_AMA_projekat.model.Grad;
import com.example.ISA_AMA_projekat.model.Korisnik;
import com.example.ISA_AMA_projekat.repository.FriendRequestRepository;
import com.example.ISA_AMA_projekat.repository.GradRepository;
import com.example.ISA_AMA_projekat.repository.KorisnikRepository;


@Service
public class KorisnikService implements UserDetailsService{
	
	protected final Log LOGGER = LogFactory.getLog(getClass());
	
	@Autowired
	private KorisnikRepository korisnikRepository;
	
	@Autowired
	private GradRepository gradRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;
	


	@Autowired 
	private FriendRequestRepository requestRepository;




	@Autowired
	AuthenticationController authController;

	
	public Korisnik findByEmail(String email) 
	{
		return korisnikRepository.findOneByEmail(email);
	}

	public List<Korisnik> findAll() {
		return korisnikRepository.findAll();
	}
	
	public Optional<Korisnik> findById(Integer id)
	{
		return korisnikRepository.findById(id);
	}
	
	
	public List<Korisnik> getAllFriendsOfUser(int id)
	{
		List<FriendRequest> requestList = requestRepository.findBySaljeOrPrima(id);
		List<Korisnik> retVala = new ArrayList<Korisnik>();
		
		for (int i = 0 ; i < requestList.size() ; i ++ )
		{
			FriendRequest fq = requestList.get(i);
			
			Korisnik k = null;
			
			if(fq.getPrima().getId() == id && fq.getStanje()==0)
			{
				k = fq.getSalje();
			}
			if(fq.getSalje().getId() == id && fq.getStanje()==0)
			{
				k = fq.getPrima();
			}
			retVala.add(k);
		}
		
		return retVala;
	}
	
	
	
	public boolean areStrangers(Integer k1, Korisnik k2)
	{
		boolean retVal = true;
		
		if(k1 == k2.getAdmin_id())
		{
			retVal = false;
		}
		
		
		List<FriendRequest> requestList = requestRepository.findBySaljeOrPrimaAbstract(k1);
		for (int i = 0 ; i < requestList.size() ; i ++ )
		{
			FriendRequest fq = requestList.get(i);
						
			if(fq.getPrima().getId() == k2.getId() )
			{
				retVal = false;
			}
			if(fq.getSalje().getId() == k2.getId() )
			{
				retVal = false;
			}
		}
		
		return retVal;
		
	}
	
	
	@Transactional
	public Korisnik save(Korisnik korisnik)
	{
		return korisnikRepository.save(korisnik);
	}
	
	@Transactional
	public void updateAkt(boolean aktiviran, Integer id)
	{
		Korisnik user = korisnikRepository.findById(id).get();
		user.setAktiviran(aktiviran);
		korisnikRepository.save(user);
		//korisnikRepository.updateAktiviran(aktiviran, id);
	}
	
	@Transactional
	public void updateAktPass(boolean aktiviran, String new_pass, Integer id){
		Korisnik user = korisnikRepository.findById(id).get();
		user.setAktiviran(aktiviran);
		user.setLozinka(new_pass);
		korisnikRepository.save(user);
		//korisnikRepository.updateAktiviranPass(aktiviran, new_pass, id);
	}
	
	@Transactional
	public void updateKorisnik(Integer id, String ime, String prezime, String email, String telefon, Grad grad) {
		Korisnik user = korisnikRepository.findById(id).get();
		user.setIme(ime);
		user.setPrezime(prezime);
		user.setEmail(email);
		user.setTelefon(telefon);
		user.setGrad(grad);
		korisnikRepository.save(user);
		//korisnikRepository.updateKorisnik(id, ime, prezime, email, telefon, grad);
	}
	
	@Transactional
	public void updateBonusPoints(int points, Integer id) {
		Korisnik user = korisnikRepository.findById(id).get();
		user.setBonus_poeni(points);
		korisnikRepository.save(user);
		//korisnikRepository.updateBonusPoints(points, id);
	}

	@Override
	public UserDetails loadUserByUsername(String email)
			throws UsernameNotFoundException {
		Korisnik user = korisnikRepository.findOneByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException(String.format("No user found with email '%s'.", email));
		} else {
			return user;
		}
	}
	

	@Transactional
	public boolean changePassword(String oldPassword, String newPassword) {

		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		String username = currentUser.getName(); //ovde treba da bude email, ne znam sta ce vratiti
		System.out.println("[KorisnikService: changePassword] username: " + username);
		
		Korisnik user = (Korisnik) findByEmail(username);
		
		if(!(passwordEncoder.matches(oldPassword, user.getLozinka()))) {
			System.out.println("[KorisnikService: changePassword] matches vraca false ");
			return false;
		}

		if (authenticationManager != null) {
			LOGGER.debug("Re-authenticating user '" + username + "' for password change request.");

			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
		} else {
			LOGGER.debug("No authentication manager set. can't change Password!");

			return false;
		}

		LOGGER.debug("Changing password for user '" + username + "'");

		
		//final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, newPassword));
		//SecurityContextHolder.getContext().setAuthentication(authentication);

		// pre nego sto u bazu upisemo novu lozinku, potrebno ju je hesirati
		// ne zelimo da u bazi cuvamo lozinke u plain text formatu
		//user.setPassword(passwordEncoder.encode(newPassword));
		user.setLozinka(passwordEncoder.encode(newPassword));
		korisnikRepository.save(user);

		return true;
	}
	
	public boolean areFriends(Korisnik k1, Korisnik k2)
	{
		boolean retVal = false;
		
		Set<FriendRequest> k1Friends = k1.getPrijateljstva();
		Set<FriendRequest> k2Friends = k2.getPrijateljstva();
		
		for( FriendRequest fr : k1Friends)
		{
			if(fr.getPrima().equals(k2) || fr.getSalje().equals(k2))
			{
				if(fr.getStanje() == 0)
				{
					retVal = true;
				}
			}
		}
		
		for( FriendRequest fr : k2Friends)
		{
			if(fr.getPrima().equals(k1) || fr.getSalje().equals(k1))
			{
				if(fr.getStanje() == 0)
				{
					retVal = true;
				}
			}
		}
		
		return retVal;
	}
	

}
