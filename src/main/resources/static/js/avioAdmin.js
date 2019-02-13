var admin =  null;
var aviokompanija = null;
token = localStorage.getItem('jwtToken');
var letovi = [];

$(document).ready(function()
{

	getAdmin();

});


function setupPage()
{
	$("#editAirlineButton").click(function(){
		clearAllForms();
		insertCurrentValuesAirline();
		$("#editAirlineDiv").show();
	});
	
	$("#backButtonAirline").click(function(){
		$("#editAirlineDiv").hide();
	});
	
	$("#editLuggageInfo").click(function(){
		clearAllForms();
		insertCurrentValuesLuggage();
		$("#editLuggageInfoDiv").show();
	});
	
	$("#backButtonLuggage").click(function(){
		$("#editLuggageInfoDiv").hide();
	});
	
	$("#addFlightButton").click(function(){
		clearAllForms();
		clearFlightInputs();
		$("#newFlightDiv").show();
	});
	
	$("#backButtonAddFlight").click(function(){
		$("#newFlightDiv").hide();
	});
	

	$("#editFlightButton").click(function(){
		clearAllForms();
		$("#editFlightDiv").show();
	});
	
	$("#backButtonEditFlight").click(function(){
		$("#editFlightDiv").hide();
	});
	

	
	$("#editAirlineAction").click(function()
	{
		ispravno = validateEditAirline();
		if(ispravno)
		{
			retVal = createAirline();
			console.log("salje se airline edit: " + retVal)
			$.ajax({
				type: 'PUT',
				url: 'rest/airline/editAirline/' + token,
				headers: {"Authorization": "Bearer " + token},
				data: JSON.stringify(retVal),
				contentType: 'application/json',
				success: function(data) 
				{

					if(data == 'success')
					{
							aviokompanija = retVal;
					}
					else
					{
						alert("An error has occured - request is denied.")
					}


				}
			});
		}
	});
	
	
	$("#editLuggageAction").click(function()
	{
		ispravno = validateEditLuggage();
		if(ispravno)
		{
			var retVal = createLuggageInfo();
			console.log("salje se luggage info edit: ")
			$.ajax({
				type: 'PUT',
				url: 'rest/airline/editLuggageInfo/' + token + "/" + aviokompanija.id,
				headers: {"Authorization": "Bearer " + token},
				data: JSON.stringify(retVal),
				contentType: 'application/json',
				success: function(data) 
				{

					if(data == 'success')
					{
							console.log(retVal);
							aviokompanija.luggageInfo = retVal;
					}
					else
					{
						alert("An error has occured - request is denied.")
					}


				}
			});
		}
		
	});
	
	
	
	$("#addFlightAction").click(function()
			{
			ispravno = validateAddFlight();
			if(ispravno)
			{

				retVal = createFlight();
				$.ajax({
					type: 'POST',
					url: 'api/let/addFlight/' + token + "/" + aviokompanija.id,
					headers: {"Authorization": "Bearer " + token},
					data: JSON.stringify(retVal),
					contentType: 'application/json',
					success: function(data) 
					{
						if(data == null)
						{
							alert("An error has occured - request is denied.")
							console.log(data)
						}
						else
						{
							
							console.log(data)
							
							var temp_index = letovi.length;
							letovi.push(data);
							insertFlight(data);
							$("#selectFlight").append('<option id="'+ temp_index + '" value="'+ data.id + '" ">' +	data.odakle + ' - ' + data.dokle + '</option>')

						}
						
					}
				});

				
			}
			else
			{
				console.log("neispravan unos");
			}
		});
	
	
	
	
	
	
	
	$("#editFlightAction").click(function()
			{
			ispravno = validateEditFlight();
			//gettovanje id-a selektovanog leta
			flightID =  $("#selectFlight option:selected").val();
			if(ispravno)
			{
				var retVal = createFlightEdit();
				retVal.id = flightID;
				$.ajax({
					type: 'PUT',
					url: 'api/let/editFlight/' + token,
					headers: {"Authorization": "Bearer " + token},
					data: JSON.stringify(retVal),
					contentType: 'application/json',
					success: function(data) 
					{
						if(data == "success")
						{
							window.location.reload(true);
						}
						else
						{
							alert("An error has occured - request is denied.")
							
						}
					}
				});
			}
		});
	
	
	
	
}



