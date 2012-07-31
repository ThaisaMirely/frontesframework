package mdcc.frontes.taskmodel;

import java.util.Hashtable;
import java.util.Set;

public class Message {

	private Hashtable<String, Object> content;
	//Inserir tipo da mesagem 
	// campo conte�do e remover o hash acima. 
	
	public Message() {
		content = new Hashtable<String, Object>();
	}
	
	public void put(String key, Object value) {
		this.content.put(key, value);
	}
	
	public Object get(String key) {
		return this.content.get(key);
	}
	
	@Override
	public String toString() {
		String retorno = "";
		Set<String> keys = content.keySet();
		for (String key : keys) {
			retorno = retorno + key + ":"+ content.get(key).toString() + " \n ";  
		}
		return retorno;
	}
	
}
