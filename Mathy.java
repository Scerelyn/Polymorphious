package edu.neumont.csc110.EquationParsing;

public class Mathy {
	
	//this class is solely to run the program
	public static void main(String[] args) {
		Polynomial p = new Polynomial("","x^-1+1.5x^-3.2-1.1x^-2.3+50");
		p.sortIntoStandardOrder();
		System.out.println(p);
		System.out.println(p.findAZero(-1, 2));
	}

}
