package edu.neumont.csc110.EquationParsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;

//handles parsing the inputs of the user and prints stuff into the console
public class UserInterface {
	public static final int LOWER_BOUND_INDEX = 0, UPPER_BOUND_INDEX = 1;
	public static final double PLOTTING_STEP_SIZE = 1E-1; //doesnt do much for continuity though :c
	private ArrayList<Polynomial> allPolys = new ArrayList<Polynomial>();
	public static final DecimalFormat df3 = new DecimalFormat("0.###"); //most answers are only within 3 decimal place accuracy
	
	/**
	 * Verifies and returns a Double from an input String
	 * 
	 * @param input
	 *            The String to parse as the Double
	 * @return A Double of value shown by the input string. Returns null if the
	 *         number is invalid
	 */
	public Double verifyDouble(String input){ //CSC101 Requirement 12: User input validation
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
		} catch(NumberFormatException | StringIndexOutOfBoundsException e){
			System.out.println("Invalid bounds: " + input);
			return null;
		} 
	}
	
	/**
	 * Verifies and returns a pair of validated integers for a bound from an
	 * input string given
	 * 
	 * @param input
	 *            The string to parse as a bound to use. Input as :[integer,
	 *            integer], ex: [1,2]
	 * @return An integer array of both validated integers. Returns null if
	 *         either is invalid.
	 */
	public int[] verifyBoundsInt(String input){
		try{
			int lowerBound = 0;
			int upperBound = 0;
			lowerBound = Integer.parseInt( input.substring(1, input.indexOf(",")) );
			upperBound = Integer.parseInt( input.substring(input.indexOf(",")+1, input.length()-1) );
			return new int[]{lowerBound,upperBound};
		} catch(NumberFormatException | StringIndexOutOfBoundsException e){
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
	 * @throws InvalidFormatException
	 *             If the input string is invalid, meaning too many or too
	 *             little information that is needed
	 */
	public Polynomial createPolynomial(String input) throws InvalidFormatException{
		Polynomial poly = null; 
		String[] splitInput = input.trim().split(" ");
		//CSC101 Requirement 3: Branching a) if b) else if
		if(splitInput.length != 3 && splitInput.length != 2){ //going to be strict here
			throw new InvalidFormatException("Invalid input string. Format should be: create <name> <polynomial entry> or create <polynomial entry>");
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
			if(!name.isEmpty()){
				System.out.println("No polynomial was found named: " + name);
			} else {
				System.out.println("No default polynomial found");
			}
		}
		return null;
	}
	
	/**
	 * Receives an input string formatted for the output command, then outputs
	 * the output of the given polynomial at the given x value
	 * 
	 * @param input
	 *            The input string
	 * @throws InvalidFormatException 
	 *             If the input string is invalid, meaning too many or too
	 *             little information that is needed
	 */
	public void printOutput(String input) throws InvalidFormatException{
		String[] splitInput = input.trim().split(" ");
		if(splitInput.length != 3 && splitInput.length != 2){ //going to be strict here
			throw new InvalidFormatException("Invalid input string. Format should be: output <name> <x value> or output <x value>");
		}
		Double xVal = null; //using nulls for checks in validity. null = invalid number/input
		Polynomial poly = null; //null and uninstantiated are for some odd reason different. you'd think uninstantiated would be null on default
		if(splitInput.length == 3){
			xVal = verifyDouble(splitInput[2]);
			poly = getPolynomialByName(splitInput[1],false);
		}
		else {
			xVal = verifyDouble(splitInput[1]);
			poly = getPolynomialByName("",false);
		}
		if(xVal == null || poly == null){
			return; //if the number fails to verify, end the method here
		}
		System.out.println( "The value of " + poly.toString() + " at x = " + xVal + " is " + df3.format( poly.getFunc().output(xVal) ) );
	}
	
	/**
	 * Find and prints the zeros within the given bounds of the given polynomial
	 * 
	 * @param input
	 *            The string input telling the polymonial and bounds to use
	 * @throws InvalidFormatException 
	 *             If the input string is invalid, meaning too many or too
	 *             little information that is needed
	 */
	public void printZero(String input) throws InvalidFormatException{
		String[] splitInput = input.trim().split(" ");
		if(splitInput.length != 3 && splitInput.length != 2){
			throw new InvalidFormatException("Invalid input string. Format should be: zero <name> [LowerBound,UpperBound] or zero [LowerBound,UpperBound]");
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
		if(bounds == null || poly == null){ //invalid bounds
			return;
		}
		ArrayList<Double> zeros = poly.findAllZeroesInBoundNewtons(bounds[LOWER_BOUND_INDEX], bounds[UPPER_BOUND_INDEX]);
		if(zeros == null){
			return;
		}
		if (!zeros.isEmpty()) {
			System.out.print("Found zeros in bounds " + boundString + " at x values of: ");
			for (Double d : zeros) {
				System.out.print(df3.format(d) + ", ");
			}
			System.out.println();
		} else {
			System.out.println("Found no zeroes in the given bound");
		}
	}
	
	/**
	 * Gives the numerical integral of the given polynomial over given bounds
	 * 
	 * @param input
	 *            The input string to parse and use
	 * @throws InvalidFormatException 
	 *             If the input string is invalid, meaning too many or too
	 *             little information that is needed
	 */
	public void printIntegral(String input) throws InvalidFormatException{
		String[] splitInput = input.trim().split(" ");
		if(splitInput.length != 3 && splitInput.length != 2){
			throw new InvalidFormatException("Invalid input string. Format should be: integrate <name> [LowerBound,UpperBound] or integrate [LowerBound,UpperBound]");
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
		if(bounds == null || poly == null){ //invalid bounds
			return;
		}
		double area = poly.simpsons(bounds[LOWER_BOUND_INDEX], bounds[UPPER_BOUND_INDEX],200);
		System.out.println("The integral of " + poly + " over bounds " + boundString + " is " + df3.format(area));		
	}
	
	/**
	 * Finds and prints the derivative of the given polynomial at the given
	 * point
	 * 
	 * @param input
	 *            The string input telling the polynomial and x value to use
	 * @throws InvalidFormatException 
	 *             If the input string is invalid, meaning too many or too
	 *             little information that is needed
	 */
	public void printDerivative(String input) throws InvalidFormatException{
		String[] splitInput = input.trim().split(" ");
		if(splitInput.length != 3 && splitInput.length != 2){
			throw new InvalidFormatException("Invalid input string. Format should be: integrate <name> [LowerBound,UpperBound] or integrate [LowerBound,UpperBound]");
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
		if(xVal == null || poly == null){
			return; //if the number or polynomial fail to verify, end the method here
		}
		System.out.println("The numerical derivative of " + poly.toString() + " at x = " + xVal + " is " + df3.format( poly.differentiate(xVal) ));
	}
	
	/**
	 * Finds and prints extrema of the function within a given bound
	 * 
	 * @param input
	 *            The input string to parse from
	 * @throws InvalidFormatException
	 *             If the string input is invalid
	 */
	public void printExtrema(String input) throws InvalidFormatException{ //after a few times you can really see that most of these methods is verification, with minimal changes
		String[] splitInput = input.trim().split(" ");
		if(splitInput.length != 3 && splitInput.length != 2){
			throw new InvalidFormatException("Invalid input string. Format should be: extrema <name> [LowerBound,UpperBound] or extrema [LowerBound,UpperBound]");
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
		if(bounds == null || poly == null){ //invalid bounds
			return;
		}
		ArrayList<Double> extrema = poly.findExtrema(bounds[LOWER_BOUND_INDEX],bounds[UPPER_BOUND_INDEX]);
		if(extrema == null){
			return;
		}
		System.out.println("Extrema found: " + extrema.size());
		System.out.println("Absolute minimum at x = " + df3.format(extrema.get(0)) + " with value of " + df3.format( poly.getFunc().output(extrema.get(0))) );
		System.out.println("Absolute maximum at x = " + df3.format( extrema.get(extrema.size()-1) ) + " with value of " + df3.format(  poly.getFunc().output( extrema.get(extrema.size()-1) ))  );
		System.out.println("Other extrema: ");
		for(Double extremaLoc : extrema){
			double concavity = poly.getDerivativePolynomial().differentiate(extremaLoc.doubleValue()); //getting nth derivatives is easy, just spam getDerivativePolynomial()
			if(concavity == 0){ //using concavity test for determining if its a min or a max value. only one number needed for this test
				System.out.println("Inflection point at x = " + df3.format( extremaLoc ) + " of value " + df3.format( poly.getFunc().output(extremaLoc)) );
			} else if(concavity < 0){
				System.out.println("Maximum value at x = " + df3.format( extremaLoc ) + " of value " + df3.format( poly.getFunc().output(extremaLoc)) );
			} else {
				System.out.println("Minimum value at x = " + df3.format( extremaLoc ) + " of value " + df3.format( poly.getFunc().output(extremaLoc)) );
			}
		}
	}

	/**
	 * Plots the polynomial over given bounds
	 * 
	 * @param input
	 *            A string input to parse from, in format: plot <polynomial>
	 *            [xLower,xUpper] [yLower,yUpper]
	 * @throws InvalidFormatException
	 *             If the input string is invalid
	 */
	public void printPolyOnBounds(String input) throws InvalidFormatException{
		String[] splitInput = input.trim().split(" ");
		if(splitInput.length != 3 && splitInput.length != 4){
			throw new InvalidFormatException("Invalid input string. Format should be: plot <name> [xLower,xUpper] [yLower,yUpper] or plot [xLower,xUpper] [yLower,yUpper]. Integer bounds only");
		}
		String xBoundString = "", yBoundString = "";
		Polynomial poly = null; //still have no idea why default isnt null
		if(splitInput.length == 3){
			xBoundString = splitInput[1];
			yBoundString = splitInput[2];
			poly = getPolynomialByName("",false);
		} else { //length == 4
			xBoundString = splitInput[2];
			yBoundString = splitInput[3];
			poly = getPolynomialByName(splitInput[1],false);
		}
		int[] xBounds = verifyBoundsInt(xBoundString); //ints because setting steps for plotting is a difficult pain to deal with
		int[] yBounds = verifyBoundsInt(yBoundString);
		if(xBounds == null || yBounds == null || poly == null){
			return; //cant do much with nulls
		}
		if(xBounds[UPPER_BOUND_INDEX] < xBounds[LOWER_BOUND_INDEX] || yBounds[UPPER_BOUND_INDEX] < yBounds[LOWER_BOUND_INDEX]){
			System.out.println("Invalid bounds, they should be reversed");
			return;
		}
		int xLength = (int)(xBounds[UPPER_BOUND_INDEX] - xBounds[LOWER_BOUND_INDEX]) + 1; //plus one since the outer bound spot wouldnt be counted otherwise
		int yLength = (int)(yBounds[UPPER_BOUND_INDEX] - yBounds[LOWER_BOUND_INDEX]) + 1;
		boolean[][] plane = new boolean[yLength][xLength]; //boolean since values are either: a point here or a point isnt here
		for(double x = xBounds[LOWER_BOUND_INDEX]; x <= xBounds[UPPER_BOUND_INDEX] + PLOTTING_STEP_SIZE; x+= PLOTTING_STEP_SIZE){ //the + 0.1 on the conditional is to make sure the last corner point is plotted
			try{
				double output = poly.getFunc().output(x); //will be a bit off
				if(x < 0){
					plane[ (int)Math.round(output) - yBounds[LOWER_BOUND_INDEX] ][ (int)Math.round(x) - xBounds[LOWER_BOUND_INDEX] ] = true; //converted from view bounds to array bounds
				}
				else {
					plane[ (int)Math.round(output) - yBounds[LOWER_BOUND_INDEX] ][ (int)Math.round(x) - xBounds[LOWER_BOUND_INDEX] ] = true; //converted from view bounds to array bounds
				}
			} catch (IndexOutOfBoundsException e){
				//means the output at the given x is out of bounds and not on the graph
			}
		}
		String planeView = "\t";
		for(int i = 0; i < xLength+2; i++){
			planeView += "_";
		} //just adding the upper part of the view box
		planeView += "\n";
		for(int y = plane.length-1; y >= 0; y--){ //x and y are array aligned; y is inverted hence the backwards forloop
			planeView += ( y + yBounds[LOWER_BOUND_INDEX] ) + "\t|";
			for(int x = 0; x < plane[y].length; x++){
				if(x + xBounds[LOWER_BOUND_INDEX] != 0 && y + yBounds[LOWER_BOUND_INDEX] != 0){
					planeView += plane[y][x] ? "x" : "."; //if true (point present) add on x, no point here add on a . 
				} else if (x + xBounds[LOWER_BOUND_INDEX] == 0 && y + yBounds[LOWER_BOUND_INDEX] != 0){ //on the y axis
					planeView += plane[y][x] ? "x" : "|";
				} else { //x axis
					planeView += plane[y][x] ? "x" : "-";
				}
			}
			planeView += "|\n"; //end of this y row
		}
		planeView += " \t|"; //now we get the bottom part of the box
		for(int i = 0; i < xLength; i++){
			planeView += "_";
		} //just adding the upper part of the view box
		planeView += "|"; //and the last peice of the box
		System.out.println("Printing " + poly.toString() + " over bounds x:" + xBoundString + " y:" + yBoundString);
		System.out.println(planeView);
	}
	
	/**
	 * Prints the concavity at the given value of x
	 * 
	 * @param input
	 *            The string to parse
	 * @throws InvalidFormatException
	 *             Thrown if the input is invalid
	 */
	public void printConcavity(String input) throws InvalidFormatException {
		String[] splitInput = input.trim().split(" ");
		if(splitInput.length != 3 && splitInput.length != 2){
			throw new InvalidFormatException("Invalid input string. Format should be: integrate <name> [LowerBound,UpperBound] or integrate [LowerBound,UpperBound]");
		}
		Polynomial poly = null;
		Double xVal = null; 
		if(splitInput.length == 3){
			xVal = verifyDouble(splitInput[2]);
			poly = getPolynomialByName(splitInput[1],false);
		}
		else {
			xVal = verifyDouble(splitInput[1]);
			poly = getPolynomialByName("",false);
		}
		if(xVal == null || poly == null){
			return; 
		}
		System.out.println("The concavity of " + poly.toString() + " at x = " + xVal + " is " + df3.format( poly.getDerivativePolynomial().differentiate(xVal) ));
	}
	
	/**
	 * Combines polynomials and add them to the list, and replaces of the same
	 * name exists
	 * 
	 * @param input
	 *            The input string
	 * @throws InvalidFormatException
	 *             Thrown if the input is invalid
	 */
	public void combinePolynomials(String input) throws InvalidFormatException{
		String[] splitInput = input.trim().split(" ");
		if(splitInput.length != 3 && splitInput.length != 4){
			throw new InvalidFormatException("Invalid input string. Format should be: combine <polynomial 1> <polynomial 2> <optional: polynomial 3>");
		}
		Polynomial poly1 = getPolynomialByName(splitInput[1],false);
		Polynomial poly2 = getPolynomialByName(splitInput[2],false);
		Polynomial sum = null;
		if(poly1 == null || poly2 == null){
			return;
		}
		ArrayList<Term> sumTermList = new ArrayList<Term>();
		sumTermList.addAll(poly1.getTermList());
		sumTermList.addAll(poly2.getTermList());
		if(splitInput.length == 3){
			sum = new Polynomial("", sumTermList);
			allPolys.remove(getPolynomialByName("",true));
		} else {
			sum = new Polynomial(splitInput[3], sumTermList);
			allPolys.remove(getPolynomialByName(splitInput[3],true));
		}
		sum.sortIntoStandardOrder();
		allPolys.add(sum);
	}
	
	/**
	 * Prints the polynomial
	 * 
	 * @param input
	 *            The string input to parse
	 * @throws InvalidFormatException
	 *             Thrown if the input is invalid
	 */
	public void showPoly(String input) throws InvalidFormatException{
		String[] splitInput = input.trim().split(" ");
		if(splitInput.length != 1 && splitInput.length != 2){
			throw new InvalidFormatException("Invalid input string. Format should be: combine <polynomial 1> <polynomial 2> <optional: polynomial 3>");
		}
		Polynomial poly = null;
		if(splitInput.length == 1){
			poly = getPolynomialByName("",false);
		} else {
			poly = getPolynomialByName(splitInput[1],false);
		}
		if(poly == null){
			return;
		}
		System.out.println(poly);
	}
	
	/**
	 * Receives and interprets a user input string
	 * 
	 * @param input
	 *            The string inputed from the user
	 */
	public void interpret(String input){
		String[] splitInput = input.trim().split(" ");
		try {
			switch (splitInput[0].toLowerCase()) { //CSC101 Requirement 9: Switch statement
				case "new":
					this.createPolynomial(input);
					break;
				case "output":
					this.printOutput(input);
			 		break;
				case "diff":
				case "differentiate":
				case "derivative":
				case "d/dx":
				case "slope":
					this.printDerivative(input);
					break;
				case "area":
				case "inte":
				case "integrate":
					this.printIntegral(input);
					break;
				case "zero":
				case "zeroes":
				case "intercepts":
				case "root":
				case "roots":
					this.printZero(input);
					break;
				case "extrema":
					this.printExtrema(input);
					break;
				case "concavity":
				case "conc":
					this.printConcavity(input);
					break;
				case "plot":
				case "graph":
					this.printPolyOnBounds(input);
					break;
				case "combine":
					this.combinePolynomials(input);
					break;
				case "show":
					this.showPoly(input);
					break;
				case "help":
				case "?":
					this.readHelp();
					break;
				default:
					System.out.println("Invalid command starter: " + splitInput[0]);
			}
		} catch (InvalidFormatException | FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * The main loop for the program
	 */
	public void programLoop(){ //look how small this method is
		Scanner in = new Scanner(System.in);
		boolean keepGoing = true;
		System.out.println("Welcome to Polymorphious");
		do{
			//CSC101 Requirement 1: Console I/O
			System.out.println("What do you wish to do? Type help or ? for command listing");
			String input = in.nextLine();
			if(input.equals("qq")){
				System.out.println("Exiting program...");
				keepGoing = false;
			} else {
				this.interpret(input);
			}
		} while(keepGoing);
		in.close();
		System.out.println("Thank you for using Polymorphious");
	}

	/**
	 * Reads the userhelp.txt file
	 * 
	 * @throws FileNotFoundException
	 *             Thrown if the file is not found
	 */
	public void readHelp() throws FileNotFoundException {
		System.out.println("Printing userhelp.txt\n");
		File userhelp = new File("src\\edu\\neumont\\csc110\\EquationParsing\\userhelp.txt");
		Scanner txtScan = new Scanner(userhelp);
		while(txtScan.hasNextLine()){
			System.out.println(txtScan.nextLine());
		}
		txtScan.close();
	}
}