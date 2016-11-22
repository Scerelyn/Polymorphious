package edu.neumont.csc110.EquationParsing;

import java.text.DecimalFormat;
import java.util.*;

//handles parsing the inputs of the user and prints stuff into the console
public class UserInterface {
	public static final int LOWER_BOUND_INDEX = 0, UPPER_BOUND_INDEX = 1;
	private ArrayList<Polynomial> allPolys = new ArrayList<Polynomial>();
	public static final DecimalFormat df3 = new DecimalFormat("0.###"); //most answers are only within 3 decimal place accuracy
	public static final DecimalFormat df2 = new DecimalFormat("0.##"); //for zeros, since for some reason they are quite inaccurate
	
	/**
	 * Verifies and returns a Double from an input String
	 * 
	 * @param input
	 *            The String to parse as the Double
	 * @return A Double. Returns null if the number is invalid
	 */
	public Double verifyDouble(String input){
		try{
			return Double.parseDouble(input);
		} catch(NumberFormatException e){
			System.out.println("Invalid number input");
			return null;
		}
	}
	
	/**
	 * Verifies and returns a pair of valid doubles for a bound from an input
	 * string
	 * 
	 * @param input
	 *            The string to parse as a bound, input as: [double,double] ex:
	 *            [1,2]
	 * @return A double array with both validated doubles. Returns null if
	 *         either number fails to validate
	 */
	public double[] verifyBounds(String input){
		try{
			double lowerBound = 0;
			double upperBound = 0;
			lowerBound = Double.parseDouble( input.substring(1, input.indexOf(",")) );
			upperBound = Double.parseDouble( input.substring(input.indexOf(",")+1, input.length()-1) );
			return new double[]{lowerBound,upperBound};
		} catch(NumberFormatException e){
			System.out.println("Invalid bounds: " + input);
			return null;
		}
	}
	
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
		poly.sortIntoStandardOrder();
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
			throw new IndexOutOfBoundsException("Invalid input string. Format should be: output <name> <x value> or output <x value>");
		}
		Double xVal = null; //using nulls for checks in validity. null = invalid number/input
		if(splitInput.length == 3){
			xVal = verifyDouble(splitInput[2]);
		}
		else {
			xVal = verifyDouble(splitInput[1]);
		}
		Polynomial poly = null; //null and uninstantiated are for some odd reason different. you'd think uninstantiated would be null on default
		if(xVal == null){
			return; //if the number fails to verify, end the method here
		}
		try{
			if(splitInput.length >= 3){
				poly = getPolynomialByName(splitInput[1],false);
			} else { //implied to be length of 2, or the exception is thrown beforehand
				poly = getPolynomialByName("",false);
			}
			System.out.println( "The value of " + poly.toString() + " at x = " + xVal + " is "
					+ df3.format( poly.getFunc().output(xVal) ) );
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
		if(splitInput.length != 3 && splitInput.length != 2){
			throw new IndexOutOfBoundsException("Invalid input string. Format should be: zero <name> [LowerBound,UpperBound] or zero [LowerBound,UpperBound]");
		}
		Polynomial poly = null;
		String boundString = "";
		if(splitInput.length == 3){
			poly = getPolynomialByName(splitInput[1],false);
			boundString = splitInput[2];
		} else {
			poly = getPolynomialByName("",false);
			boundString = splitInput[1];
		}
		double[] bounds = verifyBounds(boundString);
		if(bounds == null){ //invalid bounds
			return;
		}
		try{
			ArrayList<Double> zeros = poly.findAllZeroesInBound(bounds[LOWER_BOUND_INDEX], bounds[UPPER_BOUND_INDEX]);
			if (!zeros.isEmpty()) {
				System.out.print("Found zeros in bounds " + boundString + " at x values of: ");
				for (Double d : zeros) {
					System.out.print(df2.format(d) + ", ");
				}
				System.out.println();
			}
		} catch(NullPointerException e){
			if(splitInput.length == 3){
				System.out.println("No polynomial by the name of: " + splitInput[1]);
			} else {
				System.out.println("No default polynomial found");
			}
		}
	}
	
	/**
	 * Gives the numerical integral of the given polynomial over given bounds
	 * 
	 * @param input
	 *            The input string to parse and use
	 */
	public void printIntegral(String input){
		String[] splitInput = input.split(" ");
		if(splitInput.length != 3 && splitInput.length != 2){
			throw new IndexOutOfBoundsException("Invalid input string. Format should be: integrate <name> [LowerBound,UpperBound] or integrate [LowerBound,UpperBound]");
		}
		Polynomial poly = null;
		String boundString = "";
		if(splitInput.length == 3){
			poly = getPolynomialByName(splitInput[1],false);
			boundString = splitInput[2];
		} else {
			poly = getPolynomialByName("",false);
			boundString = splitInput[1];
		}
		double[] bounds = verifyBounds(boundString);
		if(bounds == null){ //invalid bounds
			return;
		}
		try{
			double area = poly.simpsons(bounds[LOWER_BOUND_INDEX], bounds[UPPER_BOUND_INDEX],200);
			System.out.println("The integral of " + poly + " over bounds " + boundString + " is " + df3.format(area));
		} catch(NullPointerException e){
			if(splitInput.length == 3){
				System.out.println("No polynomial by the name of: " + splitInput[1]);
			} else {
				System.out.println("No default polynomial found");
			}
		}
	}
	
	public void printDerivative(String input){
		String[] splitInput = input.split(" ");
		if(splitInput.length != 3 && splitInput.length != 2){
			throw new IndexOutOfBoundsException("Invalid input string. Format should be: integrate <name> [LowerBound,UpperBound] or integrate [LowerBound,UpperBound]");
		}
		Polynomial poly = null;
		Double xVal = null; //using nulls for checks in validity. null = invalid number/input
		if(splitInput.length == 3){
			xVal = verifyDouble(splitInput[2]);
			poly = getPolynomialByName(splitInput[1],false);
		}
		else {
			xVal = verifyDouble(splitInput[1]);
			poly = getPolynomialByName("",false);
		}
		if(xVal == null){
			return; //if the number fails to verify, end the method here
		}
		try{
			System.out.println("The numerical derivative of " + poly.toString() + " at x = " + xVal + " is " + df3.format( poly.differentiate(xVal) ));
		} catch(NullPointerException e){
			if(splitInput.length == 3){
				System.out.println("No polynomial by the name of: " + splitInput[1]);
			} else {
				System.out.println("No default polynomial found");
			}
		}
	}
}
