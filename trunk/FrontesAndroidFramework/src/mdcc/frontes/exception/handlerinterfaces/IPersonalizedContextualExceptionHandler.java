package mdcc.frontes.exception.handlerinterfaces;

import mdcc.frontes.exception.ContextualException;
import mdcc.frontes.taskmodel.task.Task;

public interface IPersonalizedContextualExceptionHandler {

	// TODO - Inserir método "canHandleException(task, exception)" - 01/08/2012
	
	public void handleContextualException(Task task, ContextualException exception);
	
}
