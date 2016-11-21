package edu.neumont.csc110.EquationParsing;

import java.util.*;

//handles parsing the inputs of the user and prints stuff into the console
public class UserInterface {
	
	ArrayList<Polynomial> allPolys = new ArrayList<Polynomial>();
	
	/**
	 * Receives an create input string, assuming it is a valid create input, and
	 * makes a polynomial from it
	 * 
	 * @param input
	 *            The user input string. Consult Userhelp.txt for input formats
	 * @return A built polynomial if input is valid
	 */
	public Polynomial createPolynomial(String input){
		Polynomial poly = null; 
		String[] splitInput = input.trim().split(" ");
		System.out.println(splitInput.length);
		if(splitInput.length != 3 && splitInput.length != 2){ //going to be strict here
			throw new IndexOutOfBoundsException("Invalid input string. Format should be: create <name> <polynomial entry> or create <polynomial entry>");
		}
		if(splitInput.length == 2){
			poly = new Polynomial("",splitInput[1]);
			allPolys.remove( getPolynomialByName("",true) ); //overriding the polynomial, only one poly per name
			allPolys.add(poly);
		}
		else if(splitInput.length == 3){
			poly = new Polynomial(splitInput[1],splitInput[2]);
			allPolys.remove( getPolynomialByName(splitInput[1],true) );
			allPolys.add(poly);
		}
		else {
			System.out.println("Invalid input, reenter and try again");
		}
		return poly;
	}
	
	/**
	 * Finds and returns the polynomial in the allPolys list by the name given
	 * 
	 * @param name
	 *            The name to search for
	 * @param supressPrint
	 *            Whether or not to suppress the error print out, as sometimes
	 *            the null finding is a valid non error result
	 * 
	 * @return The matching polynomial. Returns null if no such polynomial us
	 *         found
	 */
	public Polynomial getPolynomialByName(String name, boolean suppressPrint){
		for(Polynomial poly : allPolys){
			if(poly.getName().equals(name)){
				return poly;
			}
		}
		if(!suppressPrint){
			System.out.println("No polynomial was found named: " + name);
		}
		return null;
	}
	
	/**
	 * Receives an input string formatted for the output command, then outputs
	 * the output of the given polynomial at the given x value
	 * 
	 * @param input
	 *            The input string
	 */
	public void printOutput(String input){
		String[] splitInput = input.trim().split(" ");
		if(splitInput.length != 3 && splitInput.length != 2){ //going to be strict here
			throw new IndexOutOfBoundsException("Invalid input string. Format should be: output <name> <x value> or create <x value>");
		}
		double xVal;
		Polynomial poly = null; //null and uninstantiated are for some odd reason different. you'd think uninstantiated would be null on default
		try{
			xVal = Double.parseDouble(splitInput[2]);
		} catch(NumberFormatException e){
			System.out.println("Invalid x value input: " + splitInput[2]);
			return; //no point in continuing the method from here
		}
		try{
			if(splitInput.length >= 3){
				poly = getPolynomialByName(splitInput[1],false);
			} else { //implied to be length of 2, or the exception is thrown beforehand
				poly = getPolynomialByName("",false);
			}
			System.out.println( "The value of " + poly.toString() + " at x = " + xVal + " is "
					+ poly.getFunc().output(xVal) );
		} catch(NullPointerException e){
			if(splitInput.length == 3){
				System.out.println("No such polynomial by the name of: " + splitInput[1]);
			} else { 
				System.out.println("No default polynomial found");
			}
			return;
		}
	}
	
	
	public void printZero(String input){
		String[] splitInput = input.split(" ");
		if(splitInput.length != 3 && splitInput.length != 2){ //going to be strict here
			throw new IndexOutOfBoundsException("Invalid input string. Format should be: zero <name> [LowerBound,UpperBound] or zero [LowerBound,UpperBound]");
		}
		Polynomial poly = null;
		String boundString = "";
		double lowerBound = 0;
		double upperBound = 0;
		if(splitInput.length == 3){
			poly = getPolynomialByName(splitInput[1],false);
			boundString = splitInput[2];
		} else {
			poly = getPolynomialByName("",false);
			boundString = splitInput[1];
		}
		try{
			lowerBound = Double.parseDouble( boundString.substring(1, boundString.indexOf(",")) );
			upperBound = Double.parseDouble( boundString.substring(boundString.indexOf(",")+1, boundString.length()-1) );
		} catch(NumberFormatException e){
			System.out.println("Invalid bounds: " + boundString);
		}
		try{
			Double zeroLoc = poly.findAZero(lowerBound, upperBound);
			if(zeroLoc != null){
				System.out.println("A zero within the bounds has been found at: " + poly.findAZero(lowerBound, upperBound));
			}
		} catch(NullPointerException e){
			if(splitInput.length == 3){
				System.out.println("No polynomial by the name of: " + splitInput[1]);
			} else {
				System.out.println("No default polynomial found");
			}
		}
	}
}
