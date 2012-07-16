package mdcc.frontes.exception;

import mdcc.frontes.exception.globalcontextualexception.GlobalContextualException;
import mdcc.frontes.exception.handlerinterfaces.IContextualExceptionHandlerService;
import mdcc.frontes.taskmodel.task.Task;

public class DefaultContextualExceptionHandlerService implements IContextualExceptionHandlerService{

	@Override
	public void handleContextualException(Task task,
			ContextualException exception) {
	
		// TODO - Ap�s funcionar, entrando aqui, testar se consigo enviar um outro controlFlow com outros passos para a tarefa - 05/07/2012
		
		if(exception.getClass().getSimpleName().equals(GlobalContextualException.class.getSimpleName())){
			System.out.println("Colocar aqui os tratamento para exce��es globais mais importantes");
		}else {
			// TODO - Definir aqui o tratamento para um conjunto de exce��es criticas como ex "FecheTudoException"  - 05/07/2012
			System.out.println("aqui s�o encaminhadas as exce��es");
			task.propagateExceptionToUserDefinedHandler(exception);
		}
				
	}
	
}
