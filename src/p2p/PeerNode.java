package p2p;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.imageio.ImageIO;

import log.Console;
import p2p.Message;

public class PeerNode implements Runnable {
	private Socket socket = null;
	private String key = null;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;
	private BufferedImage img = null;
	
	public PeerNode(Socket socket){
		this.socket = socket;
		this.key = this.socket.getInetAddress().getHostAddress();
	}
	
	private PeerNode(String ip) throws Exception{
		this.key = ip;
		socket = new Socket(ip, PeerServer.PORT);
	}
	
	public static PeerNode createPeer(String ip){
		try{
			return new PeerNode(ip);
		}catch(Exception ex){
			return null;
		}
	}
	
	public boolean initializeStreams(){
		try {
			this.out = new ObjectOutputStream(this.socket.getOutputStream());
			this.in = new ObjectInputStream(this.socket.getInputStream());
			return true;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
	}
	
	public boolean sendObj(Message obj){
		try {
			this.out.writeObject(obj);
			return true;
		} catch (Exception e) {
			Console.print("Problema no envio.");
			return false;
		}
	}
	public Message readObj(){
		Message obj = null;
		try {
			obj = (Message)this.in.readObject();
			return obj;
		} catch (Exception e) {
			Console.print("Problema na leitura.");
			return null;
		}
		
	}
	public boolean close(){
		try {
			if(!this.socket.isClosed()) this.socket.close();
			PeerServer.getInstance().removePeerByKey(getKey());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public InetAddress getInetAddress(){
		return socket.getInetAddress();
	}
	
	public String getKey(){
		return key;
	}
	
	public void setKey(String key){
		this.key = key;
	}
	
	public void run(){
		// connected successfully
		this.initializeStreams();
		PeerServer server = PeerServer.getInstance();
		// wait for messages and for the socket to be detached
		while(!this.socket.isClosed()){
			Message msg = this.readObj();
			if(msg == null) continue;
			
			switch(msg.getType()){
			case PROPAGATE:
				String key = new String(msg.data);
				server.propagate(key);
				server.connect(key);
				// log
				Console.logEvent("PROPAGATE", key);
				break;
			case IMAGE:
				ByteArrayInputStream stream = new ByteArrayInputStream(msg.data);
				try {
					BufferedImage tmpImg = ImageIO.read(stream);
					stream.close();
					img = tmpImg;
					// log
					Console.logEvent("IMAGE", "received from " + getKey());
					server.getMainWindow().updateImage();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Console.logEvent("IMAGE", "couldnt be received");
				}
				break;
			default:
				Console.logEvent("UNKNOWN", "message received");
			}
		}
		
		// connection closed;
		this.close();
	}
	
	public Socket getSocket(){
		return socket;
	}
	
	public BufferedImage getBufferedImage(){
		return img;
	}
}
