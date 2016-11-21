package edu.neumont.csc110.EquationParsing;

import java.util.*;

//handles parsing the inputs of the user and prints stuff into the console
public class UserInterface {
	
	ArrayList<Polynomial> allPolys = new ArrayList<Polynomial>();
	
	/**
	 * 
	 * @param input The user input string. Consult Userhelp.txt for input formats
	 * @return A built polynomial if input is valid
	 */
	public Polynomial createPolynomial(String input){
		String[] splitInput = input.split(" ");
		if(splitInput.length == 2){
			return new Polynomial("",splitInput[1]);
		}
		else if(splitInput.length == 3){
			return new Polynomial(splitInput[1],splitInput[2]);
		}
		else {
			System.out.println("Invalid input, reenter and try again");
			return null;
		}
	}
	
	
}
