package mdcc.frontes.taskmodel;

import java.util.Hashtable;

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
}