function clearAllForms()
{
	$("#editAirlineDiv").hide();
	$("#editLuggageInfoDiv").hide();
	$("#newFlightDiv").hide();
	$("#editFlightDiv").hide();
}


function insertCurrentValuesAirline ()
{
	$("#airlineNameField").val(aviokompanija.naziv);
	$("#airlineCity").val(aviokompanija.adresa.grad.naziv)
	$("#airlineStreet").val(aviokompanija.adresa.ulica);
	$("#airlineNumber").val(aviokompanija.adresa.broj);
	$("#airlineLatituda").val(aviokompanija.adresa.latitude);
	$("#airlineLongituda").val(aviokompanija.adresa.longitude);
	$("#airlineDesc").val(aviokompanija.opis);
}

function clearFlightInputs()
{
	$("#flightFromAdd").val("");
	$("#flightFromAdd").val("");
	$("#flightToAdd").val("");
	$("#departureAdd").val("");
	$("#arrivalAdd").val("");
	$("#durationAdd").val("");
	$("#distanceAdd").val("");
	$("#priceAdd").val("");
	$("#maxKapAdd").val("");
	$("#discountAdd").val("");
}

function insertCurrentValuesLuggage()
{
	prnje = aviokompanija.luggageInfo
	$("#standardMax").val(prnje.standardMaxKila);
	$("#standardPrice").val(prnje.cenaStandard.toFixed(2));
	$("#extraMax").val(prnje.extraMaxKila);
	$("#extraPrice").val(prnje.cenaExtra.toFixed(2));
	$("#luggageDesc").val(prnje.opis);	
}

function validateEditAirline()
{
	var ispravno = true;
	
	var nameText = $("#airlineNameField").val();

	var cityText = $("#airlineCity").val();
	var streetText = $("#airlineStreet").val();
	var numberText = $("#airlineNumber").val();
	
	var latitudaText = $("#airlineLatituda").val();
	var longitudaText = $("#airlineLongituda").val();
	var descText = $("#airlineDesc").val();
	

	var nameRegex = new RegExp('^[a-zA-Z]{1,}');
	var numberRegex = new RegExp('^[0-9]+$');
	var decimalRegex = new RegExp('^[0-9.]+$');
	
	if(nameRegex.test(nameText) == false)
	{
		$("#airlineNameError").prop("hidden",false)
		ispravno=false;
	}
	else
	{
		$("#airlineNameError").prop("hidden",true)
	}
	
	
	
	if(nameRegex.test(cityText) == false)
	{
		$("#airlineCityError").prop("hidden",false)
		ispravno=false;
	}
	else
	{
		$("#airlineCityError").prop("hidden",true)
	}
	
	
	
	if(nameRegex.test(streetText) == false)
	{
		$("#airlineStreetError").prop("hidden",false)
		ispravno=false;
	}
	else
	{
		$("#airlineStreetError").prop("hidden",true)
	}
	
	
	
	if(decimalRegex.test(numberText) == false)
	{
		$("#airlineNumberError").prop("hidden",false)
		ispravno=false;
	}
	else
	{
		$("#airlineNumberError").prop("hidden",true)
	}
	
	
	if(decimalRegex.test(latitudaText) == false)
	{
		$("#airlineLatitudaError").prop("hidden",false)
		ispravno=false;
	}
	else
	{
		$("#airlineLatitudaError").prop("hidden",true)
	}
	
	
	if(decimalRegex.test(longitudaText) == false)
	{
		$("#airlineLongitudaError").prop("hidden",false)
		ispravno=false;
	}
	else
	{
		$("#airlineLongitudaError").prop("hidden",true)
	}
	
	
	if(descText == "")
	{
		$("#descError").prop("hidden",false)
		ispravno=false;
	}
	else
	{
		$("#descError").prop("hidden",true)
	}
	
		
	return ispravno;
}

