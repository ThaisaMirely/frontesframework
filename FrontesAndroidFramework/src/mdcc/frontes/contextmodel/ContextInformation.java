package mdcc.frontes.contextmodel;


public class ContextInformation { 

	protected String name;
	protected Object value;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	public ContextInformation() {
		
	}
	
	public ContextInformation(String name, Object value) {
		this.name = name;
		this.value = value;
		
	}
	
	@Override
	public String toString() {
		return this.getClass().getName() + ":" + getName() + getValue().toString();
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(o.toString().equals(this.toString())){
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
}
