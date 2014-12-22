package ui;

import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import java.awt.Color;
import java.awt.Image;

import javax.swing.JScrollPane;
import javax.swing.JButton;

import p2p.PeerNode;
import p2p.PeerServer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.Map;

import log.Console;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class MainWindow {
	
	public PeerServer server = null;
	private JFrame frame;
	private JTextField ipField;
	private JTextPane logField;
	private JList<String> list;
	private JButton connectButton;
	private JLabel imgLabel;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow win = new MainWindow();
					PeerServer peer = PeerServer.createInstance(win);
					win.server = peer;
					new Thread(peer).start();
					win.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 779, 622);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 485, 755, 97);
		frame.getContentPane().add(scrollPane);
		
		logField = new JTextPane(); 
		scrollPane.setViewportView(logField);
		
		ipField = new JTextField();
		ipField.setBounds(12, 12, 147, 19);
		frame.getContentPane().add(ipField);
		ipField.setColumns(10);
		
		connectButton = new JButton("Conectar");
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!ipField.getText().isEmpty()) server.connect(ipField.getText());
			}
		});
		connectButton.setBounds(171, 9, 98, 25);
		//connectButton.setEnabled(false);
		frame.getContentPane().add(connectButton);
		
		list = new JList<String>(new DefaultListModel<String>());
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				updateImage();
			}
		});
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setBorder(new LineBorder(new Color(0, 0, 0)));
		list.setBounds(618, 32, 149, 441);
		frame.getContentPane().add(list);
		
		imgLabel = new JLabel("");
		imgLabel.setBounds(12, 43, 604, 419);
		frame.getContentPane().add(imgLabel);
	}

	public void log(String txt) {
		Document paneDoc = logField.getDocument();
		try {
			paneDoc.insertString(paneDoc.getLength(), txt, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void updateImage(){
		String selectedKey = (String)list.getSelectedValue();

		if(selectedKey != null){
			BufferedImage bimg = server.getImageByKey(selectedKey);
			if(bimg != null){
				Console.print("IMAGEM NORMAL");
				imgLabel.setIcon(new ImageIcon(bimg.getScaledInstance(imgLabel.getWidth(), imgLabel.getHeight(), Image.SCALE_SMOOTH)));
			}else{
				Console.print("IMAGEM NULL");
				imgLabel.setIcon(null);
			}
		}
	}
	
	public void updateList(){
		DefaultListModel<String> model = (DefaultListModel<String>)list.getModel();
		model.removeAllElements();
		
		for(Map.Entry<String, PeerNode> entry: server.getPeers().entrySet()){
			PeerNode peer = (PeerNode)entry.getValue();
			model.addElement(peer.getKey());
		}
	}
}
