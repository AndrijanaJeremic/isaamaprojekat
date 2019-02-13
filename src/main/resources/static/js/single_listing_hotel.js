var id_rez = null;

$(document).ready(function()	
{
	var search = window.location.search;
	var splitted = search.split('&');
	
    id_presented = splitted[0].substring(4);
    check_in = splitted[1].substring(9);
    check_out = splitted[2].substring(10);
    adults = splitted[3].substring(7);
    id_rez = splitted[4].substring(7);
    
    console.log('[single_listing_hotel: document.ready()]: id hotela: ' + id_presented);

    $.ajax({
        type: 'GET',
        url: 'api/hotels/' + id_presented + '/' + check_in + '/' + check_out + '/' + adults,
        success: function (hotel)
		{
        	longitude = hotel.adresa.longitude;
        	latitude = hotel.adresa.latitude;
            console.log(hotel);
            showHotel(hotel);
            initMap();
		}
    });
    
	$.get({
		url: '/api/hotels/rezervacija/' + check_in + '/' + check_out,
		success: function(rezervacija) {
			rezervacijaHotela = rezervacija;
			console.log('rezervacija uspesno vracena');

		},
		error : function(data){
			alert('Greska prilikom rezervacije.');
		}
	});
	
});

function showHotel(hotel)
{

    $("#title_hotel").text(hotel.naziv);
    $("#hotel_name").text(hotel.naziv);
    $('#hotel_info_text').text(hotel.promotivni_opis);
    $('#average_rating_hotel').text(hotel.prosecna_ocena);
    $('#adresa_hotel').text(hotel.adresa.ulica + ' ' + hotel.adresa.broj + ', ' + hotel.adresa.grad.naziv);
    
    if(hotel.sobe.length == 0)
	{
    	$("#text_no_rooms").text("There are no rooms in this hotel.");
    	
    	$.ajax({
    		url: 'api/hotels/get_number_of_days/' + check_in + '/' + check_out,
    		type: 'POST',
    		success: function(number) {
    			console.log("number of days: " + number);
    			number_of_days = number;
    			
    			getSpecialPrice();
    		}
    	});
	}
    else
	{
    	$("#text_no_rooms").text("");
    	broj_soba = 1;
    	
    	$.ajax({
    		url: 'api/hotels/get_number_of_days/' + check_in + '/' + check_out,
    		type: 'POST',
    		success: function(number) {
    			console.log("number of days: " + number);
    			number_of_days = number;
    			
    			for (let soba of hotel.sobe) 
    	    	{
    	    		addSoba(soba);
    	    	}
    			
    			getSpecialPrice();
    		}
    	});
    	
    	
	}
    
    if(hotel.usluge.length == 0)
	{
    	$("#text_no_services").text("There are no additional services for this hotel.");
	}
    else
	{
    	$("#text_no_services").text("Additional services: ");
    	for (let usluga of hotel.usluge) 
    	{
    		if(usluga.popust != 0)
    			var li = $('<li><span>' + usluga.naziv + ' - $' + usluga.cena + '/per day - discount: ' + usluga.popust +'%</span></li>');
    		else
    			var li = $('<li><span>' + usluga.naziv + ' - $' + usluga.cena + '/per day</span></li>');
    		$('#usluge_hotela').append(li);
    		
    		if(id_rez != 0)
        	{
    			let buttonAdd;
    			//var token = localStorage.getItem('jwtToken');
        		//if(token!=null)
        		//{
        			buttonAdd = $('<span class="button_add book_button button trans_200" style="cursor: pointer; width: 70px; height: 30px; margin-left: 10px; text-align: center; color: white; display: inline-table; vertical-align: middle" id="add'+usluga.id+'"><a style="padding-left: 0px; padding-right: 0px;">Add<a/></span>');
        			buttonAdd.click(clickAddUsluga(usluga));
        			li.append(buttonAdd);
        		//}
        		//else
        			//buttonAdd = $('<span hidden="true" class="button_add book_button button trans_200" style="cursor: pointer; width: 70px; height: 30px; margin-left: 10px; text-align: center; color: white; display: inline-table; vertical-align: middle" id="add'+usluga.id+'"><a style="padding-left: 0px; padding-right: 0px;">Add<a/></span>');
    			
    		}
    		
    		
    		$('#usluge_hotela').append(li);
    	}
	}

}

