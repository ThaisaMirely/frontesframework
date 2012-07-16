package mdcc.frontes.utils;

/**
 * @author Carlos
 *
 */
public class TupleSpaceConfiguration {

	//private static final String ipUbicentre = "10.0.2.2";
	private static final String ipUbicentre = "192.168.0.121";
	//private static final String ipUbicentre = "192.168.0.130";
	private static final int portUbicentre = 9090;
	private static int reactionPort = 9098;
	
	/**
	 * @return Fornece o IP do UbiCentre. Deve ser configurado. 
	 */
	public static String getIpubicentre() {
		return ipUbicentre;
	}
	
	/**
	 * @return Fornece o porta que o UbiCentre está escutando. Deve ser configurado.
	 */
	public static int getPortubicentre() {
		return portUbicentre;
	}
	
	/**
	 * @return Porta na qual serão recebida as reações. 
	 */
	public static int getReactionport() {
		return reactionPort++;
	}
	
	
	
	
	
	
	
	
}
