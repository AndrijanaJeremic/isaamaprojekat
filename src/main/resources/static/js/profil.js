var korisnik = null;
var token;
var cnt_fr = 0;
var friend_list = [];
$(document).ready(function()
		{
	token=localStorage.getItem('jwtToken');
	
	$.post({
		url: "/auth/userprofile",
		headers: {"Authorization": "Bearer " + token},
		contentType: 'application/json',
		data : token,
		  
		success: function(user) {
			
			if(user != "")
			{
				korisnik = user;
				console.log(user);
				if(korisnik.authority.authority!="ROLE_SYSADMIN")
				{
					$("#div_buttons").hide();
					$("#div_buttons_admin").hide();
					$("#div_buttons_system_admin").hide();
					$('#div_button_discount').hide();
					
				}
				else
				{
					$("#div_buttons").show();
					$("#div_buttons_admin").show();
					$("#div_buttons_system_admin").show();
					$('#div_button_discount').show();
				}
				
				document.getElementById("name_profile").value  = user.ime;
				document.getElementById("lastname_profile").value  = user.prezime;
				document.getElementById("email_profile").value  = user.email;
				document.getElementById("phone_profile").value  = user.telefon;
				document.getElementById("city_profile").value  = user.grad.naziv;
				document.getElementById("bonus_profile").value  = user.bonuspoeni;
				
				
				
				getRezervacije();
				setFriendShipRequests();
				setFriends();
				
				selectDefaulView();
			}
			else
			{
				localStorage.clear();
				window.location.href = 'index.html';
			}
			
		},
		
		error: function() {
			//alert('Error');
			console.log('istekao je token');
			localStorage.clear();
			window.location.href = 'index.html';
			
		}
		

		
		
		
		
	
	});
	
	/*
	$('textarea').on('keyup', function(){
  	  $(this).val($(this).val().replace(/[\r\n\v]+/g, ''));
	});
	*/
	
	$("input[name=bonus_points]").bind('keyup mouseup', function () {
		$('#error_bonus_points').hide();
	});
	
	$("input[name=discount]").bind('keyup mouseup', function () {
		$('#error_bonus_points').hide();
	});
	
	$('#add_form').submit(function(event) {
		console.log('add_form submit');
		event.preventDefault();
		
		name = $('input[name="new_name"]').val();
		city = $('input[name="new_city"]').val();
		street = $('input[name="new_street"]').val();
		number = $('input[name="new_street_number"]').val();
		latitude = $('input[name="new_latitude"]').val();
		longitude = $('input[name="new_longitude"]').val();
		description = $('textarea[name="new_description"]').val();
		
		let ispravno = true;
		
		if(!(/[A-Z]/.test(name[0])))
		{
			document.getElementById("error_new_name").innerHTML = "First letter of name must be capital.";
			document.getElementById("error_new_name").style.display  = 'block';
			ispravno=false;
		}
		else
		{
			document.getElementById("error_new_name").innerHTML = "";
			document.getElementById("error_new_name").style.display  = 'none';
		}
			
	
		if(!(/^[a-zA-ZćĆčČšŠđĐžŽ ]+$/.test(city)))
		{
			document.getElementById("error_new_city").innerHTML = "Field City must contains only the letters.";
			document.getElementById("error_new_city").style.display  = 'block';
			ispravno=false;
		}
		else if(!(/[A-Z]/.test( city[0])))
		{
			document.getElementById("error_new_city").innerHTML = "First letter of the city's name must be capital.";
			document.getElementById("error_new_city").style.display  = 'block';
			ispravno=false;
		}
		else
		{
			document.getElementById('error_new_city').innerHTML = "";
			document.getElementById("error_new_city").style.display  = 'none';
		}
		
		
		if(!(/[A-Z]/.test( street[0])))
		{
			document.getElementById("error_new_street").innerHTML = "First letter of the street's name must be capital.";
			document.getElementById("error_new_street").style.display  = 'block';
			ispravno=false;
		}
		else
		{
			document.getElementById('error_new_street').innerHTML = "";
			document.getElementById("error_new_street").style.display  = 'none';
		}
			
		
		if(!(/^[0-9.]+$/.test(latitude)))
		{
			document.getElementById("error_new_latitude").innerHTML = "Field Latitude must contains only numbers.";
			document.getElementById("error_new_latitude").style.display  = 'block';
			ispravno=false;
		}
		else
		{
			document.getElementById("error_new_latitude").innerHTML = "";
			document.getElementById("error_new_latitude").style.display  = 'none';
		}
		
		
		if(!(/^[0-9.]+$/.test(longitude)))
		{
			document.getElementById("error_new_longitude").innerHTML = "Field Latitude must contains only numbers.";
			document.getElementById("error_new_longitude").style.display  = 'block';
			ispravno=false;
		}
		else
		{
			document.getElementById("error_new_longitude").innerHTML = "";
			document.getElementById("error_new_longitude").style.display  = 'none';
		}
		
		if(ispravno == true)
		{
			$.get({
				url: "/api/address/checkCity/" + city + '/' + street + '/' + number + '/' + longitude + '/' + latitude,
				headers: {"Authorization": "Bearer " + token},
				contentType: 'application/json',
				success: function(data) {
					if(data == null || data == "")
					{
						alert('nije pronadjen grad');
						//newCity(city);
					}
					else
					{
						console.log('pronadjena adresa: ' + data.id);
						finalAdd(data);
					}
					//window.location.search='';
				},
				
				error: function() {
					alert('Error with loading city');
				}
			
			});
		}
			
	});
	
	$('#add_form_admin').submit(function(event) {
		console.log('add_form_admin submit');
		event.preventDefault();
		
		let name_admin = $('input[name="first_name_admin"]').val();
		let last_name_admin = $('input[name="last_name_admin"]').val();
		let email_admin = $('input[name="email_admin"]').val();
		let pass_admin = $('input[name="pass_admin"]').val();
		let city_admin = $('input[name="city_admin"]').val();
		let phone_admin = $('input[name="phone_admin"]').val();
		let option = $( "select[name=admin_id] option:selected" ).val();
		option = parseInt(option, 10);
		
		let ispravno = true;
		document.getElementById("error_email").innerHTML = "";
		document.getElementById("error_email").style.display  = 'none';
		
		if(!(/^[a-zA-ZćĆčČšŠđĐžŽ ]+$/.test(name_admin)))
		{
			document.getElementById("error_name").innerHTML = "First name must contains only the letters.";
			document.getElementById("error_name").style.display = 'block';
			ispravno=false;
		}
		else if(!(/[A-Z]/.test( name_admin[0])))
		{
			document.getElementById("error_name").innerHTML = "First letter of name must be capital.";
			document.getElementById("error_name").style.display  = 'block';
			ispravno=false;
		}
		else
		{
			document.getElementById("error_name").innerHTML = "";
			document.getElementById("error_name").style.display  = 'none';
		}
		
	
		if(!(/^[a-zA-ZćĆčČšŠđĐžŽ ]+$/.test(last_name_admin)))
		{
			document.getElementById("error_last_name").innerHTML = "Last name must contains only the letters.";
			document.getElementById("error_last_name").style.display  = 'block';
			ispravno=false;
		}
		else if(!(/[A-Z]/.test( last_name_admin[0])))
		{
			document.getElementById("error_last_name").innerHTML = "First letter of last name must be capital.";
			document.getElementById("error_last_name").style.display  = 'block';
			ispravno=false;
		}
		else
		{
			document.getElementById("error_last_name").innerHTML = ("");
			document.getElementById("error_last_name").style.display  = 'none';
		}
			
		
	
		if(!(/^[a-zA-ZćĆčČšŠđĐžŽ ]+$/.test(city_admin)))
		{
			document.getElementById("error_city").innerHTML = "Field City must contains only the letters.";
			document.getElementById("error_city").style.display  = 'block';
			ispravno=false;
		}
		else if(!(/[A-Z]/.test( city_admin[0])))
		{
			document.getElementById("error_city").innerHTML = "First letter of the city's name must be capital.";
			document.getElementById("error_city").style.display  = 'block';
			ispravno=false;
		}
		else
		{
			document.getElementById('error_city').innerHTML = "";
			document.getElementById("error_city").style.display  = 'none';
		}
			
		
		if(!(/^[0-9]+$/.test(phone_admin)))
		{
			document.getElementById("error_phone").innerHTML = "Field Phone number must contains only numbers.";
			document.getElementById("error_phone").style.display  = 'block';
			ispravno=false;
		}
		else
		{
			document.getElementById("error_phone").innerHTML = "";
			document.getElementById("error_phone").style.display  = 'none';
		}
		
		//atribut aktiviran ce se ovde koristiti kao identifikator uloge: 0-system admin; 1-avio admin; 2-hotel admin; 3-rental admin
		let uloga;
		
		if(document.getElementById('admin_id').innerHTML == 'Car rentals: ')
		{
			uloga = 3;
		}
		else if(document.getElementById('admin_id').innerHTML == 'Hotels: ')
		{
			uloga = 2;
		}
		else if(document.getElementById('admin_id').innerHTML == 'Airlines:')
		{
			uloga = 1;
		}
		else
		{
			uloga = 0;
		}
		
		if(ispravno == true)
		{
			$.post({
				url: "/api/users/registruj_admina/" + uloga,
				data: JSON.stringify({email: email_admin, lozinka: pass_admin, ime: name_admin, prezime: last_name_admin, grad: city_admin, telefon: phone_admin, admin_id: option}),
				headers: {"Authorization": "Bearer " + token},
				contentType: 'application/json',
				success: function(data) {
					if(data == null || data == ""){
						document.getElementById("error_email").innerHTML = 'User with this email already exists!';
						document.getElementById("error_email").style.display = 'block';
					}
					else {
						console.log('Uspesno ste dodali novog administratora');
						window.location.href="profil.html";
					
					}
				}
			
				
			});
		}
			
	});
	
	$('#search_form_1').submit(function(event) {
		console.log('search_form_1 submit');
		event.preventDefault();
		
		var attr = $('#passChange').attr('pomoc');
		let email_profile = $("#email_profile").val();
		
		if($('input[value=edit]').val() == 'edit' && (typeof attr == typeof undefined || attr == false))
		{
			$("#name_profile").prop('readonly', false);
			$("#lastname_profile").prop('readonly', false);
			$("#phone_profile").prop('readonly', false);
			$("#city_profile").prop('readonly', false);
			
			$('input[value=edit]').val('save changes');
			$('button[onclick="javascript:changePassword()"]').hide();
		}
		else if($('input[value="save changes"]').val() == 'save changes' && (typeof attr == typeof undefined || attr == false))
		{
			let name_profile = $("#name_profile").val();
			let lastname_profile = $("#lastname_profile").val();
			let email_profile = $("#email_profile").val();
			let phone_profile = $("#phone_profile").val();
			let city_profile = $("#city_profile").val();
			
			$.post({
				url: "/api/users/changeData/" + korisnik.email,
				data: JSON.stringify({email: email_profile, ime: name_profile, prezime: lastname_profile, grad: city_profile, telefon: phone_profile}),
				contentType: 'application/json',
				headers: {"Authorization": "Bearer " + token},
				success: function() {
					console.log('uspesna promena podataka korisnika');
					window.location.href = 'profil.html';
				}
			});
		}
		else
		{
			console.log('password is changing');
			
			let ispravno = true;
			
			$('#error_old_pass_profile').hide();
			let old_password = $("#old_password_profile").val();
			let new_password = $("#new_password_profile").val();
			let confirm_new_password = $("#confirm_new_password_profile").val();
			
			if(!(new_password == confirm_new_password))
			{
				$('#error_pass_profile').show();
				ispravno=false;
			}
			else
				$('#error_pass_profile').hide();
			
			if(ispravno == true)
			{
				$.post({
					url: "/auth/change_password/",
					headers: {"Authorization": "Bearer " + token},
					data: JSON.stringify({oldPassword: old_password, newPassword: new_password}),
					contentType: 'application/json',
					success: function(data) {
						
						if(data.result == 'success')
						{
							console.log('uspesna promena lozinke korisnika');
							
							$('#passChange').removeAttr('pomoc');
							$("#old_password_profile").prop('required', false);
					        $("#new_password_profile").prop('required', false);
					        $("#confirm_new_password_profile").prop('required', false);
					        $('#error_old_pass_profile').hide();
					        
							loginAgain(email_profile, new_password);
						}
						else
						{
							$('#error_old_pass_profile').show();
						}
						
					},
					error: function(data)
					{
						console.log('greska prilikom menjanja lozinke');
					}
				});
				
					
				
			}
			
			
			
			
		}
		
	});
	
	
	$('#form_discount').submit(function(event) {
		console.log('form_discount submit');
		event.preventDefault();
		
		let bonus_points = $('input[name="bonus_points"]').val();
		let discount = $('input[name="discount"]').val();
		
		$.post({
			url: "/api/bonus/save/",
			headers: {"Authorization": "Bearer " + token},
			data: JSON.stringify({bonusPoeni: bonus_points, popust: discount}),
			contentType: 'application/json',
			success: function(data) {
				if(data.result == 'success')
				{
					console.log('uspesno dodat bonus');
					$('#error_bonus_points').hide();
					window.location.href = 'profil.html';
				}
				else
				{
					$('#error_bonus_points').show();
				}
			}
		});
	});



	function getRezervacije()
	{
		$.get({
			url: "/api/rezervacija/getAllReservations/" + korisnik.id,
			headers: {"Authorization": "Bearer " + token},
			success: function(data) 
			{
				$.each(data, function( index, value ) 
				{			
					if(value.zavrsena==true && value.korisnik.id!=korisnik.id)
					{	
					insertReservation(value);
					}
					
					if(value.korisnik.id==korisnik.id)
						{
						insertReservation(value);
						}
				});
				if(data.length > 0)
				{
					$("#noRezervations").prop("hidden", true);
				}
			},
		});
	}



	function insertReservation(rezervacija)
	{
		var $reservationTemplate = $('#reservationTemplate');
	    var $item = $($reservationTemplate.html())
	    
	    $item.find("#fromTo").text(rezervacija.let.odakle + " to " + rezervacija.let.dokle);
	    
	    $item.find("#previewButton").prop("id","previewButton" + rezervacija.id);
	    $item.find("#creator").text("created by: " + rezervacija.korisnik.ime + " " + rezervacija.korisnik.prezime);
	  

	    
	    if(potvrdjenaRez(rezervacija))
	    {
	    	 $item.find("#nepotvrdjeaRez").prop("hidden",true);
	    	 $item.find("#prihvatiButton").prop("hidden",true);
		    	$item.find("#odbijButton").prop("hidden",true);
	    }
	    else
	    {
	    	var today = new Date();
	    	var today_vreme = today.getTime();
	    	
	    	var vreme = rezervacija.let.vremePoletanja;
	    	var date = new Date(vreme);
	    	var pravi = date.toString();
	    	var date_pol = new Date(pravi);
	    	var vr_poletanja = date_pol.getTime();
	    	var vreme3=vr_poletanja-10800000;
	    	
	    	var datum_rez = rezervacija.datum_rezervacije;
	    	var date_rez = new Date(datum_rez);
	    	var pravi_rez = date_rez.toString();
	    	var date_vrrez = new Date(pravi_rez);
	    	var vr_rez = date_vrrez.getTime();
	    	var vreme3dana=vr_rez+259200000;
	    	
	    	if(today_vreme>vreme3 || today_vreme>vreme3dana)
	    	{
	    		$.ajax({
	    	        type: 'DELETE',
	    	        url: 'api/rezervacija/obrisiOsobu/' + korisnik.id + '/' + rezervacija.id,
	    	        headers: {"Authorization": "Bearer " + token},
	    	        contentType: 'application/json',
	    	        success: function (rez)
	    			{
	    	            console.log("Osoba obrisana iz osoba_iz_rez i nije vise u rezervaciji " + rez.id);
	    	            window.location.href="profil.html";
	    			}
	    	    });
	    	}
	    	else
	    	{
		    	$item.find("#nepotvrdjeaRez").prop("hidden",false);
		    	$item.find("#prihvatiButton").prop("hidden",false);
		    	$item.find("#prihvatiButton").click(function(){ prihvatiRez(rezervacija.id);});
		    	$item.find("#odbijButton").prop("hidden",false);
		    	$item.find("#odbijButton").click(function(){ odbijRez(rezervacija.id);});
	    	}
	    }
	    
	    var $reservationList = $("#reservationList");
	    $reservationList.append($item);
	    
		   $("#previewButton"+rezervacija.id).click(function(){
			   window.location.replace("/rezervacijaPreview.html?id="+rezervacija.id);
		   });

	}

function odbijRez(rez_id)
{
	
	$.ajax({
        type: 'DELETE',
        url: 'api/rezervacija/obrisiOsobu/' + korisnik.id + '/' + rez_id,
        headers: {"Authorization": "Bearer " + token},
        contentType: 'application/json',
        success: function (rez)
		{
        	
            console.log("Osoba obrisana iz osoba_iz_rez i nije vise u rezervaciji " + rez.id);
            window.location.href="profil.html";
		}
    });


}

function prihvatiRez(rez_id)
{
	//alert("REZ ID: " + rez_id);
	$.ajax({
        type: 'PUT',
        url: 'api/rezervacija/prihvatiRez/' + korisnik.id + '/' + rez_id,
        headers: {"Authorization": "Bearer " + token},
        contentType: 'application/json',
        success: function (rez)
		{
            console.log("Osoba prihvatila rezervaciju " + rez.id);
            window.location.href="profil.html";
		}
    });


}

	function potvrdjenaRez(rezervacija)
	{
		retVal = true;
		
		for(var i = 0 ; i < rezervacija.osobe.length ; i++)
		{
			osoba = rezervacija.osobe[i];
			if(osoba.email == korisnik.email)
			{
				console.log("nasao potvrdu.")
				return osoba.potvrdjeno;
			}
		}
		
	}

	
	function setFriendShipRequests()
	{
		cnt_fr = 0;
		console.log(korisnik.prijateljstva)
		for( var i = 0 ; i < korisnik.prijateljstva.length ; i ++ )
		{
			if(korisnik.prijateljstva[i].stanje == 1) //stanje na cekanju
			{
				cnt_fr++;
				zahtevOd = korisnik.prijateljstva[i].salje;
				ubaciZahtev(korisnik.prijateljstva[i].id, zahtevOd);		
			}
		}
		if(cnt_fr == 0)
		{
			$("#noNewRequests").prop("hidden",false);
		}
		
		
	}


	function ubaciZahtev(zahtevID, luv)
	{
		var $requestTemplate = $('#requestTemplate');
	    var $item = $($requestTemplate.html())
	    
	    $item.find("#from").text(luv.ime + " " + luv.prezime);
	    
	    $item.find("#confirmButton").prop("id","confirmButton" + zahtevID);
	    $item.find("#declineButton").prop("id","declineButton" + zahtevID);
	  

	    $item.find("#requestDiv").prop("id","requestDiv"+zahtevID);
	 
	    var $requestList = $("#requestList");
	    $requestList.append($item);
	    
		$("#confirmButton"+zahtevID).click(function()
		{
		    $.ajax({
		        type: 'PUT',
		        url: 'api/friendRequest/' + token + "/" + zahtevID,
		        headers: {"Authorization": "Bearer " + token},
		        complete: function (data)
				{
		        	if(data == null)
		        	{
		        		console.log("greska");
		        		
		        	}
		        	else
		        	{
		        		//izbrisi i ubaci u listu prijatelja
		        		$("#requestDiv"+zahtevID).prop("hidden",true);
		        		cnt_fr--;
		        		if(cnt_fr == 0)
		        		{
		        			$("#noNewRequests").prop("hidden",false);
		        		}
		        		insertFirend(luv);
		        	}
				}
		    });
		});
		
		$("#declineButton"+zahtevID).click(function()
		{
		
					    $.ajax({
					        type: 'DELETE',
					        url: 'api/friendRequest/delete/' + token + "/" + zahtevID,
					        headers: {"Authorization": "Bearer " + token},
					        complete: function (data)
							{
					        	if(data == null)
					        	{
					        		console.log("greska")
					        	}
					        	else
					        	{
					        		//izbrisi samo
					        		$("#requestDiv"+zahtevID).prop("hidden",true);
					        		cnt_fr--;
					        		if(cnt_fr == 0)
					        		{
					        			$("#noNewRequests").prop("hidden",false);
					        		}
					        	}
							}
					    });
			
			
		});

	}


	
		
		
}
);
/*
function refresh()
{
	$.post({
		url: "/auth/refresh",
		headers: 'Authorization',
		contentType: 'application/json',
		  
		success: function(res) {
			if(res!="" && res.accessToken != null)
			{
				localStorage.setItem('jwtToken', res.accessToken);
			}
			else
			{
				localStorage.removeItem('jwtToken');
			}
		},
		
		error: function() {
			console.log('greska prilikom refresh-ovanja tokena');
		}
	
	});
}
*/

