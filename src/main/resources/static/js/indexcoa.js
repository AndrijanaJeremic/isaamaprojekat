var korisnik = null;
token = localStorage.getItem('jwtToken');


$(document).ready(function()
{
	

		$.post
		({
			url: "/auth/userprofile",
			headers: {"Authorization": "Bearer " + token},
			contentType: 'application/json',
			data : token,
			  
			success: function(user) 
			{
				korisnik = user;
				console.log(korisnik)
				

				
				
			},
			error: function() 
			{
			}
		});
	
		
		getAirlines();
		
		getAllActions();
		
		
		
		
		$("#searchFlightsButton").click(function(){
		
			var ispravna = validateSearchQuery();
			if(ispravna)
			{
				console.log("salji upit");
				gimmieFlights();
			}
			
		});
	

});


function getAirlines()
{
	
    $.ajax({
        type: 'GET',
        url: '/rest/airline/all',
        success: function (airlines)
		{
        	if(airlines == null)
			{
				console.log('There are no airlines');
			}
			else
			{
				console.log('There are ' + airlines.length + ' airlines in memory.');
				for (let airline of airlines) 
				{
					insertAirline(airline);
					
				}
			}
		}
    });
	

}


function insertAirline(airline)
{	
	var $airlineList = $("#aviokompanije");
	
	var airlineTemplate = $("#airlineTemplate").html();
		
	var $item = $(airlineTemplate);

	var $naziv = $item.find("#airlineNaziv");
	var $link = $item.find("#link");

	
	$naziv.text(airline.naziv);
	$link.prop("href","http://localhost:8080/aviokompanija.html?id="+airline.id);
	
	$airlineList.append($item);
	
}

function getAllActions()
{
    $.ajax({
        type: 'GET',
        url: '/api/let/allActions',
        success: function (flights)
		{
        	if(flights == null)
			{

			}
			else
			{
				console.log(flights);
				for (let flight of flights) 
				{
					insertAkcija(flight);
					
					
					
					$("#quickRezButton"+flight.id).click(function(){
						if(korisnik == null)
						{
							alert("To make a quick reservation you need to be logged on to your account.");
							return;
						}

						$.post({
							url: "/api/rezervacija/createBrza/" + flight.id + "/" + token,
							headers: {"Authorization": "Bearer " + token},
							contentType: 'application/json',
							success: function(data) {
								if(data==null || data=="")
								{
									alert("An error occured while processing information.")
								}
								else 
								{
									alert("Your flight is succesfully booked.")
									window.location.replace("rezervacijaPreview.html?id=" + data);
								}
							}
						
							
						});
						
						
					});
					
				}
			}
		}
    });
	
}


function insertAkcija(action)
{
	var actionTemplate = $('#action_template').html();
	var $listaAkcija = $("#action_list");
	var item = Mustache.render(actionTemplate, action);
	var $item_element = $(item);
	var $Qdeparted = $item_element.find("#Qdeparted");    	

	
	var $poletanje = $item_element.find("#vremePoletanja");
	var $sletanje = $item_element.find("#vremeSletanja");
	var $cenaPopust = $item_element.find("#cenaPopust");

	$item_element.find("#quickRezButton").prop("id","quickRezButton"+action.id);
	$Qdeparted.prop("id","Qdeparted"+action.id);
	//parsiranje teksta vremena
	$poletanje.text("Departures: " + action.vremePoletanja.substring(0,16).replace("T","   "));
	$sletanje.text("Arrives: " + action.vremeSletanja.substring(0,16).replace("T","   "));
	

    $cenaPopust.text( (action.cena - (action.cena*(action.popust/100))).toFixed(2));
	
    
	$listaAkcija.append($item_element.prop('outerHTML'));
	
	var today = new Date();
	d = new Date(action.vremePoletanja);
	if( today >= d )
	{
		$("#Qdeparted"+flight.id).prop("hidden",false);
		$("#quickRez"+flight.id).prop("hidden",true);
	}
	

	
	
}
function gimmieFlights()
{
	var searchFrom = $("#searchFrom").val();
	var seatchTo = $("#seatchTo").val();
	var searchDeparture = $("#searchDeparture").val();
	var searchMaxPrice = $("#searchMaxPrice").val();
	var searchMaxDuration = $("#searchMaxDuration").val();
	
	if(searchFrom == "")
		searchFrom="empt";
	
	if(seatchTo == "")
		seatchTo="empt";
	
	if(searchDeparture == "")
		searchDeparture="empt";
	
	if(searchMaxPrice == "")
		searchMaxPrice=-1;
	
	if(searchMaxDuration == "")
		searchMaxDuration=-1;
	
	
	console.log("datum: " + searchDeparture);
	
    $.ajax({
        type: 'GET',
        url: '/api/let/search/' + searchFrom + '/' + seatchTo + '/' + searchDeparture + '/' + searchMaxPrice + '/' +  searchMaxDuration,
        success: function (flights)
		{
        	if(flights == null)
			{

			}
			else
			{
				
				$("#searchListing").show();
				
				if(flights.length == 0)
				{
					$("#noFlightsFound").prop("hidden",false);
				}
				
				$("#selektovaniLetovi").empty();
				
				
				for(var i = 0 ; i < flights.length ; i++)
				{
					insertFlight(flights[i]);
				}
			}
		}
    });
	
	


}





