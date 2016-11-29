package edu.neumont.csc110.EquationParsing;

public class Term{
	private final double termData[];
	public final static int CONSTANT_INDEX = 0;
	public final static int EXPONENT_INDEX = 1;
	
	public Term(double constant, double exponent){
		termData = new double[]{constant,exponent};
	}
	
	/**
	 * Gives the value of the expression if the given x is substituted in
	 * 
	 * @param x
	 *            The value to substitute x for
	 * @return The value of the expression with x substituted
	 */
	public double subInX(double x){ 
		return termData[CONSTANT_INDEX] * (Math.pow(x, termData[EXPONENT_INDEX]));
	}
	
	@Override
	public String toString(){
		if(termData[CONSTANT_INDEX] == 0){
			return "0";
		}
		else if(termData[EXPONENT_INDEX] == 0){
			return "" + termData[CONSTANT_INDEX];
		}
		return  (termData[CONSTANT_INDEX] == -1 ? "-" : "") +
				( (termData[CONSTANT_INDEX] == 1 && termData[EXPONENT_INDEX] != 0 || termData[CONSTANT_INDEX] == -1)  ? "" : termData[CONSTANT_INDEX]) + //if one and exponent is not zero or constant is -1, give empty string
				(termData[EXPONENT_INDEX] == 0 ? "" : "x") +//if exponent is zero, give no x
				( (termData[EXPONENT_INDEX] == 1 || termData[EXPONENT_INDEX] == 0)  ? "" : "^" + termData[EXPONENT_INDEX]); //if one or zero, dont give exponent
	} //that mess is so constants and exponents of ones are omitted, and x's with exponents of zero are omitted
	//tried teriaries for the first time and man i cant read this very well at all, but it works :V
	public double[] getTermData() {
		return termData;
	}
}