function loginAgain(email, pass)
{
	$.post({
		url: "/auth/login",
		headers: {"Authorization": "Bearer " + token},
		data: JSON.stringify({email: email, lozinka: pass}),
		contentType: 'application/json',
		  
		success: function(res) {
			if(res!="" && res.accessToken != null)
			{
				localStorage.setItem('jwtToken', res.accessToken);
				window.location.href="profil.html";
			}
			else
			{
				console.log('greska prilikom logovanja sa novom lozinkom');
			}
				
		}
	});
}

function newAirline()
{
	console.log('[profil.js: newAirline()]');
	document.getElementById("div_new").style.display  = 'block';
	document.getElementById("div_buttons").style.display  = 'none';
	document.getElementById("div_new_admin").style.display  = 'none';
	document.getElementById("div_buttons_admin").style.display  = 'block';
	document.getElementById("div_buttons_system_admin").style.display  = 'block';
	document.getElementById('button_add').value = 'Add airline';
	document.getElementById("div_define_discount").style.display  = 'block';
	
	var el = document.getElementById('div_new');
    el.scrollIntoView(true);
    window.scrollBy(0, -100);
}

function newHotel()
{
	console.log('[profil.js: newHotel()]');
	document.getElementById("div_new").style.display  = 'block';
	document.getElementById("div_buttons").style.display  = 'none';
	document.getElementById("div_new_admin").style.display  = 'none';
	document.getElementById("div_buttons_admin").style.display  = 'block';
	document.getElementById("div_buttons_system_admin").style.display  = 'block';
	document.getElementById('button_add').value = 'Add hotel';
	document.getElementById("div_define_discount").style.display  = 'block';
	
	var el = document.getElementById('div_new');
    el.scrollIntoView(true);
    window.scrollBy(0, -100);
}

