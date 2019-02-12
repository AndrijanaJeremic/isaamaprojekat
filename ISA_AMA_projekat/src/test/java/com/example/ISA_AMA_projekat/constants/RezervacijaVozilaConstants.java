package com.example.ISA_AMA_projekat.constants;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class RezervacijaVozilaConstants {
	public static final Calendar dateCalendar;
	
	
	public static final Integer NEW_ID_RV = 3;
    public static final boolean NEW_AKTIVIRANA_RV = true;
    public static final boolean NEW_BRZA_RV = false;
    public static final int NEW_BR_PUTNIKA_RV = 4;
    public static final double NEW_POPUST_RV = 0;
    public static final double NEW_CENA_RV = 4;
    public static final Integer NEW_ID_PREUZ_RV = 3;
    public static final Integer NEW_ID_VRACA_RV = 3;
    public static final Integer NEW_ID_VOZILO_RV = 2;
    static {
	    dateCalendar = GregorianCalendar.getInstance();
	    dateCalendar.clear();
	    dateCalendar.set(2019, 1, 10);
	}
    public static final Date NEW_DAT_PREUZ_RV = dateCalendar.getTime();  
    
    static
    {
    	dateCalendar.clear();
    	dateCalendar.set(2019,1,25);
    }
    public static final Date NEW_DAT_VRACA_RV = dateCalendar.getTime();  
   
    
    public static final Integer DB_ID_RV = 1;
    public static final boolean DB_AKTIVIRANA_RV = true;
    public static final boolean DB_BRZA_RV = false;
    public static final int DB_BR_PUTNIKA_RV = 3;
    public static final double DB_POPUST_RV = 0;
    public static final double DB_CENA_RV = 1105;
    static {
	   
	    dateCalendar.clear();
	    dateCalendar.set(2019, 4, 3);
	}
    public static final Date DB_DAT_PREUZ_RV = dateCalendar.getTime();  
    
    static
    {
    	dateCalendar.clear();
    	dateCalendar.set(2019,4,11);
    }
    public static final Date DB_DAT_VRACA_RV = dateCalendar.getTime();  
    
    public static final int DB_COUNT_RV = 2;
   

}
