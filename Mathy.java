package edu.neumont.csc110.EquationParsing;

public class Mathy {
	
	//this class is solely to run the program
	public static void main(String[] args) {
		Polynomial p = new Polynomial("","2x^2-x-3");
		p.sortIntoStandardOrder();
		System.out.println(p);
		System.out.println(p.findAZero(-1, 2));
		UserInterface UI = new UserInterface();
		UI.createPolynomial("create Joey x^2-2x+1");
		UI.printOutput("output Joey 1");
		UI.printZero("zero Joey [-2,2]");
		UI.printIntegral("integrate Joey [0,5]");
		UI.printDerivative("derivative Joey 0");
	}

}
