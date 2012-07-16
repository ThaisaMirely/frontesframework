package mdcc.frontes.propagation.tuplespace;

public enum FrontesTupleTypeValues {

	CONTEXTUAL_EXCEPTION_TUPLE("ContextualException"),
	VALUE2("outro");
	
	private String value;
	
	FrontesTupleTypeValues(String value){
		this.value = value;
	}
	
	public String getTypeValue() {
		return value;
	}
	
}
