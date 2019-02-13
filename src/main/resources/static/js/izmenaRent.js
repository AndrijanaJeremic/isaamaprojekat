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
	let token=localStorage.getItem('jwtToken');
    let id = splitted[0].substring(4);
    
	$.ajax({
        type: 'GET',
        url: 'api/rents/admin/' + id,
        headers: {"Authorization": "Bearer " + token},
        contentType: 'application/json',
        success: function (rentacar)
		{
            let servis=rentacar;
            $('input[name="rent_naziv"]').val(servis.naziv);
        	$('input[name="rent_adresa"]').val(servis.adresa.ulica);
        	$('input[name="rent_broj"]').val(servis.adresa.broj);
        	$('input[name="rent_grad"]').val(servis.adresa.grad.naziv);
        	$('input[name="rent_latitude"]').val(servis.adresa.latitude);
        	$('input[name="rent_longitude"]').val(servis.adresa.longitude);
        	$('textarea[name="rent_opis"]').val(servis.promotivni_opis);
        	
		}
    });
	
	
	
	$('#forma').submit(function(event) {
		event.preventDefault();
		let ispravno=true;
		let naziv = $('input[name="rent_naziv"]').val();
		let ulica = $('input[name="rent_adresa"]').val();
		let broj = $('input[name="rent_broj"]').val();
		let grad = $('input[name="rent_grad"]').val();
		let latitude = $('input[name="rent_latitude"]').val();
		let longitude = $('input[name="rent_longitude"]').val();
		let opis = $('textarea[name="rent_opis"]').val();
		
		$('#uspesno').text("");
		$('#neuspesno').text("");
		
		if(!naziv || !ulica || !grad || !opis || !broj || !latitude || !longitude){
			$('#neuspesno').text('All fields must be filled!');
			ispravno=false;
		}
		else
		{
			if(!(/[A-Z]/.test( naziv[0])))
				{
				$('#validacijaNaziv').text("First letter of name must be capital!");
				ispravno=false;
				
				}
			else
				$('#validacijaNaziv').text("");
		
			if(!(/^[a-zA-ZćĆčČšŠđĐžŽ ]+$/.test(grad)))
			{
			$('#validacijaGrad').text("Field City must contains the letters!");
			ispravno=false;
			}
			else if(!(/[A-Z]/.test( grad[0])))
			{
			$('#validacijaGrad').text("First letter of the city's name must be capital!");
			ispravno=false;
			
			}
		else
			$('#validacijaGrad').text("");
			
			if(!(/^[0-9.]+$/.test(latitude)))
			{
			$('#validacijaLatitude').text("Field Latitude must contains only numbers!");
			ispravno=false;
			
			}
			else
			$('#validacijaLatitude').text("");
			
			if(!(/^[0-9.]+$/.test(longitude)))
			{
			$('#validacijaLongitude').text("Field Longitude must contains only numbers!");
			ispravno=false;
			
			}
			else
			$('#validacijaLongitude').text("");
		
			}
		
		if(ispravno==true)
			{
			
			$.post({
				url: '/api/rents/admin/izmenaRent/' + id + '/' + naziv + '/' + ulica + '/' + broj + '/' + grad + '/' + opis + '/' + latitude + '/' + longitude,
				headers: {"Authorization": "Bearer " + token},
				contentType: 'application/json',
				success: function(data) {
					if(data==null || data==""){
						//$('#validacijaEmail').text('User with this email already exists!');
						alert('Nije izmenjen!');
					}
					else {
						
						$('#uspesno').text('Rentacar successfully edited!');
						$('#forma').hide();
						
									window.location.href="rentAdmin.html?id="+id;
									
									
						
						
					}
				}
			
				
		});
			}
		
			
	});
			});
	