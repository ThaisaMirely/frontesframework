package mdcc.frontes.exception;

import mdcc.frontes.contextmodel.GeneralContext;
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
	private GeneralContext context;
	
	
	public ContextualException(GeneralContext exceptionalContext) {
		this.context = exceptionalContext;
	}
	
	public ContextualException(GeneralContext exceptionalContext, Why why) {
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

	public GeneralContext getContext() {
		return context;
	}

	public void setContext(GeneralContext context) {
		this.context = context;
	}
	
	
}
