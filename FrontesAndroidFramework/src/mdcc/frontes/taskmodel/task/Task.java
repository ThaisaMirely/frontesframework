package mdcc.frontes.taskmodel.task;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.concurrent.Callable;

import mdcc.frontes.contextmodel.GeneralContext;
import mdcc.frontes.dimension.AbstractRole;
import mdcc.frontes.dimension.DefaultRole;
import mdcc.frontes.exception.ContextualException;
import mdcc.frontes.exception.globalcontextualexception.GlobalContextualException;
import mdcc.frontes.exception.handlerinterfaces.IContextualExceptionHandlerService;
import mdcc.frontes.exception.handlerinterfaces.IPersonalizedContextualExceptionHandler;
import mdcc.frontes.services.ContextManagerService;
import mdcc.frontes.taskmodel.Message;
import mdcc.frontes.taskmodel.State;


/**
 * @author Carlos
 *
 */
public class Task implements Callable<Message>, Observer, ITask {
	
	private String mTaskName;
	private UUID mTaskId;
	private TaskControlFlow mControlFlow;
	private AbstractRole mRole;
	
	private static final String UNNAMED_TASK_STR = "Unnamed Task"; 
	
	private State mTaskState;
	private TaskExecutionStrategy selectedExecutionStrategy;
	private String firstStepName;
	private IContextualExceptionHandlerService defaultContectualExceptionHandlerService;
	private IPersonalizedContextualExceptionHandler userDefinedContextualExceptionHandler;
	
	private static enum TaskExecutionStrategy{
		ADDITIONORDER, SPECIFIEDORDER;
	}
	
	private static ContextManagerService contextServiceObservable;
	private GeneralContext runningContext;
	protected Message messageReturnBundle;
	
	
	
	/**
	 *	Ao usar esse construtor os passos devem ser adicionados um a um. 
	 */
	public Task(){
		this.mTaskName = UNNAMED_TASK_STR; 
		this.mTaskId = UUID.randomUUID();
		this.setRole(new DefaultRole());
		this.mControlFlow = new TaskControlFlow();
		this.selectedExecutionStrategy = TaskExecutionStrategy.ADDITIONORDER;
		this.messageReturnBundle = new Message();
		subscribeToReceiveContextUpdates();
		
	}
	
	/**
	 * Construtor onde a tarefa será executada seguindo a ordem em que os passos foram adicionados. Nï¿½o precisa fornecer o prox. passo.
	 * @param taskStepWorkLoad
	 */
	public Task(LinkedHashMap<String , TaskStep> taskStepWorkLoad) {
		this();
		setControlFlow(new TaskControlFlow(taskStepWorkLoad));
		
	}
	
	/**
	 * Ao usar esse construtor todos os passos deverï¿½o indicar o prï¿½x. passo a ser executado.
	 * @param taskStepWorkLoad
	 * @param firstStepName
	 */
	public Task(LinkedHashMap<String , TaskStep> taskStepWorkLoad, String firstStepName) {
	
		this(taskStepWorkLoad);
		setSelectedExecutionStrategy(TaskExecutionStrategy.SPECIFIEDORDER); 
		this.firstStepName = firstStepName;
			
	}

	private void subscribeToReceiveContextUpdates() {
		contextServiceObservable = ContextManagerService.getInstance();
		this.runningContext = contextServiceObservable.getContext();
		contextServiceObservable.addObserver(this);
		
	}

	public void setDefaultExceptionHandler(
			IContextualExceptionHandlerService defaultContextualExceptionHandler) {
		this.defaultContectualExceptionHandlerService = defaultContextualExceptionHandler;
		
	}
	
	public void propagateExceptionToUserDefinedHandler(
			ContextualException exception) {
		getUserDefinedContextualExceptionHandler().handleContextualException(this, exception);
		
	}
	
	private IContextualExceptionHandlerService getDefaultExceptionHandler(){
		return this.defaultContectualExceptionHandlerService;
	}
	
