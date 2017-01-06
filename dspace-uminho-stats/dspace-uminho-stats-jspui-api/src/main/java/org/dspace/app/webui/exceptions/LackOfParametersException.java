package org.dspace.app.webui.exceptions;


public class LackOfParametersException extends Exception {

	public LackOfParametersException () {
		super();
	}
	
	public LackOfParametersException (String msg, Throwable t) {
		super(msg, t);
	}
}
