var rezervacija = null;
var korisnik = null;
var flight = null;
var vozilo = null;
var rentacar_servis=null;
var soba=null;
var hotel=null;
var sediste=null;
var odabrano = null;
var token =null;
$(document).ready(function(){
	token = localStorage.getItem('jwtToken');
	
	getKorisnik();
	
	
	$('form#rentacar').submit(function(event){
		event.preventDefault();
		 datum = $('input[name="check_out_car"]').val();
		 mesto = $('input[name="check_out_town"]').val();
		 let split = flight.dokle.split("-");
		 let check_in_town=split[0];
		 check_in_town=check_in_town.substring(0, check_in_town.length-1);
		let check_in_date = flight.vremeSletanja.substring(0,10);
		 let passengers = rezervacija.osobe.length;
		console.log(datum);
		console.log(mesto);
		//window.location.replace("index.html");
		preusmeri(check_in_town, check_in_date, datum, mesto, passengers);
	});
	
	$('#hotel_form').submit(function(event){
		event.preventDefault();
		
		check_out_date = $('input[name="check_out_hotel"]').val();
		
		let split = flight.dokle.split("-");
		let check_in_town=split[0];
		check_in_town = check_in_town.substring(0, check_in_town.length-1);
		
		let check_in_date = flight.vremeSletanja.substring(0,10);
		let adults = rezervacija.osobe.length;
		
		preusmeriHotel(check_in_town, check_in_date, check_out_date, adults);
	});
	
	$('.bonus_points').click(function(){
		text = $(this).text(); 
		
		$('#div_bonus_points').hide();
		$('#div_zavrsena_rez').show();
		
		if(text == "Yes"){
			console.log('kliknuo YES');
			
			$('#finish_total_cost').text('Total cost: $' + cenaSaPopustom);
			
			odabrano = 'yes';
			
			$.ajax({
		        type: 'POST',
		        url: 'api/rezervacija/updateCena/' + rezervacija.id + '/' + cenaSaPopustom,
		        headers: {"Authorization": "Bearer " + token},
		        contentType: 'application/json',
		        success: function (rez)
				{
		            console.log('uspesno azurirana cena rezervacije');
				}
		    });
		}
		else
		{
			console.log('kliknuo NO');
			
			$('#finish_total_cost').text('Total cost: $' + rezervacija.cena);
			
			odabrano = 'no';
		}
	});
		
});

function preusmeri(check_in_town, check_in_date, datum, mesto, passengers)
{
	//alert("Usao u preusmeri");
	
	var idiNa = "index.html?name_location_rentacar=" + check_in_town + "&check_in_car=" + check_in_date + "&check_in_town=" + check_in_town + "&check_out_car=" + datum + "&check_out_town=" + mesto + "&passengers_rent=" + passengers + "&id_rez=" + rezervacija.id;
	window.location.href = idiNa;
}

function preusmeriHotel(check_in_town, check_in_date, check_out_date, adults){
	
	//window.location.href = "index.html?hotel&id_rez"
	window.location.href = "index.html?name_location_hotel=" + check_in_town + "&check_in_hotel=" + check_in_date + "&check_out_hotel=" + check_out_date + "&adults_hotel=" + adults + "&id_rez=" + rezervacija.id;
}

function getKorisnik()
{
	
	$.post
	({
		url: "/auth/userprofile",
		headers: {"Authorization": "Bearer " + token},
		contentType: 'application/json',
		data : token,
		  
		success: function(user) 
		{
			if(user == null || user == "")
			{
				localStorage.clear();
				window.location.replace("index.html");
			}
			
			korisnik = user;
			getRezervacija();
		},
		error: function() 
		{
			window.location.replace("index.html");
		}
	});
}


