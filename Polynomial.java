package edu.neumont.csc110.EquationParsing;

import java.util.*;
import java.text.*;
//handles the data of and actions of polynomials
public class Polynomial {
	
	interface Function{ //functional interface
		public double output(double x);
	}
	
	ArrayList<Term> termList = new ArrayList<Term>();
	private final Function func;
	private final String name; //these dont change
	public final double BASICALLY_ZERO = 1.0E-25; //how accurate a number should be to be considered "enough" or equal to zero
	public final int BOUND_SPLIT_AMOUNT = 1000;
	public Polynomial(String name, String str){
		this.name = name;
		str = wipeSpacesOut(str);
		this.populateTerms( this.splitStringToTermStrings(str) );
		func = (double x) -> { //lambdas are really cool, letting me make methods like a datatype, sorta
			double answer = 0;
			for(Term t : termList){
				answer += t.subInX(x);
			}
			return answer;
		};
	}
	
	public Polynomial(String name, ArrayList<Term> terms){
		this.name = name;
		func = (double x) -> {
			double answer = 0;
			for(Term t : termList){
				answer += t.subInX(x);
			}
			return answer;
		};
		this.termList = terms;
	}

	/**
	 * Removes all spaces inside a given string. Made for user friendliness
	 * 
	 * @param toBeWiped
	 *            The string to clear spaces
	 * @return The original string without spaces in it
	 */
	private String wipeSpacesOut(String toBeWiped){
		String[] splitUp = toBeWiped.split(" "); //all elements wont have a space in it
		String cleared = "";
		for(String part : splitUp){
			cleared += part; //so just add them back up
		}
		return cleared;
	}
	
	/**
	 * Breaks apart a string input and separates it into terms
	 * 
	 * @param str
	 *            The input String to break up
	 * @return An Arraylist containing elements of one term each
	 */
	private ArrayList<String> splitStringToTermStrings(String str){
		ArrayList<String> listOfTerms = new ArrayList<String>();
		String[] termSplitSub; //sub suffix since it may be broken down again, hence it is a sub step
		termSplitSub = str.split("\\+"); //breaks up based on + signs; however may not be entirely split by terms, since 7x-4 would be grouped still
		for(String termGroup : termSplitSub){
			if(termGroup.indexOf("-",1) >= 1){ //not at zero, since 0 could belong to the number as the negative sign. The mimimum size case with two terms is 1-x, so index at 1 gives the -
				String[] termSplit = termGroup.split("-");
				for(int i = 0; i < termSplit.length; i++){ //would use enhanced, but need the index value
					if(i == 0 && termGroup.charAt(0) != '-'){ //first term is nonnegative
						if(i != termSplit.length - 1 && termSplit[i].endsWith("^")){ //negative exponents should not be split
							if(i > 0 && termSplit[i-1].isEmpty()){ //missing negative, which gives an empty string in the array slot before because split() does that
								listOfTerms.add("-" + termSplit[i] + "-" + termSplit[i+1]); //split strips off the minus
							} else {
								listOfTerms.add(termSplit[i] + "-" + termSplit[i+1]);
							}
							i++; //skip the exponent, dont read it
						} else {
							listOfTerms.add(termSplit[i]);
						} //negative exponents are obnoxious wow
					}
					else if(i > 0 && i != termSplit.length - 1 && termSplit[i].endsWith("^")){ //negative exponents should not be split
						listOfTerms.add("-" + termSplit[i] + "-" + termSplit[i+1]); //split strips off the minus
						i++; //skip the exponent, dont read it
					} //nearly identical to the above
					else if(i > 0){ //the split can result in the first term, if it was negative, being an empty string in front in split(). Annoying really
						listOfTerms.add("-" + termSplit[i]); //split strips off the minus
					}
					
				}
			}
			else {
				listOfTerms.add(termGroup);
			}
		}
		//System.out.println(listOfTerms);
		return listOfTerms;
	}

