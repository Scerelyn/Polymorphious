package edu.neumont.csc110.EquationParsing;

public class Mathy {
	
	//this class is solely to run the program
	public static void main(String[] args) {
		UserInterface UI = new UserInterface();
		try{
			UI.createPolynomial("create Joey x^4+8x^3+22x^2+24x+8");
			UI.createPolynomial("create x^2");
			//UI.printOutput("output Joey 1");
			//UI.printZero("zero Joey [-3,3]");
			//UI.printIntegral("integrate Joey [0,5]");
			//UI.printDerivative("derivative Joey 0");
			//UI.printExtrema("extrema [-5,5]");
			//UI.printExtrema("extrema Joey [-10,10]");
			UI.printPolyOnBounds("print [-10,10] [-10,10]");
		} catch(InvalidFormatException e){
			System.out.println(e.getMessage());
		}
		
	}
}
