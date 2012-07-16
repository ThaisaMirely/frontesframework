package mdcc.frontes.taskmodel;

public enum State {

	READY("Ready"), RUNNING("Running"), ADAPTING("Adapting"), HANDLING("Handling"), STOPPED("Stopped"), WAITING("Waiting");
	
	private State(String string) {
		this.name = string;
	}
	
	private final String name;
	public String toString() {
	return name;
	}
	
}