	/**
	 * Populates the termList arraylist with Term objects
	 * 
	 * @param termInputs
	 *            The arraylist of Strings to make terms from
	 * @return true if the population succeeds, false if it does not
	 */
	private boolean populateTerms(ArrayList<String> termInputs){
		for(String term : termInputs){
			double constant = 1, exponent = 1;
			if(term.contains("x")){
				try{ //scope is a real hassle with try statements sometimes
					constant = Double.parseDouble( term.substring(0,term.indexOf("x")) );
				} catch(NumberFormatException e){
					if(term.startsWith("x")){
						//do nothing, let the default value take care of it as empty means constant = 1
					}
					else if(term.startsWith("-x")){
						constant = -1;
					}
					else {
						System.out.println("Malformed constant on one of the terms, reenter and try again");
						System.out.println("Erroneous term: " + term);
						return false; //end the method here, or an incomplete polynomial will happen
					}
					
				}
				if(term.contains("^")){ //This is within the if-has-x block because ^ is invalid without an x
					try{
						exponent = Double.parseDouble( term.substring(term.indexOf("^")+1) );
					} catch(NumberFormatException e){
						System.out.println("Malformed exponent on one of the exponents, reenter and try again");
						System.out.println(term.endsWith("^") ? "no Exponent was input after ^" : "Erroneous input " + term); //if empty say so, else print the invalid term
						return false;
					}
				}
			}
			else { //no x portion
				exponent = 0;
				try{
					constant = Double.parseDouble(term);
				} catch(NumberFormatException e){
					System.out.println("Malformed constant term, reenter and try again");
					System.out.println(term.isEmpty() ? "No exponent was input after ^" : "Erroneous input " + term); //if empty say so, else print the invalid term
					return false;
				}
			}
			this.termList.add(new Term(constant, exponent));
		}
		return true;
	}

	/**
	 * Outputs the value of the polynomial at a given value of x
	 * 
	 * @param x
	 *            The input value
	 * @return The value of the polynomial at x
	 */
	public double giveOutput(double x){
		return func.output(x);
	}
	
	/**
	 * Compares the exponents between terms
	 * 
	 * @param t1
	 *            Term 1 to compare
	 * @param t2
	 *            Term 2 to compare
	 * @return -1 if t1's exponent is larger, 1 if t2's is larger, 0 if the
	 *         same. The backwardness is intentional so that when listing terms,
	 *         the biggest goes in front
	 */
	private int compareExponent(Term t1, Term t2){
		return (int)Math.signum(t2.getTermData()[Term.EXPONENT_INDEX] - t1.getTermData()[Term.EXPONENT_INDEX]);
	}
	
	/**
	 * Sorts the termList arraylist into standard form, with the biggest exponent first, and in descending order
	 */
	public void sortIntoStandardOrder(){
		Collections.sort( termList, (t1,t2) -> compareExponent(t1,t2) );
	}
	
	/**
	 * Simpson's rule for integration of a function over a given bound
	 * 
	 * @param lowerBound
	 *            The lower bound to integrate from
	 * @param upperBound
	 *            The upper bound to integrate from
	 * @param subIntervals
	 *            The amount of sub intervals to use
	 * @return The approximation of the definite integral over the given bound
	 */
	public double simpsons(double lowerBound, double upperBound, int subIntervals){ //up to 3 decimal accuracy, for 200 subintervals
		int direction = 1;
		if(upperBound < lowerBound){
			direction = -1;
			double temp = lowerBound;
			lowerBound = upperBound;
			upperBound = temp;
		}
		double boundSize = (upperBound - lowerBound) / subIntervals; //smaller bound size -> more accuracy, or at least until double can't store more accuracy
		double sum = 0;
		for(double l = lowerBound; l < upperBound; l += boundSize){
			sum += simpsonSubStep(l, l+boundSize);
		}
		return direction*sum;
	}

