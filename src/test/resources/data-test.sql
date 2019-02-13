
insert into grad (id, naziv, verzija) values (1, 'Grad1', 0);
insert into grad (id, naziv, verzija) values (2, 'Grad2', 0);
insert into grad (id, naziv, verzija) values (3, 'Grad3', 0);
insert into grad (id, naziv, verzija) values (4, 'Grad4', 0);
insert into grad (id, naziv, verzija) values (5, 'Grad5', 0);
insert into grad (id, naziv, verzija) values (6, 'Novi Sad', 0);
insert into grad (id, naziv, verzija) values (7, 'Stepanovicevo', 0);

insert into adresa (id, grad_id, ulica, broj, latitude, longitude, verzija) values (1, 1, 'Ulica1', '1', 1.1, 1.1, 0);
insert into adresa (id, grad_id, ulica, broj, latitude, longitude, verzija) values (2, 2, 'Ulica2', '2', 2.2, 2.2, 0);
insert into adresa (id, grad_id, ulica, broj, latitude, longitude, verzija) values (3, 3, 'Ulica3', '3', 3.3, 3.3, 0);
insert into adresa (id, grad_id, ulica, broj, latitude, longitude, verzija) values (4, 4, 'Ulica4', '4', 4.4, 4.4, 0);
insert into adresa (id, grad_id, ulica, broj, latitude, longitude, verzija) values (5, 5, 'Ulica5', '5', 5.5, 5.5, 0);
insert into adresa (id, grad_id, ulica, broj, latitude, longitude, verzija) values (6, 7, 'Njegoseva', '46', 19.36, 42.58, 0);

insert into rentacar_servis (id, naziv, adresa_id, promotivni_opis, prosecna_ocena, verzija) values (1, 'Rent1', 1, 'Rent opis 1', 3.2, 0);
insert into rentacar_servis (id, naziv, adresa_id, promotivni_opis, prosecna_ocena, id_admin, verzija) values (2, 'Rent2', 2, 'Rent opis 2', 4.2, 1, 0);
insert into rentacar_servis (id, naziv, adresa_id, promotivni_opis, prosecna_ocena, verzija) values (3, 'Rent3', 3, 'Rent opis 3', 3.5, 0);

insert into filijala (id, adresa_id, rentacar_id, verzija) values (1, 1, 1, 0);
insert into filijala (id, adresa_id, rentacar_id, verzija) values (2, 4, 1, 0);
insert into filijala (id, adresa_id, rentacar_id, verzija) values (3, 5, 1, 0);

insert into vozilo (id, naziv, marka, model, godina_proizvodnje, broj_sedista, tip, cena_dan, prosecna_ocena, filijala_id, cena_popust, zauzeto, verzija) values (1, 'Vozilo1', 'V1', 'V', 2011, 5, 'Tip vozila 1', 400,4.5, 1 , 0, false, 0);
insert into vozilo (id, naziv, marka, model, godina_proizvodnje, broj_sedista, tip, cena_dan, prosecna_ocena, filijala_id, cena_popust, zauzeto, verzija) values (2, 'Vozilo2', 'V2', 'VV', 2011, 5, 'Tip vozila 2', 400,4.5, 1 , 0, false, 0);


insert into rezervacija_vozila (id, aktivirana, broj_putnika, brza, datum_preuzimanja, datum_vracanja, popust, ukupna_cena, mesto_preuzimanja_id, mesto_vracanja_id, vozilo_id, verzija) values (1, true, 3, false, '2019-03-03', '2019-03-11', 0, 1105, 1, 1, 1, 0);
insert into rezervacija_vozila (id, aktivirana, broj_putnika, brza, datum_preuzimanja, datum_vracanja, popust, ukupna_cena, mesto_preuzimanja_id, mesto_vracanja_id, vozilo_id, verzija) values (2, true, 3, false, '2019-05-03', '2019-05-11', 0, 1105, 1, 1, 2, 0);


insert into hotel (id, naziv, promotivni_opis, prosecna_ocena, verzija) values (1, 'Hotel Park', 'hotel sa 4 zvezdice', 4.8, 0);
insert into hotel (id, naziv, promotivni_opis, prosecna_ocena, verzija) values (2, 'Hotel Novi Sad', 'fantastican hotel', 3.5, 0);
insert into hotel (id, naziv, promotivni_opis, prosecna_ocena, verzija) values (3, 'Hotel Moskva', 'Odlican hotel', 4.8, 0);

insert into AUTHORITY (id, name) values (3, 'ROLE_HOTELADMIN');


insert into korisnik (id, email, lozinka, ime, prezime, grad_id, telefon, bonuspoeni, aktiviran, admin_id, verzija) values (3, 'makaric.milica@gmail.com', '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'Milica', 'Makaric', 7, '0652034133', 5, false, 3, 0);
insert into user_authority (user_id, authority_id) values (3, 3);
update hotel h set h.id_admin = 3 where h.id = 3;

insert into soba (id, prosecna_ocena, cena_nocenja, broj_kreveta, hotel_id, opis, cena_popust, zauzeta, verzija) values (1, 4.8, 120, 3, 3, 'Apartman', 0, false, 0);
insert into soba (id, prosecna_ocena, cena_nocenja, broj_kreveta, hotel_id, opis, cena_popust, zauzeta, verzija) values (2, 3.77, 53, 2, 3, 'Soba na drugom spratu', 0, false, 0);

insert into rezervacija_hotel (id, datum_dolaska, datum_odlaska, soba_id, broj_nocenja, brza, ukupna_cena, aktivirana, popust, verzija) values (1, '2019-02-03', '2019-02-06', 1, 0, false, 130, true, 0, 0);
insert into rezervacija_hotel (id, datum_dolaska, datum_odlaska, soba_id, broj_nocenja, brza, ukupna_cena, aktivirana, popust, verzija) values (2, '2019-02-15', '2019-02-25', 1, 11, false, 1500, true, 0, 0);

insert into soba_rezervacije (soba_id, rezervacije_id) values (1, 1);
insert into soba_rezervacije (soba_id, rezervacije_id) values (1, 2);

insert into popust (id, pocetak_vazenja, kraj_vazenja, popust, verzija) values (2, '2019-02-16', '2019-02-20', 30, 0);
insert into popust (id, pocetak_vazenja, kraj_vazenja, popust, verzija) values (1, '2019-01-07', '2019-01-08', 20, 0);

insert into soba_popusti (soba_id, popusti_id) values (1, 1);
insert into soba_popusti (soba_id, popusti_id) values (1, 2);

insert into usluga (id, naziv, cena, popust, verzija) values (1, 'air-conditioner', 5, 0, 0);
insert into usluga (id, naziv, cena, popust, verzija) values (2, 'Wi-Fi', 2, 0, 0);


insert into bonus (id, bonus_poeni, popust, verzija) values (2, 13, 3, 0);
insert into bonus (id, bonus_poeni, popust, verzija) values (1, 15, 5, 0);