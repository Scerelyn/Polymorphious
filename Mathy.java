package edu.neumont.csc110.EquationParsing;

public class Mathy {
	
	//this class is solely to run the program
	public static void main(String[] args) {
		UserInterface UI = new UserInterface();
		//UI.programLoop(); //look at that, one method
		try {
			UI.createPolynomial("create x^2+1");
			UI.printZero("zero [-10,10]");
		} catch (Exception e) {

		}
	}
}
