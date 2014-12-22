package p2p;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageMessage extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1934423293819873215L;

	public ImageMessage(BufferedImage img) throws IOException{
		super(MessageType.IMAGE);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ImageIO.write(img, "JPG", stream);
		this.setData(stream.toByteArray());
		stream.close();
	}
}