	/**
	 * The sub steps for simpson's rule: the approximate area under a small
	 * piece of the polynomial
	 * 
	 * @param lowerBound
	 *            The lower bound to use for this sub step
	 * @param upperBound
	 *            The upper bound to use for this sub step
	 * @return The approximate area under the polynomial within the bounds
	 */
	private double simpsonSubStep(double lowerBound, double upperBound){
		return ( (upperBound - lowerBound)/6) * (func.output(lowerBound) + 
				(4*func.output( ((upperBound+lowerBound)/2) )) + 
				func.output(upperBound) );
	}
	/**
	 * Finds the slope of the polynomial at a given value of x
	 * @param x The value to find the slope on
	 * @return The slope at that point
	 */
	public double differentiate(double x){ //accurate up to six decimal places
		return this.getDerivativePolynomial().getFunc().output(x);
	} //its basically the slope formula on a very small line

	/**
	 * Finds a zero within the given bounds using the Bisection algorithm. 
	 * Note: this defers to the root closest to zero if there are multiple 
	 * roots within the bound, and only has 3 decimal place accuracy. 
	 * This is a substep method for findAZero()
	 * 
	 * @param lowerBound
	 *            The lower bound to search in for a zero/root
	 * @param upperBound
	 *            The upper bound to search in for a zero/root
	 * @param iterations
	 *            The number of iterations to go through. More iterations gives
	 *            more accuracy
	 * @return The x value of where the zero/root is located at
	 */
	public Double findAZeroInBound(double lowerBound, double upperBound, int iterations){
		double midPoint = (lowerBound + upperBound) / 2,  midPointValue = this.func.output(midPoint);
		double lowerOut = this.func.output(lowerBound),upperOut = this.func.output(upperBound);
		System.out.println("[" + lowerBound + "(" + func.output(lowerBound) + "), " + upperBound + "(" + func.output(upperBound) + "]");
		if(lowerOut < 0 && midPointValue > 0 || //if one is positive and the other is negative, there is a zero in there somewhere
				lowerOut > 0 && midPointValue < 0){ //this checks for that in the lower half of the bound
			if(iterations <= 0){ //ends the recursion
				return midPoint;
			} else {
				return findAZeroInBound(lowerBound, midPoint, iterations-1); //recursive, so further split the bounds
			}
		} else if(upperOut < 0 && midPointValue > 0 ||
				upperOut > 0 && midPointValue < 0){ //now for the upper half
			if(iterations <= 0){
				return midPoint;
			} else {
				return findAZeroInBound(midPoint, upperBound, iterations-1);
			}
		} else if(Math.abs(midPointValue) <= BASICALLY_ZERO && Math.abs(midPointValue) >= 0 ||
				(Math.abs( lowerOut ) <= BASICALLY_ZERO && Math.abs( lowerOut ) >= 0) ||
				(Math.abs( upperOut ) <= BASICALLY_ZERO && Math.abs( upperOut ) >= 0) ){
			return midPoint;
		} else {
			//System.out.println("No zeroes found within bound [" + lowerBound + "," + upperBound + "]");
			return null; //this is why i used Double and not double, because i need this extraneous value to use for checks
		}
	}

