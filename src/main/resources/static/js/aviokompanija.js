var flightArray = [];
var uniqueDests = [];
var korisnik = null;
var aviokompanija = null
$(document).ready(function()	
{
    var id_presented = window.location.search.substring(4);
    
    
    
    getKorisnik();

	$.ajax({
        type: 'GET',
        url: 'rest/airline/' + id_presented,
        contentType: 'application/json',
        dataType: "json",
        complete: function (data)
		{
            aviokompanija = data.responseJSON;
            console.log(aviokompanija);
            setUpForAirline(aviokompanija)
		}
    });

    
	
	
	
	

	
	
});

function getKorisnik()
{
	token = localStorage.getItem('jwtToken');
	$.post
	({
		url: "/auth/userprofile",
		headers: {"Authorization": "Bearer " + token},
		contentType: 'application/json',
		data : token,
		  
		success: function(user) 
		{
			korisnik = user;
			setUpForUser();
		},
		error: function() 
		{
		}
	});
}






function setUpForAirline(airline)
{

    //naslovi aviokompanije
    var $title = $("#titl_aviokompanija");
    var $naslov = $("#aviokompanija_ime");
    var $listaDestinacija = $("#destination_list");
    var $listaAkcija = $("#action_list");
    
    
    
    $("#adresaLokala").text(aviokompanija.adresa.ulica + " " + aviokompanija.adresa.broj + ", " + aviokompanija.adresa.grad.naziv);
    $title.text(airline.naziv);
    $naslov.text(airline.naziv);

    //template destinacija - generisanje svih
    var destinationTemplate = $('#destination_template').html();
    
 
    $("#airlineDesc").text(airline.opis);
    $("#luggageInfo").text(airline.info)
    $("#returnButton").click(function(){
    	$('#defaultAirlineView').prop("hidden",false);
    	 $('#searchListing').prop("hidden",true)
    });
    		
    setLuggageInfo();

    $.each(airline.letovi,function(i, dest) 
    {
    	//ubacivanje svakog leta u listu letova
    	if(jQuery.inArray(dest, flightArray) == -1)
    	{
    		flightArray.push(dest);
    	}
    	
    	//ukoliko se u listi jedinstvenih destinacije ne nalazi ova, bice ubacena i prikazana
    	if(jQuery.inArray(dest.dokle, uniqueDests) == -1 )
    	{
    		
    		uniqueDests.push(dest.dokle);
    		
        	console.log(dest);
        	
        	
            var item = Mustache.render(destinationTemplate, dest);
        	var $item_element = $(item);

            var $dugmeZaSelektDestinacije= $item_element.find(".selektovanjeDestinacije");
            
       
            $dugmeZaSelektDestinacije.attr('id', "showdest" + jQuery.inArray(dest.dokle,uniqueDests));
            
            $listaDestinacija.append($item_element.prop('outerHTML'));

            
            $("#showdest" + jQuery.inArray(dest.dokle,uniqueDests)).click(function ()
            		{
            	

            	  		
            	  		var searchValue  = dest.dokle;

            		    $('#defaultAirlineView').prop("hidden",true);

            		    
            		    //PRIKAZ SVIH LETOVA KOJI ODGOVARAJU DESTINACIJI
            		    var $naslovListing = $('#naslovListing');
            		    $naslovListing.show();
            		    $naslovListing.text("Destinations for:  " + searchValue);
            		    $('#searchListing').prop("hidden",false)
            		     $("#selektovaniLetovi").empty();

            		    //ubacivanje u listu 
            		    $.each(flightArray,function(i, flight) 
            		    	    {
            		    	    	    	if(flight.dokle == searchValue)
            		    	    	    	{
            		    	    	    		insertFlightIntoDestination(flight)	
            		    	    	    		$("#rezButton"+flight.id).click(function(){
            		    	    	    			
            		    	    	    			if(korisnik == null)
            		    	    	    			{
            		    	    	    				alert("To make a reservation you need to be logged on to your account.");
            		    	    	    				return;
            		    	    	    			}
            		    	    	    			
            		    	    	    			window.location.replace("rezervacija.html?id="+flight.id);
            		    	    	    		});
            		    	    	    	}
            		    	    });
            		    
            		    
            		});
            
        
    		
    	}
    	
    });



    $.each(airline.brziLetovi,function(i, action) 
    {
    	
    	insertAkcija(action);
    	$("#quickRezButton"+action.id).click(function(){
    		if(korisnik == null)
    		{
    			alert("To make a quick reservation you need to be logged on to your account.");
    			return;
    		}

    		$.post({
    			url: "/api/rezervacija/createBrza/" + action.id + "/" + token,
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
    		
    		
    		console.log(action.id);
    	});
    		
    });
    
   
    initMap();

}


function insertFlightIntoDestination(flight)
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
          center: ol.proj.fromLonLat([aviokompanija.adresa.longitude, aviokompanija.adresa.latitude]),
          zoom: 17
        })
      });
}


function insertAkcija(action)
{
	var actionTemplate = $('#action_template').html();
	var $listaAkcija = $("#action_list");
	var item = Mustache.render(actionTemplate, action);
	var $item_element = $(item);
	
	var $poletanje = $item_element.find("#vremePoletanja");
	var $sletanje = $item_element.find("#vremeSletanja");
	var $cenaPopust = $item_element.find("#cenaPopust");
	var $Qdeparted = $item_element.find("#Qdeparted");    	

	
	$item_element.find("#quickRezButton").prop("id","quickRezButton"+action.id);
	
	//parsiranje teksta vremena
	$poletanje.text("Departures: " + action.vremePoletanja.substring(0,16).replace("T","   "));
	$sletanje.text("Arrives: " + action.vremeSletanja.substring(0,16).replace("T","   "));
	

    $cenaPopust.text( (action.cena - (action.cena*(action.popust/100))).toFixed(2));
	$Qdeparted.prop("id","Qdeparted"+action.id);
	$listaAkcija.append($item_element.prop('outerHTML'));
	
	var today = new Date();
	d = new Date(action.vremePoletanja);
	if( today >= d )
	{
		$("#Qdeparted"+flight.id).prop("hidden",false);
		$("#quickRez"+flight.id).prop("hidden",true);
	}
	
}



function setUpForUser()
{
	$("#registerButton").prop("hidden",true);
	$("#loginButton").prop("hidden",true);
	$("#profileButton").prop("hidden",false);
	$("#logoutButton").prop("hidden",false);

	
}

function odjava()
{
	localStorage.clear();
}


function setLuggageInfo()
{
	$("#luggageInfo").text(aviokompanija.luggageInfo.opis);
	$("#standard").html("<b>STANDARD</b> - " + aviokompanija.luggageInfo.standardMaxKila + "kg max costs " + aviokompanija.luggageInfo.cenaStandard.toFixed(2));
	$("#extra").html("<b>SUPERSIZED LUGGAGE</b> - " + aviokompanija.luggageInfo.extraMaxKila + "kg max costs " + aviokompanija.luggageInfo.cenaExtra.toFixed(2));
	
}