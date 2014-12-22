package log;

import p2p.PeerServer;

public class Console {
	public static void print(String txt){
		System.out.println(txt);
	}
	public static void message(String text){
		PeerServer.getInstance().getMainWindow().log("ALERT " + text + "\n");
	}
	public static void logEvent(String event, String txt){
		PeerServer.getInstance().getMainWindow().log(event + " " + txt + "\n");
	}
}