function newRental()
{
	console.log('[profil.js: newRental()]');
	document.getElementById("div_new").style.display  = 'block';
	document.getElementById("div_buttons").style.display  = 'none';
	document.getElementById("div_new_admin").style.display  = 'none';
	document.getElementById("div_buttons_admin").style.display  = 'block';
	document.getElementById("div_buttons_system_admin").style.display  = 'block';
	document.getElementById('button_add').value = 'Add car rental';
	document.getElementById("div_define_discount").style.display  = 'block';
	
	var el = document.getElementById('div_new');
    el.scrollIntoView(true);
    window.scrollBy(0, -100);
}

function back()
{
	console.log('[profil.js: back()]');
	document.getElementById("div_new").style.display  = 'none';
	document.getElementById("div_buttons").style.display  = 'block';
	
	var el = document.getElementById('div_buttons');
    el.scrollIntoView(true);
    window.scrollBy(0, -100);
}

function finalAdd(address)
{
	console.log('[profil.js: finalAdd()]');
	
	if(document.getElementById('button_add').value == 'Add airline')
	{
		$.post({
			url: "/rest/airline/save",
			data: JSON.stringify({naziv: name, opis: description, adresa: address, prosecna_ocena: 0}),
			contentType: 'application/json',
			headers: {"Authorization": "Bearer " + token},
			success: function(airline) {
				console.log('aviokompanija uspesno uneta');
				var url = window.location.search='';
			},
			
			error: function() {
				//alert('Error');
				document.getElementById("error_new_name").innerHTML = "There is already airline with this name.";
				document.getElementById("error_new_name").style.display  = 'block';
			}
		
		});
	}
	else if(document.getElementById('button_add').value == 'Add hotel')
	{
		$.post({
			url: "/api/hotels/save",
			data: JSON.stringify({naziv: name, promotivni_opis: description, adresa: address, prosecna_ocena: 0}),
			contentType: 'application/json',
			headers: {"Authorization": "Bearer " + token},
			success: function(airline) {
				console.log('hotel uspesno unet');
				var url = window.location.search='';
			},
			
			error: function() {
				//alert('Error');
				document.getElementById("error_new_name").innerHTML = "There is already car rental with this name.";
				document.getElementById("error_new_name").style.display  = 'block';
			}
		
		});
	}
	else
	{
		$.post({
			url: "/api/rents/save",
			data: JSON.stringify({naziv: name, promotivni_opis: description, adresa: address, prosecna_ocena: 0}),
			headers: {"Authorization": "Bearer " + token},
			contentType: 'application/json',
			  
			success: function(airline) {
				console.log('rental uspesno unet');
				var url = window.location.search='';
			},
			
			error: function() {
				//alert('Error');
				document.getElementById("error_new_name").innerHTML = "There is already hotel with this name.";
				document.getElementById("error_new_name").style.display  = 'block';
			}
		
		});
	}
}

