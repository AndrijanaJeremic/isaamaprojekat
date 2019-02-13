package com.example.ISA_AMA_projekat.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.ISA_AMA_projekat.model.FriendRequest;
import com.example.ISA_AMA_projekat.model.Korisnik;
import com.example.ISA_AMA_projekat.security.TokenUtils;
import com.example.ISA_AMA_projekat.service.FriendRequestService;
import com.example.ISA_AMA_projekat.service.KorisnikService;

@RestController
@RequestMapping(value="api/friendRequest")
public class FriendRequestController 
{
	@Autowired
	TokenUtils tokenUtils;
	
	@Autowired
	private FriendRequestService friendRequestService;
	
	@Autowired
	private KorisnikService userDetailsService;
	

	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/{token}/{id}",
			method = RequestMethod.PUT)
	public String changeStatus(@PathVariable("token") String token, @PathVariable("id") Integer frID)
	{
		String email = tokenUtils.getUsernameFromToken(token);
		
	    Korisnik user = (Korisnik) this.userDetailsService.loadUserByUsername(email);
	    
	   //****validacija
	    boolean valid = false;
	    for(FriendRequest fr : user.getPrijateljstva())
	    {
	    	if(fr.getId() == frID)
	    	{
	    		if(fr.getStanje() == 1)
	    		{
	    			valid = true;
	    		}
	    	}
	    }
	    
	    if(valid)
	    {
	    	friendRequestService.updateRequest(0, frID);
	    	return "success";
	    }
	    else
	    {
	    	System.err.println("Upit nije validan");
	    	return null;
	    }
	    
	}
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/delete/{token}/{id}",
			method = RequestMethod.DELETE)
	public String deleteRequest(@PathVariable("token") String token, @PathVariable("id") Integer frID)
	{
		String email = tokenUtils.getUsernameFromToken(token);
		
	    Korisnik user = (Korisnik) this.userDetailsService.loadUserByUsername(email);
	    
	   //****validacija
	    boolean valid = false;
	    for(FriendRequest fr : user.getPrijateljstva())
	    {
	    	if(fr.getId() == frID)
	    	{
	    		if(fr.getStanje() == 1)
	    		{
	    			valid = true;
	    		}
	    	}
	    }
	    if(valid)
	    {
	    	friendRequestService.deleteRequest(frID);
	    	return "success";
	    }
	    else
	    {
	    	System.err.println("Upit nije validan");
	    	return null;
	    }
	}
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/deleteFriend/{token}/{id}",
			method = RequestMethod.DELETE)
	public String deleteFriendRequest(@PathVariable("token") String token, @PathVariable("id") Integer userID)
	{
		String email = tokenUtils.getUsernameFromToken(token);
		
	    Korisnik user = (Korisnik) this.userDetailsService.loadUserByUsername(email);
	    
	    Optional<FriendRequest> frOption = friendRequestService.findByUsers(user.getId(), userID);
	    
	    try
	    {
	    	FriendRequest fr = frOption.get();
	    	Integer frID = fr.getId();
	    	friendRequestService.deleteRequest(frID);
	    	return "success";
	    }
	    catch(Exception e)
	    {
	    	return null;
	    }
	}
	
	
	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(
			value = "/befriend/{token}/{id}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FriendRequest> sendRequest(@PathVariable("token") String token, @PathVariable("id") Integer userID)
	{
		String email = tokenUtils.getUsernameFromToken(token);
		
	    Korisnik user_salje = (Korisnik) this.userDetailsService.loadUserByUsername(email);
	    Optional<Korisnik> opp = userDetailsService.findById(userID);
	    try
	    {
	    	Korisnik user_prima = opp.get();
		    if(userDetailsService.areStrangers(userID, user_salje))
		    {
		    	FriendRequest newRequest = new FriendRequest();
		    	newRequest.setPrima(user_prima);
		    	newRequest.setSalje(user_salje);
		    	newRequest.setStanje(1); //stanje cekanja
		    	
		    	FriendRequest retVal = friendRequestService.save(newRequest);
		    	return new ResponseEntity<FriendRequest>(retVal, HttpStatus.CREATED);
		    }
		    else
		    {
		    	return null;
		    }
	    }
	    catch(Exception e)
	    {
	    	return null;
	    }
	}
	

	
}
