package mdcc.frontes.taskmodel.task;

import java.util.Set;

import mdcc.frontes.contextmodel.Context;
import mdcc.frontes.contextmodel.ContextInformation;
import mdcc.frontes.exception.ContextualException;
import mdcc.frontes.taskmodel.Message;


public abstract class TaskStep extends Thread {

	private boolean stepAlive;
	private String nextStepName;
	private Context mContext;
	private Message mBundle;

	public TaskStep(String name) {
		stepAlive = true;
		this.setName(name);
	}
	
	

	@Override
	public void run() {
		while (stepAlive) {
			try {
				doWhileExecutingstep();
			} catch (InterruptedException e) {
				Thread.interrupted();
			}
			stopStep();
		}

	}

	protected abstract void doWhileExecutingstep() throws InterruptedException;

	/**
	 * @return Implementar esse metodo sempre retornando o padrao a ser
	 *         usado para matching de contexto a ser considerado
	 *         excepcional para o passo
	 */
	protected abstract Set<? extends ContextualException> getExceptionalContext();

	public void addReturn(String key, Object value) {
		mBundle.put(key, value);
	}

	protected void setBundleMessage(Message bundle) {
		this.mBundle = bundle;
	}
	
	public void stopStep() {
		this.stepAlive = false;
	}

	public boolean hasNextStep() {
		if (nextStepName != null && !nextStepName.equals("")) {
			return true;
		}
		return false;
	}

	public String getNextStrepName() {
		return nextStepName;
	}

	public void setNextStrep(String nextStrepName) {
		this.nextStepName = nextStrepName;
	}

	public void notifyContextChanged(Object data) throws ContextualException {
		evaluateContext(data);
	}

	private void evaluateContext(Object data) throws ContextualException {

		//TODO - ZBAIXA Prioridade - aqui pode-se melhor o matching entre o contexto corrente e o padrão de contexto das exceções de um passo
		Context runningContext = (Context)data;
		
		Set<? extends ContextualException> exceptionalContextExceptions= getExceptionalContext();
		if(exceptionalContextExceptions!=null && !exceptionalContextExceptions.isEmpty()){
			for (ContextualException contextualException : exceptionalContextExceptions) {
				Context tempExceptionalcontext =  contextualException.getContext();
				if(compareContext(runningContext, tempExceptionalcontext)){
					interrupt();
					throw contextualException;
				}
			}	
		}
		
	}



	private boolean compareContext(Context runningContext,
			Context exceptionalContext) {
		Set<ContextInformation> runningContextInfo = runningContext.getContextualInformation();
		Set<ContextInformation> exceptionalContextInfo = exceptionalContext.getContextualInformation();
		
		return runningContextInfo.containsAll(exceptionalContextInfo);
	}

}