function clickAddUsluga(usluga){
	return function(){
		var token=localStorage.getItem('jwtToken');
		if($(this).hasClass('dodata_usluga'))
		{
			$(this).removeClass('dodata_usluga');
			var button = document.getElementById('add' + usluga.id);
			button.childNodes[0].innerHTML = 'Add';
			
			$.ajax({
				url: 'api/hotels/remove_usluga/' + usluga.id,
				type: 'POST',
				data:  JSON.stringify(rezervacijaHotela),
				headers: {"Authorization": "Bearer " + token},
				contentType: 'application/json',
				success: function(rezervacija) {
					rezervacijaHotela = rezervacija;
				}
			});
		}
		else
		{
			var id = "#add" + usluga.id;
			$(id).addClass("dodata_usluga");
			var button = document.getElementById('add' + usluga.id);
			button.childNodes[0].innerHTML = 'Remove';
			
			$.ajax({
				url: 'api/hotels/add_usluga/' + usluga.id,
				type: 'POST',
				data: JSON.stringify(rezervacijaHotela),
				headers: {"Authorization": "Bearer " + token},
				contentType: 'application/json',
				success: function(rezervacija) {
					rezervacijaHotela = rezervacija;
				}
			});
		}
		
	};
}

function addSoba(soba)
{
	var temp, div, a;
	temp = document.getElementById("template_room");
	div = temp.content.querySelector("div#ubaci_sobu");
	var token = localStorage.getItem('jwtToken');
	if(token!=null)
		{
		temp.content.getElementById("button_book_room").removeAttribute("hidden");
		}
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
	
	temp.content.getElementById("naziv").innerHTML = 'Soba ' + broj_soba;
	broj_soba = broj_soba + 1;
	temp.content.getElementById("broj_kreveta").innerHTML = broj_kreveta_string + '\r\n' + soba.opis;
	
	if(number_of_days != 0)
	{
		cena_rez = number_of_days * soba.cena_nocenja;
		temp.content.getElementById("cena_nocenja").innerHTML = '$' + soba.cena_nocenja + '/night\r\nTotal: $' + cena_rez;
	}
	else
	{
		temp.content.getElementById("cena_nocenja").innerHTML = '$' + soba.cena_nocenja + '/night';
	}
	
	temp.content.getElementById("prosecna_ocena").innerHTML = 'Rating: ' + soba.prosecna_ocena;
	
	a = document.importNode(div, true);
    document.getElementById("ubaci_sobe_template").appendChild(a);
    
    if(number_of_days != 0)
	{
    	if(id_rez != 0)
    	{
    		$(".button_book_room").addClass('soba_id_' + soba.id);
    		var elements = document.getElementsByClassName('button_book_room');
    		for(element of elements)
    		{
    			$(element).removeClass('button_book_room');
    			$(element).click(bookRoom(soba.id));
    		}
    	}
    	else
    	{
    		var elements = document.getElementsByClassName('button_book_room');
    		for(element of elements)
    		{
    			element.parentElement.style.display = 'none';
    		}
    	}
	}
    else
    {
    	var elements = document.getElementsByClassName('button_book_room');
		for(element of elements)
		{
			element.parentElement.style.display = 'none';
		}
    }
}

