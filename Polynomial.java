package edu.neumont.csc110.EquationParsing;

import java.util.*;

public class Polynomial {
	
	interface Function{ //functional interface
		public double output(double x);
	}
	
	ArrayList<Term> termList = new ArrayList<Term>();
	private Function func;
	private final String name;
	public final double DIFFERENTIAL_CONSTANT = 0.001; //refers to the "h" in the limit/approximation formula for differentiation. 0.001 gives decent accuracy
	public Polynomial(String name, String str){
		this.name = name;
		str = wipeSpacesOut(str);
		this.populateTerms( this.splitStringToTermStrings(str) );
		
		func = (double x) -> {
			double answer = 0;
			for(Term t : termList){
				answer += t.subInX(x);
			}
			return answer;
		};
	}

	/**
	 * Removes all spaces inside a given string. Made for user friendliness
	 * 
	 * @param toBeWiped
	 *            The string to clear spaces
	 * @return The original string without spaces in it
	 */
	private String wipeSpacesOut(String toBeWiped){
		String[] splitUp = toBeWiped.split(" ");
		String cleared = "";
		for(String part : splitUp){
			cleared += part;
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
						listOfTerms.add(termSplit[i]);
					}
					else if(!termSplit[i].isEmpty()){ //the split can result in the first term, if it was negative, being an empty string in front in split(). Annoying really
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
						System.out.println(term.isEmpty() ? "no Exponent was input after ^" : "Erroneous input " + term); //if empty say so, else print the invalid term
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
		double boundSize = (upperBound - lowerBound) / subIntervals; //smaller bound size -> more accuracy, or at least until double can't store more accuracy
		double sum = 0;
		for(double l = lowerBound; l < upperBound; l += boundSize){
			sum += simpsonSubStep(l, l+boundSize);
		}
		return sum;
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
	 * @return The approximate slope at that point
	 */
	public double differentiate(double x){ //accurate up to six decimal places
		return ( func.output(x + DIFFERENTIAL_CONSTANT) - func.output(x - DIFFERENTIAL_CONSTANT) ) / (2*DIFFERENTIAL_CONSTANT);
	} //its basically the slope formula on a very small line
	
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
