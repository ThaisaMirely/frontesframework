package br.ufc.ubiparkingclient_using_frontes;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;

import mdcc.frontes.contextmodel.ContextInformation;
import mdcc.frontes.contextmodel.GeneralContext;
import mdcc.frontes.exception.ContextualException;
import mdcc.frontes.exception.handlerinterfaces.IPersonalizedContextualExceptionHandler;
import mdcc.frontes.taskmanager.TaskExecutor;
import mdcc.frontes.taskmodel.task.Task;
import mdcc.frontes.taskmodel.task.TaskStep;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import br.ufc.great.syssu.base.Pattern;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.TupleField;
import br.ufc.great.syssu.base.TupleSpaceException;
import br.ufc.great.syssu.base.TupleSpaceSecurityException;
import br.ufc.great.syssu.base.interfaces.IDomain;
import br.ufc.great.syssu.ubibroker.UbiBroker;

public class UbiParkingClientActivity extends Activity {
    /** Called when the activity is first created. */
    
	
	private Task estacionarTask;
	
	// Variaveis relacionadas ao uso do espaco de tuplas
	private static UbiBroker broker;
	private static IDomain domain;
	//private static String ipUbicentre = "192.168.0.120";
	private static String ipUbicentre = "192.168.5.151";
	private static int portUbicentre = 9090;
	private static int reactionPort = 9094;
	private static final String CONTEXT_DOMAIN_NAME = "#DomainName#";
	public static final String parkingRequestStr = "parkingRequest";
	public static final String parkingResponseStr = "parkingResponse";
	public static final String parkingVagaFieldStr = "#vaga#";
	public static final String parkingReservationStr = "parkingReservation";
	public static final String parkingFinishParkingStr = "parkingFinish";
	public static final String parkingRegisterStr = "parkingRegister";
	public static final String parkingRestrictionStr = "";
	public static final String parkingKeyStr = "";
	
	
	//private Button btRequestVaga, btConfirmVaga, btFinishParking;
	private Button btStartTask;
	private EditText edittextLog; 
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //btRequestVaga = (Button) findViewById(R.id.buttonRequestVaga);
        //btConfirmVaga = (Button) findViewById(R.id.ButtonConfirmVaga);
        //btFinishParking = (Button) findViewById(R.id.ButtonFinishParking);
        edittextLog = (EditText) findViewById(R.id.editTextLog);
        edittextLog.setClickable(false);
        btStartTask = (Button) findViewById(R.id.buttonStartParkingTask);
        
        
        
