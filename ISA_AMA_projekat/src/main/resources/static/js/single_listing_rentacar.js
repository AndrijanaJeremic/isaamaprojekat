var id_rez=null;
var token = null;
$(document).ready(function()	
{
	token=localStorage.getItem('jwtToken');
	
	var search = window.location.search;
	var splitted = search.split('&');
	
    id_presented = splitted[0].substring(4);
    check_in = splitted[1].substring(9);
    check_in_town = splitted[2].substring(14);
    check_in_town=check_in_town.split('+').join(' ');
    check_out = splitted[3].substring(10);
    check_out_town = splitted[4].substring(15);
    check_out_town=check_out_town.split('+').join(' ');
    passengers = splitted[5].substring(11);
    id_rez=splitted[6].substring(7);
    console.log('[single_listing_rent: document.ready()]: id hotela: ' + id_presented);

	$.ajax({
		url: 'api/rents/get_number_of_days/' + check_in + '/' + check_out,
		type: 'POST',
		success: function(number) {
			console.log("number of days: " + number);
			number_of_days = number;
			
		}
	});
	
	getSpecialPrice();
    $.ajax({
        type: 'GET',
        url: 'api/rents/' + id_presented + '/' + check_in + '/' + check_in_town + '/' + check_out + '/' + check_out_town + '/' + passengers,
        success: function (rentacar)
		{
            longitude = rentacar.adresa.longitude;
        	latitude = rentacar.adresa.latitude;
            console.log(rentacar);
            showRentacar(rentacar);
            initMap();
		}
    });
    
    if(check_in_town!="prazan" && check_out_town!="prazan")
    	{
    $.get({
		url: '/api/rents/rezervacija/' + check_in + '/' + check_in_town + '/' + check_out + '/' + check_out_town + '/' + passengers,
		success: function(rezervacija) {
			rezervacijaVozila = rezervacija;
			console.log('rezervacija uspesno vracena');

		},
		error : function(data){
			alert('Greska prilikom rezervacije.');
		}
	});
    	}
	
});

function showRentacar(rentacar)
{

    $("#title_rentacar").text(rentacar.naziv);
    $("#rentacar_name").text(rentacar.naziv);
    $('#rentacar_info_text').text(rentacar.promotivni_opis);
    $('#adresa_rentacar').text(rentacar.adresa.ulica + ' ' + rentacar.adresa.broj + ', ' + rentacar.adresa.grad.naziv);
    
    if(rentacar.filijale == 0)
	{
    	$("#text_no_filijale").text("There are no branches in this rentacar service.");
	}
    else
	{
    	$("#text_no_filijale").text("Branches:");
    	for (let filijala of rentacar.filijale) 
    	{
    		addFilijala(filijala);
    	}
    	
    	
	}
    
    if(rentacar.usluge.length == 0)
	{
    	$("#text_no_services").text("There are no additional services.");
	}
    else
	{
    	$("#text_no_services").text("Additional services: ");
    	for (let usluga of rentacar.usluge) 
    	{
    		let li = $('<li>' + usluga.naziv + ' - $' + usluga.cena + '/per day</li>');
    		$('#usluge_rentacar').append(li);
    	}
	}

}

function getSpecialPrice(){
    $.ajax({
        type: 'GET',
        url: 'api/rents/specialPrice/' + id_presented + '/' + check_in + '/' + check_in_town + '/' + check_out + '/' + check_out_town + '/' + passengers,
        success: function (vozila)
		{
            if(vozila.length != 0)
            {
            	document.getElementById("special_price").style.display = 'block';
            	
            	
            	for(let vozilo of vozila)
            	{
            		addSpecialPrice(vozilo);
            	}
            }
		}
    });
}