function getRezervacija()
{
	var url = window.location.toString();
	var splitstr = url.split("=");
	var rezID = splitstr[1];
	
    $.ajax({
        type: 'GET',
        url: 'api/rezervacija/' + rezID,
        headers: {"Authorization": "Bearer " + token},
        contentType: 'application/json',
        dataType: "json",
        complete: function (data)
		{
        	
        	if(data == null)
        	{
        		alert("Unauthorized.");
        		window.location.replace("index.html");
        	}
        	else
        	{
            	rezervacija = data.responseJSON;
            	console.log(rezervacija);
            	flight = rezervacija.let;
            	$('input[name=check_out_hotel]').attr("min", flight.vremeSletanja.substring(0,10));
            	$('input[name=check_out_car]').attr("min", flight.vremeSletanja.substring(0,10));
            	
            	if(!nalaziURezervaciji(rezervacija))
            	{
            		alert("Unauthorized.");
            		window.location.replace("index.html");
            	}  
            	
            	initWindow();
            	
        	}

		}
    });
}



function initWindow()
{ 
	if(rezervacija.zavrsena==true)
		{
		$("#zavrsena_dugme").hide();
		$("#bookCar").hide();
		$("#bookHotel").hide();
		$('#finish_total_cost').text('Total cost: $' + rezervacija.cena);
		$('#div_zavrsena_rez').show();
		$('#finish_total_cost').show();
		$('#div_bonus_points').hide();
		}
	
	
	$("#flightFrom").text("From: " + capitalize(flight.odakle));
	$("#flightTo").text("To: " + capitalize(flight.dokle));
	$("#flightDuration").text("Duration: " + flight.trajanje + " hours");
	$("#flightDeparture").text("Departure time: " + processTime(flight.vremePoletanja));
	$("#flightArrival").text("Arrival time: " + processTime(flight.vremeSletanja));
	$("#flightReserver").text("Reservation made by: " + rezervacija.korisnik.ime + " " + rezervacija.korisnik.prezime);
	
	if(rezervacija.zavrsena == false)
	{
		$('#total_cost').text('Total cost without bonus points: $' + rezervacija.cena);
		
		$.post({
			url: "/api/users/set_bonus_points/" + rezervacija.let.udaljenost + '/' + rezervacija.korisnik.id,
			headers: {"Authorization": "Bearer " + token}, 
			success: function(data) {
				
				if(data.result == 'success')
				{
					bonus_poeni = data.bonusPoeni;
					poeni_najblizeg = data.poeniNajblizeg;
					
					$('#users_bonus_points').text('Your bonus points: ' + data.bonusPoeni);
					cenaSaPopustom = rezervacija.cena - rezervacija.cena*data.popust/100;
					$('#cost_discount').text('Total cost with ' + data.poeniNajblizeg + ' bonus points: $' + cenaSaPopustom);
				}
				else
				{
					$('#div_bonus_points').hide();
					$('#finish_total_cost').text('Total cost: $' + rezervacija.cena);
					$('#div_zavrsena_rez').show();
				}
				
			}
		
		});
	}
	
	var vreme = flight.vremePoletanja;
	var date = new Date(vreme);
	var pravi = date.toString();
	var date_pol = new Date(pravi);
	var vr_pol = date_pol.getTime();
	var vreme3=vr_pol-10800000;
	
	
	var today = new Date();
	var today_vreme = today.getTime();
	
	var vreme_sl = flight.vremeSletanja;
	var date_sl = new Date(vreme_sl);
	var pravi_sl = date_sl.toString();
	var date_sletanja = new Date(pravi_sl);
	if(today_vreme<=vreme3)
		{
		$('#cancel_let').attr("hidden", false);
		}
	else
		{
		$('#cancel_let').attr("hidden", true);
		}
	
	if(rezervacija.korisnik.id!=korisnik.id)
		$('#cancel_let').attr("hidden", true);
		
	if(today>date_sletanja && rezervacija.zavrsena==true)
		{
		$('#rate_flight').attr("hidden", false);
		$('#rate_avio').attr("hidden", false);
		}
	else
		{
		$('#rate_flight').attr("hidden", true);
		$('#rate_avio').attr("hidden", true);
		}
	
	fillPasengerInfo();
	
	
	if(rezervacija.rezevacijaHotel == null)
	{
		if(rezervacija.noHotel == true)
		{
			$('#bookHotel').hide();
		}
		
		$("#noHotel").prop("hidden",false);
		$("#hasHotelReservation").attr("hidden", true);
		$("#cancel_hotel").attr("hidden", true);
	}
	else
	{
		
		var id_rez_hotel = rezervacija.rezevacijaHotel.id;
		
		$.get({
			url: "/api/hotels/get_room/" + id_rez_hotel,
			  
			success: function(data) {
				if(data == null || data == "")
				{
					console.log('nije pronadjena soba');
				}
				else
				{
					hotel=data.hotel;
					soba=data.soba;
					console.log('pronadjena soba');
					$("#hotel_res").text("Hotel: " + data.hotel.naziv);
					$("#room_res").text("Number of beds in room: " + data.soba.broj_kreveta);
				}
			},
			
			error: function() {
				alert('Error with loading room');
			}
		
		});
		
		 $("#noHotel").prop("hidden",true);
		 $("#hasHotelReservation").attr("hidden", false);
		 
		 var check_in = new Date(rezervacija.rezevacijaHotel.datum_dolaska.substring(0, 10) + "Z");
	     var pravi = check_in.toString();
	     var date_check_in = new Date(pravi);
	     date_check_in.setDate(date_check_in.getDate()+1);
	     
	     var check_out = new Date(rezervacija.rezevacijaHotel.datum_odlaska.substring(0, 10) + "Z");
	     var pravi = check_out.toString();
	     var date_check_out = new Date(pravi);
	     date_check_out.setDate(date_check_out.getDate()+1);
		 
		 $("#check_in_hotel_res").text("Check in date: " + date_check_in.toString().substring(0, 10));
		 $("#check_out_hotel_res").text("Check out date: " + date_check_out.toString().substring(0, 10));
		 
		 if(rezervacija.rezevacijaHotel.usluge.length == 0)
		 {
			 $('#services_included_p').hide();
		 }
		 else
		 {
			 $('#services_included_p').show();
			 
			 for(var i = 0, size = rezervacija.rezevacijaHotel.usluge.length; i < size ; i++)
			 {
				 let usluga = rezervacija.rezevacijaHotel.usluge[i];
			
				var li = $('<li><p>' + usluga.naziv + '</p></li>');
		 		$('#included_services').append(li);
			 }
		 }
		 
		 
		 
		//var li = $('<li><p>Cost: $' + rezervacija.rezevacijaHotel.ukupna_cena + '</p></li>');
 		//$('#list_hotel').append(li);
		$("#cost_hotel_res").text("Cost: $" + rezervacija.rezevacijaHotel.ukupna_cena);
		 
		 	var today_h = new Date();
	    	today_h.setHours(0, 0, 0);
	    	
	    	var dolaz = rezervacija.rezevacijaHotel.datum_dolaska;
	    	var date_dol = new Date(dolaz);
	    	var pravi_dol = date_dol.toString();
	    	var date_dolaska = new Date(pravi_dol);
	    	date_dolaska.setDate(date_dolaska.getDate() - 2);
	    	console.log(date_dolaska.toString());
	    	
	    	var odlaz = rezervacija.rezevacijaHotel.datum_odlaska;
	    	var date_odl = new Date(odlaz);
	    	var pravi_odl = date_odl.toString();
	    	var date_odlaska = new Date(pravi_odl);
	    	
	    	if(today_h<date_dolaska)
	    		{
	    		$("#cancel_hotel").attr("hidden", false);
	    		}
	    	else
	    		{
	    		$("#cancel_hotel").attr("hidden", true);
	    		}
	    	
	    	if(rezervacija.korisnik.id!=korisnik.id)
	    		$('#cancel_hotel').attr("hidden", true);
	    	
	    	if(today_h>date_odlaska && rezervacija.zavrsena==true)
	    		{
	    		$("#rate_hotel").attr("hidden", false);
	    		$("#rate_room").attr("hidden", false);
	    		}
	    	else
	    		{
	    		$("#rate_hotel").attr("hidden", true);
	    		$("#rate_room").attr("hidden", true);
	    		}
	}
	
	if(rezervacija.rezervacijaVozila == null)
	{	
		$("#noRental").prop("hidden",false);
		$("#hasCar").attr("hidden", true);
		$("#cancel_car").attr("hidden", true);
	}
	else
	{

		$.get({
			url: '/api/rents/getVozilo/' + rezervacija.rezervacijaVozila.id,
			success: function(auto) {
				vozilo = auto;
				console.log('auto uspesno vraceno');
				 $("#noRental").prop("hidden",true);
				 $("#hasCar").attr("hidden", false);
				 
				 var check_in_car = new Date(rezervacija.rezervacijaVozila.datum_preuzimanja.substring(0, 10) + "Z");
			     var pravi = check_in_car.toString();
			     var date_check_in_car = new Date(pravi);
			     date_check_in_car.setDate(date_check_in_car.getDate()+1);
			     
			     var check_out_car = new Date(rezervacija.rezervacijaVozila.datum_vracanja.substring(0, 10) + "Z");
			     var pravi = check_out_car.toString();
			     var date_check_out_car = new Date(pravi);
			     date_check_out_car.setDate(date_check_out_car.getDate()+1);
				 
				 $("#check_in").text("Check in date and town: " + date_check_in_car.toString().substring(0, 10) + " " + rezervacija.rezervacijaVozila.mesto_preuzimanja.naziv);
				 $("#check_out").text("Check out date and town: " + date_check_out_car.toString().substring(0, 10) + " " + rezervacija.rezervacijaVozila.mesto_vracanja.naziv);
				 $("#cost").text("Cost: $" + rezervacija.rezervacijaVozila.ukupna_cena);
				$("#car").text("Car: " + vozilo.marka + " " + vozilo.model + " " + vozilo.naziv + " " + vozilo.tip);
				$("#cost").text("Cost: $" + rezervacija.rezervacijaVozila.ukupna_cena);
				
				var today = new Date();
		    	today.setHours(0, 0, 0);
		    	
		    	var preuz = rezervacija.rezervacijaVozila.datum_preuzimanja;
		    	var date_pr = new Date(preuz);
		    	var pravi_pr = date_pr.toString();
		    	var date_preuz = new Date(pravi_pr);
		    	date_preuz.setDate(date_preuz.getDate() - 2);
		    	console.log(date_preuz.toString());
		    	
		    	var vraca = rezervacija.rezervacijaVozila.datum_vracanja;
		    	var date_vr = new Date(vraca);
		    	var pravi_vr = date_vr.toString();
		    	var date_vracanja = new Date(pravi_vr);
		    	
		    	if(today<date_preuz)
		    		{
		    		$("#cancel_car").attr("hidden", false);
		    		}
		    	else
		    		{
		    		$("#cancel_car").attr("hidden", true);
		    		}
		    	
		    	if(rezervacija.korisnik.id!=korisnik.id)
		    		$('#cancel_car').attr("hidden", true);
		    	
		    if(today>date_vracanja && rezervacija.zavrsena==true)
		    		{
		    		$("#rate_rentacar").attr("hidden", false);
		    		$("#rate_car").attr("hidden", false);
		    		}
		    	else
		    		{
		    		$("#rate_rentacar").attr("hidden", true);
		    		$("#rate_car").attr("hidden", true);
		    		}

			},
			error : function(data){
				alert('Greska prilikom vracanja auta.');
			}
		});
		
		$.get({
			url: '/api/rents/getRent/' + rezervacija.rezervacijaVozila.id,
			success: function(servis) {
				rentacar_servis = servis;
				console.log('rent uspesno vraceno');
				
				$("#servis").text("Rentacar: " + rentacar_servis.naziv + ", " + rentacar_servis.adresa.ulica + " " + rentacar_servis.adresa.broj + " " +rentacar_servis.adresa.grad.naziv);
				

			},
			error : function(data){
				alert('Greska prilikom vracanja renta.');
			}
		});
	 

	

	}
}

