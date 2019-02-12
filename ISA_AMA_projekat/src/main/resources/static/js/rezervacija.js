var niz_osoba = [];
var korisnik = null;
var flight = null;
var personList = null;
var personCount = 0;
var friend_list = []
var token = "";
var canvasList = [];

var rezervacijaOsobe = []

$(document).ready(function()
{
	
	
    personList = $('#personsList');

	getKorisnik()
	getFlightById()

	//dodavanje odmah osobe koja je kreirala rezervaciju:
	

	$("#inserNewPerson").click( function ()
	{
		insertNewPerson(function(){
			fillAllFields(personCount.toString());
		});
		price = personCount * flight.cena
		$("#price").text(price.toFixed(2));
		
	});
	
	$("#makeReservation").click(function()
	{
		
		var ispravna = true;
		ispravna = validateFields();
		if(ispravna == false) return;
		ispravna = validateSemantic();
		
		if(ispravna == false) return;
				
		if(ispravna == true)
		{
			makeRezervation();
		}

	});
	
	
	
	
	
	



	function PersonInReservation(name, last, email, passNum, baggage, seat)
	{
		  this.name = name;
		  this.last = last;
		  this.email = email;
		  this.passNum = passNum;
		  this.baggage = baggage;
		  this.seat = seat;
	}

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
				if(user == null)
				{
					window.location.replace("index.html");
				}
				
				korisnik = user;
				//ako je null idi na pocetnu
				getFriendListForUser(korisnik.id, function() {
					setPageInfo();
				});
				
			},
			error: function() 
			{
				window.location.replace("index.html");
			}
		});
	}

	function getFlightById()
	{
		
		var url = window.location.toString();
		var flightID = url[url.length-1]
		
		$.get({
			url: "/api/let/" + flightID,
			success: function(data) 
			{
							
				if(data == null)
				{
					console.log("let je nullcina");
				}
				else
				{		
					flight = data;
					text1 = data.odakle;
					text2 = data.dokle;
					$("#flightLocationTittle1").text(text1);
					$("#flightLocationTittle2").text(text2);
					
					$("#flightFrom").text("From: " + capitalize(flight.odakle));
					$("#flightTo").text("To: " + capitalize(flight.dokle));
					$("#flightDuration").text("Duration: " + flight.trajanje + " hours");
					$("#flightDeparture").text("Departure time: " + processTime(flight.vremePoletanja));
					$("#flightArrival").text("Arrival time: " + processTime(flight.vremeSletanja));
				}
							
			},
			error : function(data)
			{
				console.log("errorcina prilikom prihvatanja leta");
			}
			});
	}


	function setPageInfo()
	{
		// ime leta nalepi na naziv
		// koristi 
		
		//dodaj korisnika koji je izabrao opciju rezervisi kao prvog u listi
		insertUser(korisnik, function()
		{	
			fillAllFields("");
		});
		
		

		$("#friends").prop("hidden", true);
		$("#personIndex").prop("hidden", true);

	}


	function insertUser(user, callback)
	{
	    var $personTemplate = $('#personTemplate');
	    
	    var $item = $($personTemplate.html())
	    
	    $item.find("#imePersone").val(user.ime)
	    $item.find("#przPersone").val(user.prezime)
	    $item.find("#emailPersone").val(user.email)
	    
	    
	    $item.find("#imePersone").prop("readonly", true);
	    $item.find("#przPersone").prop("readonly", true);
	    $item.find("#emailPersone").prop("readonly", true);
	    
	    
		$("#price").text(flight.cena.toFixed(2));

	    
	    personList.append($item);
	    callback();
	    personCount++;
	    drawSedista(flight.maxKapacitet,"");
	    $("#tajtl").css("margin-bottom","-5%");

	}


	function insertNewPerson(callback)
	{
		var personTemplate = $('#personTemplate').html();	
		var $personTemplate = $(personTemplate);
		
	 
		var $removeButton = $personTemplate.find("#removeButton");
		$removeButton.attr("id","removeButton" + personCount);
		
		var $imePersone = $personTemplate.find("#imePersone");
		$imePersone.attr("id","imePersone" + personCount);
		
		var $przPersone = $personTemplate.find("#przPersone");
		$przPersone.attr("id","przPersone" + personCount);
		
		var $emailPersone = $personTemplate.find("#emailPersone");
		$emailPersone.attr("id","emailPersone" + personCount);
		
		var $passPersone = $personTemplate.find("#passPersone");
		$passPersone.attr("id","passPersone" + personCount);
		
		var $selectFriend = $personTemplate.find("#friends");
		$selectFriend.attr("id","friends" + personCount);
		
		var $selectSeat = $personTemplate.find("#seatCanvas");
		$selectSeat.attr("id","seatCanvas" + personCount);
		
		
		$personTemplate.find('input[name=radioButton]').prop("name","radioButton"+personCount);
		
		
		
		var $blah = $personTemplate.find("#errorName");
		$blah.attr("id","errorName" + personCount);
		var $blah = $personTemplate.find("#errorLastname");
		$blah.attr("id","errorLastname" + personCount);
		var $blah = $personTemplate.find("#errorEmail");
		$blah.attr("id","errorEmail" + personCount);
		var $blah = $personTemplate.find("#errorPass");
		$blah.attr("id","errorPass" + personCount);
		var $blah = $personTemplate.find("#errorSeat");
		$blah.attr("id","errorSeat" + personCount);
		
		
		
	            
	    personList.append($personTemplate.prop('outerHTML'));
	    drawSedista(flight.maxKapacitet, personCount)	
		callback();
		$(".selectFriendList").change(function() 
				{
					temp = $(this).attr("id").substring(7);
					index = $("#friends"+temp).prop('selectedIndex');
					if(index != 0)
					{
					    $("#imePersone"+temp).val(friend_list[index-1].ime)
					    $("#przPersone"+temp).val(friend_list[index-1].prezime)
					    $("#emailPersone"+temp).val(friend_list[index-1].email)
					    
					    $("#imePersone"+temp).prop("readonly", true);
					    $("#przPersone"+temp).prop("readonly", true);
					    $("#emailPersone"+temp).prop("readonly", true);
					}
					else
					{
						$("#imePersone"+temp).val("");
					    $("#przPersone"+temp).val("");
					    $("#emailPersone"+temp).val("");
					    
					    $("#imePersone"+temp).prop("readonly", false);
					    $("#przPersone"+temp).prop("readonly", false);
					    $("#emailPersone"+temp).prop("readonly", false);
					}
				});

		
		personCount++;
		

		
	 }



	function getFriendListForUser(korisnikId, callback)
	{
		$.get({
		url: "/api/users/friendsOf/" + korisnikId,
		headers: {"Authorization": "Bearer " + token},
		success: function(data) 
		{
			if(data == null)
			{
				console.log("nullcina");
			}		
			for(let friend of data)
			{
				friend_list.push(friend);
			}
			
			callback();
			
		},
		error : function(data)
		{
			console.log("errorcina prilikom prihvatanja prijatelja");
		}
		});
	}


	function fillAllFields(number)
	{

			var friendListElement = document.getElementById("friends" + number)
			var $friendListElement = $(friendListElement);
				
			$friendListElement.append('<option value="null"></option>')
			for (i = 0; i < friend_list.length; i++)
			{
				
				$friendListElement.append('<option value="' + friend_list[i].id +'">'  + friend_list[i].ime + ' ' + friend_list[i].prezime + '</option>')
			}
	}

	function validateFields()
	{
		var correct = true;
		
		var nameText = $("#imePersone").val();
		var lastnameText = $("#przPersone").val();
		var emailText = $("#emailPersone").val();
		var passText = $("#passPersone").val();
		var baggageValue = $('input[name=radioButton]:checked').val();
		var seatValue = getSeatFromCanvas(0);
		
		console.log("pass 0: " + seatValue);
		
		var emailRegex = new RegExp('^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$');
		var nameRegex = new RegExp('^[a-zA-Z]{1,}');
		var passRegex = new RegExp('^[0-9]{2,}');
		if(passText=="" || 	passRegex.test(passText)==false)
		{
			correct = false;
			$("#errorPass").prop("hidden",false);
		}
		
		if(seatValue == -1)
		{
			correct = false;
			$("#errorSeat").prop("hidden",false);
		}
		if(seatValue < 1 || seatValue > flight.maxKapacitet)
		{
			correct = false;
			$("#errorSeat").prop("hidden",false);
		}
		
		
		
		
		//PREUZIMANJE SEDISTA TODO
		var putnik = new osobaRezervacije(null,nameText,lastnameText,emailText,passText,baggageValue,seatValue);
		rezervacijaOsobe.push(putnik);
		
		
		for(var i = 1 ; i < personCount ; i++)
		{
			var nameText = $("#imePersone" + i).val();
			var lastnameText = $("#przPersone" + i).val();
			var emailText = $("#emailPersone" + i).val();
			var passText = $("#passPersone" + i).val();
			var seatValue = getSeatFromCanvas(i);
			
			console.log("pass " + i + ": " + seatValue);
			
			if(nameText=="" || 	nameRegex.test(nameText)==false)
			{
				correct = false;
				$("#errorName"+i).prop("hidden",false);
			}
			
			if(lastnameText=="" || 	nameRegex.test(lastnameText)==false)
			{
				correct = false;
				$("#errorLastname"+i).prop("hidden",false);
			}
			
			if(emailText=="" || emailRegex.test(emailText)==false)
			{
				correct = false;
				$("#errorEmail"+i).prop("hidden",false);
			}
			
			if(passText=="" || 	passRegex.test(passText)==false)
			{
				correct = false;
				$("#errorPass" + i).prop("hidden",false);
			}
			
			if(seatValue == -1)
			{
				correct = false;
				$("#errorSeat"+i).prop("hidden",false);
			}
			if(seatValue < 1 || seatValue > flight.maxKapacitet)
			{
				correct = false;
				$("#errorSeat"+i).prop("hidden",false);
			}
			
			var baggageValue = $('input[name=radioButton]:checked').val();
			
			//PREUZIMANJE SEDISTA TODO
			
			var putnik = new osobaRezervacije(null,nameText,lastnameText,emailText,passText,baggageValue,seatValue);
			rezervacijaOsobe.push(putnik);
			
		}

		return correct;
	}


	function validateSemantic()
	{
		correct = true;
		
		temp_list = [];
		temp_list2 = [];
		var passText = $("#passPersone").val();
		var emailText = $("#emailPersone").val();
		temp_list.push(passText);
		temp_list2.push(emailText);
		
		for(var i = 1 ; i < personCount ; i++)
		{
			passText = $("#passPersone" + i).val();
			emailText = $("#emailPersone" + i).val();
			if($.inArray(passText, temp_list) != -1 || $.inArray(emailText, temp_list2) != -1)
			{
				alert("Error: All pasengers must be unique.");
				correct = false;	
			}
			temp_list.push(passText);
			temp_list2.push(emailText);	
		}
		
		
		
		// ne sme postojati duplikati sedista
		seats = [];
		for(var i = 0 ; i < canvasList.length ; i ++ )
		{
			var current = getSeatFromCanvas(i);
			if($.inArray(current,seats) != -1)
			{
				correct = false;
				alert("Every passenger must reserve a unique seat.");
			}
			seats.push(current);
		}
		
		return correct;
		
	}

	function makeRezervation()
	{
		$.post({
			url: "/api/rezervacija/create/" + flight.id + "/" + token,
			data: JSON.stringify(rezervacijaOsobe),
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
	}


	function osobaRezervacije(id, ime,prezime,email,broj_pasosa,prtljag,sediste)
	{
		this.id = id; //u ovom IDu se nalazi id korisnika koji je prijatelj, u bekendu se menja generickim IDjem za ovaj tip
		this.ime = ime;
		this.prezime = prezime;
		this.email = email;
		this.brojPasosa = broj_pasosa;
		this.prtljag = prtljag;
		this.sediste = sediste;
	}


	function drawSedista(seatCount, ajdi)
	{
		
		id_kanvasa = "seatCanvas" + ajdi;
		
		$(id_kanvasa).prop("width",flight.maxKapacitet*15);
		var canvas = new fabric.Canvas(id_kanvasa);
		
		
		
		
		
		canvas.selection = false;
		
		var dodatno = 0;
		var razmakRedova = 0;
		var cnt = 0;
		for( var i = 1 ; i <= flight.maxKapacitet ; i = i + 4 )
		{
			
			if(cnt == 6)
			{
				razmakRedova = razmakRedova + 30;
				cnt = 0;
				console.log("razmak")
			}
			cnt++;
			
			dodatno = 0;
			if(i >= 10) dodatno = 5;
			if(i >= 100) dodatno = 10;

				
			
			var rect = new fabric.Rect({
				  left: 50 + 50*i/4 + razmakRedova,
				  top: 20,
				  fill: 'gray',
				  width: 35,
				  height: 35,
				  opacity: 0.15
				});
				var text = new fabric.Text(i.toString(), {
					  fontSize: 20,
					  left: 63 + 50*i/4-dodatno + razmakRedova,
					  top: 25
					});

				
				dodatno = 0;
				if(i+1 >= 10) dodatno = 5;
				if(i+1 >= 100) dodatno = 10;
				
				
				
				var rect1 = new fabric.Rect({
					  left: 50 + 50*i/4 + razmakRedova,
					  top: 70,
					  fill: 'gray',
					  width: 35,
					  height: 35,
					  opacity: 0.15
					});
				var text1 = new fabric.Text((i+1).toString(), {
					  fontSize: 20,
					  left: 63 + 50*i/4-dodatno + razmakRedova,
					  top: 75
					});

				
				
				dodatno = 0;
				if(i+2 >= 10) dodatno = 5;
				if(i+2 >= 100) dodatno = 10;
				
				
				var rect2 = new fabric.Rect({
					  left: 50 + 50*i/4 + razmakRedova,
					  top: 160,
					  fill: 'gray',
					  width: 35,
					  height: 35,
					  opacity: 0.15
					});
				var text2 = new fabric.Text((i+2).toString(), {
					  fontSize: 20,
					  left: 63 + 50*i/4-dodatno + razmakRedova,
					  top: 165
					});

				
				
				dodatno = 0;
				if(i+3 >= 10) dodatno = 5;
				if(i+3 >= 100) dodatno = 10;
				
				
				
				var rect3 = new fabric.Rect({
					  left: 50 + 50*i/4 + razmakRedova,
					  top: 210,
					  fill: 'gray',
					  width: 35,
					  height: 35,
					  opacity: 0.15
					});
				var text3 = new fabric.Text((i+3).toString(), {
					  fontSize: 20,
					  left: 63 + 50*i/4-dodatno + razmakRedova,
					  top: 215
					});	

				
				var group = new fabric.Group([ rect, text ]);
				var group1 = new fabric.Group([ rect1, text1 ]);
				var group2 = new fabric.Group([ rect2, text2 ]);
				var group3 = new fabric.Group([ rect3, text3 ]);
				
				
				//hendlanje zauzetih sedista:
				if(zauzeto(i))
				{
					rect.set({opacity:0.75});
					group.set('selectable', false); 
				}
				if(zauzeto(i+1))
				{
					rect1.set({opacity:0.75});
					group1.set('selectable', false);
				}
				if(zauzeto(i+2))
				{
					rect2.set({opacity:0.75});
					group2.set('selectable', false);
				}
				if(zauzeto(i+3))
				{
					rect3.set({opacity:0.75});
					group3.set('selectable', false);
				}
				
				
				
				group.hasControls = false;
				group1.hasControls = false;
				group2.hasControls = false;
				group3.hasControls = false;
				group.lockMovementX = true;
				group.lockMovementY = true;
				group1.lockMovementX = true;
				group1.lockMovementY = true;
				group2.lockMovementX = true;
				group2.lockMovementY = true;
				group3.lockMovementX = true;
				group3.lockMovementY = true;
			
				
				// "add" rectangle onto canvas
				canvas.add(group);
				canvas.add(group1);
				canvas.add(group2);
				canvas.add(group3);		
		}
		
		canvasList.push(canvas);

	}

	function zauzeto(sediste)
	{
		if($.inArray(sediste,flight.zauzetaSedista) != -1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}	
});

function getSeatFromCanvas(numberOfCanvas)
{
	canvas = canvasList[numberOfCanvas];
	try
	{
		object_array = canvas.getActiveObject().get('_objects');
		return object_array[1].text;
	}
	catch(err)
	{
		return -1;
	}

	
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

function odjava()
{
	localStorage.clear();
}
