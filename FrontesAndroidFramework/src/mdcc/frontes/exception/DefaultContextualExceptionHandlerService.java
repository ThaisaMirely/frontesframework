package mdcc.frontes.exception;

import mdcc.frontes.exception.globalcontextualexception.GlobalContextualException;
import mdcc.frontes.exception.handlerinterfaces.IContextualExceptionHandlerService;
import mdcc.frontes.taskmodel.task.Task;

public class DefaultContextualExceptionHandlerService implements IContextualExceptionHandlerService{

	@Override
	public void handleContextualException(Task task,
			ContextualException exception) {
	
		// TODO - Após funcionar, entrando aqui, testar se consigo enviar um outro controlFlow com outros passos para a tarefa - 05/07/2012
		
		if(exception.getClass().getSimpleName().equals(GlobalContextualException.class.getSimpleName())){
			System.out.println("Colocar aqui os tratamento para exceções globais mais importantes");
		}else {
			// TODO - Definir aqui o tratamento para um conjunto de exceções criticas como ex "FecheTudoException"  - 05/07/2012
			System.out.println("aqui são encaminhadas as exceções");
			task.propagateExceptionToUserDefinedHandler(exception);
		}
				
	}
	
}