function rezervisiVozilo()
{
	$("#noRental").attr("hidden", true);
	$("#hocuVozilo").attr("hidden", false);
	
}

function bookHotel(){ 
	
	$("#noHotel").attr("hidden", true);
	$("#infoHotel").attr("hidden", false);
	
}


function capitalize(string) 
{
    return string.charAt(0).toUpperCase() + string.slice(1);
}

function processTime(string)
{
	temp = string.substring(0,16);
	
	temp = temp.replace("-","/");
	temp = temp.replace("-","/");
	
	temp = temp.replace("T"," at ");
	
	return temp;
}



function fillPasengerInfo()
{
	
	var $putniciTabela = $("#putniciTabela");
	
	var $personTemplate = $("#itemTemplate");
    
    
    
    
    for(var i = 0 ; i < rezervacija.osobe.length ; i++)
    {
    	
    	console.log("personTemplate.html: " + $personTemplate.html());

    	
    	var $item = $($personTemplate.html());
    	var osoba = rezervacija.osobe[i];
    	
    	
    	var prtljag = ""
    	if(osoba.prtljag == 0) prtljag = "CARRY ON";
    	if(osoba.prtljag == 1) prtljag = "STANDARD - 20kg MAX";
    	if(osoba.prtljag == 2) prtljag = "SUPERSIZED - 35kg MAX";
    	
    	var potvrdjeno = ""
    	if(osoba.potvrdjeno) potvrdjeno = "✔"
    	else potvrdjeno = "✖"
    	
    	$item.find("#itemNumber").text(i+1);
    	$item.find("#itemName").text(capitalize(osoba.ime) + " " + capitalize(osoba.prezime));
    	$item.find("#itemPass").text(osoba.brojPasosa);
    	$item.find("#itemLug").text(prtljag);
    	$item.find("#itemSeat").text(osoba.sediste);
    	sediste=osoba.sediste;
    	$item.find("#itemConfirmed").text(potvrdjeno);

    	$putniciTabela.append($item);
    	console.log("item.outterHTML: " + $item.prop('outterHTML'));    	
    }
    
    
    
}


