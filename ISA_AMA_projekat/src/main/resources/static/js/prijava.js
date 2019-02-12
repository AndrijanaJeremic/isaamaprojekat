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
	var token=localStorage.getItem('jwtToken');
	"use strict";

	/* 

	1. Vars and Inits

	*/
	$('input[name=new_password]').hide();
	$('input[name=confirm_new_password]').hide();
	$('input[name=lozinka]').show();
	$('#form_submit_button').val('Login');
	
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
	
	
	
	$('#formaPrijava').submit(function(event) {
		event.preventDefault();
		let ispravno=true;
		let email = $('input[name="email"]').val();
		let lozinka = $('input[name="lozinka"]').val();
		$('#uspesno').text("");
		$('#neuspesno').text("");
		$('#validacijaLozinka').text("");
		
		
		if(!email || !lozinka){
			$('#neuspesno').text('All fields must be filled!');
			ispravno=false;
		}
		
			
		if(ispravno==true)
		{
			if($('#form_submit_button').val() == 'Change password and login')
			{
				console.log('menjanje lozinke');
				
				let check = true;
				
				let new_pass = $('input[name="new_password"]').val();
				let confirm_new_pass = $('input[name="confirm_new_password"]').val();
				
				if(!(new_pass == confirm_new_pass))
				{
					$('#validacijaLozinka').text("The password must be the same!");
					check = false;
				}
				else
					$('#validacijaLozinka').text("");
				
				if(check == true)
				{
					$.post({
						url: "/auth/first_admin_login/" + new_pass,
						data: JSON.stringify({email: email, lozinka: lozinka}),
						contentType: 'application/json',
						headers: {"Authorization": "Bearer " + token},
						  
						success: function(res) {
							if(res!="")
							{
								localStorage.setItem('jwtToken', res.accessToken);
								$('#neuspesno').text("");
								$('#uspesno').text('User successfully logged in! Enjoy!');
								$('#formaPrijava').hide();
								$("#uspesno").show().delay(1500).fadeOut(function(){
									window.location.href="index.html";
								});
							}
							else
							{
								$('#neuspesno').text("The new password is the same as the previous.");
								localStorage.removeItem('jwtToken');
							}
								
						}
					});
				}
				
			}
			else
			{
				$.post({
					url: "/auth/login",
					data: JSON.stringify({email: email, lozinka: lozinka}),
					contentType: 'application/json',
					headers: {"Authorization": "Bearer " + token},
					  
					success: function(res) {
						if(res!="" && res.accessToken != null)
						{
							localStorage.setItem('jwtToken', res.accessToken);
							$('#neuspesno').text("");
							$('#uspesno').text('User successfully logged in! Enjoy!');
							$('#formaPrijava').hide();
							$("#uspesno").show().delay(1500).fadeOut(function(){
								window.location.href="index.html";
							});
						}
						else if(res!="" && res.id != null)
						{
							console.log('admin se loguje prvi put');
							$('#neuspesno').text("You have to change your password, because it's your first time to login.");
							$('input[name=new_password]').show();
							$('input[name=confirm_new_password]').show();
							$('input[name=lozinka]').hide();
							$('input[name=new_password]').prop('required',true);
							$('input[name=confirm_new_password]').prop('required',true);
							$('#form_submit_button').val('Change password and login');
						}
						else
						{
							$('#neuspesno').text("Incorrect email or password or you did not activate your profile with link you received on email!");
							localStorage.removeItem('jwtToken');
						}
							
					}
				});
			}
				
		}
	});
	
});
