package p2p;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
public class Screenshot implements Runnable{
	    private int interval;
	    private PeerServer server;
		public Screenshot(PeerServer server, int interval){
			this.interval = interval;
			this.server = server;
		}
	
	    public static BufferedImage getScreenshot() {
	    	BufferedImage bi = null;
	        try {
	            
	        	Robot screenshot = new Robot();
	            bi = screenshot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
	            //ImageIO.write(bi, "jpg", new File("C:/imageTest.jpg"));
	            
	        } catch (AWTException e) {
	            e.printStackTrace();
	        } //catch (IOException e) {
	          //  e.printStackTrace();
	        //}
	        return bi;
	    }

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				try {
					// Console.print("DEBUG");
					ImageMessage message = new ImageMessage(Screenshot.getScreenshot());
					server.deliverObj(message);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}