	/**
	 * Finds all zeroes within the given bound
	 * 
	 * @param lowerBound
	 *            The lower bound to search from
	 * @param upperBound
	 *            The upper bound to search until
	 * @return An array list containing Double objects
	 */
	public ArrayList<Double> findAllZeroesInBound(double lowerBound, double upperBound){
		if(upperBound < lowerBound){
			System.out.println("Invalid bounds, order should be reversed");
			return null;
		}
		double iterStep = Math.abs(upperBound - lowerBound) / BOUND_SPLIT_AMOUNT;
		int iterationCount = findOptimalIterationCount(lowerBound,upperBound);
		ArrayList<Double> zeros = new ArrayList<Double>(); //arraylist since we dont know how many, and fundamental theorem of algebra cannot confirm how many real zeroes exist
		Double zero = new Double(0.0); //using more nulls for checks
		double[] bounds = {};
		while(lowerBound <= upperBound){ //one of these will happen and end the while loop
			zero = findAZeroInBound(lowerBound, lowerBound + iterStep, iterationCount);
			if(zero != null){ //zero isnt null
				if(zeros.size() == 0 
						|| (zeros.size() > 0 && !( Math.abs( zeros.get(zeros.size()-1) - zero ) <= BASICALLY_ZERO ))){ //no duplicates
					zeros.add(zero);
					lowerBound = zero + BASICALLY_ZERO;
				}
			} else {
				lowerBound += iterStep;
			}
			
		}
//		System.out.println(zeros);
//		ArrayList<Double> zerosOut = new ArrayList<Double>();
//		for(double d : zeros){
//			zerosOut.add(this.func.output(d));
//		}
//		System.out.println(zerosOut);
		return zeros;
	}
	
	/**
	 * Finds an optimal amount of iterations that would give a good result for
	 * zero finding, depending on bound length since a simple constant would
	 * kill accuracy on large bounds.
	 * 
	 * @param lowerBound
	 *            The lower bound to use
	 * @param upperBound
	 *            The upper bound to use
	 * @return The suitable iteration count to use with the given bounds
	 */
	public int findOptimalIterationCount(double lowerBound, double upperBound){
		double boundLength = Math.abs(upperBound) - Math.abs(lowerBound);
		if(boundLength < 1){
			boundLength *= 10;
		}
		int boundLengthNoDecimals = (int)boundLength;
		int iterationCount = 10; //at least ten iterations, the findAZeroInBound() will end prematurely if a zero is found, so too many isnt an issue
		while(boundLengthNoDecimals > 10){
			boundLengthNoDecimals %= 10;
			iterationCount += 20; //iterations per digit
		}
		return iterationCount;
	}
	
	/**
	 * Creates and returns the algebraic derivative of the polynomial
	 * 
	 * @return A Polynomial object that is the derivative of the polynomial
	 *         calling this method
	 */
	public Polynomial getDerivativePolynomial(){
		ArrayList<Term> newTerms = new ArrayList<Term>();
		for(Term t : this.termList){
			if(t.getTermData()[Term.EXPONENT_INDEX] == 0){ //a constant
				newTerms.add( new Term(0,0) );
			} else {
				newTerms.add( new Term(
						t.getTermData()[Term.CONSTANT_INDEX]*t.getTermData()[Term.EXPONENT_INDEX],
						t.getTermData()[Term.EXPONENT_INDEX]-1 ) ); //power rule, ezpz
			}
		}
		return new Polynomial(this.getName() + "_Derivative",newTerms);
	}
	
	public ArrayList<Double> findExtrema(double lowerBound, double upperBound){
		if(upperBound < lowerBound){
			System.out.println("Invalid bounds, order should be reversed");
			return null;
		}
		Polynomial derivative = this.getDerivativePolynomial();
		//System.out.println(derivative);
		ArrayList<Double> extrema = derivative.findAllZeroesInBound(lowerBound, upperBound);
		extrema.add(lowerBound);
		extrema.add(upperBound); //you count edge cases too
		//System.out.println(extrema);
		Collections.sort(extrema, (d1,d2) -> compareOutputs((double)d1,(double)d2)); //so absolute mins and maxes are easy to find
		//System.out.println(extrema);
		return extrema;
	}
	
	public int compareOutputs(double x1, double x2){ //orders from least to greatest in output
		return (int)Math.signum( this.func.output(x2) - this.func.output(x1) );
	}
	
	@Override
	public String toString(){
		String expression = "";
		for(Term t : termList){
			expression += " + " + t.toString();
		}
		return expression.substring(3); //expression will have an extra plus sign and spaces in the front
	}
	public ArrayList<Term> getTermList() {
		return termList;
	}
	public Function getFunc() {
		return func;
	}
	public String getName(){
		return this.name;
	}
}