	//Carlos TO-DO Uma tarefa deve ter um retorno. Devo capturar e tratar exceï¿½ï¿½es de um dado passo qualquer.
	@Override
	public Message call() throws ContextualException, Exception {
		
		//Avaliar se o contexto é valido para a tarefa
		//Ficar veriicando o passo em execução e se ele quebra a regra de contexto
		//se for lançar exceção.
		//contextServiceObservable.addObserver(this);		
		
		if(getSelectedExecutionStrategy()== TaskExecutionStrategy.ADDITIONORDER){
			this.mControlFlow.execute();
		}else {
			this.mControlFlow.execute(firstStepName);
		}
		
		messageReturnBundle.put("taskName", getTaskName());
		messageReturnBundle.put("taskId", getTaskId());
		messageReturnBundle.put("resultStatus", "ok");
		
		contextServiceObservable.deleteObserver(this);
		
		return messageReturnBundle;
	}
	
	@Override
	public void update(Observable observable, Object data) {
		try {
			mControlFlow.notifyContextChanged(data);
		} catch (ContextualException e) {
			
			// TODO - Alterar o estado para Handling quando o método Execute for atualizado - 05/07/2012
			this.setState(State.STOPPED);
			// TODO - ALTA Prioridade -  Jogar a exceção pra cima e ver como tratar (Não é nesse trecho de código , talvez, mas pensar em como jogar para o espaço de tuplas uma exceção que não foi tratada em uma tarefa) - 05/07/2012 
			getDefaultExceptionHandler().handleContextualException(this, e);
			
		}
		
	}
		
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		mControlFlow.getRunningStep().interrupt();
		setState(State.STOPPED);
		System.out.println("Tarefa " + getTaskName() + "Finalizada" );
		
	}
	
	public String getTaskName() {
		return mTaskName;
	}

	public void setTaskName(String taskName) {
		this.mTaskName = taskName;
	}

	public UUID getTaskId() {
		return mTaskId;
	}
	
	private void setState(State taskState) {
		this.mTaskState = taskState;
	}

	/**
	 * @return O estado atual da tarefa
	 */
	public State getState(){
		return mTaskState;
	}
	
	/**
	 * @return O nome do passo-de-tarefa em execução
	 */
	public String getRunningStepName(){
		return mControlFlow.getRunningStepName();
	}
	
	public void setRole(AbstractRole mRole) {
		this.mRole = mRole;
	}

	public AbstractRole getRole() {
		return mRole;
	}
	
	private void setControlFlow(TaskControlFlow mControlFlow) {
		this.mControlFlow = mControlFlow;
	}


	private TaskExecutionStrategy getSelectedExecutionStrategy() {
		return selectedExecutionStrategy;
	}

	private void setSelectedExecutionStrategy(TaskExecutionStrategy selectedExecutionStrategy) {
		this.selectedExecutionStrategy = selectedExecutionStrategy;
	}



	public void setUserDefinedContextualExceptionHandler(
			IPersonalizedContextualExceptionHandler userDefinedContextualExceptionHandler) {
		this.userDefinedContextualExceptionHandler = userDefinedContextualExceptionHandler;
	}

	/**
	 * Caso o usuário não tenha definido um Tratador Personalizado então a exceção simplesmente não será tratada.
	 * @return
	 */
	public IPersonalizedContextualExceptionHandler getUserDefinedContextualExceptionHandler() {
		if(userDefinedContextualExceptionHandler==null){
			return new IPersonalizedContextualExceptionHandler() {
				public void handleContextualException(Task task,
						ContextualException exception) {
					System.out.println("Exceção não tratada pelo usuário");
				}
			};
		}else {
			return userDefinedContextualExceptionHandler;
		}
	}



	/**
	 * @author Carlos
	 *	Esta classe representa o gerenciador do estado da tarefa, bem como controla o fluxo dos passos a serem executados.
	 */

	private class TaskControlFlow{
		
		private final String STR_NO_STEP_RUNNING = "There's no step running";
		
		private LinkedHashMap<String, TaskStep> mTaskStepsWorkLoad;
		private TaskStep mRunningTaskStep;
		
		/**
		 * Se usado este construtor, devem-se adicionar os passos-de-tarefa antes de executá-la. 
		 */
		public TaskControlFlow() {
			this.mTaskStepsWorkLoad = new LinkedHashMap<String, TaskStep>();
			setState(State.READY);
		}
		
		public void notifyContextChanged(Object data) throws ContextualException {
			getRunningStep().notifyContextChanged(data);
		}

		protected TaskControlFlow(LinkedHashMap<String, TaskStep> taskStepsWorkLoad) {
			this.mTaskStepsWorkLoad = taskStepsWorkLoad;
			setState(State.READY);
		}

		/**
		 * Inicia a execução do conjunto de passos tarefa seguindo a ordem em que foram adicionados no TaskStepsWorkLoad.
		 */
		protected void execute(){
			
			setState(State.RUNNING);
			
			if (!mTaskStepsWorkLoad.isEmpty()) {
				Iterator<Map.Entry<String, TaskStep>> it = mTaskStepsWorkLoad.entrySet()
						.iterator();

				// TODO - Colocar condição para os outros estados (handling, adapting, etc) - 05/07/2012
				while (it.hasNext() && getState() != State.STOPPED) {
					TaskStep tempRunningStep = it.next().getValue();
					tempRunningStep.setBundleMessage(messageReturnBundle);
					mRunningTaskStep = tempRunningStep;
					mRunningTaskStep.run();
					try {
						mRunningTaskStep.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
						mRunningTaskStep.interrupt();
					}
				}

			}
			/*if(BuildConfig.DEBUG){*/
				System.out.println("Tarefa " + getTaskName() + "Finalizada" );
			/*}*/
			
			setState(State.STOPPED);

		}

		/**
		 * Este método inicia a execução do conjunto de passos tarefa a partir do passo especificado pela chave firstStepName.
		 * Obs: Todos os passos, exceto o último, devem conhecer o nome do passo seguinte.  
		 * @param firstStepName Nome do primeiro passo a ser executado. 
		 */
		protected void execute(String firstStepName)  {
			
			
			setState(State.RUNNING);
			if ((mRunningTaskStep = mTaskStepsWorkLoad.get(firstStepName))!=null) {
			
				TaskStep nextTaskStep = mRunningTaskStep;
				TaskStep lastTaskStep = mRunningTaskStep;
				
				do {
					if (mRunningTaskStep.hasNextStep()) {
						nextTaskStep = mTaskStepsWorkLoad.get(mRunningTaskStep.getNextStrepName());
					} else {
						nextTaskStep = null;
					}
					mRunningTaskStep.setBundleMessage(messageReturnBundle);
					mRunningTaskStep.run();
					try {
						mRunningTaskStep.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
						mRunningTaskStep.interrupt();
					}

					lastTaskStep = mRunningTaskStep;
					if (nextTaskStep != null && nextTaskStep!= mRunningTaskStep) {
						mRunningTaskStep = nextTaskStep;
						nextTaskStep = null;
					}

				} while (lastTaskStep!=mRunningTaskStep);
				// TODO - Colocar condição para os outros estados (handling, adapting, etc) - 05/07/2012
			}
			/*if(BuildConfig.DEBUG){*/
				System.out.println("Tarefa " + getTaskName() + "Finalizada" );
			/*}*/
			setState(State.STOPPED);
		}

		private String getRunningStepName() {
			if (getState() == State.RUNNING) {
				return mRunningTaskStep.getName();
			} else {
				return STR_NO_STEP_RUNNING;
			}
		}
		
		private TaskStep getRunningStep() {
			if (getState() == State.RUNNING) {
				return mRunningTaskStep;
			} else {
				return null;
			}
		}

		
		// TODO - Não é possível adicionar passos em tarefas que já foram iniciadas. Verificar se posso alterar essa limitação sem problemas de concorrência. - 19/06/2012
		/**
		 * @param taskStep
		 * @return Retorna 1 se o passo-de-tarefa foi adicionado com sucesso, caso contrário retorna -1.  
		 */
		public int addTaskStep(TaskStep taskStep) {
			if (getState() == State.READY) {
				mTaskStepsWorkLoad.put(taskStep.getName(), taskStep);
				return 1;
			} else {
				return -1;
			}

		}
		
	}



	@Override
	public void handleGlobalContextualException(
			GlobalContextualException exception) {
		// TODO - Testar se cai aqui quando passar um exceção global - OK - 07/07/2012
		getDefaultExceptionHandler().handleContextualException(this, exception);
		
	}








	
	


}
