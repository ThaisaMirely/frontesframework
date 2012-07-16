package mdcc.frontes.dimension;

public abstract class AbstractRole {

	private String roleName;
	
	public AbstractRole(String roleName) {
		this.roleName = roleName;
	}
	
	public AbstractRole() {
		this.roleName = "Default";
	}
	
	public String getRoleName() {
		return roleName;
	}

	abstract String getRoleAccessKey();
	
}