function validateEditLuggage()
{

	var ispravno = true;
	var standardMaxText = $("#standardMax").val();
	var standardCenaText = $("#standardPrice").val();
	var extraMaxText = $("#extraMax").val();
	var extraCenaText = $("#extraPrice").val();
	var descText = $("#luggageDesc").val();	
	
	var numberRegex = new RegExp('^[0-9]+$');
	var decimalRegex = new RegExp('^[0-9.]+$');
	
		
	if(numberRegex.test(standardMaxText) == false)
	{
		ispravno = false;
		$("#standardMaxError").prop("hidden",false);
	}
	else
	{
		$("#standardMaxError").prop("hidden",true);
	}
	
	
	
	if(numberRegex.test(extraMaxText) == false)
	{
		ispravno = false;
		$("#extraMaxError").prop("hidden",false);
	}
	else
	{
		$("#extraMaxError").prop("hidden",true);
	}
	
	
	
	if(decimalRegex.test(standardCenaText) == false)
	{
		ispravno = false;
		$("#standardPriceError").prop("hidden",false);
	}
	else
	{
		$("#standardPriceError").prop("hidden",true);
	}
	
	
	if(decimalRegex.test(extraCenaText) == false)
	{
		ispravno = false;
		$("#extraPriceError").prop("hidden",false);
	}
	else
	{
		$("#extraPriceError").prop("hidden",true);
	}
	
	
	return ispravno;
}

function validateAddFlight()
{
	var ispravno = true;
	
	var fromText = $("#flightFromAdd").val();
	var toText = $("#flightToAdd").val();
	var departureText = $("#departureAdd").val();
	var arrivesText = $("#arrivalAdd").val();
	var durationText = $("#durationAdd").val();
	var distanceText = $("#distanceAdd").val();
	var priceText = $("#priceAdd").val();
	var maxKapText = $("#maxKapAdd").val();
	var discountText = $("#discountAdd").val();
	
	
	var numberRegex = new RegExp('^[0-9]+$');
	var decimalRegex = new RegExp('^[0-9.]+$');
	
	
	if(fromText == "")
	{
		ispravno = false;
		$("#flightFromAddError").prop("hidden",false);
	}
	else
	{
		$("#flightFromAddError").prop("hidden",true);
	}
	
	
	if(toText == "")
	{
		ispravno = false;
		$("#flightToAddError").prop("hidden",false);
	}
	else
	{
		$("#flightToAddError").prop("hidden",true);
	}
	
	
	
	var d = null;
	if(departureText == "")
	{
		ispravno = false;
		$("#departureAddError").prop("hidden",false);
	}
	else
	{
		var today = new Date();
		d = new Date(departureText);
		
		if( today >= d )
		{
			console.log("semantika departure time nije dobra")
			ispravno = false;
			$("#departureAddError").prop("hidden",false);
		}
		else
		{
			$("#departureAddError").prop("hidden",true);
		}
		
	}
	
	
	
	if(arrivesText == "")
	{
		ispravno = false;
		$("#arrivalAddError").prop("hidden",false);
	}
	else
	{
		var today = new Date();
		var e = new Date(arrivesText);
		
		if(today >= e)
		{
			console.log("semantika departure time nije dobra")
			ispravno = false;
			$("#arrivalAddError").prop("hidden",false);
		}
		else
		{
			$("#arrivalAddError").prop("hidden",true);
		}
		if(d!=null)
		{
			if(d >= e)
			{
				console.log("polazak posle dolaska")
				ispravno = false;
				$("#arrivalAddError").prop("hidden",false);
			}
			else
			{
				$("#arrivalAddError").prop("hidden",true);
			}
			
		}	
	}
	
	
	if(numberRegex.test(durationText)==false || durationText == "")
	{
		ispravno = false;
		$("#duratioAddnError").prop("hidden",false);
	}
	else
	{
		try
		{
			test = parseInt(durationText)
			$("#duratioAddnError").prop("hidden",true);

		}
		catch(error)
		{
			ispravno = false;
			$("#duratioAddnError").prop("hidden",false);
		}
	}
	
	
	if(decimalRegex.test(distanceText)==false || distanceText == "")
	{
		ispravno = false;
		$("#distanceAddError").prop("hidden",false);
	}
	else
	{
		try
		{
			test = parseFloat(distanceText)
			$("#distanceAddError").prop("hidden",true);

		}
		catch(error)
		{
			ispravno = false;
			$("#distanceAddError").prop("hidden",false);
		}
	}

	
	
	
	if(decimalRegex.test(priceText)==false || priceText == "")
	{
		ispravno = false;
		$("#priceAddError").prop("hidden",false);
	}
	else
	{
		try
		{
			test = parseFloat(priceText)
			$("#priceAddError").prop("hidden",true);

		}
		catch(error)
		{
			ispravno = false;
			$("#priceAddError").prop("hidden",false);
		}
	}
	
	
	
	if(numberRegex.test(maxKapText)==false || maxKapText == "")
	{
		ispravno = false;
		$("#maxKapAddError").prop("hidden",false);
	}
	else
	{
		try
		{
			if(parseInt(maxKapText) % 4 == 0)
			{
				$("#maxKapAddError").prop("hidden",true);	
			}
			else
			{
				ispravno = false;
				$("#maxKapAddError").prop("hidden",false);
			}
		}
		catch(error)
		{
			ispravno = false;
			$("#maxKapAddError").prop("hidden",false);
		}
	}
	
	
	if(numberRegex.test(discountText)==false || discountText == "")
	{
		ispravno = false;
		$("#discountAddError").prop("hidden",false);
	}
	else
	{
		
		try
		{
			var test = parseInt(discountText)
			if(test > 100 || test < 0 )
			{
				ispravno = false;
				$("#discountAddError").prop("hidden",false);
			}
			else
			{
				$("#discountAddError").prop("hidden",true);
			}
			

		}
		catch(error)
		{
			ispravno = false;
			$("#discountAddError").prop("hidden",false);
		}
	}
	
	return ispravno;
}