function otkaziLet()
{
	$.ajax({
        type: 'DELETE',
        url: 'api/rezervacija/otkaziLet/' + rezervacija.id + '/' + sediste,
        headers: {"Authorization": "Bearer " + token}, 
        contentType: 'application/json',
        success: function (let)
		{
            alert('Flight successfully canceled!');
            window.location.href="index.html";
		}
    });
}

function otkaziHotel()
{
	$.ajax({
        type: 'DELETE',
        url: 'api/rezervacija/otkaziHotel/' + rezervacija.id,
        headers: {"Authorization": "Bearer " + token}, 
        contentType: 'application/json',
        success: function (hotel)
		{
            alert('Hotel reservation successfully canceled!');
            window.location.href="rezervacijaPreview.html?id=" + rezervacija.id;
		}
    });
}


function otkaziAuto()
{
	$.ajax({
        type: 'DELETE',
        url: 'api/rezervacija/otkaziAuto/' + rezervacija.id,
        headers: {"Authorization": "Bearer " + token}, 
        contentType: 'application/json',
        success: function (auto)
		{
            alert('Car reservation successfully canceled!');
            window.location.href="rezervacijaPreview.html?id=" + rezervacija.id;
		}
    });
}

function zavrsiRezervaciju()
{
	$("#zavrsena_dugme").hide();
	
	$.ajax({
        type: 'POST',
        url: 'api/rezervacija/zavrsi/' + rezervacija.id,
        headers: {"Authorization": "Bearer " + token}, 
        contentType: 'application/json',
        success: function (rez)
		{
            window.location.href="rezervacijaPreview.html?id=" + rezervacija.id;
		}
    });
	
	if(odabrano == 'yes')
	{
		points = bonus_poeni - poeni_najblizeg;
		$.ajax({
	        type: 'POST',
	        url: 'api/users/update_bonus/' + points + '/' + rezervacija.korisnik.id,
	        headers: {"Authorization": "Bearer " + token}, 
	        contentType: 'application/json',
	        success: function (rez)
			{
	        	console.log('uspesna promena bonus poena');
			}
	    });
	}
	else if(odabrano == 'no')
	{
		$.ajax({
	        type: 'POST',
	        url: 'api/users/update_bonus/' + bonus_poeni + '/' + rezervacija.korisnik.id,
	        headers: {"Authorization": "Bearer " + token}, 
	        contentType: 'application/json',
	        success: function (rez)
			{
	            console.log('uspesna promena bonus poena');
			}
	    });
	}

}