function newAirlineAdmin()
{
	console.log('[profil.js: newAirlineAdmin()]');
	document.getElementById("div_new_admin").style.display  = 'block';
	document.getElementById("div_buttons_admin").style.display  = 'none';
	document.getElementById("div_buttons_system_admin").style.display  = 'none';
	document.getElementById("div_new").style.display  = 'none';
	document.getElementById("div_buttons").style.display  = 'block';
	document.getElementById('button_add_admin').value = 'Add airline administrator';
	document.getElementById('admin_sys_close').style.display = 'block';
	document.getElementById('admin_id').innerHTML = 'Airlines:';
	document.getElementById("div_define_discount").style.display  = 'block';
	
	var el = document.getElementById('div_new_admin');
    el.scrollIntoView(true);
    window.scrollBy(0, -50);
	
	$.get({
		url: "/rest/airline/all_admin",
		headers: {"Authorization": "Bearer " + token},
		contentType: 'application/json',
		success: function(airlines) {

			if(airlines == null || airlines.length == 0)
			{
				console.log('There are no airlines');
				document.getElementById("div_new_admin").style.display  = 'none';
				document.getElementById('no_one').innerHTML = 'There are no airlines to add their administrators.';
				document.getElementById('no_one').style.display = 'block';
				document.getElementById('button_back_admin2').style.display = 'block';
			}
			else
			{
				console.log('There are ' + airlines.length + ' airlines in memory.');
				document.getElementById('no_one').style.display = 'none';
				
				var select = $('select[name=admin_id]');
				select.children().remove();
				for (let airline of airlines) 
				{
					var option = document.createElement("option");
					option.text = airline.naziv;
					option.value = airline.id;
					select.append(option);
				}
			}
		},
		error : function(data){
			alert('Error with loading airlines');
		}
	});
}

