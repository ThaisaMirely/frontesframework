package mdcc.frontes.taskmodel.task;

import mdcc.frontes.exception.globalcontextualexception.GlobalContextualException;

public interface ITask {

	public void handleGlobalContextualException(GlobalContextualException exception);
	
}
