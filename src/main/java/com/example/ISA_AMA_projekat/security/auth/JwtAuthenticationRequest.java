package com.example.ISA_AMA_projekat.security.auth;

public class JwtAuthenticationRequest {

	 private String email;
	    private String lozinka;

	    public JwtAuthenticationRequest() {
	        super();
	    }

	    public JwtAuthenticationRequest(String email, String lozinka) {
	        this.setUsername(email);
	        this.setPassword(lozinka);
	    }

	    public String getUsername() {
	        return this.email;
	    }

	    public void setUsername(String email) {
	        this.email = email;
	    }

	    public String getPassword() {
	        return this.lozinka;
	    }

	    public void setPassword(String lozinka) {
	        this.lozinka = lozinka;
	    }
}
