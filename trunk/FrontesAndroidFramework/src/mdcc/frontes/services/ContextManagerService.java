package mdcc.frontes.services;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import mdcc.frontes.contextmodel.GeneralContext;
import mdcc.frontes.contextmodel.ContextInformation;
import mdcc.frontes.utils.TupleSpaceConfiguration;
import android.util.Log;
import br.ufc.great.syssu.base.Pattern;
import br.ufc.great.syssu.base.Tuple;
import br.ufc.great.syssu.base.TupleField;
import br.ufc.great.syssu.base.TupleSpaceException;
import br.ufc.great.syssu.base.interfaces.IReaction;
import br.ufc.great.syssu.ubibroker.Domain;
import br.ufc.great.syssu.ubibroker.UbiBroker;



public class ContextManagerService extends Observable implements IReaction{

	private static final String CONTEXT_FIELD_STRING = "#ContextInformation#";
	private static final String CONTEXT_DOMAIN_NAME = "#DomainName#";
	private static final String GLOBAL_CONTEXT_RESTRICTION_STR = "";
	private static final String GLOBAL_CONTEXT_KEY_STR = "";
	
	// Singleton Pattern
	private static ContextManagerService instance;
	private GeneralContext mContext;
	
	
	//Id para implementação da IReaction - Mandatório
	private static int num = 1;
	protected Object id ;
	
	private Domain domain;
	/**
	 * Padrão de tuplas de Contexto
	 */
	private static Pattern contextPattern;
		
	private ContextManagerService() {
		setId(num++);
		createContextTuplePattern();
		this.domain = obtainTupleSpaceAccess();
		mContext = getInitialContext(domain);
		subscribeToTupleSpace(domain);
	}

	private void subscribeToTupleSpace(Domain domain){
		try {
			this.setId(domain.subscribe(this, "put", GLOBAL_CONTEXT_KEY_STR));
		} catch (TupleSpaceException e) {
			e.printStackTrace();
			Log.d("FrontesDEBUG", "Problema ao se subscrever para receber tuplas de contexto: " + e.getMessage());
		}
	}

	private GeneralContext getInitialContext(Domain domain){
		List<Tuple> initialContext = null;
		try {
			initialContext = domain.read(contextPattern, GLOBAL_CONTEXT_RESTRICTION_STR, GLOBAL_CONTEXT_KEY_STR);
		} catch (TupleSpaceException e) {
			e.printStackTrace();
			Log.d("FrontesDEBUG", "Problema ao capturar o contexto inicial: " + e.getMessage());
		}
		return  parseContextTuplesToContext(initialContext);
	}
	
	private Domain obtainTupleSpaceAccess()  {
		UbiBroker broker;
		Domain domain = null;
		try {
				broker = UbiBroker.createUbibroker(TupleSpaceConfiguration.getIpubicentre(), TupleSpaceConfiguration.getPortubicentre(), TupleSpaceConfiguration.getReactionport());
				domain = (Domain) broker.getDomain(CONTEXT_DOMAIN_NAME);
			
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("FrontesDEBUG", "ContextManagerService" + e.getMessage());
		} 
		catch (TupleSpaceException e) {
			e.printStackTrace();
			Log.d("FrontesDEBUG", "ContextManagerService" + e.getMessage());
		} 
		
		return domain;
	}

	private void createContextTuplePattern() {
		contextPattern = new Pattern();
		contextPattern.addField(CONTEXT_FIELD_STRING, "?");
	}
	
	private GeneralContext parseContextTuplesToContext(List<Tuple> initialContext) {
		GeneralContext tempCtx = new GeneralContext();
		if(initialContext!=null && !initialContext.isEmpty()){
			for (Tuple tuple : initialContext) {
				Iterator<TupleField> it = tuple.iterator();
				while (it.hasNext()) {
					TupleField tupleField = (TupleField) it.next();
					tempCtx.addContextInformation(new ContextInformation(tupleField.getName(), tupleField.getValue()));
				}
			}
		}
		
		return tempCtx;
	}

	public static ContextManagerService getInstance(){

		if (instance==null){
			return instance = new ContextManagerService();
		}else{
			return instance;
		}
		
	}
	
	public GeneralContext getContext(){
		return mContext;
	}
	
	public void setContext(GeneralContext ctx){
		this.mContext = ctx;
		setChanged();
		notifyObservers(ctx);
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
		
		return contextPattern;
	}

	@Override
	public String getRestriction() {
		
		return GLOBAL_CONTEXT_RESTRICTION_STR;
	}

	@Override
	public void react(Tuple tuple) {
		GeneralContext tempCtx = new GeneralContext();
		Iterator<TupleField> it = tuple.iterator();
		while (it.hasNext()) {
			TupleField tupleField = (TupleField) it.next();
			tempCtx.addContextInformation(new ContextInformation(tupleField.getName(), tupleField.getValue()));
		}
		this.setContext(tempCtx);
		notifyObservers(tempCtx);
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		domain.unsubscribe(getId(), GLOBAL_CONTEXT_KEY_STR);
	}
	
	
	
}
