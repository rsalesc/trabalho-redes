package p2p;

import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = 9865698781L;
	protected MessageType type;
	protected byte[] data;
	
	protected Message(MessageType type){
		this.type = type;
	}
	
	protected Message(MessageType type, byte[] data){
		this(type);
		this.setData(data);
	}
	protected void setData(byte[] data){
		this.data = data;
	}
	public MessageType getType(){ return type;}
}
