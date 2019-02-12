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
	
    let idr = splitted[0].substring(5);
    let id = splitted[1].substring(3);
	$.ajax({
        type: 'GET',
        url: '/api/vozila/' + id,
        success: function (vozilo)
		{
            let auto=vozilo;
           
        	$('input[name="naziv_auta"]').val(auto.naziv);
        	$('input[name="marka_auta"]').val(auto.marka);
        	$('input[name="model_auta"]').val(auto.model);
        	$('input[name="godina_auta"]').val(auto.godina_proizvodnje);
        	$('input[name="sedista_auta"]').val(auto.broj_sedista);
        	$('input[name="tip_auta"]').val(auto.tip);
        	$('input[name="cena_auta"]').val(auto.cena_dan);
        	
        	
		}
    });
	
	
	
	$('#forma').submit(function(event) {
		event.preventDefault();
		let ispravno=true;
		
		let naziv = $('input[name="naziv_auta"]').val();
    	let marka = $('input[name="marka_auta"]').val();
    	let model = $('input[name="model_auta"]').val();
    	let godina = $('input[name="godina_auta"]').val();
    	let sedista = $('input[name="sedista_auta"]').val();
    	let tip = $('input[name="tip_auta"]').val();
    	let cena = $('input[name="cena_auta"]').val();
    	
		$('#uspesno').text("");
		$('#neuspesno').text("");
		
		if(!naziv || !marka || !model || !godina || !sedista || !tip || !cena){
			$('#neuspesno').text('All fields must be filled!');
			ispravno=false;
		}
		else
		{
			
		
			if(!(/[A-Z]/.test( naziv[0])))
			{
			$('#validacijaNaziv').text("First letter of the name must be capital!");
			ispravno=false;
			
			}
			else
			$('#validacijaNaziv').text("");
		
			
		
			if(!(/[A-Z]/.test( naziv[0])))
			{
			$('#validacijaMarka').text("First letter of the brand must be capital!");
			ispravno=false;
			
			}
			else
			$('#validacijaMarka').text("");
	
		}
	
		if(ispravno==true)
			{
			let token=localStorage.getItem('jwtToken');
			$.post({
				url: '/api/vozila/admin/izmenaVozila/' + id + '/'  + naziv + '/' + marka + '/' + model+ '/' + godina + '/' + sedista + '/' + tip + '/' + cena,
				headers: {"Authorization": "Bearer " + token},
				contentType: 'application/json',
				success: function(data) {
					if(data==null || data==""){
						//$('#validacijaEmail').text('User with this email already exists!');
						alert('Nije izmenjen!');
					}
					else {
						
						$('#uspesno').text('Car successfully edited!');
						$('#forma').hide();
						
									window.location.href="rentAdmin.html?id="+idr;
									
									
						
						
					}
				}
			
				
		});
			}
		
			
	});
			});
