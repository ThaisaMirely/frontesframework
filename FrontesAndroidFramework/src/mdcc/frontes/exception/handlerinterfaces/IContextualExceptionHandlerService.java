package mdcc.frontes.exception.handlerinterfaces;

import mdcc.frontes.exception.ContextualException;
import mdcc.frontes.taskmodel.task.Task;


public interface IContextualExceptionHandlerService {

	public void handleContextualException(Task task, ContextualException exception);
	
}