function bookRoom(id)
{
	
	return function(){
		var token=localStorage.getItem('jwtToken');
		$.ajax({
			url: 'api/hotels/book_room/' + id + '/' + id_rez,
			headers: {"Authorization": "Bearer " + token},
			contentType: 'application/json',
			type: 'PUT',
			data: JSON.stringify(rezervacijaHotela),
			contentType: 'application/json; charset=utf-8',
			success: function(soba) {
				console.log("uspesna rezervacija sobe sa id_sobe: " + soba.id);
				alert('uspesna rezervacija sobe sa id_sobe: ' + soba.id);
				window.location.href = 'rezervacijaPreview.html?id=' + id_rez;
			}
		});
	};
	
}
/*
function getSpecialPrice(){
    $.ajax({
        type: 'GET',
        url: 'api/hotels/specialPrice/' + id_presented + '/' + check_in + '/' + check_out,
        success: function (rezervacije)
		{
            if(rezervacije.length != 0)
            {
            	document.getElementById("special_price").style.display = 'block';
            	
            	broj_sobasp = 1;
            	for(let rezervacija of rezervacije)
            	{
            		addSpecialPrice(rezervacija);
            	}
            }
		}
    });
}

function addSpecialPrice(rezervacija)
{
	var temp, div, a;
	temp = document.getElementById("template_special_price");
	div = temp.content.querySelector("div#ubaci_special_price");
	var token = localStorage.getItem('jwtToken');
	if(token!=null)
		{
		temp.content.getElementById("button_book_roomsp").removeAttribute("hidden");
		}
	var soba = rezervacija.soba;

	
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
	
	temp.content.getElementById("nazivsp").innerHTML = 'Soba ' + broj_sobasp;
	broj_sobasp = broj_sobasp + 1;
	temp.content.getElementById("broj_krevetasp").innerHTML = broj_kreveta_string + '\r\n' + soba.opis;
	
	var popust = soba.cena_nocenja * 0.01 * (100 - rezervacija.popust);
	
	temp.content.getElementById("cena_nocenjasp").innerHTML = 'Regular price: $' + soba.cena_nocenja + '/night\r\nPrice with discount: $' + popust + '/night';
	
	
	temp.content.getElementById("prosecna_ocenasp").innerHTML = soba.prosecna_ocena;
	
	/*
	for (let popust of soba.popusti) 
	{	
		let li = document.createElement("li");
		li.innerHTML =	'<span>' + popust.pocetak_vazenja.substring(0, 10) + '  -  ' + popust.kraj_vazenja.substring(0, 10) + '  -  ' + popust.popust + '%</span>';
		temp.content.getElementById("discount_list").appendChild(li);
	}
	
	
	//temp.content.getElementById("text_services_included_list").innerHTML = 'Additional services included:';
	
	$.ajax({
        type: 'GET',
        url: 'api/hotels/popust/' + soba.id + '/' + check_in + '/' + check_out,
        success: function (popust)
		{
        	if(popust == null || popust.usluge.length == 0)
        	{
        		var element = document.getElementById("text_services_included_list");
        		element.innerHTML = 'There are no additional services included.';
        	}
        	else
            {
            	for (let usluga of popust.usluge) 
        		{	
            		document.getElementById("text_services_included_list").innerHTML = 'Additional services included:';
            			
        			let li = document.createElement("li");
        			li.innerHTML =	'<span>' + usluga.naziv + ' - $' + usluga.cena + '/per day</span>';
        			document.getElementById("services_included_list").appendChild(li);
        		}
            }
		}
    });
	
	
	for (let usluga of rezervacija.usluge) 
	{	
		temp.content.getElementById("text_services_included_list").innerHTML = 'Additional services included:';
			
		let li = document.createElement("li");
		li.innerHTML =	'<span>' + usluga.naziv + ' - $' + usluga.cena + '/per day</span>';
		temp.content.getElementById("services_included_list").appendChild(li);
	}
	
	a = document.importNode(div, true);
    document.getElementById("special_price_div").appendChild(a);
    

	$(".button_book_roomsp").addClass('soba_id_' + soba.id);
	var elements = document.getElementsByClassName('button_book_roomsp');
	for(element of elements)
	{
		$(element).removeClass('button_book_roomsp');
		$(element).click(bookRoomSpecial(rezervacija.id));
	}
	
}*/

function getSpecialPrice(){
    $.ajax({
        type: 'GET',
        url: 'api/hotels/specialPrice/' + id_presented + '/' + check_in + '/' + check_out + '/' + adults,
        success: function (sobe)
		{
            if(sobe.length != 0)
            {
            	document.getElementById("special_price").style.display = 'block';
            	
            	broj_sobasp = 1;
            	for(let soba of sobe)
            	{
            		addSpecialPrice(soba);
            	}
            }
		}
    });
}