function oceniLet()
{
	$("#rate_flight").hide();
	$("#rating1").attr("hidden", false);
	
	var aktivne=0;
	document.querySelector('#rating1').addEventListener('click', function (e) {
	    let action = 'add';
	    for (const span of this.children) {
	    	
	        span.classList[action]('active');
	        if (span === e.target) action = 'remove';
	    }
	    aktivne=0;
	    for (const span of this.children)
	    	{
	    	if(span.className=='active')
	    		aktivne+=1;
	    	}
	    
	    console.log(aktivne);
	    
	    $("#ocenaLeta").click(function (){
	    	$.ajax({
	            type: 'POST',
	            url: 'api/rezervacija/ocenaLeta/' + aktivne + '/' + flight.id + '/' + korisnik.id,
	            headers: {"Authorization": "Bearer " + token}, 
	            contentType: 'application/json',
	            success: function (prosek)
	    		{
	            	console.log(prosek);
	                window.location.href="rezervacijaPreview.html?id=" + rezervacija.id;
	    		}
	        });
	    });
	});

}
function oceniAvio()
	{
		$("#rate_avio").hide();
		$("#rating2").attr("hidden", false);
		
		var aktivne=0;
		document.querySelector('#rating2').addEventListener('click', function (e) {
		    let action = 'add';
		    for (const span of this.children) {
		    	
		        span.classList[action]('active');
		        if (span === e.target) action = 'remove';
		    }
		    aktivne=0;
		    for (const span of this.children)
		    	{
		    	if(span.className=='active')
		    		aktivne+=1;
		    	}
		    
		    console.log(aktivne);
		    
		    $("#ocenaAvio").click(function (){
		    	$.ajax({
		            type: 'POST',
		            url: 'api/rezervacija/ocenaAvio/' + aktivne + '/' + flight.id + '/' + korisnik.id,
		            headers: {"Authorization": "Bearer " + token}, 
		            contentType: 'application/json',
		            success: function (prosek)
		    		{
		            	console.log(prosek);
		                window.location.href="rezervacijaPreview.html?id=" + rezervacija.id;
		    		}
		        });
		    });
		});
	}


