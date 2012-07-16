package mdcc.frontes.taskmanager;

import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import mdcc.frontes.exception.DefaultContextualExceptionHandlerService;
import mdcc.frontes.exception.globalcontextualexception.GlobalContextualException;
import mdcc.frontes.exception.handlerinterfaces.IContextualExceptionHandlerService;
import mdcc.frontes.services.ExceptionManagerService;
import mdcc.frontes.taskmodel.Message;
import mdcc.frontes.taskmodel.task.Task;


public class TaskExecutor{

	private static ExecutorService mExecutorService;
	private static CompletionService<Message> mCompletionService;
	private static TaskExecutor instance;
	
	private static Hashtable<UUID, Future<Message>> mTasksResultPool;
	private static Hashtable<UUID, Task> mTaskPool;
	private IContextualExceptionHandlerService mDefaultContextualExceptionHandler;
	private ExceptionManagerService mExceptionManagerService;
	
	private TaskExecutor() {
		mTasksResultPool = new Hashtable<UUID, Future<Message>>();
		mTaskPool = new Hashtable<UUID, Task>();
		mExecutorService = Executors.newCachedThreadPool();
		mCompletionService = new ExecutorCompletionService<Message>(mExecutorService);
		
		// TODO - Deixar esse ExceptionHandler mais extensível (Tem que ter um padrão para tratar as exceções globais - Posso Definir um subconjunto fixo de exceções) - 05/07/2012
		mDefaultContextualExceptionHandler = new DefaultContextualExceptionHandlerService(); 
		
		mExceptionManagerService = ExceptionManagerService.getInstance();
		mExceptionManagerService.addObserver(new GlobalExceptionHandlerDispatcher());
		
	}
	
	
	
	public static TaskExecutor getInstance(){

		if (instance!=null){
			return instance;	
		}else{
			instance = new TaskExecutor();
			return instance;
		}
		
	}
	
	public synchronized UUID submitTask(Task task){
		
		task.setDefaultExceptionHandler(mDefaultContextualExceptionHandler);
		mTasksResultPool.put(task.getTaskId(), mCompletionService.submit(task));
		mTaskPool.put(task.getTaskId(), task);
		return task.getTaskId();
	}
	
	public synchronized Future<Message> getTask(UUID taskUUID){
		
		return mTasksResultPool.get(taskUUID);
	}
	
	public void cancelTask(UUID taskUUID){
		
		Future<Message> taskTemp = mTasksResultPool.get(taskUUID);
		
		if (!taskTemp.isCancelled() && !taskTemp.isDone()){
			taskTemp.cancel(true);
		}
				
	}
	
	public boolean isTaskCancelled(UUID taskUUID){
	
		Future<Message> taskTemp = mTasksResultPool.get(taskUUID);
		if( taskTemp.isCancelled()){
			return true;
		}else{
			return false;
		}
		
	}
	
	//Terminated - pode ser Done, cancelled ou exception 
	public boolean isTaskTerminated(UUID taskUUID){
		
		Future<Message> taskTemp = mTasksResultPool.get(taskUUID);
		if( taskTemp.isDone()){
			return true;
		}else{
			return false;
		}
		
	}
	
	//CARLOS TO-DO implementar as outras formas de pedir pra desligar quando der tempo
	public void shutdown(){
	
		mExecutorService.shutdown();	
		
	}

	

	
	
	/**
	 * Verifica a fila de execução e retorna alguma tarefa que tenha sido completada, caso contário retorna null. A tarefa retornada é removida. Para checar qual tarefa terminou basta examinar o objeto "Message" retornado no Future( e.g., tempFuture.get().get("taskId");)
	 * @return
	 */
	public Future<Message> getACompletedTask(){
		
		Future<Message> tempFuture = mCompletionService.poll();
		
		try {
			if(tempFuture!=null){
				Message m = tempFuture.get();
				UUID id = (UUID) m.get("taskId");
				mTaskPool.remove(id);
				mTasksResultPool.remove(id);	
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return tempFuture;
	}
	
	
	public String getTaskRunningStep (UUID taskId){
		
		Task t = mTaskPool.get(taskId);
		if (t!=null){
			return t.getRunningStepName();
		}else{
			return null;
		}
		
	}
	
	private class GlobalExceptionHandlerDispatcher implements Observer{

		@Override
		public void update(Observable observable, Object data) {
			
			GlobalContextualException exception = (GlobalContextualException) data;
			
			Set<UUID> set = mTasksResultPool.keySet();
			for (UUID uuid : set) {
				if(mTasksResultPool.get(uuid).isDone()||mTasksResultPool.get(uuid).isCancelled()){
					mTaskPool.remove(uuid);
				}else{
				mTaskPool.get(uuid).handleGlobalContextualException(exception);	
				}
			}	
		}
		
	}
	
}
