/* JS Document */

/******************************

[Table of Contents]

1. Vars and Inits
2. Set Header
3. Init Menu
4. Init Google Map
5. Init Search Form


******************************/

$(document).ready(function()
{
	"use strict";

	/* 

	1. Vars and Inits

	*/

	var menu = $('.menu');
	var menuActive = false;
	var header = $('.header');
	var map;
	var searchActive = false;

	setHeader();

	$(window).on('resize', function()
	{
		setHeader();
	});

	$(document).on('scroll', function()
	{
		setHeader();
	});

	initMenu();
	//initGoogleMap();
	initSearchForm();

	/* 

	2. Set Header

	*/

	function setHeader()
	{
		if(window.innerWidth < 992)
		{
			if($(window).scrollTop() > 100)
			{
				header.addClass('scrolled');
			}
			else
			{
				header.removeClass('scrolled');
			}
		}
		else
		{
			if($(window).scrollTop() > 100)
			{
				header.addClass('scrolled');
			}
			else
			{
				header.removeClass('scrolled');
			}
		}
		if(window.innerWidth > 991 && menuActive)
		{
			closeMenu();
		}
	}

	/* 

	3. Init Menu

	*/

	function initMenu()
	{
		if($('.hamburger').length && $('.menu').length)
		{
			var hamb = $('.hamburger');
			var close = $('.menu_close_container');

			hamb.on('click', function()
			{
				if(!menuActive)
				{
					openMenu();
				}
				else
				{
					closeMenu();
				}
			});

			close.on('click', function()
			{
				if(!menuActive)
				{
					openMenu();
				}
				else
				{
					closeMenu();
				}
			});
		}
	}

	function openMenu()
	{
		menu.addClass('active');
		menuActive = true;
	}

	function closeMenu()
	{
		menu.removeClass('active');
		menuActive = false;
	}

	/* 

	4. Init Google Map

	*/

	function initGoogleMap()
	{
		var myLatlng = new google.maps.LatLng(36.132229, -5.351153);
    	var mapOptions = 
    	{
    		center: myLatlng,
	       	zoom: 17,
			mapTypeId: google.maps.MapTypeId.ROADMAP,
			draggable: true,
			scrollwheel: false,
			zoomControl: true,
			zoomControlOptions:
			{
				position: google.maps.ControlPosition.RIGHT_CENTER
			},
			mapTypeControl: false,
			scaleControl: false,
			streetViewControl: false,
			rotateControl: false,
			fullscreenControl: true,
			styles:[]
    	}

    	// Initialize a map with options
    	map = new google.maps.Map(document.getElementById('map'), mapOptions);
   
		// Re-center map after window resize
		google.maps.event.addDomListener(window, 'resize', function()
		{
			setTimeout(function()
			{
				google.maps.event.trigger(map, "resize");
				map.setCenter(myLatlng);
			}, 1400);
		});
	}

	/* 

	8. Init Search Form

	*/

	function initSearchForm()
	{
		if($('.search_form').length)
		{
			var searchForm = $('.search_form');
			var searchInput = $('.search_content_input');
			var searchButton = $('.content_search');

			searchButton.on('click', function(event)
			{
				event.stopPropagation();

				if(!searchActive)
				{
					searchForm.addClass('active');
					searchActive = true;

					$(document).one('click', function closeForm(e)
					{
						if($(e.target).hasClass('search_content_input'))
						{
							$(document).one('click', closeForm);
						}
						else
						{
							searchForm.removeClass('active');
							searchActive = false;
						}
					});
				}
				else
				{
					searchForm.removeClass('active');
					searchActive = false;
				}
			});	
		}
	}
	
	
	var search = window.location.search;
	var splitted = search.split('&');
	
    let id = splitted[0].substring(4);
   let  token=localStorage.getItem('jwtToken');
    $.ajax({
        type: 'GET',
        url: '/api/rents/admin/' + id,
    	headers: {"Authorization": "Bearer " + token},
		contentType: 'application/json',
        success: function (rentacar)
		{
            let servis=rentacar;
            for(let filijala of servis.filijale)
            	{
            	var opcija='<option value="' + filijala.id + '">' + filijala.adresa.ulica + ' ' + filijala.adresa.broj + ' ' + filijala.adresa.grad.naziv + '</option>';
            	$("#filijale_lista").append(opcija);
            	}
        	
		}
    });
	
	
	
	$('#forma').submit(function(event) {
		event.preventDefault();
		let ispravno=true;
		let filijala = $("#filijale_lista").val();
		let naziv = $('input[name="naziv_auta"]').val();
    	let marka = $('input[name="marka_auta"]').val();
    	let model = $('input[name="model_auta"]').val();
    	let godina = $('input[name="godina_auta"]').val();
    	let sedista = $('input[name="sedista_auta"]').val();
    	let tip = $('input[name="tip_auta"]').val();
    	let cena = $('input[name="cena_auta"]').val();
    	
		$('#uspesno').text("");
		$('#neuspesno').text("");
		
		if(!naziv || !marka || !model || !godina || !sedista || !tip || !cena || !filijala){
			$('#neuspesno').text('All fields must be filled!');
			ispravno=false;
		}
		else
		{
			
		
			
			$('#validacijaNaziv').text("");
		
			
		
			if(!(/[A-Z]/.test( marka[0])))
			{
			$('#validacijaMarka').text("First letter of the brand must be capital!");
			ispravno=false;
			
			}
			else
			$('#validacijaMarka').text("");
	
		}
	
		if(ispravno==true)
			{
			$.post({
				url: '/api/vozila/admin/dodajVozilo/' + filijala + '/'  + naziv + '/' + marka + '/' + model+ '/' + godina + '/' + sedista + '/' + tip + '/' + cena,
				headers: {"Authorization": "Bearer " + token},
				contentType: 'application/json',
				success: function(data) {
					if(data==null || data==""){
						//$('#validacijaEmail').text('User with this email already exists!');
						$('#neuspesno').text('Car with this name, brand and model already exists!');
					}
					else {
						$('#neuspesno').text("");
						$('#uspesno').text('Car successfully added!');
						$('#forma').hide();
						
									window.location.href="rentAdmin.html?id="+id;
									
									
						
						
					}
				}
			
				
		});
			}
		
			
	});
			});
