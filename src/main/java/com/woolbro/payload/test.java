package com.woolbro.payload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class test {

	    public static void main(String[] args) {
	        
	        //create new ArrayList
	        ArrayList<String> aListColors 
	            = new ArrayList<String>();
	        
	        aListColors.add("Red");
	        aListColors.add("Green");
	        aListColors.add("Blue");
	        aListColors.add("Red");
	        aListColors.add("Yellow");
	        
	        
	        /*
	         * To get random item/element from ArrayList, use
	         * Random class along with get method of ArrayList
	         */
	        
	        Random random = new Random();
	        
	        //lets get 5 random elements from ArrayList
	        for(int i=0; i < 100; i++){
	            System.out.println(
	                    aListColors.get( random.nextInt(aListColors.size()) )
	                    );
	        }
	        
	    }

}
