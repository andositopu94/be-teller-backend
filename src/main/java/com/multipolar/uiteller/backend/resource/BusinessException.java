package com.multipolar.uiteller.backend.resource;

public class BusinessException extends Exception {
   
	private static final long serialVersionUID = 7047059071943798363L;
	public BusinessException(String message){
        super(message);
    }
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
    public BusinessException() {
        super();
    }
    public BusinessException(Throwable cause) {
        super(cause);
    }
}
