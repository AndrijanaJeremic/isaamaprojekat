package com.example.ISA_AMA_projekat.constants;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class HotelConstants {
	
    public static final String NEW_NAZIV = "Hotel Palisad";
    public static final String NEW_NAZIV_NEG = "Hotel Park";
    public static final String NEW_PROMOTIVNI_OPIS = "Hotel sa 5 zvezdica";
    public static final double NEW_PROSECNA_OCENA = 4.8;
    public static final Integer NEW_ID = 4;
    
    public static final Integer DB_ID = 3;
    public static final String DB_NAZIV = "Hotel Moskva";
    public static final String DB_PROMOTIVNI_OPIS = "Odlican hotel";
    public static final double DB_PROSECNA_OCENA = 4.8;
    
    public static final int DB_ADULTS = 3;
    
    public static final int DB_COUNT_HOTELS = 3;
    public static final int DB_COUNT_HOTELS_ADMINS = 2;
    public static final int DB_COUNT_SEARCH = 0;
    
    public static final String DB_NAZIV_SEARCH = "Hotel";
    
    public static final Calendar dateCalendar;

	static {
	    dateCalendar = GregorianCalendar.getInstance();
	    dateCalendar.clear();
	    dateCalendar.set(2019, 2, 7);
	}
	
	public static final Date DB_DATE = dateCalendar.getTime();
	public static final String DB_DATE_CHECK_IN = "2019-02-07";
	public static final String DB_DATE_CHECK_OUT = "2019-02-17";
	
	public static final Integer DB_ADMIN_ID = 3;
	public static final Integer DB_ADMIN_ID_FALSE = 2;
	 
	public static final String DB_POPUST_CHECK_IN = "2019-01-07";
	public static final String DB_POPUST_CHECK_OUT = "2019-01-08";
	
	public static final String DB_POPUST_CHECK_IN_NEG = "2019-02-16";
	public static final String DB_POPUST_CHECK_OUT_NEG = "2019-02-20";
	
	public static final int DB_COUNT_POPUST = 1;
	public static final int DB_COUNT_POPUST_NEG = 0;
	
	public static final Integer DB_NUMBER_OF_DAYS = 11;

	public static final Integer DB_ID_EDIT = 2;
	
	public static final String NEW_NAZIV1 = "Hotel novi";
	
	public static final String SEARCH = "Hotel";
	public static final int SEARCH_SIZE = 3;
	
	public static final Integer ID = 3;
	public static final Integer ADMIN_ID = 3;
	public static final Integer ADMIN_ID_CHANGED = 2;
}
