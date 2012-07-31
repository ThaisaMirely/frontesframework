package mdcc.frontes.exception;

import java.util.LinkedHashMap;
import java.util.Set;

import mdcc.frontes.exception.globalcontextualexception.GlobalContextualException;
import mdcc.frontes.exception.handlerinterfaces.IContextualExceptionHandlerService;
import mdcc.frontes.taskmodel.task.Task;
import mdcc.frontes.taskmodel.task.TaskStep;

public class DefaultContextualExceptionHandlerService implements IContextualExceptionHandlerService{

	@Override
	public void handleContextualException(Task task,
			ContextualException exception) {
	
		// TODO - Ap�s funcionar, entrando aqui, testar se consigo enviar um outro controlFlow com outros passos para a tarefa - 05/07/2012
		
		if(exception.getClass().getSimpleName().equals(GlobalContextualException.class.getSimpleName())){
			System.out.println("Colocar aqui os tratamento para exce��es globais mais importantes");
			
			LinkedHashMap< String, TaskStep> newFlow = new LinkedHashMap<String, TaskStep>();
			TaskStep p1 = new TaskStep("passoAdaptado") {
				
				@Override
				protected Set<? extends ContextualException> getExceptionalContext() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				protected void doWhileExecutingstep() throws InterruptedException {
					
					System.out.println("Executando o passo tempor�rio - ADAPT��O.");
					
				}
			};
			
			TaskStep p2 = new TaskStep("passoAdaptado2") {
				
				@Override
				protected Set<? extends ContextualException> getExceptionalContext() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				protected void doWhileExecutingstep() throws InterruptedException {
					
					System.out.println("Executando o passo tempor�rio 2 - ADAPT��O.");
					
				}
			};
			
			
			newFlow.put(p1.getName(), p1);
			newFlow.put(p2.getName(), p2);
			
			task.adaptControlFlow(newFlow);
			
		}else {
			// TODO - Definir aqui o tratamento para um conjunto de exce��es criticas como ex "FecheTudoException"  - 05/07/2012
			System.out.println("aqui s�o encaminhadas as exce��es");
			task.propagateExceptionToUserDefinedHandler(exception);
		}
				
	}
	
}
