package mdcc.frontes.exception;

import mdcc.frontes.contextmodel.Context;
import mdcc.frontes.dimension.AbstractRole;
import mdcc.frontes.dimension.Where;
import mdcc.frontes.dimension.Why;

// TODO - ZBAIXA Prioridade - Deverão ainda implementar alguma interface de serialização - 19/06/2012
public class ContextualException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AbstractRole executorRole;
	private Where location;
	private Context context;
	
	
	public ContextualException(Context exceptionalContext) {
		this.context = exceptionalContext;
	}
	
	public ContextualException(Context exceptionalContext, Why why) {
		super(why.getCause());
		this.context = exceptionalContext;
	}

	public AbstractRole getExecutorRole() {
		return executorRole;
	}

	public void setExecutorRole(AbstractRole executorRole) {
		this.executorRole = executorRole;
	}

	public Where getLocation() {
		return location;
	}

	public void setLocation(Where location) {
		this.location = location;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	
}
