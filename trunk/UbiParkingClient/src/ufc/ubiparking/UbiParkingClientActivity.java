package ufc.ubiparking;

import java.io.IOException;
import java.util.UUID;

import ufc.ubiparking.R;
import ufc.ubiparking.R.id;
import ufc.ubiparking.R.layout;

import android.app.Activity;
import android.os.AsyncTask;
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
import br.ufc.great.syssu.base.interfaces.IReaction;
import br.ufc.great.syssu.ubibroker.UbiBroker;

public class UbiParkingClientActivity extends Activity {
    /** Called when the activity is first created. */
    
	// TODO - ID temporário para fins de teste - 19/07/2012
	private String taskID = UUID.randomUUID().toString();
	private String vagaRecomendada = "";
	
	// Variaveis relacionadas ao uso do espaco de tuplas
	private static UbiBroker broker;
	private static IDomain domain;
	//private static String ipUbicentre = "192.168.5.151";
	private static String ipUbicentre = "192.168.0.120";
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
	
	
	private Button btRequestVaga, btConfirmVaga, btFinishParking;
	private EditText edittextLog; 
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        btRequestVaga = (Button) findViewById(R.id.buttonRequestVaga);
        btConfirmVaga = (Button) findViewById(R.id.ButtonConfirmVaga);
        btFinishParking = (Button) findViewById(R.id.ButtonFinishParking);
        edittextLog = (EditText) findViewById(R.id.editTextLog);
        
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
		btRequestVaga.setOnClickListener(new OnClickListener() {
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
		});
		
		//confirmVaga
		btConfirmVaga.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					postVagaConfirmation(taskID, vagaRecomendada);
				} catch (Exception e) {
					Toast.makeText(v.getContext(), "Problema ao postar no Espaço de Tuplas." + " " + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});
		
		
		//finish Parking
		btFinishParking.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					postParkingFinish(taskID);
				} catch (Exception e) {
					Toast.makeText(v.getContext(), "Problema ao postar no Espaço de Tuplas." + " " + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});
		
	}

	
	protected void postParkingFinish(String taskID) throws TupleSpaceException, TupleSpaceSecurityException {
		Tuple t = new Tuple();
		t.addField(parkingFinishParkingStr, taskID);
		domain.put(t,parkingKeyStr);
		updateEditText("Tupla de Finish postada: " + t.toString());
		
	}


	private String postRequestVagaTuple(String taskid) throws TupleSpaceException, TupleSpaceSecurityException{
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