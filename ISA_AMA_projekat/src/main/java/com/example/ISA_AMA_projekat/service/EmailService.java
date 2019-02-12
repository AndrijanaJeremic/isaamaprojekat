package com.example.ISA_AMA_projekat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.ISA_AMA_projekat.model.Korisnik;
import com.example.ISA_AMA_projekat.model.Rezervacija;



@Service
public class EmailService {
	
	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private Environment env;

	
	@Async
	public void sendNotificaitionAsync(Korisnik korisnik) throws MailException, InterruptedException 
	{

		//Simulacija duze aktivnosti da bi se uocila razlika
		//Thread.sleep(2500);
		System.out.println("Slanje emaila...");

		SimpleMailMessage mail = new SimpleMailMessage();
		String confirmationUrl 
        =  "http://localhost:8080/api/users/registrationConfirm/" + korisnik.getEmail();
		mail.setTo(korisnik.getEmail());
		mail.setFrom(env.getProperty("spring.mail.username"));
		mail.setSubject("Confirm your registration!");
		mail.setText("Hello, " + korisnik.getIme() + " " + korisnik.getPrezime() + "\n To confirm your registration on our site, click on this link below.\n "
				+ confirmationUrl + "\n Sincerely,\n your WhereTo.");
		javaMailSender.send(mail);

		System.out.println("Email poslat!");
	}
	
	
	@Async
	public void sendReservationExpensesConformation(Korisnik korisnik, Rezervacija rez) throws MailException, InterruptedException
	{
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setSubject("New Flight Invitation");
		mail.setTo(korisnik.getEmail());
		mail.setFrom(env.getProperty("spring.mail.username"));
		mail.setText("Hello " + korisnik.getIme() + ",\n\nYou have been invited by " + rez.getKorisnik().getIme() + " " + rez.getKorisnik().getPrezime() + " on a flight from: " + rez.getLet().getOdakle() + " to: " + rez.getLet().getDokle() + ".\nGo to your WhereTo profile page to accept or decline your share of expenses for this invitation.\n\nYours sincerely,\nWhereTo");		
		javaMailSender.send(mail);
	}
	
	
	@Async
	public void sendReservationConformation(Korisnik korisnik, Rezervacija rez) throws MailException, InterruptedException
	{
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setSubject("New Flight Invitation");
		mail.setTo(korisnik.getEmail());
		mail.setFrom(env.getProperty("spring.mail.username"));
		mail.setText("Hello " + korisnik.getIme() + ",\n\nYou have been invited by " + rez.getKorisnik().getIme() + " " + rez.getKorisnik().getPrezime() + " on a flight from: " + rez.getLet().getOdakle() + " to: " + rez.getLet().getDokle() + ".\nAll the expenses have been covered by the reserver. After you create a WhereTo profile and friends, you will you be able to preview the reservation.\n\nYours sincerely,\n WhereTo");		
		javaMailSender.send(mail);
	}

	@Async
	public void toUser(Korisnik korisnik, Rezervacija rez) throws MailException, InterruptedException
	{
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setSubject("New Flight Invitation");
		mail.setTo(korisnik.getEmail());
		mail.setFrom(env.getProperty("spring.mail.username"));
		mail.setText("Hello " + korisnik.getIme() + ",\n\nYou have succesfully created a reservation on flight from: " + rez.getLet().getOdakle() + " to: " + rez.getLet().getDokle() + "!\nYou are able to preview the information about your reservation and make changes to it on your WhereTo profile.\n\nYours sincerely,\n WhereTo");		
		javaMailSender.send(mail);
	}

}