function validateEditFlight()
{
	var ispravno = true;
	
	var fromText = $("#flightFromEdit").val();
	var toText = $("#flightToEdit").val();
	var departureText = $("#departureEdit").val();
	var arrivesText = $("#arrivalEdit").val();
	var durationText = $("#durationEdit").val();
	var distanceText = $("#distanceEdit").val();
	var priceText = $("#priceEdit").val();
	var maxKapText = $("#maxKapEdit").val();
	var discountText = $("#discountEdit").val();
	
	
	var numberRegex = new RegExp('^[0-9]+$');
	var decimalRegex = new RegExp('^[0-9.]+$');
	
	
	if(fromText == "")
	{
		ispravno = false;
		$("#flightFromEditError").prop("hidden",false);
	}
	else
	{
		$("#flightFromEditError").prop("hidden",true);
	}
	
	
	if(toText == "")
	{
		ispravno = false;
		$("#flightToEditError").prop("hidden",false);
	}
	else
	{
		$("#flightToEditError").prop("hidden",true);
	}
	
	
	
	var d = null;
	if(departureText == "")
	{
		ispravno = false;
		$("#departureEditError").prop("hidden",false);
	}
	else
	{
		var today = new Date();
		d = new Date(departureText);
		
		if( today >= d )
		{
			console.log("semantika departure time nije dobra")
			ispravno = false;
			$("#departureEditError").prop("hidden",false);
		}
		else
		{
			$("#departureEditError").prop("hidden",true);
		}
		
	}
	
	
	
	if(arrivesText == "")
	{
		ispravno = false;
		$("#arrivalEditError").prop("hidden",false);
	}
	else
	{
		var today = new Date();
		var e = new Date(arrivesText);
		
		if(today >= e)
		{
			console.log("semantika departure time nije dobra")
			ispravno = false;
			$("#arrivalEditError").prop("hidden",false);
		}
		else
		{
			$("#arrivalEditError").prop("hidden",true);
		}
		if(d!=null)
		{
			if(d >= e)
			{
				console.log("polazak posle dolaska")
				ispravno = false;
				$("#arrivalEditError").prop("hidden",false);
			}
			else
			{
				$("#arrivalEditError").prop("hidden",true);
			}
			
		}	
	}
	
	
	if(numberRegex.test(durationText)==false || durationText == "")
	{
		ispravno = false;
		$("#durationEditError").prop("hidden",false);
	}
	else
	{
		try
		{
			test = parseInt(durationText)
			$("#durationEditError").prop("hidden",true);

		}
		catch(error)
		{
			ispravno = false;
			$("#durationEditError").prop("hidden",false);
		}
	}
	
	
	if(decimalRegex.test(distanceText)==false || distanceText == "")
	{
		ispravno = false;
		$("#distanceEditError").prop("hidden",false);
	}
	else
	{
		try
		{
			test = parseFloat(distanceText)
			$("#distanceEditError").prop("hidden",true);

		}
		catch(error)
		{
			ispravno = false;
			$("#distanceEditError").prop("hidden",false);
		}
	}

	
	
	
	if(decimalRegex.test(priceText)==false || priceText == "")
	{
		ispravno = false;
		$("#priceEditError").prop("hidden",false);
	}
	else
	{
		try
		{
			test = parseFloat(priceText)
			$("#priceEditError").prop("hidden",true);

		}
		catch(error)
		{
			ispravno = false;
			$("#priceEditError").prop("hidden",false);
		}
	}
	
	
	
	if(numberRegex.test(maxKapText)==false || maxKapText == "")
	{
		ispravno = false;
		$("#maxKapErrorError").prop("hidden",false);
	}
	else
	{
		try
		{
			if(parseInt(maxKapText) % 4 == 0)
			{
				$("#maxKapErrorError").prop("hidden",true);	
			}
			else
			{
				ispravno = false;
				$("#maxKapErrorError").prop("hidden",false);
			}
		}
		catch(error)
		{
			ispravno = false;
			$("#maxKapErrorError").prop("hidden",false);
		}
	}
	
	
	if(numberRegex.test(discountText)==false || discountText == "")
	{
		ispravno = false;
		$("#discountEditError").prop("hidden",false);
	}
	else
	{
		
		try
		{
			var test = parseInt(discountText)
			if(test > 100 || test < 0 )
			{
				ispravno = false;
				$("#discountEditError").prop("hidden",false);
			}
			else
			{
				$("#discountEditError").prop("hidden",true);
			}
			

		}
		catch(error)
		{
			ispravno = false;
			$("#discountEditError").prop("hidden",false);
		}
	}
	
	return ispravno;
}



