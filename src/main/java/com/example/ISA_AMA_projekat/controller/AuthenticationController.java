package com.example.ISA_AMA_projekat.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.ISA_AMA_projekat.common.DeviceProvider;
import com.example.ISA_AMA_projekat.model.Korisnik;
import com.example.ISA_AMA_projekat.model.UserTokenState;
import com.example.ISA_AMA_projekat.security.TokenUtils;
import com.example.ISA_AMA_projekat.service.KorisnikService;
import com.example.ISA_AMA_projekat.service.OsobaIzRezService;
import com.example.ISA_AMA_projekat.service.RezervacijaService;


@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

	@Autowired
	TokenUtils tokenUtils;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private KorisnikService userDetailsService;

	
	@Autowired
	private DeviceProvider deviceProvider;
	
	@Autowired
	private KorisnikService korisnikService;
	
	@Autowired
	private OsobaIzRezService osobaIzRezService;
	
	
	
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody Korisnik korisnik,HttpServletResponse response, Device device) throws AuthenticationException, IOException {
		Korisnik postoji = userDetailsService.findByEmail(korisnik.getEmail());
		if(postoji==null)
		{
			System.out.println("KORISNIK SA OVIM EMAIL-OM NE POSTOJI");
			return null;
		}
		else
		{
			System.out.println("korisnik lozinka: " + korisnik.getLozinka() + "; postoji lozinka: " + postoji.getLozinka());
		 if(!passwordEncoder.matches(korisnik.getLozinka(), postoji.getLozinka()))
		 {
			 System.out.println("Pogresna lozinka");
				return null;
		 }
		 else
		 {
			 if(postoji.getAktiviran()==false)
			 {
				 System.out.println("Nije aktiviran");
				 
				 if(postoji.getAdmin_id() != null) {
					 System.out.println("Loguje se admin");
					 
					 return ResponseEntity.ok(postoji);
				 }
				 
				 return null;
			 }
			 else
			 {
				 final Authentication authentication = authenticationManager
							.authenticate(new UsernamePasswordAuthenticationToken(
									postoji.getEmail(),
									korisnik.getLozinka()));

					// Ubaci username + password u kontext
					SecurityContextHolder.getContext().setAuthentication(authentication);

					// Kreiraj token
					Korisnik user = (Korisnik) authentication.getPrincipal();
					System.out.println("DA LI JE AKRIVAN: " + user.getAktiviran() + "DA LI JE ENABLED: " + user.isEnabled());
					System.out.println("[AuthenticationController: firstAdminLogin] user lozinka: " + user.getLozinka() + "; user email: " + user.getEmail());
					String jwt = tokenUtils.generateToken(user.getEmail(), device);
					int expiresIn = tokenUtils.getExpiredIn(device);

					// Vrati token kao odgovor na uspesno autentifikaciju
					return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));
			 }
		 }
		
		}
		
	}
	
	@RequestMapping(value = "/refresh", method = RequestMethod.POST)
	public ResponseEntity<?> refreshAuthenticationToken(HttpServletRequest request) {

		String token = tokenUtils.getToken(request);
		String username = this.tokenUtils.getUsernameFromToken(token);
	    Korisnik user = (Korisnik) this.userDetailsService.loadUserByUsername(username);

		Device device = deviceProvider.getCurrentDevice(request);

		if (this.tokenUtils.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
			String refreshedToken = tokenUtils.refreshToken(token, device);
			int expiresIn = tokenUtils.getExpiredIn(device);

			return ResponseEntity.ok(new UserTokenState(refreshedToken, expiresIn));
		} else {
			UserTokenState userTokenState = new UserTokenState();
			return ResponseEntity.badRequest().body(userTokenState);
		}
	}


	@PreAuthorize("hasRole('SYSADMIN') or hasRole('HOTELADMIN') or hasRole('RENTADMIN') or hasRole('AVIOADMIN') or hasRole('USER')")
	@RequestMapping(value = "/change_password", method = RequestMethod.POST)
	public ResponseEntity<?> changePassword(@RequestBody PasswordChanger passwordChanger) {
		
		System.out.println("[AuthenticationController: changePassword] oldPassword: " + passwordChanger.oldPassword + "; newPassword: " + passwordChanger.newPassword);
		
		boolean uspesno = userDetailsService.changePassword(passwordChanger.oldPassword, passwordChanger.newPassword);
		
		Map<String, String> result = new HashMap<>();
		
		if(uspesno) {
			result.put("result", "success");
		}else {
			result.put("result", "error");
		}
		
		return ResponseEntity.accepted().body(result);
	}

	static class PasswordChanger {
		public String oldPassword;
		public String newPassword;
	}
	
	
	@RequestMapping(value = "/userprofile", method = RequestMethod.POST)
	public ResponseEntity<?> getProfile(@RequestBody String token) 
	{

		System.out.println("IMA TOKEN: " + token);
		String email = tokenUtils.getUsernameFromToken(token);
		
		System.out.println("USERNAME: " + email);
	    Korisnik user = (Korisnik) this.userDetailsService.loadUserByUsername(email);
	    
	    System.out.println("Korisnik: " + user.getEmail());
	    		
		return  new ResponseEntity<Korisnik>(user, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/first_admin_login/{new_pass}", method = RequestMethod.POST)
	public ResponseEntity<?> firstAdminLogin(@RequestBody Korisnik korisnik, HttpServletResponse response, Device device, @PathVariable("new_pass") String new_pass) throws AuthenticationException, IOException {
		
		Korisnik postoji = userDetailsService.findByEmail(korisnik.getEmail());
		String encoded = passwordEncoder.encode(new_pass);
		
		System.out.println("[AuthenticationController: firstAdminLogin] postoji lozinka: " + postoji.getLozinka() + "; new_pass: " + new_pass + "; encoded: " + encoded);
			
		 if(passwordEncoder.matches(new_pass, postoji.getLozinka())){
			 System.out.println("[AuthenticationController: firstAdminLogin] Lozinka nije promenjena");
			 
			 return null;
		 }else {
			 
			 System.out.println("[AuthenticationController: firstAdminLogin] Lozinka promenjena");
			 
			 postoji.setLozinka(passwordEncoder.encode(new_pass));
			 postoji.setAktiviran(true);
			 
			 korisnikService.updateAktPass(true, passwordEncoder.encode(new_pass), postoji.getId());
			 
			 final Authentication authentication = authenticationManager
						.authenticate(new UsernamePasswordAuthenticationToken(
								postoji.getEmail(),
								new_pass));
			 
			// Ubaci username + password u kontext
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			// Kreiraj token
			Korisnik user = (Korisnik) authentication.getPrincipal();
			System.out.println("[AuthenticationController: firstAdminLogin] admin aktivan: " + user.getAktiviran() + "; admin enabled: " + user.isEnabled());
			System.out.println("[AuthenticationController: firstAdminLogin] user lozinka: " + user.getLozinka() + "; user email: " + user.getEmail());
			
			String jwt = tokenUtils.generateToken(user.getEmail(), device);
			int expiresIn = tokenUtils.getExpiredIn(device);
			
			// Vrati token kao odgovor na uspesno autentifikaciju
			return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));

		 }
		
	}

}
