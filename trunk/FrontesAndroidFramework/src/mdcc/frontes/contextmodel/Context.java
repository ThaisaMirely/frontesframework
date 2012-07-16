package mdcc.frontes.contextmodel;

import java.util.HashSet;
import java.util.Set;

public class Context {

	Set<ContextInformation> contextualInformation;
	
	
	public Context() {
		contextualInformation = new HashSet<ContextInformation>();
	}
	
	/**
	 * @param key
	 * @param value
	 * @return Return true for success,  false otherwise. 
	 */
	public boolean addContextInformation(ContextInformation value){
		
		if(value!=null){
			return contextualInformation.add(value);
		}else {
			return false;	
		}
	}
	
	/**
	 * @param key
	 * @return Return an object with the context information. It returns null if the context associated with that key is not found
	 *//*
	public ContextInformation getContextInformation(String key){
		
		if(key!=null){
			return contextualInformation.get(key);
		}
		return null;
			
	}*/
	
	/**
	 * @return Quantidade de informações de contexto armazenadas
	 */
	public int getSize(){
		return contextualInformation.size();
	}
	
	public Set<ContextInformation> getContextualInformation() {
		return contextualInformation;
	}
	
}