function newHotelAdmin()
{
	console.log('[profil.js: newHotelAdmin()]');
	document.getElementById("div_new_admin").style.display  = 'block';
	document.getElementById("div_buttons_admin").style.display  = 'none';
	document.getElementById("div_buttons_system_admin").style.display  = 'none';
	document.getElementById("div_new").style.display  = 'none';
	document.getElementById("div_buttons").style.display  = 'block';
	document.getElementById('button_add_admin').value = 'Add hotel administrator';
	document.getElementById('admin_sys_close').style.display = 'block';
	document.getElementById('admin_id').innerHTML = 'Hotels: ';
	document.getElementById("div_define_discount").style.display  = 'block';
	
	var el = document.getElementById('div_new_admin');
    el.scrollIntoView(true);
    window.scrollBy(0, -50);
	
	$.get({
		url: "/api/hotels/all_admin",
		headers: {"Authorization": "Bearer " + token},
		contentType: 'application/json',
		success: function(hoteli) {

			if(hoteli == null || hoteli.length == 0)
			{
				console.log('There are no hotels');
				document.getElementById("div_new_admin").style.display  = 'none';
				document.getElementById('no_one').innerHTML = 'There are no hotels to add their administrators.';
				document.getElementById('no_one').style.display = 'block';
				document.getElementById('button_back_admin2').style.display = 'block';
			}
			else
			{
				console.log('There are ' + hoteli.length + ' hotels in memory.');
				document.getElementById('no_one').style.display = 'none';
				
				var select = $('select[name=admin_id]');
				select.children().remove();
				for (let hotel of hoteli) 
				{
					var option = document.createElement("option");
					option.text = hotel.naziv;
					option.value = hotel.id;
					select.append(option);
				}
			}
		},
		error : function(data){
			alert('Error with loading hotels');
		}
	});
}

