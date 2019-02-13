token = localStorage.getItem('jwtToken');

$(document).ready(function()	
{
	
	provera = false;
	
	var search = window.location.search;
    id_presented = search.substring(4);
    $('.new_additional_service').hide();
    
    console.log('[hotelAdmin.js: document.ready()]: id hotela: ' + id_presented);
    
    $.post({
		url: "/auth/userprofile",
		headers: {"Authorization": "Bearer " + token},
		contentType: 'application/json',
		data : token,
		  
		success: function(user) {
			
			if(user != "")
			{
				korisnik = user;
				id_korisnik = user.id;
				console.log(user);
				
				checkAdmin();
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
    $('input[name=price_service]').on('keyup', function(){
  	  $('#error_price_service').hide();
    });
    
    var today = new Date();
	var dd = today.getDate();
	var mm = today.getMonth()+1; //January is 0!
	var yyyy = today.getFullYear();
	 if(dd<10){
	        dd='0'+dd
	    } 
	    if(mm<10){
	        mm='0'+mm
	    } 

	today = yyyy+'-'+mm+'-'+dd;
	
	$('input[name=start_special_price]').attr("min", today);
	$('input[name=end_special_price]').attr("min", today);
	
	$('input[name=start_special_price]').on('change', function(){
		
		var datum = $('input[name=start_special_price]').val();
		$('input[name=end_special_price]').attr("min", datum);
		
		var date_start = new Date(datum);
		var date_end = new Date($('input[name=end_special_price]').val());
		
		if(date_end < date_start)
		{
			$('input[name=end_special_price]').val(datum);
		}
	});
	
	$('#edit_hotel_form').submit(function(event) {
		console.log('edit_hotel_form submit');
		event.preventDefault();
		
		var attr = $('#button_hotel_back').attr('pritisnuto');
		
		if (typeof attr !== typeof undefined && attr !== false)
		{
			$('#button_hotel_back').removeAttr('pritisnuto');
		}
		else
		{
			console.log('promena hotela');
			
			name = $('input[name="hotel_name"]').val();
			city = $('input[name="hotel_city"]').val();
			street = $('input[name="hotel_street"]').val();
			number = $('input[name="hotel_street_number"]').val();
			latitude = $('input[name="hotel_latitude"]').val();
			longitude = $('input[name="hotel_longitude"]').val();
			description = $('textarea[name="hotel_description"]').val();
			
			let ispravno = true;
			
			if(!(/[A-Z]/.test(name[0])))
			{
				document.getElementById("error_hotel_name").innerHTML = "First letter of name must be capital.";
				document.getElementById("error_hotel_name").style.display  = 'block';
				ispravno=false;
			}
			else
			{
				document.getElementById("error_hotel_name").innerHTML = "";
				document.getElementById("error_hotel_name").style.display  = 'none';
			}
				
		
			if(!(/^[a-zA-ZćĆčČšŠđĐžŽ ]+$/.test(city)))
			{
				document.getElementById("error_hotel_city").innerHTML = "Field City must contains only the letters.";
				document.getElementById("error_hotel_city").style.display  = 'block';
				ispravno=false;
			}
			else if(!(/[A-Z]/.test( city[0])))
			{
				document.getElementById("error_hotel_city").innerHTML = "First letter of the city's name must be capital.";
				document.getElementById("error_hotel_city").style.display  = 'block';
				ispravno=false;
			}
			else
			{
				document.getElementById('error_hotel_city').innerHTML = "";
				document.getElementById("error_hotel_city").style.display  = 'none';
			}
			
			
			if(!(/[A-Z]/.test( street[0])))
			{
				document.getElementById("error_hotel_street").innerHTML = "First letter of the street's name must be capital.";
				document.getElementById("error_hotel_street").style.display  = 'block';
				ispravno=false;
			}
			else
			{
				document.getElementById('error_hotel_street').innerHTML = "";
				document.getElementById("error_hotel_street").style.display  = 'none';
			}
				
			
			if(!(/^[0-9.]+$/.test(latitude)))
			{
				document.getElementById("error_hotel_latitude").innerHTML = "Field Latitude must contains only numbers.";
				document.getElementById("error_hotel_latitude").style.display  = 'block';
				ispravno=false;
			}
			else
			{
				document.getElementById("error_hotel_latitude").innerHTML = "";
				document.getElementById("error_hotel_latitude").style.display  = 'none';
			}
			
			
			if(!(/^[0-9.]+$/.test(longitude)))
			{
				document.getElementById("error_hotel_longitude").innerHTML = "Field Latitude must contains only numbers.";
				document.getElementById("error_hotel_longitude").style.display  = 'block';
				ispravno=false;
			}
			else
			{
				document.getElementById("error_hotel_longitude").innerHTML = "";
				document.getElementById("error_hotel_longitude").style.display  = 'none';
			}
			
			if(ispravno == true)
			{	
				$.get({
					url: "/api/address/check/" + city +'/' + street + '/' + number + '/' + latitude + '/' + longitude,
					success: function(data) {
						if(data == null || data == "")
						{
							console.log('nije pronadjena adresa');
						}
						else
						{
							console.log('pronadjena adresa sa id: ' + data.id);
							finalEdit(data);
						}
						
					},
					
					error: function(data) {
						alert('Error with loading address');
					}
				
				});
				
			}
		}
	});
	

	$('#change_price_form').submit(function(event) {
		console.log('change_price_form submit');
		event.preventDefault();
		
		document.getElementById("error_price_service").style.display  = 'none';
		
		var attr = $('#button_hotel_back').attr('pritisnuto');
		var attr2 = $('#button_add_service').attr('pritisnuto');
		
		if (typeof attr !== typeof undefined && attr !== false)
		{
			$('#button_hotel_back').removeAttr('pritisnuto');
		}
		else if (typeof attr2 !== typeof undefined && attr2 !== false)
		{
			$('#button_add_service').removeAttr('pritisnuto');
			if($('#button_add_service').text() == 'Add service')
			{
				
				
				let service = $('input[name="new_service"]').val();
				let price_service = $('input[name="new_price_service"]').val();
				let discount = $('input[name=new_discount_service]').val();
				
				if(discount == "")
					discount = 0;
				
				let ispravno = true;
				
				if(!(/^[0-9]+$/.test(price_service)))
				{
					document.getElementById("error_new_price_service").style.display  = 'block';
					ispravno=false;
				}
				else
				{
					document.getElementById("error_new_price_service").style.display  = 'none';
				}
				
				if(!(/^[0-9]+$/.test(discount)))
				{
					document.getElementById("error_new_discount_service").style.display  = 'block';
					ispravno=false;
				}
				else
				{
					document.getElementById("error_new_discount_service").style.display  = 'none';
				}
				
				if(ispravno == true)
				{
					console.log('dodaj novu uslugu zahtev');
					
					$.post({
						url: '/api/usluge/dodaj_uslugu/' + service + '/'  + price_service + '/' + discount + '/' + id_presented,
						headers: {"Authorization": "Bearer " + token},
						contentType: 'application/json',
						success: function(data) {
							if(data==null || data==""){
								console.log('Nije dodata usluga');
							}
							else {
								
								console.log('Usluga dodata!');
								//window.location.href="hotelAdmin.html?id=" + id_presented;
								
								$('.new_additional_service').hide();
								$('#button_add_service').text('Add new service');
								
								$('input[name=new_price_service]').prop('required',false);
								$('input[name=new_service]').prop('required',false);
								
								$('input[name=new_price_service]').val('');
								$('input[name=new_service]').val('');
								$('input[name=new_discount_service]').val('');
								
					    		var option = document.createElement("option");
								option.text = data.naziv;
								option.value = data.id;
								$('select[name=select_services]').append(option);
								
								$('select[name=select_services]').val(data.id);
								editServices();
					    		
					    		if(data.popust != 0)
					    			var li = $('<li><span>' + data.naziv + ' - $' + data.cena + '/per day - discount: ' + data.popust +'%</span></li>');
					    		else
					    			var li = $('<li><span>' + data.naziv + ' - $' + data.cena + '/per day</span></li>');
					    		$('#usluge_hotela').append(li);
							}
						}
					});
				}
			}
		}
		else
		{
			console.log('promena cene');
		
			let price = $('input[name=price_service]').val();
			let id = $( "select[name=select_services] option:selected" ).val();
			let discount = $('input[name=discount_service]').val();
			
			if(discount == "")
				discount = 0;
			
			let ispravno = true;
			
			if(!(/^[0-9]+$/.test(price)))
			{
				document.getElementById("error_price_service").style.display  = 'block';
				ispravno=false;
			}
			else
			{
				document.getElementById("error_price_service").style.display  = 'none';
			}
			
			if(!(/^[0-9]+$/.test(discount)))
			{
				document.getElementById("error_discount_service").style.display  = 'block';
				ispravno=false;
			}
			else
			{
				document.getElementById("error_discount_service").style.display  = 'none';
			}
			
			if(ispravno == true)
			{
				$.post({
					url: '/api/usluge/admin/izmenaUsluge/' + id + '/'  + price,
					headers: {"Authorization": "Bearer " + token},
					contentType: 'application/json',
					success: function(data) {
						if(data==null || data==""){
							console.log('Nije izmenjena cena usluge!');
						}
						else {
							
							console.log('Cost of additional service successfully changed!');
							window.location.href="hotelAdmin.html?id=" + id_presented;
						}
					}
				});
				
				$.post({
					url: '/api/usluge/admin/izmenaPopustaUsluge/' + id + '/'  + discount,
					headers: {"Authorization": "Bearer " + token},
					contentType: 'application/json',
					success: function(data) {
						if(data==null || data==""){
							console.log('Nije izmenjen popust usluge!');
						}
						else {
							
							console.log('Discount of additional service successfully changed!');
							window.location.href="hotelAdmin.html?id=" + id_presented;
						}
					}
				});
				
			}
			
		}
	});
	


	$('#add_room_form').submit(function(event) {
		console.log('add_room_form submit');
		event.preventDefault();
		
		var attr = $('#button_hotel_back').attr('pritisnuto');
		
		if (typeof attr !== typeof undefined && attr !== false)
		{
			$('#button_hotel_back').removeAttr('pritisnuto');
		}
		
		let number_of_bads = $('input[name="number_of_bads"]').val();
		let price_night = $('input[name="price_night"]').val();
		let desc_room = $('textarea[name="room_description"]').val();
		
		let ispravno = true;
		
		if(!(/^[0-9]+$/.test(price_night)))
		{
			document.getElementById("error_price_night").style.display  = 'block';
			ispravno=false;
		}
		else
		{
			document.getElementById("error_price_night").style.display  = 'none';
		}
		
		if(ispravno == true)
		{
			if($('input[id=button_click_room]').val() == 'Add')
			{

				console.log('add room');
				
				$.post({
					url: '/api/hotels/add_room/' + id_presented,
					headers: {"Authorization": "Bearer " + token},
					data: JSON.stringify({cena_nocenja: price_night, broj_kreveta: number_of_bads, opis: desc_room}),
					contentType: 'application/json',
					success: function(data) {
						if(data==null || data==""){
							console.log('Nije dodata soba');
						}
						else {
							
							console.log('Soba uspesno dodata');
							window.location.href="hotelAdmin.html?id=" + id_presented;
						}
					}
				});
			}
			else
			{

				console.log('edit room');
				
				$.post({
					url: '/api/hotels/edit_room/' + soba_id,
					headers: {"Authorization": "Bearer " + token},
					data: JSON.stringify({cena_nocenja: price_night, broj_kreveta: number_of_bads, opis: desc_room}),
					contentType: 'application/json',
					success: function(data) {
						if(data==null || data==""){
							console.log('Nije izmenjena soba');
						}
						else {
							
							console.log('Soba uspesno izmenjena');
							window.location.href="hotelAdmin.html?id=" + id_presented;
						}
					}
				});
			}
		}
		
	});
	

	$('#special_price_form').submit(function(event) {
		console.log('special_price_form submit');
		event.preventDefault();
		

		$('#error_start_special_price').hide();
		
		let selected = $( "select[name=services_special_price]" ).val();
		let date_start = $('input[name="start_special_price"]').val();
		let date_end = $('input[name="end_special_price"]').val();
		let discount = $('input[name="discount_special_price"]').val();
		let id_soba = $('select[name="room_special_price"] option:selected').val();
		
		$.post({
			url: '/api/hotels/add_special_price/' + id_soba,
			headers: {"Authorization": "Bearer " + token},
			data: JSON.stringify({pocetak_vazenja: date_start, kraj_vazenja: date_end, popust: discount}),
			contentType: 'application/json',
			success: function(data) {
				if(data.result == 'success'){
					console.log('Popust uspesno dodat');
					window.location.href="hotelAdmin.html?id=" + id_presented;
					dodajUsluge(data.popust_id, selected);
				}
				else 
				{
					console.log('Popust nije uspesno dodat');
					$('#error_start_special_price').show();
				}
			}
		});
		
		
	});
});

function checkAdmin()
{
	$.ajax({
        type: 'GET',
        url: 'api/hotels/admin/' + id_presented + '/' + id_korisnik,
        headers: {"Authorization": "Bearer " + token},
        contentType: 'application/json',
        success: function (hotel)
		{
        	if(hotel != '' && hotel != null)
        	{
        		moj_hotel = hotel;
            	longitudeMap = hotel.adresa.longitude;
            	latitudeMap = hotel.adresa.latitude;
                console.log(hotel);
                showHotel(hotel);
                initMap();	
			}
        	else
        	{
        		console.log('greska prilikom ucitavanja stranice za admina hotela');
        		window.location.href = 'index.html';
        	}
		},
		error: function(data){
			console.log('greska prilikom ucitavanja stranice za admina hotela');
		}
    });	
}

function dodajUsluge(popust_id, usluge)
{
	size = usluge.length;
	var id_popust = '' + popust_id + '';
	
	$.post({
		url: '/api/hotels/add_usluga_special_price/' + usluge + '/' + size + '/' + id_popust,
		headers: {"Authorization": "Bearer " + token},
		contentType: 'application/json',
		success: function(data) {
			if(data.result == 'success'){
				console.log('Popust uspesno dodat');
				window.location.href="hotelAdmin.html?id=" + id_presented;
			}
			else 
			{
				console.log('Popust nije uspesno dodat');
				
			}
		}
	});
	
}

function finalEdit(address)
{
	$.post({
		url: "/api/hotels/editHotel/" + id_presented,
		headers: {"Authorization": "Bearer " + token},
		data: JSON.stringify({naziv: name, promotivni_opis: description, adresa: address}),
		contentType: 'application/json',
		success: function(data) {
			if(data.result == 'success')
			{
				console.log('uspesna promena podataka hotela');
				window.location.href = 'hotelAdmin.html?id=' + id_presented;
				editHotel();
			}
			else
			{
				console.log('neuspesna promena podataka hotela');
				window.location.href = 'hotelAdmin.html?id=' + id_presented;
			}
		}
	})
}

function showHotel(hotel)
{

    $("#title_hotel").text(hotel.naziv);
    $("#hotel_name").text(hotel.naziv);
    $('#hotel_info_text').text(hotel.promotivni_opis);
    $('#average_rating').text(hotel.prosecna_ocena);
    $('#adresa_hotel').text(hotel.adresa.ulica + ' ' + hotel.adresa.broj + ', ' + hotel.adresa.grad.naziv);
    
    if(hotel.sobe.length == 0)
	{
    	$("#text_no_rooms").text("There are no rooms in this hotel.");
    	
	}
    else
	{
    	$("#text_no_rooms").text("");
    	broj_soba = 1;
    
    	select_soba = $('select[name=room_special_price]');
    	select_soba.children().remove();
    	
		for (let soba of hotel.sobe) 
    	{
    		addSoba(soba);
    	}
		
		
    	
	}
    
    if(hotel.usluge.length == 0)
	{
    	$("#text_no_services").text("There are no additional services for this hotel.");
	}
    else
	{
    	$("#text_no_services").text("Additional services: ");
    	
    	var select_services = $('select[name=select_services]');
		select_services.children().remove();
		
		var selectsp = $('select[name=services_special_price]');
		selectsp.children().remove();
		
		var option = document.createElement("option");
    	option.text = '';
    	option.value = 0;
    	selectsp.append(option);
    	
    	for (let usluga of hotel.usluge) 
    	{
    		var option = document.createElement("option");
			option.text = usluga.naziv;
			option.value = usluga.id;
			select_services.append(option);
    	}
		
    	for (let usluga of hotel.usluge) 
    	{
    		if(usluga.popust != 0)
    			var li = $('<li><span>' + usluga.naziv + ' - $' + usluga.cena + '/per day - discount: ' + usluga.popust +'%</span></li>');
    		else
    			var li = $('<li><span>' + usluga.naziv + ' - $' + usluga.cena + '/per day</span></li>');
    		$('#usluge_hotela').append(li);
    		
    		var option = document.createElement("option");
			option.text = usluga.naziv;
			option.value = usluga.id;
			selectsp.append(option);
    	}
	}

}

function addSoba(soba)
{
	var temp, div, a;
	temp = document.getElementById("template_room");
	div = temp.content.querySelector("div#ubaci_sobu");
	
	var broj_kreveta_string;
	if(soba.broj_kreveta == 1)
	{
		broj_kreveta_string = "Jednokrevetna soba. ";
	}
	else if(soba.broj_kreveta == 2)
	{
		broj_kreveta_string = "Dvokrevetna soba. ";
	}
	else if(soba.broj_kreveta == 3)
	{
		broj_kreveta_string = "Trokrevetna soba. ";
	}
	else if(soba.broj_kreveta == 4)
	{
		broj_kreveta_string = "Četvorokrevetna soba. ";
	}
	else if(soba.broj_kreveta == 5)
	{
		broj_kreveta_string = "Petokrevetna soba. ";
	}
	
	var inner = 'Soba ' + broj_soba;
	temp.content.getElementById("naziv").innerHTML = inner;
	
	var option = document.createElement("option");
	option.text = inner;
	option.value = soba.id;
	select_soba.append(option);
	
	broj_soba = broj_soba + 1;
	temp.content.getElementById("broj_kreveta").innerHTML = broj_kreveta_string + '\r\n' + soba.opis;
	temp.content.getElementById("cena_nocenja").innerHTML = '$' + soba.cena_nocenja + '/night';
	temp.content.getElementById("prosecna_ocena").innerHTML = 'Rating: ' + soba.prosecna_ocena;
	
	if(soba.popusti.length == 0)
	{
		temp.content.getElementById("text_discount_list").style.display = 'none';
		temp.content.getElementById("discount_list").style.display = 'none';
	}
	
	list = temp.content.getElementById("discount_list");
	while (list.hasChildNodes()) 
	{   
		list.removeChild(list.firstChild);
	}
	
	for (let popust of soba.popusti) 
	{	
		temp.content.getElementById("text_discount_list").style.display = 'block';
		
		list.style.display = 'block';
		
		let li = document.createElement("li");
		var inner = '<span>' + popust.pocetak_vazenja.substring(0, 10) + '  -  ' + popust.kraj_vazenja.substring(0, 10) + '  -  ' + popust.popust + '%';
		
		var usluge_str = '';
		for(let usluga of popust.usluge)
		{
			usluge_str += usluga.naziv + ', ';
		}
		
		usluge_str = usluge_str.substring(0, usluge_str.length-2);
		
		if(popust.usluge.length >0)
			inner += ' (' + usluge_str + ')';
		
		inner += '</span>';
				
		//li.innerHTML =	'<span>' + popust.pocetak_vazenja.substring(0, 10) + '  -  ' + popust.kraj_vazenja.substring(0, 10) + '  -  ' + popust.popust + '%</span>';
		li.innerHTML = inner;
		temp.content.getElementById("discount_list").appendChild(li);
	}
	
	a = document.importNode(div, true);
    document.getElementById("ubaci_sobe_template").appendChild(a);
    
    let rezervisana = false;
    
    for(let rezervacija of soba.rezervacije)
    {
    	var today = new Date();
    	today.setHours(0, 0, 0);
    	
    	var end = rezervacija.datum_odlaska.substring(0, 19);
    	var date = new Date(end + "Z");
    	var pravi = date.toString();
    	var date_end = new Date(pravi);
    	
    	console.log(today);
    	console.log(date_end);
    	
    	if(today < date_end || (today.getFullYear() == date_end.getFullYear() && today.getMonth() == date_end.getMonth() && today.getDate() == date_end.getDate()))
    	{
    		rezervisana = true;
    		break;
    	}
    	
    	console.log(today === date_end);

    }
    
    if(rezervisana == false)
    {
		var elements = document.getElementsByClassName('button_edit_room');
		for(element of elements)
		{
			$(element).removeClass('button_edit_room');
			$(element).click(editRoom(soba.id));
		}
		
		$(".button_delete_room").addClass('soba_id_' + soba.id);
		var elements = document.getElementsByClassName('button_delete_room');
		for(element of elements)
		{
			$(element).removeClass('button_delete_room');
			$(element).click(deleteRoom(soba.id));
		}
    }
    else
    {
    	var elements = document.getElementsByClassName('button_edit_room');
		for(element of elements)
		{
			$(element).removeClass('button_edit_room');
			element.parentElement.style.display = 'none';
		}
		
		var elements = document.getElementsByClassName('button_delete_room');
		for(element of elements)
		{
			$(element).removeClass('button_delete_room');
			element.parentElement.style.display = 'none';
		}
    }
}

function editRoom(id)
{
	return function(){
		
		console.log('edit room with id: ' + id);
		
		$.ajax({
			url: 'api/hotels/get_soba/' + id,
			type: 'POST',
			success: function(soba) {
				
				console.log("uspesno nadjena soba sa id_sobe: " + soba.id);
				editingRoom(soba);
			}
		});
	};
	
}

function deleteRoom(id)
{
	return function(){
		
		console.log('delete room with id: ' + id);
		
		$.ajax({
			url: 'api/hotels/delete_room/' + id,
			headers: {"Authorization": "Bearer " + token},
			type: 'DELETE',
			success: function(soba) {
				
				console.log("uspesno obrisana soba sa id_sobe: " + soba.id);
				window.location.href="hotelAdmin.html?id=" + id_presented;
			},
			error: function(data){
				console.log("neuspesno brisanje sobe");
			}
		});
		
		
	};
	
}

function editingRoom(soba)
{
	soba_id = soba.id;
	
	console.log('editing room');
	
	$('#div_add_room').show();
	$('#div_edit_hotel').hide();
	$('#div_edit_services').hide();
	
	var el = document.getElementById('div_add_room');
    el.scrollIntoView(true);
    window.scrollBy(0, -330);
    
    $('input[name="number_of_bads"]').val(soba.broj_kreveta);
	$('input[name="price_night"]').val(soba.cena_nocenja);
	$('textarea[name="room_description"]').val(soba.opis);
	$('input[id=button_click_room]').val('Edit');
}

function editHotel()
{
	$('input[name=hotel_name').val(moj_hotel.naziv);
    $('input[name=hotel_city').val(moj_hotel.adresa.grad.naziv);
    $('input[name=hotel_street').val(moj_hotel.adresa.ulica);
    $('input[name=hotel_street_number').val(moj_hotel.adresa.broj);
    $('input[name=hotel_latitude').val(moj_hotel.adresa.latitude);
    $('input[name=hotel_longitude').val(moj_hotel.adresa.longitude);
    $('textarea[name=hotel_description').val(moj_hotel.promotivni_opis);
    
    $('#div_edit_services').hide();
	$('#div_add_room').hide();
	$('#div_report').hide();
	$('#div_special_price').hide();
	
	$('#div_edit_hotel').show();
	var el = document.getElementById('div_edit_hotel');
    el.scrollIntoView(true);
    window.scrollBy(0, -100);
}

function editServices()
{
	console.log('editing services');
	$('#div_edit_hotel').hide();
	$('#div_add_room').hide();
	$('#div_report').hide();
	$('#div_special_price').hide();
	
	$('input[name=price_service]').prop('required',true);
	
	$('#div_edit_services').show();
	var el = document.getElementById('div_edit_services');
    el.scrollIntoView(true);
    window.scrollBy(0, -350);
    changeSelection();
}

function addRoom()
{
	console.log('adding room');
	
	$('#div_add_room').show();
	$('#div_edit_hotel').hide();
	$('#div_edit_services').hide();
	$('#div_report').hide();
	$('#div_special_price').hide();
	
	var el = document.getElementById('div_add_room');
    el.scrollIntoView(true);
    window.scrollBy(0, -330);
    
    $('input[name="number_of_bads"]').val('');
	$('input[name="price_night"]').val('');
	$('textarea[name="room_description"]').val('');
	$('input[id=button_click_room]').val('Add');
}

function addSpecialPrice()
{
	console.log('add special price');
	
	$('#div_special_price').show();

	$('#div_edit_hotel').hide();
	$('#div_edit_services').hide();
	$('#div_add_room').hide();
	$('#div_report').hide();
	
	var el = document.getElementById('div_special_price');
    el.scrollIntoView(true);
    window.scrollBy(0, -150);
}

function backEdit()
{
	$('#div_edit_hotel').hide();
	$('#div_edit_services').hide();
	$('.new_additional_service').hide();
	$('#div_add_room').hide();
	$('#div_report').hide();
	$('#div_special_price').hide();
	
	$('#button_hotel_back').attr('pritisnuto', 'true');
	
	var el = document.getElementById('div_changing_buttons');
    el.scrollIntoView(true);
    window.scrollBy(0, -500);
}

function changeSelection()
{
	let id = $( "select[name=select_services] option:selected" ).val();
	
	if(id != undefined)
	{
		$.get({
			url: "/api/usluge/get/" + id,
			success: function(data) {
				$('input[name=price_service]').val(data.cena);
				$('input[name=discount_service]').val(data.popust);
			},
			
			error: function(data) {
				console.log('Error with loading service');
			}
		
		});
	}
	else
	{

		$('input[name=price_service]').prop('required',false);
	}
}

function addService()
{
	$('#button_add_service').attr('pritisnuto', 'true');
	$('.new_additional_service').show();
	$('#button_add_service').text('Add service');
	
	$('input[name=new_price_service]').prop('required',true);
	$('input[name=new_service]').prop('required',true);
	
}

function showReport()
{
	console.log('show report');
	
	$('#div_report').show();

	$('#div_edit_hotel').hide();
	$('#div_edit_services').hide();
	$('.new_additional_service').hide();
	$('#div_add_room').hide();
	$('#div_special_price').hide();
	
	var el = document.getElementById('div_report');
    el.scrollIntoView(true);
    window.scrollBy(0, -100);
    
    if(provera == false)
    {
        document.getElementById("bar-chart-day").height=70;
        document.getElementById("bar-chart-week").height=70;
        document.getElementById("bar-chart-month").height=70;
    }
    
    provera = true;
	
	$.ajax({
        type: 'GET',
        url: 'api/hotels/total_earnings/' + id_presented,
        headers: {"Authorization": "Bearer " + token},
        contentType: 'application/json',
        success: function (data)
		{
        	console.log('day: ' + data.day);
        	console.log('week: ' + data.week);
        	console.log('month: ' + data.month);
        	
        	$("#totalDay").text("Total earnings today: $" + data.day);
        	$("#totalWeek").text("Total earnings in last week: $" + data.week);
        	$("#totalMonth").text("Total earnings in last month: $" + data.month);
        	
        	
        	
		}
    });	
	
	$.ajax({
        type: 'GET',
        url: 'api/hotels/daily_report/' + id_presented,
        headers: {"Authorization": "Bearer " + token},
        contentType: 'application/json',
        success: function (result)
		{
        	
        	let day = new Chart(document.getElementById("bar-chart-day"), {
	        	type: 'pie',
	        	data: {
	        	labels:['Reserved', 'Unreserved'],
	        	datasets: [{
	        	        	
	        	        	data : [result.ret, 100-result.ret],
	        	        	backgroundColor: ['#ffce56', '#cc65fe'] ,
	        	        	
	        	           }]
	        	},
	        	options: {
	                
	        		title : {
	        			display : true,
	        			text : "Today's reservations in percentages",
	        			fontSize : 25
	        			
	        		}
        	}
        	});
		}
    });
	
	var today = new Date();
	var months = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"];
	
	var day1 = new Date(today);
	day1.setDate(today.getDate() - 1);
	var day1_str = months[day1.getMonth()] + ' ' + day1.getDate();
	
	var day2 = new Date(today);
	day2.setDate(today.getDate() - 2);
	var day2_str = months[day2.getMonth()] + ' ' + day2.getDate();
	
	var day3 = new Date(today);
	day3.setDate(today.getDate() - 3);
	var day3_str = months[day3.getMonth()] + ' ' + day3.getDate();
	
	
	var day4 = new Date(today);
	day4.setDate(today.getDate() - 4);
	var day4_str = months[day4.getMonth()] + ' ' + day4.getDate();
	
	
	var day5 = new Date(today);
	day5.setDate(today.getDate() - 5);
	var day5_str = months[day5.getMonth()] + ' ' + day5.getDate();
	
	
	var day6 = new Date(today);
	day6.setDate(today.getDate() - 6);
	var day6_str = months[day6.getMonth()] + ' ' + day6.getDate();
	
	
	var day7 = new Date(today);
	day7.setDate(today.getDate() - 7);
	var day7_str = months[day7.getMonth()] + ' ' + day7.getDate();

	
	$.ajax({
        type: 'GET',
        url: 'api/hotels/weekly_report/' + id_presented,
        headers: {"Authorization": "Bearer " + token},
        contentType: 'application/json',
        success: function (result)
		{
        	
        	let week = new Chart(document.getElementById("bar-chart-week"), {
        	type: 'bar',
        	data: {
        	labels:[day7_str, day6_str, day5_str, day4_str, day3_str, day2_str, day1_str],
        	datasets: [{
        	        	label : 'Reservations' ,
        	        	data : [result.day7, result.day6, result.day5, result.day4, result.day3, result.day2, result.day1],
        	        	backgroundColor: '#ffce56',
        	        	borderWidth : 4,
        	        	borderColor : 'black'
        	           }]
        	},
        	options: {
        		 scales: {
                    
                     yAxes: [{
                             display: true,
                             ticks: {
                                 beginAtZero: true,
                             }
                         }]
                 },
        		title : {
        			display : true,
        			text : 'Reservations in last 7 days',
        			fontSize : 25
        			
        		}
        	}
        	});
        	
        
		}
    });
	
	$.ajax({
        type: 'GET',
        url: 'api/hotels/monthly_report/' + id_presented,
        headers: {"Authorization": "Bearer " + token},
        contentType: 'application/json',
        success: function (result)
		{
        	
        	let month = new Chart(document.getElementById("bar-chart-month"), {
        	type: 'bar',
        	data: {
        	labels:['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
        	datasets: [{
        	        	label : 'Reservations' ,
        	        	data : [result.jan, result.feb, result.mar, result.apr, result.may, result.jun, result.jul, result.aug, result.sep, result.oct, result.nov, result.dec],
        	        	backgroundColor: '#cc65fe',
        	        	borderWidth : 4,
        	        	borderColor : 'black'
        	           }]
        	},
        	options: {
        		 scales: {
                    
                     yAxes: [{
                             display: true,
                             ticks: {
                                 beginAtZero: true,
                             }
                         }]
                 },
        		title : {
        			display : true,
        			text : 'Reservations in this year',
        			fontSize : 25
        			
        		}
        	}
        	});
        	
        
		}
    });
}

function initMap()
{
	var map = new ol.Map({
        target: 'map',
        layers: [
          new ol.layer.Tile({
            source: new ol.source.OSM()
          })
        ],
        view: new ol.View({
          center: ol.proj.fromLonLat([longitudeMap, latitudeMap]),
          zoom: 17
        })
      });
}