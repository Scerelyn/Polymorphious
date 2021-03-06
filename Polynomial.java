package edu.neumont.csc110.finalproject.group24;

import java.util.*;
//handles the data of and actions of polynomials
public class Polynomial {
	
	interface Function{ //functional interface
		public double output(double x);
	}
	private final Function func;
	//CSC101 Requirement 5: Array/Collection
	private ArrayList<Term> termList = new ArrayList<Term>();
	//CSC101 Requirement 2: Primitive Types
	//CSC101 Requirement 8: Constants
	private final String name; //these dont change
	public final double BASICALLY_ZERO = 1.0E-10; //how accurate a number should be to be considered "enough" or equal to zero
	public final int BOUND_SPLIT_AMOUNT = 1000;
	public final int NEWTONS_METHOD_ITERATIONS = 10; //ten can get a p good amount of accuracy
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
	//CSC101 Requirement 11: Javadoc comment syntax annotation
	/**
	 * Removes all spaces inside a given string. Made for user friendliness
	 * 
	 * @param toBeWiped
	 *            The string to clear spaces
	 * @return The original string without spaces in it
	 */
	private String wipeSpacesOut(String toBeWiped){ //CSC101 Requirement 6: Methods/Functions
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
		//CSC101 Requirement 4: Iteration: a) For loop
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
	 * 
	 * @param x
	 *            The value to find the slope on
	 * @return The slope at that point
	 */
	public double differentiate(double x){ //exact because power rule :^)
		return this.getDerivativePolynomial().getFunc().output(x);
	}
	
	/**
	 * Finds all the zeros in the given bound, using Newton's method
	 * 
	 * @param lowerBound
	 *            The lowerbound to check
	 * @param upperBound
	 *            The upperbound to check
	 * @return An arraylist of Doubles containing the zeros found
	 */
	public ArrayList<Double> findAllZeroesInBoundNewtons(double lowerBound, double upperBound){
		if(upperBound < lowerBound){
			System.out.println("Invalid bounds, order should be reversed");
			return null;
		}
		int maxPossibleRoots = Math.abs( (int)Math.ceil(this.getTermList().get(0).getTermData()[Term.EXPONENT_INDEX]) ); //highest exponents tells maximum roots that can exist; negatives too if we take absolute value
		double iterStep = (upperBound - lowerBound) / (maxPossibleRoots*2); //*2 to double the amount of subintervals
		ArrayList<Double> zeros = new ArrayList<Double>(); //arraylist since we dont know how many, and fundamental theorem of algebra cannot confirm how many real zeroes exist
		double zero = 0;
		//CSC101 Requirement 4: b) While loop
		while(lowerBound <= upperBound){ //one of these will happen and end the while loop
			zero = this.findZeroNewtonsFirst(lowerBound, NEWTONS_METHOD_ITERATIONS);
			if(Math.abs(this.func.output(zero)) <= BASICALLY_ZERO && Math.abs(this.func.output(zero)) >= 0){ //checking to see if the result is close enough to zero
				if(zeros.size() == 0 
						|| (zeros.size() > 0 && !( Math.abs( zeros.get(zeros.size()-1) - zero ) <= BASICALLY_ZERO ))){ //no duplicates
					zeros.add(zero);
					lowerBound += iterStep;
				} else {
					lowerBound += (iterStep/2); //denominator is arbitrary, i just need to move a little bit
				}
			} else {
				lowerBound += iterStep;
			}
		}
		return zeros;
	}
	
	/**
	 * Checks to see if all the exponents in the polynomial are all integer values
	 * @return True if no decimal exponents exist within the polynomial's terms
	 */
	public boolean checkIntegerExponents(){
		for(Term t : this.getTermList()){
			if(t.getTermData()[Term.EXPONENT_INDEX] != (int)(t.getTermData()[Term.EXPONENT_INDEX])){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * **Non functional!!**
	 * Finds zeroes more efficiently by finding a zero, factoring out x-zero,
	 * then recursing until an x-a is left
	 * 
	 * @param lowerBound
	 *            Lower bound to search in
	 * @param upperBound
	 *            Upper bound to search from
	 * @return
	 */
	public ArrayList<Double> findZeroesInBoundSmart(double lowerBound, double upperBound){ //
		ArrayList<Double> zeroes = new ArrayList<Double>();
		Polynomial dupli = new Polynomial(this.name + "_duplicate", this.getTermList());
		double zero = 0;
		while(dupli.getTermList().get(0).getTermData()[Term.EXPONENT_INDEX] > 1){
			zero = this.findZeroNewtonsFirst(lowerBound, NEWTONS_METHOD_ITERATIONS);
			dupli = dupli.syntheticDivision(zero);
			zeroes.add(zero);
		}
		return zeroes;
	}
	
	/**
	 * The first "step" of Newton's method, this iterates the algorithm
	 * 
	 * @param start
	 *            The initial guess, or starting point
	 * @param iterations
	 *            How many iterations to go for
	 * @return The approximate zero value
	 */
	public Double findZeroNewtonsFirst(double start, int iterations){
		double zeroApproxLoc = start;
		for(int iter = 0; iter < iterations; iter++){
			zeroApproxLoc = findZeroNewtonsSubstep(zeroApproxLoc);
		}
		return zeroApproxLoc;
	}
	
	/**
	 * The substep method for newton's method
	 * 
	 * @param guess
	 *            The guess to based this step off of
	 * @return The next approximation that Newton's method yields
	 */
	public double findZeroNewtonsSubstep(double guess){
		Polynomial derivative = this.getDerivativePolynomial();
		if(derivative.func.output(guess) == 0){ //critical point present, this avoids division by zero from occurring below
			return this.func.output(guess);
		}
		return guess - ( (this.func.output(guess)) / (derivative.func.output(guess)) ); // x0 - ( f(x0) / f'(x0) )
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
	
	/**
	 * Finds extrema within a given bound
	 * 
	 * @param lowerBound
	 *            The lower bound to search in
	 * @param upperBound
	 *            The upper bound to search until
	 * @return An arraylist of doubles of x values of where extrema have been
	 *         found
	 */
	public ArrayList<Double> findExtrema(double lowerBound, double upperBound){
		if(upperBound < lowerBound){
			System.out.println("Invalid bounds, order should be reversed");
			return null;
		}
		Polynomial derivative = this.getDerivativePolynomial();
		ArrayList<Double> extrema = derivative.findAllZeroesInBoundNewtons(lowerBound, upperBound);
		extrema.add(lowerBound);
		extrema.add(upperBound); //you count edge cases too
		Collections.sort(extrema, (d1,d2) -> compareOutputs((double)d1,(double)d2)); //so absolute mins and maxes are easy to find
		return extrema;
	}
	
	/**
	 * Compares values by their outputs on the polynomial
	 * 
	 * @param x1
	 *            x value 1
	 * @param x2
	 *            x value 2
	 * @return -1 if f(x1) < f(x2), 0 if f(x1) = f(x2), 1 if f(x1) > f(x2)
	 */
	public int compareOutputs(double x1, double x2){ //orders from least to greatest in output
		return (int)Math.signum( this.func.output(x2) - this.func.output(x1) );
	}

	/**
	 * Sythetically divides this polynomial by linear polynomial x-a
	 * 
	 * @param dividingNumber
	 *            In form x-a, a is the value of dividingNumber. If the divisor
	 *            is x+a, input -a as the parameter
	 * @return The resulting polynomial divided by x-dividingNumber. The
	 *         remainder term is ignored and not included in the resulting
	 *         division
	 */
	public Polynomial syntheticDivision(double dividingNumber){
		ArrayList<Term> newTerms = new ArrayList<Term>();
		double[] coeffs = new double[(int)this.termList.get(0).getTermData()[Term.EXPONENT_INDEX]+1];
		double[] newCoeffs = new double[coeffs.length];
		if(!this.checkIntegerExponents()){
			System.out.println("Decimal exponent present, cannot do synthetic division");
			return null;
		}
		double maxExpo = this.termList.get(0).getTermData()[Term.EXPONENT_INDEX];
		for(int i = 0; i < coeffs.length; i++){ //coefficients will be set in descending order depending on exponent
			boolean exponentMatched = false;
			for (int j = 0; j < this.getTermList().size(); j++) {
				if(this.getTermList().get(j).getTermData()[Term.EXPONENT_INDEX] == maxExpo - i){
					coeffs[i] = this.termList.get(j).getTermData()[Term.CONSTANT_INDEX];
					exponentMatched = true;
					j = this.getTermList().size();
				}
			}
			if(!exponentMatched){
				coeffs[i] = 0;
			}
		}
		newCoeffs[0] = coeffs[0];
		for(int i = 1; i < coeffs.length; i++){
			newCoeffs[i] = coeffs[i] + newCoeffs[i-1]*dividingNumber;
		}
		maxExpo--; //since the resulting poly is always one degree less than the dividend
		for(int i = 0; i < newCoeffs.length-1; i++){
			newTerms.add(new Term(newCoeffs[i], maxExpo--)); //index increases, but the exponent goes down. kinda annoying eh?
		}
		return new Polynomial(this.name + "_Synthetically Divded by x-" + dividingNumber, newTerms);
	}
	
	@Override
	public String toString(){
		String expression = "";
		for(Term t : termList){
			expression += " + " + t.toString();
		}
		return expression.substring(3); //expression will have an extra plus sign and spaces in the front
	}
	//CSC101 Requirement 7: Custom class with encapsulation
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