function newRentalAdmin()
{
	console.log('[profil.js: newRentalAdmin()]');
	document.getElementById("div_new_admin").style.display  = 'block';
	document.getElementById("div_buttons_admin").style.display  = 'none';
	document.getElementById("div_buttons_system_admin").style.display  = 'none';
	document.getElementById("div_new").style.display  = 'none';
	document.getElementById("div_buttons").style.display  = 'block';
	document.getElementById('button_add_admin').value = 'Add car rental administrator';
	document.getElementById('admin_sys_close').style.display = 'block';
	document.getElementById('admin_id').innerHTML = 'Car rentals: ';
	document.getElementById("div_define_discount").style.display  = 'block';
	
	var el = document.getElementById('div_new_admin');
    el.scrollIntoView(true);
    window.scrollBy(0, -50);
	
	$.get({
		url: "/api/rents/all_admin",
		headers: {"Authorization": "Bearer " + token},
		contentType: 'application/json',
		success: function(rentals) {

			if(rentals == null || rentals.length == 0)
			{
				console.log('There are no rentals');
				document.getElementById("div_new_admin").style.display  = 'none';
				document.getElementById('no_one').innerHTML = 'There are no rentals to add their administrators.';
				document.getElementById('no_one').style.display = 'block';
				document.getElementById('button_back_admin2').style.display = 'block';
			}
			else
			{
				console.log('There are ' + rentals.length + ' rentals in memory.');
				document.getElementById('no_one').style.display = 'none';
				
				var select = $('select[name=admin_id]');
				select.children().remove();
				for (let rental of rentals) 
				{
					var option = document.createElement("option");
					option.text = rental.naziv;
					option.value = rental.id;
					select.append(option);
				}
			}
		},
		error : function(data){
			alert('Error with loading rentals');
		}
	});
}

