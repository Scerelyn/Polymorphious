package edu.neumont.csc110.finalproject.group24;

public class InvalidFormatException extends Exception{
	/**
	 * Exceptions require this
	 */
	private static final long serialVersionUID = -8945333206036015386L;
	
	public InvalidFormatException(String message){
		super(message); 
	}
}