function getAdmin()
{
	var search = window.location.search;
	var n = search.lastIndexOf("=");
	var id_presented = search.substring(n+1);
	
    $.ajax({
        type: 'GET',
        url: 'rest/airline/admin/' + id_presented + "/" + token,
        headers: {"Authorization": "Bearer " + token},
        contentType: 'application/json',
        success: function (data)
		{	
        	if(data == null)
        	{
        		alert("Unauthoratized.");
        		window.location.replace("index.html");
        	}
        	aviokompanija = data;
        	
        	
        	clearAllForms()
        	setupPage();
        	
        	console.log(aviokompanija)
        	fillAviokompanija();
        	calculateRatings();
		},
		error: function(data){
    		alert("Unauthoratized.");
    		window.location.replace("index.html");	
		}
    });
}


function fillAviokompanija()
{
	
	$("#airlineDeskriptor").text(aviokompanija.opis);
	$("#airlineName").text(aviokompanija.naziv);
	
    $.each(aviokompanija.brziLetovi,function(i, action) 
    	    {
    	    	
    	    	insertAkcija(action);
    	    	$("#quickRezButton"+action.id).click(function(){
    	    		//post metoda za brzu rezervaciju
    	    		console.log(action.id);
    	    	});
    	    		
    	    });
    //ubacivanje u listu 
    $.each(aviokompanija.letovi,function(i, flight) 
    	    {
    			insertFlight(flight)	
	    		$("#rezButton"+flight.id).click(function()
	    		{	    			
	    			window.location.replace("rezervacija.html?id="+flight.id);
	    		});
    	    	 
    	    });
    $("#adresaLokala").text(aviokompanija.adresa.ulica + " " + aviokompanija.adresa.broj + ", " + aviokompanija.adresa.grad.naziv);
    initMap();
    
    
    fillSelectFlight();
    if(aviokompanija.brziLetovi.length == 0)
    {
    	$("#noActions").prop("hidden", false);
    }
    
    
   
}