function newSystemAdmin()
{
	console.log('[profil.js: newSystemAdmin()]');
	document.getElementById("div_new_admin").style.display  = 'block';
	document.getElementById("div_buttons_system_admin").style.display  = 'none';
	document.getElementById("div_buttons_admin").style.display  = 'none';
	document.getElementById("div_new").style.display  = 'none';
	document.getElementById("div_buttons").style.display  = 'block';
	document.getElementById('button_add_admin').value = 'Add system administrator';
	document.getElementById('admin_sys_close').style.display = 'none';
	document.getElementById("div_define_discount").style.display  = 'block';
	
	var el = document.getElementById('div_new_admin');
    el.scrollIntoView(true);
    window.scrollBy(0, -100);
}

function defineDiscount()
{
	console.log('[profil.js: defineDiscount()]');
	document.getElementById("div_define_discount").style.display  = 'block';
	document.getElementById("div_button_discount").style.display  = 'none';
	document.getElementById("div_new_admin").style.display  = 'none';
	document.getElementById("div_buttons_system_admin").style.display  = 'block';
	document.getElementById("div_buttons_admin").style.display  = 'block';
	document.getElementById("div_new").style.display  = 'none';
	document.getElementById("div_buttons").style.display  = 'block';
	document.getElementById('admin_sys_close').style.display = 'none';
	
	var el = document.getElementById('div_define_discount');
    el.scrollIntoView(true);
    window.scrollBy(0, -250);
}

function backAdmin()
{
	console.log('[profil.js: backAdmin()]');
	document.getElementById("div_new_admin").style.display  = 'none';
	document.getElementById("div_buttons_admin").style.display  = 'block';
	document.getElementById("div_buttons_system_admin").style.display  = 'block';
	document.getElementById("no_one").style.display  = 'none';
	document.getElementById("button_back_admin2").style.display  = 'none';
	document.getElementById("div_define_discount").style.display  = 'none';
	document.getElementById("div_button_discount").style.display  = 'block';
	
	var el = document.getElementById('div_buttons_admin');
    el.scrollIntoView(true);
    window.scrollBy(0, -250);
}

