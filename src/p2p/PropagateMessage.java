package p2p;

public class PropagateMessage extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 669255347382801012L;

	public PropagateMessage(String key){
		super(MessageType.PROPAGATE, key.getBytes());
	}
}