        try {
			setupTupleSpace();
		}  catch (Exception e) {
			Toast.makeText(this, "Problema no acesso ao Espaço de Tuplas", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
        setListeners();
        
        
    }


	private void setupTupleSpace() throws IOException, TupleSpaceException {
		if(broker == null && domain ==null){
			broker = UbiBroker.createUbibroker(ipUbicentre, portUbicentre,
					reactionPort);
			domain = broker.getDomain(CONTEXT_DOMAIN_NAME);	
		}
	}

	private void setListeners() {
		// Request Vaga 
		/*btRequestVaga.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

								try {
									
									vagaRecomendada = postRequestVagaTuple(taskID);
									System.out.println("Recebido a recomendação de vaga: Vaga" + vagaRecomendada );
					
								} catch (TupleSpaceException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (TupleSpaceSecurityException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
			}
		});*/
		
		//confirmVaga
		/*btConfirmVaga.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					postVagaConfirmation(taskID, vagaRecomendada);
				} catch (Exception e) {
					Toast.makeText(v.getContext(), "Problema ao postar no Espaço de Tuplas." + " " + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});*/
		
		
		//finish Parking
		/*btFinishParking.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					postParkingFinish(taskID);
				} catch (Exception e) {
					Toast.makeText(v.getContext(), "Problema ao postar no Espaço de Tuplas." + " " + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});
		*/
		
		btStartTask.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(domain!=null){
					updateEditText("Acessando ao SysSU - Domain name: " + domain.getName());
				}
				updateEditText("Tarefa Estacionar - Iniciada...");
				
				//Inicia a tarefa criada
				setupTaskSteps();
				final TaskExecutor te = TaskExecutor.getInstance();
				final UUID id = te.submitTask(estacionarTask);
				//Message result = te.getTask(id).get();
				
			}
		});
		
	}

	
	private void setupTaskSteps(){
		
		//Cria os tasksteps
		
		TaskStep requisitarVagaStep = new TaskStep("Step1") {
			
			@Override
			protected Set<? extends ContextualException> getExceptionalContext() {
				return null;
			}
			
			@Override
			protected void doWhileExecutingstep() throws InterruptedException {
				try {
					Thread.sleep(60000);
					String vaga = postRequestVagaTuple((String)getBundle().get("taskid"));
					getBundle().put("vaga", vaga);
					updateEditText("Recomendação de vaga recebida: Vaga " + vaga);
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Exception de comunicação." + e.getMessage(), Toast.LENGTH_LONG).show();
				}
				
			}
		};
		
		TaskStep confirmaVagaStep = new TaskStep("Step2") {
			
			@Override
			protected Set<? extends ContextualException> getExceptionalContext() {
				return null;
			}
			
			@Override
			protected void doWhileExecutingstep() throws InterruptedException {
				try {
					postVagaConfirmation((String)getBundle().get("taskid"), (String)getBundle().get("vaga"));
					updateEditText("Postado - Aceitação da vaga.");
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Exception de comunicação." + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		};
		
		TaskStep finalizaEstacionamentoStep = new TaskStep("Step3") {
			
			@Override
			protected Set<? extends ContextualException> getExceptionalContext() {
				
				// TODO - Adicionar uma exceção contextual e testar - 21/08/2012
				/*	GeneralContext exceptionalContext = new GeneralContext();
				exceptionalContext.addContextInformation(new ContextInformation("vaga_"+(String)getBundle().get("vaga"), "bloqueada"));
				
				ContextualException c = new ContextualException(exceptionalContext);
				HashSet<ContextualException> setExceptionsThatCanBeRaised = new HashSet<ContextualException>();
				setExceptionsThatCanBeRaised.add(c);
				return setExceptionsThatCanBeRaised;*/
				return null;
			}
			
			@Override
			protected void doWhileExecutingstep() throws InterruptedException {
				try {
					updateEditText("Passo de finalização em progresso...");
					//Thread.sleep(60000);
					postParkingFinish((String)getBundle().get("taskid"));
					updateEditText("Estacionamento Finalizado com sucesso.");
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Exception de comunicação." + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		};
	
		//Cria Carga de trabalho da tarefa
		LinkedHashMap<String, TaskStep> workload = new LinkedHashMap<String, TaskStep>();
		workload.put(requisitarVagaStep.getName(), requisitarVagaStep);
		workload.put(confirmaVagaStep.getName(), confirmaVagaStep);
		workload.put(finalizaEstacionamentoStep.getName(), finalizaEstacionamentoStep);

		//Instancia a tarefa com a carga de trabalho criada.
		estacionarTask = new Task(workload);
		
		estacionarTask.setUserDefinedContextualExceptionHandler(new IPersonalizedContextualExceptionHandler() {
			
			@Override
			public void handleContextualException(Task task,
					ContextualException exception) {
				Toast.makeText(getApplicationContext(), "Exceção detectada", Toast.LENGTH_LONG).show();
				
			}
		});
		
	}
	
	
	
	
	protected void postParkingFinish(String taskID) throws TupleSpaceException, TupleSpaceSecurityException {
		Tuple t = new Tuple();
		t.addField(parkingFinishParkingStr, taskID);
		domain.put(t,parkingKeyStr);
		updateEditText("Tupla de Finish postada: " + t.toString());
		
	}


	public String postRequestVagaTuple(String taskid) throws TupleSpaceException, TupleSpaceSecurityException{
		String retorno;
		Tuple t = new Tuple();
		t.addField(parkingRequestStr, taskid);
		domain.put(t, parkingKeyStr);
		retorno = waitVagaRecommendation(taskid);
		updateEditText("Tupla de request postada: " + t.toString());
		
		return retorno;
	}


	private String waitVagaRecommendation(String taskid) throws TupleSpaceException, TupleSpaceSecurityException {
		
		String retorno = null;
		Tuple t = domain.takeOneSync(getVagaRecomendationPattern(taskid), parkingRestrictionStr,parkingKeyStr, 10000);
		updateEditText("Tupla de recomendação recebida: " + t.toString());
		for (TupleField tuplefield : t) {
			if(tuplefield.getName().equalsIgnoreCase(parkingVagaFieldStr)){
				retorno = (String) tuplefield.getValue();
			}
		}
		return retorno;
	}


	private Pattern getVagaRecomendationPattern(String taskid2) {
		// TODO - Colocar mais um campo na recomendação de vaga (Lado cliente e servidor) - 27/07/2012
		Pattern p = new Pattern();
		//p.addField(parkingRequestStr, taskid2);
		p.addField(parkingVagaFieldStr, "?");
		return p;
	}
	
	private void postVagaConfirmation(String taskID,
			String vagaRecomendada) throws TupleSpaceException, TupleSpaceSecurityException {
		Tuple t = new Tuple();
		t.addField(parkingReservationStr, taskID);
		t.addField("task", taskID);
		t.addField("vaga", vagaRecomendada);
		domain.put(t, parkingKeyStr); 
		updateEditText("Tupla de confirmação postada: " + t.toString());
		
	}
	
	private void updateEditText(String appendContent){
		final String teste = appendContent; 
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String content = edittextLog.getText().toString();
				content = content + "\n" + teste;
				edittextLog.setText(content);
			}
		});
	}

}