function validateSearchQuery()
{
	var ispravno = true;
	
	var numberRegex = new RegExp('^[0-9]*$');
	var decimalRegex = new RegExp('^[0-9.]*$');
	
	
	var searchFrom = $("#searchFrom").val();
	var seatchTo = $("#seatchTo").val();
	
	var searchDeparture = $("#searchDeparture").val();
	var searchMaxPrice = $("#searchMaxPrice").val();
	var searchMaxDuration = $("#searchMaxDuration").val();
	
	
	if(searchDeparture != "")
	{
		var today = new Date();
		d = new Date(searchDeparture);
		
		if(today >= d)
		{
			ispravno = false;
			$("#searchDateError").prop("hidden",false);
		}
		else
		{
			$("#searchDateError").prop("hidden",true);

		}
	}
	else
	{
		$("#searchDateError").prop("hidden",true);
	}
	
	
	
	if(searchMaxPrice != "")
	{
		if(decimalRegex.test(searchMaxPrice) == false)
		{
			ispravno = false
			$("#searchPriceError").prop("hidden",false);
		}
		else
		{
			$("#searchPriceError").prop("hidden",true);
		}
	}
	else
	{
		$("#searchPriceError").prop("hidden",true);
	}
	
	
	if(searchMaxDuration != "")
	{
		if(numberRegex.test(searchMaxDuration) == false)
		{
			ispravno = false
			$("#searchDurationError").prop("hidden",false);
		}
		else
		{
			$("#searchDurationError").prop("hidden",true);
		}
	}
	else
	{
		$("#searchDurationError").prop("hidden",true);
	}
	
	
	
	return ispravno;
}


function insertFlight(flight)
{
		var pretragaTemplate = $('#pretragaTemplate').html();
  		var $selektovaniLetovi = $("#selektovaniLetovi");
		
		var item = Mustache.render(pretragaTemplate, flight);
    	var $item_element = $(item);
    	
    	var $poletanje = $item_element.find("#vremePoletanja");
    	var $sletanje = $item_element.find("#vremeSletanja");
    	var $cenaPopust = $item_element.find("#cenaPopust");
    	var $rezButton = $item_element.find("#rezButton");
    	var $departed = $item_element.find("#departed");


    	$cenaPopust.text(flight.cena.toFixed(2));

    	//parsiranje teksta vremena
    	$poletanje.text("Departure: " + flight.vremePoletanja.substring(0,16).replace("T","   "));
    	$sletanje.text("Arrives: " + flight.vremeSletanja.substring(0,16).replace("T","   "));            		    	    	
    	$rezButton.attr("id", "rezButton" + flight.id);
    	$departed.attr("id", "departed" + flight.id);
    	$selektovaniLetovi.append($item_element.prop('outerHTML'));
    	
    	
		var today = new Date();
		d = new Date(flight.vremePoletanja);
		if( today >= d )
		{
			$("#departed"+flight.id).prop("hidden",false);
			$("#rezButton"+flight.id).prop("hidden",true);
		}
    	
    	
		
		

    	$("#rezButton"+flight.id).click(function(){
    		if(korisnik != null)
    		{
    			
    			window.location.replace("rezervacija.html?id=" + flight.id);
    		}
    		else
    		{
				alert("To make a reservation you need to be logged on to your account.");
				return;
			}
    		
    	});
}


