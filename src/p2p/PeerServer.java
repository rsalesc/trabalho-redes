package p2p;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import ui.MainWindow;
import log.Console;

public class PeerServer implements Runnable{
	public static int PORT = 7171;
	public static int INTERVAL = 3000;
	private static PeerServer instance = null;
	private ServerSocket server;
	private Map<String, PeerNode> peers;
	private Set<String> establishing;
	private MainWindow window;
	
	private PeerServer(int port){
		try {
			server = new ServerSocket(port);
			peers = new HashMap<String, PeerNode>();
			establishing = new HashSet<String>();
			Screenshot ss = new Screenshot(this, 3000);
			new Thread(ss).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static PeerServer createInstance(MainWindow window){
		instance = new PeerServer(PORT);
		instance.setMainWindow(window);
		Console.logEvent("LISTENING", String.valueOf(PORT));
		return instance;
	}
	
	public static PeerServer getInstance(){
		if(instance == null)
			instance = new PeerServer(PORT);
		return instance;
	};
	
	public void connect(String key){
		// check if it is already connected to this key
		if(peers.containsKey(key) || establishing.contains(key)) return;
		
		establishing.add(key);
		PeerNode newPeer = PeerNode.createPeer(key);
		if(newPeer == null){
			Console.message("A conexao com o par " + key + " nao pode ser estabelecida.");
		}else{
			// connected successfully
			peers.put(newPeer.getKey(), newPeer);
			new Thread(newPeer).start();
			Console.logEvent("CONNECTED", key);
			getMainWindow().updateList();
		}
		establishing.remove(key);
	}
	
	public void run(){
		// wait for connections
		while(true){
			try{
				Socket csocket = server.accept();
				PeerNode newPeer = new PeerNode(csocket);
				// PropagateMessage prop = (PropagateMessage)newPeer.readObj();
				String key = newPeer.getKey();
				if(!peers.containsKey(key) && !establishing.contains(key)){
					peers.put(key, newPeer);
					new Thread(newPeer).start();
					Console.logEvent("CONNECTED", key);
					// propagate newPeer ip
					this.propagate(key);
					getMainWindow().updateList();
					//
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	public void propagate(String key){
		if(peers.containsKey(key) || establishing.contains(key)) return;
		Message message = new PropagateMessage(key);
		for(Map.Entry<String, PeerNode> entry : peers.entrySet()){
			PeerNode peer = (PeerNode)entry.getValue();
			if(peer.getKey() == key) continue;
			if(peer.getSocket().isClosed()){
				removePeerByKey(peer.getKey());
				continue;
			}
			peer.sendObj(message);
		}
	}
	
	public void deliverObj(Message message){
		for(Map.Entry<String, PeerNode> entry : peers.entrySet()){
			PeerNode peer = (PeerNode)entry.getValue();
			if(peer.getSocket().isClosed()){
				removePeerByKey(peer.getKey());
				continue;
			}
			peer.sendObj(message);
		}
	}
	
	public void removePeerByKey(String key){
		peers.remove(key);
		establishing.remove(key);
		getMainWindow().updateList();
	}
	
	public String[] getKeyArray(){
		return (String[]) peers.keySet().toArray();
	}
	
	public BufferedImage getImageByKey(String key){
		if(!peers.containsKey(key)) return null;
		return peers.get(key).getBufferedImage();
	}
	
	public MainWindow getMainWindow(){
		return window;
	}
	
	public void setMainWindow(MainWindow window){
		this.window = window;
	}
	
	public Map<String, PeerNode> getPeers(){
		return peers;
	}
}