function addSpecialPrice(vozilo)
{
	var temp, div, a;
	temp = document.getElementById("template_special_price");
	div = temp.content.querySelector("div#ubaci_special_price");
	var token = localStorage.getItem('jwtToken');
	if(token!=null)
	{
		temp.content.getElementById("button_book_carsp").removeAttribute("hidden");
	}
	
	var broj_sedista_string = "Number of seats: " + vozilo.broj_sedista;
	var god_proizvodnje_string = "The year of production: " + vozilo.godina_proizvodnje;
	var naziv_vozila= vozilo.marka + " " + vozilo.model + " " + vozilo.naziv + " " + vozilo.tip;
	
	
	temp.content.getElementById("naziv_autasp").innerHTML = naziv_vozila;
	temp.content.getElementById("godina_proizvodnjesp").innerHTML = god_proizvodnje_string;
	
	temp.content.getElementById("broj_sedistasp").innerHTML = broj_sedista_string;
	
	temp.content.getElementById("prosecna_ocenasp").innerHTML = vozilo.prosecna_ocena;
	
	
	temp.content.getElementById("text_services_included_list").innerHTML = 'Additional services included:';
	var pronadjen_popust;
	
	$.ajax({
        type: 'GET',
        url: 'api/rents/popust/' + vozilo.id + '/' + check_in + '/' + check_out,
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
        	var popust_cena = vozilo.cena_dan * 0.01 * (100 - popust.popust);
        	document.getElementById("cena_autasp").innerHTML = 'Regular price: $' + vozilo.cena_dan + '/day\r\nPrice with discount: $' + popust_cena + '/day';
        	if(id_rez!=0)
        		{
        	$(".button_book_carsp").addClass('vozilo_id_' + vozilo.id);
        	var elements = document.getElementsByClassName('button_book_carsp');
			        	for(element of elements)
			        	{
			        		$(element).removeClass('button_book_carsp');
			        		$(element).click(bookCarSpecial(check_in, check_out, pronadjen_popust, vozilo.id));
			        	}
        		}
        	else
        	{
        			var elements = document.getElementsByClassName('button_book_carsp');
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

function bookCarSpecial(check_in, check_out, pronadjen_popust, vozilo_id)
{
	return function(){
		
		console.log('book car special, id vozila: ' + vozilo_id + '; popust id: ' + pronadjen_popust.id);
		$.ajax({
			url: 'api/rents/book_car_special/' + vozilo_id + '/' + check_in + '/' + check_in_town + '/' + check_out + '/' + check_out_town + '/' + passengers + '/' + id_rez,
			type: 'PUT',
			data: JSON.stringify(pronadjen_popust),
			headers: {"Authorization": "Bearer " + token},
	        contentType: 'application/json',
			success: function(vozilo) {
				console.log("uspesna brza rezervacija vozila sa id: " + vozilo.id);
				alert('uspesna brza rezervacija vozila sa id: ' + vozilo.id + " dodata u rez: " + id_rez);
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
		//document.getElementById('ubaci_auto_template').style.display = 'none';
		//document.getElementById('additional_services').style.display = 'none';
		document.getElementById('special_price_a').innerHTML = 'Special prices:';
		//document.getElementById('button_back_carsp').style.display = 'block';
	}
	else
	{
		document.getElementById('special_price_div').style.display = 'none';
		//document.getElementById('ubaci_auto_template').style.display = 'block';
		//document.getElementById('additional_services').style.display = 'block';
		document.getElementById('special_price_a').innerHTML = 'Special prices (click to see)';
		//document.getElementById('button_back_carsp').style.display = 'none';
	}
}

function hideSpecialPrice()
{
	document.getElementById('special_price_div').style.display = 'none';
	//document.getElementById('ubaci_auto_template').style.display = 'block';
	//document.getElementById('additional_services').style.display = 'block';
	document.getElementById('special_price_a').innerHTML = 'Special prices (click to see)';
	//document.getElementById('button_back_carsp').style.display = 'none';
}


function addFilijala(filijala)
{
	var temp, div, a;
	temp = document.getElementById("template_filijala");
	div = temp.content.querySelector("div#ubaci_filijalu");
	
	
	temp.content.getElementById("adresa_filijale").innerHTML =  filijala.adresa.ulica + ' ' + filijala.adresa.broj + ', ' + filijala.adresa.grad.naziv; ;
	
	
    if(filijala.vozila.length == 0)
	{
    	//$("#text_no_cars").text("There are no cars in this branch of rentacar service.");
    	 temp.content.getElementById("vozila_u_ponudi").innerHTML = "";
    	 
	}
    else
	{
    	temp.content.getElementById("vozila_u_ponudi").innerHTML = '<a id="vozila_dugme'+filijala.id + '" style="cursor:pointer; color:white;" onclick="javascript:izlistajVozila(' + filijala.id + ')">Cars on offer</a>';
    	
	}
    
   
    a = document.importNode(div, true);
    document.getElementById("ubaci_filijale_template").appendChild(a);
}

function izlistajVozila(filijala_id)
{
	$("#vozila_dugme" + filijala_id).hide();
	$.get({
		url: '/api/filijale/' + filijala_id,
		success: function(filijala) {
			console.log('filijala_uspesno_vracena ' + filijala.id);
			for(let vozilo of filijala.vozila)
				{
				addVozilo(vozilo);
				}
			
		},
		error : function(data){
			alert('Greska prilikom vracanja filijale.');
		}
	});




}
function addVozilo(car)
{
	var temp, div, a;
	temp = document.getElementById("template_auto");
	div = temp.content.querySelector("div#ubaci_auto");
	var token = localStorage.getItem('jwtToken');
	if(token!=null)
		temp.content.getElementById("dugme_book").removeAttribute("hidden");
	var broj_sedista_string = "Number of seats: " + car.broj_sedista;
	var god_proizvodnje_string = "The year of production: " + car.godina_proizvodnje;
	var naziv_vozila= car.marka + " " + car.model + " " + car.naziv + " " + car.tip;
	
	
	temp.content.getElementById("naziv_auta").innerHTML = naziv_vozila;
	temp.content.getElementById("godina_proizvodnje").innerHTML = god_proizvodnje_string;
	temp.content.getElementById("broj_sedista").innerHTML = broj_sedista_string;
	if(number_of_days!=0)
		{
		cena_rez = number_of_days * car.cena_dan;
		temp.content.getElementById("cena_auta").innerHTML = '$' + car.cena_dan + '/day\r\nTotal: $' + cena_rez;
		}
	else
		{
		temp.content.getElementById("cena_auta").innerHTML = '$' + car.cena_dan + '/day';
		}
	temp.content.getElementById("prosecna_ocena").innerHTML = car.prosecna_ocena;
	a = document.importNode(div, true);
    document.getElementById("ubaci_auto_template").appendChild(a);
    
    if(number_of_days != 0)
	{
    	if(id_rez!=0)
    	{
			$(".button_book_car").addClass('auto_id_' + car.id);
			var elements = document.getElementsByClassName('button_book_car');
			for(element of elements)
			{
				$(element).removeClass('button_book_car');
				$(element).click(bookCar(car.id));
			}
    	}else
    	{
    			var elements = document.getElementsByClassName('button_book_car');
    			for(element of elements)
    			{
    				element.parentElement.style.display = 'none';
    			}
    	}
	}
    else
    {
    	var elements = document.getElementsByClassName('button_book_car');
		for(element of elements)
		{
			element.parentElement.style.display = 'none';
		}
    }
}

function bookCar(id)
{
	return function(){
		
		$.ajax({
			url: 'api/rents/book_car/' + id  + '/' + id_rez,
			type: 'PUT',
			data: JSON.stringify(rezervacijaVozila),
			 headers: {"Authorization": "Bearer " + token},
		     contentType: 'application/json',
			success: function(vozilo) {
				console.log("uspesna rezervacija vozila sa id_vozila: " + vozilo.id);
				alert('uspesna rezervacija vozila sa id_vozila: ' + vozilo.id + " dodata u rez: " + id_rez);
				window.location.href = 'rezervacijaPreview.html?id=' + id_rez;
			}
		});
	};
	
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

