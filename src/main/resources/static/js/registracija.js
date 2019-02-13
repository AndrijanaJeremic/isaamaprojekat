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
	
	$('#forma').submit(function(event) {
		event.preventDefault();
		let ispravno=true;
		let email = $('input[name="email"]').val();
		let lozinka = $('input[name="lozinka"]').val();
		let password2 = $('input[name="lozinkaPonovo"]').val();
		let ime = $('input[name="ime"]').val();
		let prezime = $('input[name="prezime"]').val();
		let grad = $('input[name="grad"]').val();
		let telefon = $('input[name="telefon"]').val();
		$('#validacijaEmail').text("");
		$('#uspesno').text("");
		$('#neuspesno').text("");
		
		
		
		if(!email || !lozinka || !password2 || !ime || !prezime || !telefon || !grad){
			$('#neuspesno').text('All fields must be filled!');
			ispravno=false;
		}
		else
		{
			if(!(/^[a-zA-ZćĆčČšŠđĐžŽ]+$/.test(ime)))
			{
				$('#validacijaIme').text("First name must contains the letters!");
				ispravno=false;
			}
			else if(!(/[A-Z]/.test( ime[0])))
			{
				$('#validacijaIme').text("First letter of name must be capital!");
				ispravno=false;
			}
			else
				$('#validacijaIme').text("");
			
		
			if(!(/^[a-zA-ZćĆčČšŠđĐžŽ]+$/.test(prezime)))
			{
				$('#validacijaPrezime').text("Last name must contains the letters!");
				ispravno=false;
			}
			else if(!(/[A-Z]/.test( prezime[0])))
			{
				$('#validacijaPrezime').text("First letter of last name must be capital!");
				ispravno=false;
			}
			else
				$('#validacijaPrezime').text("");
			
		
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
			
			
			if(!(/^[0-9]+$/.test(telefon)))
			{
				$('#validacijaTelefon').text("Field Phone number must contains only numbers!");
				ispravno=false;
			}
			else
				$('#validacijaTelefon').text("");
		
			
			if(!(lozinka==password2))
			{
				$('#validacijaLozinka').text("The password must be the same!");
				ispravno=false;
			}
			else
				$('#validacijaLozinka').text("");
			
			
		}
		
		if(ispravno==true)
		{
			$.post({
				url: "/api/users/registruj",
				data: JSON.stringify({email: email, lozinka: lozinka, ime: ime, prezime: prezime, grad:grad, telefon: telefon}),
				contentType: 'application/json',
				success: function(data) {
					if(data==null || data==""){
						$('#validacijaEmail').text('User with this email already exists!');
					}
					else {
						//sessionStorage.setItem('ulogovan',JSON.stringify(data));
						$('#validacijaEmail').text("");
						$('#uspesno').text('User successfully registred! In few seconds, you will recieve email with link to confirm your registration. Enjoy in travels!');
						$('#forma').hide();
						//window.location.href="index.html";
					
					}
				}
			
				
			});
		}
	
});
});