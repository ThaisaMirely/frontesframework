package mdcc.frontes.exception.globalcontextualexception;

import mdcc.frontes.contextmodel.Context;
import mdcc.frontes.exception.ContextualException;

public class GlobalContextualException extends ContextualException implements IGlobalContextualException{
	
	private static final long serialVersionUID = -6623904965728381613L;
	private int globalExceptionType; 
	
	public GlobalContextualException(Context exceptionalContext,int exceptionType) {
		super(exceptionalContext);
		globalExceptionType = exceptionType;
	}

	public void setGlobalExceptionType(int globalExceptionType) {
		this.globalExceptionType = globalExceptionType;
	}

	public int getGlobalExceptionType(){
		return this.globalExceptionType; 
	}

}