function oceniHotel()
{
	$("#rate_hotel").hide();
	$("#rating3").attr("hidden", false);
	
	var aktivne=0;
	document.querySelector('#rating3').addEventListener('click', function (e) {
	    let action = 'add';
	    for (const span of this.children) {
	    	
	        span.classList[action]('active');
	        if (span === e.target) action = 'remove';
	    }
	    aktivne=0;
	    for (const span of this.children)
	    	{
	    	if(span.className=='active')
	    		aktivne+=1;
	    	}
	    
	    console.log(aktivne);
	    
	    $("#ocenaHotel").click(function (){
	    	$.ajax({
	            type: 'POST',
	            url: 'api/rezervacija/ocenaHotel/' + aktivne + '/' + hotel.id + '/' + korisnik.id,
	            headers: {"Authorization": "Bearer " + token}, 
	            contentType: 'application/json',
	            success: function (prosek)
	    		{
	            	console.log(prosek);
	                window.location.href="rezervacijaPreview.html?id=" + rezervacija.id;
	    		}
	        });
	    });
	});
}


function oceniSobu()
{
	$("#rate_room").hide();
	$("#rating4").attr("hidden", false);
	
	var aktivne=0;
	document.querySelector('#rating4').addEventListener('click', function (e) {
	    let action = 'add';
	    for (const span of this.children) {
	    	
	        span.classList[action]('active');
	        if (span === e.target) action = 'remove';
	    }
	    aktivne=0;
	    for (const span of this.children)
	    	{
	    	if(span.className=='active')
	    		aktivne+=1;
	    	}
	    
	    console.log(aktivne);
	    
	    $("#ocenaSoba").click(function (){
	    	$.ajax({
	            type: 'POST',
	            url: 'api/rezervacija/ocenaSoba/' + aktivne + '/' + soba.id + '/' + korisnik.id,
	            headers: {"Authorization": "Bearer " + token}, 
	            contentType: 'application/json',
	            success: function (prosek)
	    		{
	            	console.log(prosek);
	                window.location.href="rezervacijaPreview.html?id=" + rezervacija.id;
	    		}
	        });
	    });
	});
}


