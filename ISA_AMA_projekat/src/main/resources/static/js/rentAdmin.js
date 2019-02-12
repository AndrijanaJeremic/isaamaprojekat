var token=null;
$(document).ready(function()
{
	token=localStorage.getItem('jwtToken');
	
	
	var search = window.location.search;
	var splitted = search.split('&');
	
    id = splitted[0].substring(4);
    
	$.ajax({
        type: 'GET',
        url: 'api/rents/admin/' + id,
        headers: {"Authorization": "Bearer " + token},
        contentType: 'application/json',
        success: function (rentacar)
		{
        	servis = rentacar;
            longitude = rentacar.adresa.longitude;
        	latitude = rentacar.adresa.latitude;
            console.log(rentacar);
            showRentacar(rentacar);
            initMap();
		}
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
	
	
	
	$('#izmenaCENE').submit(function(event){
		
		event.preventDefault();
		
		let izabrano = $("#lista_usluga").val();
		let cena = $('input[name="promena_cene"]').val();
		
		$.post({
			url: '/api/usluge/admin/izmenaUsluge/' + izabrano + '/'  + cena,
			headers: {"Authorization": "Bearer " + token},
			contentType: 'application/json',
			success: function(data) {
				if(data==null || data==""){
					//$('#validacijaEmail').text('User with this email already exists!');
					alert('Nije izmenjena!');
				}
				else {
					
								alert('Cost of additional service successfully changed!');
								window.location.href="rentAdmin.html?id="+id;
								
								
					
					
				}
			}
		
			
	});
		
	});
	
	$('#special_price_form').submit(function(event) {
		console.log('special_price_form submit');
		event.preventDefault();
		

		$('#error_start_special_price').hide();
		
		let selected = $( "select[name=services_special_price]" ).val();
		let date_start = $('input[name="start_special_price"]').val();
		let date_end = $('input[name="end_special_price"]').val();
		let discount = $('input[name="discount_special_price"]').val();
		let id_vozilo = $('select[name="car_special_price"] option:selected').val();
		
		$.post({
			url: '/api/rents/add_special_price/' + id_vozilo,
			headers: {"Authorization": "Bearer " + token},
			data: JSON.stringify({pocetak_vazenja: date_start, kraj_vazenja: date_end, popust: discount}),
			contentType: 'application/json',
			success: function(data) {
				if(data.result == 'success'){
					console.log('Popust uspesno dodat');
					window.location.href="rentAdmin.html?id=" + id;
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

function showRentacar(rentacar)
{
	console.log(rentacar.naziv);
	 $("#title_rentacar").text(rentacar.naziv);
	 $("#rentacar_name").text(rentacar.naziv);
	 $("#ocenaRentacara").text(" Rate: " + rentacar.prosecna_ocena);
	 $('#rentacar_info_text').text(rentacar.promotivni_opis);
	
	 var idiNa = "izmenaRent.html?id="+rentacar.id;
	 $('#dugmeIzmena').attr("href", idiNa);
	 var dodajFil = "dodajFilijalu.html?id="+rentacar.id;
	 $('#dugmeFil').attr("href", dodajFil);
	 var dodajVozilo = "dodajVozilo.html?id="+rentacar.id;
	 $('#dugmeVozilo').attr("href", dodajVozilo);
	
	 
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
	    		addFilijala(rentacar, filijala);
	    	}
	    	
	    	
		}
	    
	    if(rentacar.usluge.length == 0)
		{
	    	$("#text_no_services").text("There are no additional services.");
		}
	    else
		{
	    	$("#text_no_services").text("Additional services: ");
	    	$("#izmenaCENE").attr("hidden", false);
	    	$('input[name="promena_cene"]').val(0);
	    	var selectsp = $('select[name=services_special_price]');
			selectsp.children().remove();
			
			var option = document.createElement("option");
	    	option.text = '';
	    	option.value = 0;
	    	selectsp.append(option);
	    	
	    	
	    	for(let usluga of rentacar.usluge)
	    		{
	    		var opcija='<option value="' + usluga.id + '">' + usluga.naziv +  '</option>';
            	$("#lista_usluga").append(opcija);
	    		}
	    	for (let usluga of rentacar.usluge) 
	    	{
	    		let li = $('<li>' + usluga.naziv + ' - $' + usluga.cena + '/per day</li>');
	    		$('#usluge_rentacar').append(li);
	    	}
	    	
	    	for (let usluga of rentacar.usluge) 
	    	{
	    		
	    		var option = document.createElement("option");
				option.text = usluga.naziv;
				option.value = usluga.id;
				selectsp.append(option);
	    	}
		}
}

function addFilijala(rentacar, filijala)
{
	for (let vozilo of filijala.vozila)
		{
		var option = '<option value="' + vozilo.id + '">' + vozilo.naziv + ' ' + vozilo.marka + ' ' + vozilo.model + '</option>';
		$('select[name="car_special_price"]').append(option);
		}
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
    	temp.content.getElementById("vozila_u_ponudi").innerHTML = '<a id="vozila_dugme'+filijala.id + '" style="cursor:pointer; color:white;" onclick="javascript:izlistajVozila(' + filijala.id + ',' + rentacar.id + ')">Cars on offer</a>';
    	
	}
    
    temp.content.getElementById("brisanje_filijale").innerHTML = '<a style="cursor:pointer; color:white;" onclick="javascript:brisanjeFil('  + filijala.id + ');">Delete branch</a>'
    temp.content.getElementById("izmena_filijale").innerHTML = '<a style="cursor:pointer; color:white;" href="izmenaFil.html?idr=' + rentacar.id + '&id=' + filijala.id + '">Edit branch</a>'
	
    a = document.importNode(div, true);
    document.getElementById("ubaci_filijale_template").appendChild(a);
}
function prikaziIzvestaj()
{
	
	$("#izvestaj").hide();
	var id_servisa = servis.id;
	$.ajax({
        type: 'GET',
        url: 'api/rents/sveRezervacijeVozila/total/' + id_servisa,
        headers: {"Authorization": "Bearer " + token},
        contentType: 'application/json',
        success: function (total)
		{
        	//alert('Pritisnuto dugme za prikaz izvestaja rentacara: ' + servis.id + ", ukupna cena: " + total);
        	$("#totalCena").text("TOTAL IN THE LAST THREE MONTHS: $" + total);
        	
		}
    });
	$.ajax({
        type: 'GET',
        url: 'api/rents/dnevni/' + id_servisa,
        headers: {"Authorization": "Bearer " + token},
        contentType: 'application/json',
        success: function (dan)
		{
        	
        	document.getElementById("bar-chart-day").height=70;
        	
        	let day = new Chart(document.getElementById("bar-chart-day"), {
        	type: 'pie',
        	data: {
        	labels:['Reserved', 'Unreserved'],
        	datasets: [{
        	        	
        	        	data : [dan, 100-dan],
        	        	backgroundColor: ['#ffce56', '#cc65fe'] ,
        	        	
        	           }]
        	},
        	options: {
                
        		title : {
        			display : true,
        			text : "Today's bookings in percentages",
        			fontSize : 25
        			
        		}
        	}
        	});
        	
        
		}
    });
	
	
	$.ajax({
        type: 'GET',
        url: 'api/rents/last7days/' + id_servisa,
        headers: {"Authorization": "Bearer " + token},
        contentType: 'application/json',
        success: function (dani)
		{
        	
        	document.getElementById("bar-chart").height=70;
        	
        	let week = new Chart(document.getElementById("bar-chart"), {
        	type: 'bar',
        	data: {
        	labels:['Day1', 'Day2', 'Day3', 'Day4', 'Day5', 'Day6', 'Day7'],
        	datasets: [{
        	        	label : 'Reservations' ,
        	        	data : dani,
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
        url: 'api/rents/months/' + id_servisa,
        headers: {"Authorization": "Bearer " + token},
        contentType: 'application/json',
        success: function (meseci)
		{
        	
        	document.getElementById("bar-chart-month").height=70;
        	
        	let month = new Chart(document.getElementById("bar-chart-month"), {
        	type: 'bar',
        	data: {
        	labels:['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Avg', 'Sep', 'Oct', 'Nov', 'Dec'],
        	datasets: [{
        	        	label : 'Reservations' ,
        	        	data : meseci,
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
function brisanjeFil(filijala_id)
{
	$.ajax({
        type: 'DELETE',
        url: 'api/filijale/admin/delete/' + filijala_id,
        headers: {"Authorization": "Bearer " + token},
        contentType: 'application/json',
        success: function (filijala)
		{
            alert('Branch with id: ' + filijala.id + 'successfully deleted!');
            window.location.href="rentAdmin.html?id="+id;
		}
    });
}
function izlistajVozila(filijala_id, rentacar_id)
{
	$("#vozila_dugme" + filijala_id).hide();
	
	$.get({
		url: '/api/filijale/' + filijala_id,
		success: function(filijala) {
			console.log('filijala_uspesno_vracena ' + filijala.id);
		
			for(let vozilo of filijala.vozila)
				{
				
					addVozilo(vozilo, rentacar_id);
					
				}
			
		},
		error : function(){
			alert('Greska prilikom vracanja filijale.');
		}
	});


}

function addVozilo(car, rentacar_id)
{
	
	
	var temp, div, a;
	temp = document.getElementById("template_auto");
	div = temp.content.querySelector("div#ubaci_auto");
	
	var broj_sedista_string = "Number of seats: " + car.broj_sedista;
	var god_proizvodnje_string = "The year of production: " + car.godina_proizvodnje;
	var naziv_vozila= car.marka + " " + car.model + " " + car.naziv + " " + car.tip;
	
	
	temp.content.getElementById("naziv_auta").innerHTML = naziv_vozila;
	temp.content.getElementById("godina_proizvodnje").innerHTML = god_proizvodnje_string;
	temp.content.getElementById("broj_sedista").innerHTML = broj_sedista_string;
	temp.content.getElementById("cena_auta").innerHTML = '$' + car.cena_dan + '/day';
	temp.content.getElementById("prosecna_ocena").innerHTML = car.prosecna_ocena;
	
	if(car.popusti.length == 0)
	{
		temp.content.getElementById("text_discount_list").style.display = 'none';
		temp.content.getElementById("discount_list").style.display = 'none';
	}
	
	list = temp.content.getElementById("discount_list");
	while (list.hasChildNodes()) 
	{   
		list.removeChild(list.firstChild);
	}
	
	for (let popust of car.popusti) 
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
    
	 let rezervisana = false;
	    
	    for(let rezervacija of car.rezervacije)
	    {
	    	var today = new Date();
	    	today.setHours(0, 0, 0);
	    	
	    	var end = rezervacija.datum_vracanja.substring(0, 19);
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
	if(!rezervisana)
		{
	temp.content.getElementById("izmena_auta").innerHTML = '<a style="cursor:pointer; color:white;" href="izmenaAuta.html?idr=' + rentacar_id + '&id=' + car.id + '">Edit car</a>'
	temp.content.getElementById("brisanje_auta").innerHTML = '<a style="cursor:pointer; color:white;" onclick="javascript:brisanjeVozila('  + car.id + ');">Delete car</a>'
		}
	else
		{
		temp.content.getElementById("izmena_auta").innerHTML = '';
		temp.content.getElementById("brisanje_auta").innerHTML = '';
		}
	
	a = document.importNode(div, true);
    document.getElementById("ubaci_auto_template").appendChild(a);
    
	
	
    
}

function brisanjeVozila(car_id)
{
	$.ajax({
        type: 'DELETE',
        url: 'api/vozila/admin/delete/' + car_id,
        headers: {"Authorization": "Bearer " + token},
        contentType: 'application/json',
        success: function (vozilo)
		{
            alert('Car with id: ' + vozilo.id + 'successfully deleted!');
            window.location.href="rentAdmin.html?id="+id;
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
          center: ol.proj.fromLonLat([longitude, latitude]),
          zoom: 17
        })
      });
}


function addSpecialPrice()
{
	console.log('add special price');
	
	$('#div_special_price').show();

	
	var el = document.getElementById('div_special_price');
    el.scrollIntoView(true);
    window.scrollBy(0, -150);
}

function backEdit()
{
	
	$('#div_special_price').hide();
	
	$('#button_hotel_back').attr('pritisnuto', 'true');
	
	var el = document.getElementById('div_changing_buttons');
    el.scrollIntoView(true);
    window.scrollBy(0, -500);
}


function dodajUsluge(popust_id, usluge)
{
	for(var i = 0, size = usluge.length; i < size ; i++)
	{
		var usluga_id = usluge[i];
		var id_popust = '' + popust_id + '';
		
		if(usluga_id != '0')
		{
			$.post({
				url: '/api/rents/add_usluga_special_price/' + id_popust + '/' + usluga_id,
				headers: {"Authorization": "Bearer " + token},
				contentType: 'application/json',
				success: function(data) {
					if(data.result == 'success'){
						console.log('Popust uspesno dodat');
						window.location.href="rentAdmin.html?id=" + id;
					}
					else 
					{
						console.log('Popust nije uspesno dodat');
						
					}
				}
			});
		}
	}	
}