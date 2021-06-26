package net.intelie.challenges;

public class IllegalStateException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private String errorMsg;
	
	IllegalStateException(){
		
	}
	
	IllegalStateException(String msg){
		
		errorMsg = msg;
	}
	
	public String getError() {
		return errorMsg;
	}

}