function fillSelectFlight()
{
	var $lista = $("#selectFlight");
	
	for(var i = 0 ; i < aviokompanija.letovi.length ; i ++)
	{
		var let = aviokompanija.letovi[i];		
		$lista.append('<option id="'+ i + '" value="'+ let.id + '" ">' +	let.odakle + ' - ' + let.dokle + '</option>')
		letovi.push(let);
	}
	
	//funkcija za promenu
	$("#selectFlight").change(function () 
	{
	   var index = $("#selectFlight option:selected").prop("id");
	   setEditFlightValues(index);
	 });
	
	
	setEditFlightValues(aviokompanija.letovi[0].id);
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
    	var $rezButton = $item_element.find("#removeButton");
    	var $speccButton = $item_element.find("#markAsSpecialButton");
    	
    	
    	
    	
    	var $flightRatingDiv = $item_element.find("#flightRating");
    	var $noFlightRatingsTag = $item_element.find("#noFlightRatings");
    	var $avgFlightRating = $item_element.find("#avgFlightRating");

    	


    	$cenaPopust.text(flight.cena.toFixed(2));

    	//parsiranje teksta vremena
    	$poletanje.text("Departure: " + flight.vremePoletanja.substring(0,16).replace("T","   "));
    	$sletanje.text("Arrives: " + flight.vremeSletanja.substring(0,16).replace("T","   "));            		    	    	
    	$rezButton.attr("id", "removeButton" + flight.id);
    	$speccButton.attr("id", "markAsSpecialButton" + flight.id);
    	
    	$flightRatingDiv.attr("id", "flightRating" + flight.id);
    	$noFlightRatingsTag.attr("id", "noFlightRatings" + flight.id);
    	$avgFlightRating.attr("id", "avgFlightRating" + flight.id);
    	
    	
    	$selektovaniLetovi.append($item_element.prop('outerHTML'));
    	
    	
    	
    	
    	$("#removeButton"+flight.id).click(function(){
    		console.log("saljem poziv za brisanje: " + flight.id);
    		$.ajax({
    	        type: 'DELETE',
    	        url: 'api/let/delete/' + token + '/' + flight.id,
    	        contentType: 'application/json',
    	        complete: function (data)
    			{
    	        	window.location.reload(true);
    			}
    	    });    	
    	});
    	
    	
    	$("#markAsSpecialButton"+flight.id).click(function(){
    		if(flight.popust == 0)
    		{
    			alert("The discount for the selected flight is 0%. Change this value and then add to Specials.")
    			return;
    		}
    		$.ajax({
    	        type: 'POST', //jer dopunajvam tabelu neku 
    	        url: 'api/let/addToActions/' + token + '/' + flight.id,
    	        contentType: 'application/json',
    	        complete: function (data)
    			{
    	        	window.location.reload(true);
    			}
    	    });    	
    	});
    	
    	
    	calculateRatingForFlight(flight);
    	
    	
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
	var $removeDiscount = $item_element.find("#removeDiscountButton");

	$item_element.find("#quickRezButton").prop("id","quickRezButton"+action.id);
	
	//parsiranje teksta vremena
	$poletanje.text("Departures: " + action.vremePoletanja.substring(0,16).replace("T","   "));
	$sletanje.text("Arrives: " + action.vremeSletanja.substring(0,16).replace("T","   "));
	$removeDiscount.attr("id","removeDiscountButton" + action.id)

    $cenaPopust.text( (action.cena - (action.cena*(action.popust/100))).toFixed(2));
	
	$listaAkcija.append($item_element.prop('outerHTML'));
	
	
	$("#removeDiscountButton" + action.id).click(function(){
		console.log("klik")
		$.ajax({
	        type: 'DELETE',
	        url: 'api/let/removeFromSpecials/' + token + '/' + action.id,
	        contentType: 'application/json',
	        complete: function (data)
			{
	        	window.location.reload(true);
			}
	    });  
		
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
          center: ol.proj.fromLonLat([aviokompanija.adresa.longitude, aviokompanija.adresa.latitude]),
          zoom: 17
        })
      });
}


function setEditFlightValues(index)
{
	var let = letovi[index]
	
	console.log(let.popust)
	
	$("#flightFromEdit").val(let.odakle);
	$("#flightToEdit").val(let.dokle);
	$("#departureEdit").val(let.vremePoletanja.substring(0,19));
	$("#arrivalEdit").val(let.vremeSletanja.substring(0,19));
	$("#durationEdit").val(let.trajanje);
	$("#distanceEdit").val(let.udaljenost);
	$("#priceEdit").val(let.cena);
	$("#maxKapEdit").val(let.maxKapacitet);
	$("#discountEdit").val(let.popust);
}



function createAirline()
{
	var retVal = aviokompanija;
	
	var nameText = $("#airlineNameField").val();

	var cityText = $("#airlineCity").val();
	var streetText = $("#airlineStreet").val();
	var numberText = $("#airlineNumber").val();
	
	var latitudaText = $("#airlineLatituda").val();
	var longitudaText = $("#airlineLongituda").val();
	var descText = $("#airlineDesc").val();
	
	retVal.naziv = nameText;
	retVal.adresa.grad = cityText;
	retVal.adresa.broj = numberText;
	retVal.adresa.latitude = latitudaText;
	retVal.adresa.longitude = longitudaText;
	retVal.adresa.ulica = streetText;
	retVal.opis = descText;
	
	return retVal;
}


