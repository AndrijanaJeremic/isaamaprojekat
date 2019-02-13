package com.example.ISA_AMA_projekat.constants;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class RezervacijaHotelConstants {

	public static final Calendar dateCalendar;

	static {
	    dateCalendar = GregorianCalendar.getInstance();
	    dateCalendar.clear();
	    dateCalendar.set(2019, 2, 7);
	}
	
	public static final Date DB_DATE = dateCalendar.getTime();
	
	public static final Integer DB_ID = 1;  
	public static final Integer DB_ID_3 = 1;  
    public static final Integer DB_USLUGA_ID = 1;  
    public static final int DB_BROJ_NOCENJA = 1;
	public static final boolean DB_BRZA = false;
	public static final boolean DB_AKTIVIRANA = false;
	public static final boolean DB_AKTIVIRANA_TRUE = true;
	public static final String DB_DATE_CHECK_IN = "2019-02-07";
	

	public static final double DB_UKUPNA_CENA_PRE = 10;
	public static final double DB_POPUST = 0;
}