function changePassword()
{
	console.log('changing password');
	
	$('#passChange').show();
	$('input[value=edit]').hide();
	
	var $passChange = $('#passChange');
	if(!$passChange.attr('pomoc'))
	{
    	$passChange.attr('pomoc', 'pomoc');

        $("#old_password_profile").prop('required', true);
        $("#new_password_profile").prop('required', true);
        $("#confirm_new_password_profile").prop('required', true);
    }
    
}


function setFriends()
{
	$.get({
		url: "/api/users/friendsOf/" + korisnik.id,
		headers: {"Authorization": "Bearer " + token},
		contentType: 'application/json',
		success: function(data) 
		{
			if(data == null)
			{
				console.log("nullcina");
			}		
			for(let friend of data)
			{
				insertFirend(friend);
				friend_list.push(friend);
			}
			
			
			
						
		},
		error : function(data)
		{
			console.log("errorcina prilikom prihvatanja prijatelja");
		}
		});
	
	setSearch();
}


function insertFirend(friend)
{
	var $friendTemplate = $('#friendTemplate');
    var $item = $($friendTemplate.html())
    
    $item.find("#friendName").text(friend.ime + " " + friend.prezime);
    
    $item.find("#unfriendButton").prop("id","unfriendButton" + friend.id);
  

    $item.find("#friendDiv").prop("id","friendDiv"+friend.id);
 
    var $friendList = $("#friendList");
    $friendList.append($item);
    
	$("#unfriendButton"+friend.id).click(function()
	{
	    $.ajax({
	        type: 'DELETE',
	        url: 'api/friendRequest/deleteFriend/' + token + "/" + friend.id,
	        headers: {"Authorization": "Bearer " + token},
			contentType: 'application/json',
	        complete: function (data)
			{
	        	if(data == null)
	        	{
	        		console.log("greska")
	        	}
	        	else
	        	{
	        		//izbrisi samo
	        		$("#friendDiv"+ friend.id).prop("hidden",true);
	        	}
			}
	    });
	});
}


function insertUser(friend)
{
	var $userTemplate = $('#userTemplate');
    var $item = $($userTemplate.html())
    
    $item.find("#userName").text(friend.ime + " " + friend.prezime);
    
    $item.find("#befriendButton").prop("id","befriendButton" + friend.id);
  

    $item.find("#userDiv").prop("id","userDiv"+friend.id);
 
    var $userList = $("#userList");
    $userList.append($item);
    
	$("#befriendButton"+friend.id).click(function()
	{
	    $.ajax({
	        type: 'POST',
	        url: 'api/friendRequest/befriend/' + token + "/" + friend.id,
	        headers: {"Authorization": "Bearer " + token},
			contentType: 'application/json',
	        complete: function (data)
			{
	        	if(data == null || data == undefined || data =="")
	        	{
	        		console.log("greska u kreiranju");        		
	        	}
	        	else
	        	{
	        		$("#befriendButton"+friend.id).html("REQUEST SENT");
	        		$("#befriendButton"+friend.id).prop("disabled",true);
	        		$("#befriendButton"+friend.id).css("background-color","#ff9966");
	        		console.log(data)
	        	}
			}
	    });
	});
}



function selectDefaulView()
{
	$("#prijateljiSection").prop("hidden",false);
	$("#neprijateljiSection").prop("hidden",true);
	
	$("#peopleView").change(function() 
	{
		var value = $( "#peopleView option:selected" ).val();
		if(value == "users")
		{
			$("#prijateljiSection").prop("hidden",true);
			$("#neprijateljiSection").prop("hidden",false);
		}
		if(value == "friends")
		{
			$("#prijateljiSection").prop("hidden",false);
			$("#neprijateljiSection").prop("hidden",true);
		}
		if(value !="friends" && value!="users")
		{
			return;
		}
	});

}

function getValOfSelect()
{
 	return $( "#peopleView option:selected" ).val();
}


function setSearch()
{

	$("#goButton").click(function(){
		
		
		var searchText = $("#searchField").val().toLowerCase();
		if(getValOfSelect() == "friends")
		{
			$("#friendList").empty();
			for(var i = 0 ; i < friend_list.length ; i ++)
			{				
				thisFriend = friend_list[i].ime.toLowerCase() + " " + friend_list[i].prezime.toLowerCase();  
				if(thisFriend.indexOf(searchText) >= 0)
				{
					insertFirend(friend_list[i]);
				}
			}
		}
		if(getValOfSelect() == "users")
		{
			$("#userList").empty();
			if(searchText == "") return;
			$.get({
				url: "/api/users/withNames/" + searchText + "/" + korisnik.id,
				headers: {"Authorization": "Bearer " + token},
				contentType: 'application/json',
				success: function(data) 
				{
					if(data == null)
					{
						console.log("nullcina");
					}		
					for(let user of data)
					{
						insertUser(user);
					}
				
				},
				error : function(data)
				{
					console.log("errorcina prilikom prihvatanja usera");
				}
				});
			
		}
		
	});

}