function createLuggageInfo()
{
	retVal = aviokompanija.luggageInfo;
	
	retVal.standardMaxKila = $("#standardMax").val();
	retVal.cenaStandard = $("#standardPrice").val();
	retVal.extraMaxKila = $("#extraMax").val();
	retVal.cenaExtra = $("#extraPrice").val();
	retVal.opis = $("#luggageDesc").val();	
	
	return retVal;
}


function createFlight()
{
	retVal = new Object();
	
	var fromText = $("#flightFromAdd").val();
	var toText = $("#flightToAdd").val();
	var departureText = $("#departureAdd").val();
	var arrivesText = $("#arrivalAdd").val();
	var durationText = $("#durationAdd").val();
	var distanceText = $("#distanceAdd").val();
	var priceText = $("#priceAdd").val();
	var maxKapText = $("#maxKapAdd").val();
	var discountText = $("#discountAdd").val();
	
	
	retVal.cena = priceText;
	retVal.dokle = toText;
	retVal.odakle = fromText;
	retVal.vremePoletanja = departureText;
	retVal.vremeSletanja = arrivesText;
	retVal.zauzetaSedista = [];
	retVal.rejtinzi = [];
	retVal.presedanja = [];
	retVal.maxKapacitet = maxKapText;
	retVal.trajanje = durationText;
	retVal.udaljenost = distanceText
	retVal.popust = discountText
	retVal.prosecna_ocena = 0;
	retVal.aviokompanija = aviokompanija;
	
	
	return retVal;

}




function createFlightEdit()
{
	retVal = new Object();
	
	var fromText = $("#flightFromEdit").val();
	var toText = $("#flightToEdit").val();
	var departureText = $("#departureEdit").val();
	var arrivesText = $("#arrivalEdit").val();
	var durationText = $("#durationEdit").val();
	var distanceText = $("#distanceEdit").val();
	var priceText = $("#priceEdit").val();
	var maxKapText = $("#maxKapEdit").val();
	var discountText = $("#discountEdit").val();
	
	
	retVal.cena = priceText;
	retVal.dokle = toText;
	retVal.odakle = fromText;
	retVal.vremePoletanja = departureText;
	retVal.vremeSletanja = arrivesText;
	retVal.zauzetaSedista = [];
	retVal.rejtinzi = [];
	retVal.presedanja = [];
	retVal.maxKapacitet = maxKapText;
	retVal.trajanje = durationText;
	retVal.udaljenost = distanceText
	retVal.popust = discountText
	retVal.prosecna_ocena = 0;
	retVal.aviokompanija = aviokompanija;
	
	
	return retVal;

}



function calculateRatings()
{
	if(aviokompanija.rejtinzi.length == 0)
	{
		$("#noRatings").prop("hidden",false);
		var ocena = 0
		$("#average_rating").text(ocena.toFixed(1))
	}
	else
	{
		var sum = 0;
		for(var i = 0 ; i < aviokompanija.rejtinzi.length ; i++)
		{
			sum = sum + aviokompanija.rejtinzi[i].ocena;
		}
		var ocena = sum/aviokompanija.rejtinzi.length;
		$("#average_rating").text(ocena.toFixed(1))
	}
}

function calculateRatingForFlight(flight)
{

	var today = new Date();
	d = new Date(flight.vremeSletanja);
	
	if(today > d)
	{
		$("#markAsSpecialButton"+flight.id).prop("hidden",true);
		$("#flightRating" + flight.id).prop("hidden",false);
		
		if(flight.rejtinzi.length == 0)
		{
			$("#noFlightRatings" + flight.id).prop("hidden",false);
			$("#avgFlightRating" + flight.id).text("0.0");
		}
		else
		{
			$("#noFlightRatings" + flight.id).prop("hidden",true);

			var sum = 0;
			for(var i = 0 ; i < flight.rejtinzi.length ; i++)
			{
				sum = sum + flight.rejtinzi[i].ocena;
			}
			var ocena = sum/flight.rejtinzi.length;
			$("#average_rating").text(ocena.toFixed(1))
		}
		
	}
	
	
}