function oceniRent()
{
	$("#rate_rentacar").hide();
	$("#rating5").attr("hidden", false);
	
	var aktivne=0;
	document.querySelector('#rating5').addEventListener('click', function (e) {
	    let action = 'add';
	    for (const span of this.children) {
	    	
	        span.classList[action]('active');
	        if (span === e.target) action = 'remove';
	    }
	    aktivne=0;
	    for (const span of this.children)
	    	{
	    	if(span.className=='active')
	    		aktivne+=1;
	    	}
	    
	    console.log(aktivne);
	    
	    $("#ocenaRent").click(function (){
	    	$.ajax({
	            type: 'POST',
	            url: 'api/rezervacija/ocenaRent/' + aktivne + '/' + rentacar_servis.id + '/' + korisnik.id,
	            headers: {"Authorization": "Bearer " + token}, 
	            contentType: 'application/json',
	            success: function (prosek)
	    		{
	            	console.log(prosek);
	                window.location.href="rezervacijaPreview.html?id=" + rezervacija.id;
	    		}
	        });
	    });
	});
}


function oceniVozilo()
{
	$("#rate_car").hide();
	$("#rating6").attr("hidden", false);
	
	var aktivne=0;
	document.querySelector('#rating6').addEventListener('click', function (e) {
	    let action = 'add';
	    for (const span of this.children) {
	    	
	        span.classList[action]('active');
	        if (span === e.target) action = 'remove';
	    }
	    aktivne=0;
	    for (const span of this.children)
	    	{
	    	if(span.className=='active')
	    		aktivne+=1;
	    	}
	    
	    console.log(aktivne);
	    
	    $("#ocenaVozilo").click(function (){
	    	$.ajax({
	            type: 'POST',
	            url: 'api/rezervacija/ocenaVozilo/' + aktivne + '/' + vozilo.id + '/' + korisnik.id,
	            headers: {"Authorization": "Bearer " + token}, 
	            contentType: 'application/json',
	            success: function (prosek)
	    		{
	            	console.log(prosek);
	                window.location.href="rezervacijaPreview.html?id=" + rezervacija.id;
	    		}
	        });
	    });
	});
}

function odjava()
{
	localStorage.clear();
}


function nalaziURezervaciji(rezervacija)
{
	retVal = false;
	for( var i = 0 ; i < rezervacija.osobe.length ; i ++ )
	{
		osoba = rezervacija.osobe[i];
		if(osoba.email == korisnik.email)
		{
			retVal = true;
		}
	}
	return retVal;
}
