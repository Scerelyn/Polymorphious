package edu.neumont.csc110.EquationParsing;

public class Mathy {
	
	//this class is solely to run the program
	public static void main(String[] args) {
		UserInterface UI = new UserInterface();
		UI.createPolynomial("create Joey -x^2-2x+1+2x^3");
		UI.printOutput("output Joey 1");
		UI.printZero("zero Joey [-3,3]");
		UI.printIntegral("integrate Joey [0,5]");
		UI.printDerivative("derivative Joey 0");
	}
}