function addSpecialPrice(soba)
{
	var temp, div, a;
	temp = document.getElementById("template_special_price");
	div = temp.content.querySelector("div#ubaci_special_price");
	var token = localStorage.getItem('jwtToken');
	if(token!=null)
	{
		temp.content.getElementById("button_book_roomsp").removeAttribute("hidden");
	}
	
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
	
	temp.content.getElementById("nazivsp").innerHTML = 'Soba ' + broj_sobasp;
	broj_sobasp = broj_sobasp + 1;
	temp.content.getElementById("broj_krevetasp").innerHTML = broj_kreveta_string + '\r\n' + soba.opis;
	
	temp.content.getElementById("prosecna_ocenasp").innerHTML = 'Rating: ' + soba.prosecna_ocena;
	
	/*
	for (let popust of soba.popusti) 
	{	
		let li = document.createElement("li");
		li.innerHTML =	'<span>' + popust.pocetak_vazenja.substring(0, 10) + '  -  ' + popust.kraj_vazenja.substring(0, 10) + '  -  ' + popust.popust + '%</span>';
		temp.content.getElementById("discount_list").appendChild(li);
	}
	*/
	
	temp.content.getElementById("text_services_included_list").innerHTML = 'Additional services included:';
	var pronadjen_popust;
	
	$.ajax({
        type: 'GET',
        url: 'api/hotels/popust/' + soba.id + '/' + check_in + '/' + check_out,
        success: function (popust)
		{
        	pronadjen_popust = popust;
        	
        	if(popust.usluge.length == 0)
        	{
        		var element = document.getElementById("text_services_included_list");
        		element.innerHTML = 'There are no additional services included.';
        	}
        	else
            {
            	for (let usluga of popust.usluge) 
        		{	
            		document.getElementById("text_services_included_list").innerHTML = 'Additional services included:';
            			
        			let li = document.createElement("li");
        			li.innerHTML =	'<span>' + usluga.naziv + ' - $' + usluga.cena + '/per day</span>';
        			document.getElementById("services_included_list").appendChild(li);
        		}
         
            }
        	var popust_cena = soba.cena_nocenja * 0.01 * (100 - popust.popust);
        	document.getElementById("cena_nocenjasp").innerHTML = 'Regular price: $' + soba.cena_nocenja + '/night\r\nPrice with discount: $' + popust_cena + '/night';
        	
        	if(id_rez!=0)
    		{
		    	$(".button_book_roomsp").addClass('soba_id_' + soba.id);
		    	var elements = document.getElementsByClassName('button_book_roomsp');
		    	for(element of elements)
		    	{
		    		$(element).removeClass('button_book_roomsp');
		    		$(element).click(bookRoomSpecial(check_in, check_out, pronadjen_popust, soba.id));
		    	}
    		}
        	else
        	{
        		var elements = document.getElementsByClassName('button_book_roomsp');
        		for(element of elements)
        		{
        			element.parentElement.style.display = 'none';
        		}
        	}
		}
    });
	
	a = document.importNode(div, true);
    document.getElementById("special_price_div").appendChild(a);
	
}

function bookRoomSpecial(check_in, check_out, pronadjen_popust, soba_id)
{
	return function(){
		var token=localStorage.getItem('jwtToken');
		console.log('book room special, id sobe: ' + soba_id + '; popust id: ' + pronadjen_popust.id);
		$.ajax({
			url: 'api/hotels/book_room_special/' + soba_id + '/' + check_in + '/' + check_out + '/' + id_rez,
			type: 'PUT',
			data: JSON.stringify(pronadjen_popust),
			headers: {"Authorization": "Bearer " + token},
			contentType: 'application/json',
			success: function(soba) {
				console.log("uspesna rezervacija brze sobe sa id_sobe: " + soba.id);
				alert('uspesna rezervacija brze sobe sa id_sobe: ' + soba.id);
				window.location.href = 'rezervacijaPreview.html?id=' + id_rez;
			}
		});
	};
	
}

function clickSpecialPrice()
{
	if(document.getElementById('special_price_div').style.display == 'none')
	{
		document.getElementById('special_price_div').style.display = 'block';
		document.getElementById('ubaci_sobe_template').style.display = 'none';
		document.getElementById('additional_services').style.display = 'none';
		document.getElementById('special_price_a').innerHTML = 'Special prices:';
		document.getElementById('button_back_roomsp').style.display = 'block';
		$("#text_no_rooms").hide();
	}
	else
	{
		document.getElementById('special_price_div').style.display = 'none';
		document.getElementById('ubaci_sobe_template').style.display = 'block';
		document.getElementById('additional_services').style.display = 'block';
		document.getElementById('special_price_a').innerHTML = 'Special prices (click to see)';
		document.getElementById('button_back_roomsp').style.display = 'none';
		$("#text_no_rooms").show();
	}
}

function hideSpecialPrice()
{
	document.getElementById('special_price_div').style.display = 'none';
	document.getElementById('ubaci_sobe_template').style.display = 'block';
	document.getElementById('additional_services').style.display = 'block';
	document.getElementById('special_price_a').innerHTML = 'Special prices (click to see)';
	document.getElementById('button_back_roomsp').style.display = 'none';
	$("#text_no_rooms").show();
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
          center: ol.proj.fromLonLat([longitude, latitude]),
          zoom: 17
        })
      });
}

function odjava()
{
	localStorage.clear();
}
