package mdcc.frontes.services;

import java.io.IOException;
import java.util.Iterator;
import java.util.Observable;

import mdcc.frontes.contextmodel.GeneralContext;
import mdcc.frontes.contextmodel.ContextInformation;
import mdcc.frontes.exception.globalcontextualexception.GlobalContextualException;
import mdcc.frontes.utils.TupleSpaceConfiguration;
import android.util.Log;
import br.ufc.great.syssu.base.Pattern;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.TupleField;
import br.ufc.great.syssu.base.TupleSpaceException;
import br.ufc.great.syssu.base.interfaces.IReaction;
import br.ufc.great.syssu.ubibroker.Domain;
import br.ufc.great.syssu.ubibroker.UbiBroker;

public class ExceptionManagerService extends Observable implements IReaction {

	private static final String GLOBAL_EXCEPTION_FIELD_STRING = "#GlobalException#";
	private static final String GLOBAL_CONTEXT_FIELD_STRING = "#GlobalContextInformation#";
	private static final String EXCEPTION_DOMAIN_NAME = "#DomainName#";
	private static final String GLOBAL_EXCEPTION_RESTRICTION_STR = "";
	private static final String GLOBAL_EXCEPTION_KEY_STR = "";
	
	//Singleton 
	private static ExceptionManagerService instance;

	//IReaction implementation
	private static int num = 1;
	protected Object id ;
	private static Pattern globalExceptionPattern;
	private Domain domain;

	
	
	private ExceptionManagerService() {
		setId(num++);
		createGlobalExceptionTuplePattern();
		this.domain = obtainTupleSpaceAccess();
		subscribeToTupleSpace(domain);
	}
	
	public static ExceptionManagerService getInstance(){
		if (instance==null){
			return instance = new ExceptionManagerService();
		}else{
			return instance;
		}
	}
	
	@Override
	public void setId(Object id) {
		this.id = id;
	}

	@Override
	public Object getId() {
		return this.id;
	}

	@Override
	public Pattern getPattern() {
		return globalExceptionPattern;
	}

	@Override
	public String getRestriction() {
		return GLOBAL_EXCEPTION_RESTRICTION_STR;
	}

	@Override
	public void react(Tuple tuple) {
		
		GeneralContext tempCtx = new GeneralContext();
		GlobalContextualException exception = null;
		
		Iterator<TupleField> it = tuple.iterator();
		while (it.hasNext()) {
			TupleField tupleField = (TupleField) it.next();
			if(tupleField.getName().equals(GLOBAL_EXCEPTION_FIELD_STRING)){
				int exceptiontypeint = Integer.valueOf(tupleField.getValue().toString());
				exception = new GlobalContextualException(tempCtx, exceptiontypeint);
				
			}else if (tupleField.getName().equals(GLOBAL_CONTEXT_FIELD_STRING)) {
				tempCtx.addContextInformation(new ContextInformation(tupleField.getName(), tupleField.getValue()));	
			}
		}
		setChanged();
		notifyObservers(exception);
	}

	
	private void createGlobalExceptionTuplePattern() {
		globalExceptionPattern = new Pattern();
		globalExceptionPattern.addField(GLOBAL_EXCEPTION_FIELD_STRING, "?");
		globalExceptionPattern.addField(GLOBAL_CONTEXT_FIELD_STRING, "?");
	}

	private Domain obtainTupleSpaceAccess()  {
		UbiBroker broker;
		Domain domain = null;
		try {
				broker = UbiBroker.createUbibroker(TupleSpaceConfiguration.getIpubicentre(), TupleSpaceConfiguration.getPortubicentre(), TupleSpaceConfiguration.getReactionport());
				domain = (Domain) broker.getDomain(EXCEPTION_DOMAIN_NAME);
			
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("FrontesDEBUG", "ExceptionManagerService" + e.getMessage());
		} 
		catch (TupleSpaceException e) {
			e.printStackTrace();
			Log.d("FrontesDEBUG", "ExceptionManagerService" + e.getMessage());
		} 
		
		return domain;
	}
	
	private void subscribeToTupleSpace(Domain domain){
		try {
			this.setId(domain.subscribe(this, "put", GLOBAL_EXCEPTION_KEY_STR));
		} catch (TupleSpaceException e) {
			e.printStackTrace();
			Log.d("FrontesDEBUG", "Problema ao se subscrever para receber tuplas de exceções: " + e.getMessage());
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		domain.unsubscribe(getId(), GLOBAL_EXCEPTION_KEY_STR);
	}
}
