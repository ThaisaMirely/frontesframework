package mdcc.frontes.propagation.tuplespace;

public enum FrontesTupleFields {

	EXCEPTION_TUPLE_TYPE("TupleType"),
	EXCEPTION_TUPLE_TYPE2("");
	
	private String fieldName;
	
	FrontesTupleFields(String fieldName){
		this.fieldName = fieldName;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
}
