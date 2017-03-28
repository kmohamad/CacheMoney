package ScreenPack;

import java.awt.Dialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JWindow;

import com.sun.glass.events.WindowEvent;
import com.sun.prism.Image;

import GamePack.*;
import InterfacePack.Music;
import InterfacePack.Sounds;
import MultiplayerPack.MBytePack;
import MultiplayerPack.MClient;
import MultiplayerPack.MHost;
import MultiplayerPack.PlayingInfo;
import MultiplayerPack.SqlRelated;
import MultiplayerPack.UnicodeForServer;

public class GameScreen extends JFrame{
	private final int NUMBER_OF_MUSIC = 5;
	private JPanel mainPanel;
	private int myComp_height;
	private DicePanel dicePanel;
	private Player[] players;
	private MoneyLabels mLabels;
	private MClient client;
	private MBytePack mPack;
	private UnicodeForServer unicode;
	private BoardPanel boardPanel;
	private JDialog playerInfo;
	private JButton showMortgage;
	private JButton showInfo;
	private Insets insets;
	private JCheckBox muteMusic;
	private JCheckBox muteSounds;
	private int scheduledMusic;
	private int loadingProgress;
	private int totalPlayers;
	private JButton btnExit;
	private SizeRelated sizeRelated;
	private PropertyDisplay pDisplay;
	private JButton giveJailFreeCard;
	private PlayingInfo pInfo;
	private JDialog mortgageWindow;
	private SqlRelated sqlRelated;
	private JComboBox selectMortgage;
	private JButton sellConfirm;
	private JButton sellCancel;
	private JLabel pleaseSelectMortgage;
	// called if user is the host
	public GameScreen(boolean isSingle, int totalplayers){
		//setAlwaysOnTop(true);
		this.totalPlayers = totalplayers;
		initEverything(true,isSingle);
		if(!isSingle)
			addHost();
		setWindowVisible();
		exitSetting(true);
	}
	// called if user is the client
	public GameScreen(boolean isSingle, String ip, int port) throws UnknownHostException, IOException{
		initEverything(false,isSingle);
		try{
			addClient(ip,port);
		}
		catch (IOException e){
			mainPanel.setVisible(false);
			this.dispose();
			throw new IOException();
		}
		setWindowVisible();
		exitSetting(false);
	}
	public void setNumPlayer(int numPlayer){
		boardPanel.PlacePiecesToBaord(numPlayer);
		System.out.print(numPlayer);
		totalPlayers = numPlayer;
	}
	private void setWindowVisible(){
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.setVisible(true);
		if (pInfo.isSingle()){
			Sounds.waitingRoomJoin.playSound();
		}
		mainPanel.add(muteSounds);
		mainPanel.add(muteMusic);
		playScheduledMusic();
		
	}
	
	private void initEverything(boolean isHost, boolean isSingle){
		loadingProgress = 0;
//		sqlRelated = SqlRelated.getInstance();
		mPack = MBytePack.getInstance();
		pInfo = PlayingInfo.getInstance();
		pInfo.setIsSingle(isSingle);
		unicode = UnicodeForServer.getInstance();
		scaleBoardToScreenSize();
		
		createPlayers();
		init(isHost);
		setGameScreenBackgroundColor();
	}
	

	private void exitSetting(boolean isHost){
		if(pInfo.isSingle())
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		else
			addActionListenerForExit(isHost);
	}
	private void setGameScreenBackgroundColor() {
		Color boardBackgroundColor = new Color(0, 180, 20); // DARK GREEN
		this.setBackground(boardBackgroundColor);
	}
	// TODO: need to find the way to send exit meesage to server when closing windows.
	private void addActionListenerForExit(boolean isHost){
		addWindowListener( new WindowAdapter() {
			@Override
            public void windowClosing(java.awt.event.WindowEvent e) {
            	super.windowClosing(e);
            	exitForServer(false,isHost);
            	
            }
        } );
	}
	private void exitForServer(boolean isSingle, boolean isHost){
		if(!isSingle){
			if(isHost){
	    		// need to figure out the problem
//	    		while(host == null || host.getOutputStream() == null){
//	        		try {
//						Thread.sleep(1);
//					} catch (InterruptedException e1) {
//						e1.printStackTrace();
//					}
//	        	}
//	        	host.writeToServer(mPack.packSimpleRequest(unicode.HOST_DISCONNECTED), mPack.getByteSize());
	    		
	    	}else{
	    		while(pInfo.getOutputStream() == null){
	        		try {
						Thread.sleep(1);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
	        	}
	        	pInfo.sendMessageToServer(mPack.packPlayerNumber(unicode.DISCONNECTED,pInfo.getMyPlayerNum()));

	    	}
		}
        System.exit(1);
	}
	private void addExitListener(boolean isHost){
		btnExit.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				exitForServer(pInfo.isSingle(), isHost);
			}
		});
	}
	private void scaleBoardToScreenSize() {
		GraphicsDevice screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		myComp_height = (int)screenSize.getDisplayMode().getHeight();
		setSize(myComp_height + 125, myComp_height - 100);
	}
	
	
	
	private void createPlayers() {
		players = new Player[4];
		for(int i=0; i<4; i++)
		{
			players[i] = Player.getInstance(i);
		}
	}
	private void addButtonListeners()
	{
		showInfo.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e) {
				Sounds.quickDisplay.playSound();
					playerInfo.setVisible(true);
					//playerInfo.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
			}
			@Override
			public void mousePressed(MouseEvent e) {
				
				
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				
				
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				
				
			}
			@Override
			public void mouseExited(MouseEvent e) {	
				
			}
		});
		giveJailFreeCard.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e) {
				players[0].setJailFreeCard(1);			//NEED TO GET PLAYER VALUE
				mLabels.reinitializeMoneyLabels();
				playerInfo.repaint();
			}
			@Override
			public void mousePressed(MouseEvent e) {
				
				
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				
				
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				
				
			}
			@Override
			public void mouseExited(MouseEvent e) {	
				
			}
		});
		showMortgage.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e) {
				mortgageWindow.setVisible(true);
			}
			@Override
			public void mousePressed(MouseEvent e) {
				
				
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				
				
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				
				
			}
			@Override
			public void mouseExited(MouseEvent e) {	
				
			}
		});
		
	}
	
	private void init(boolean isHost){
		// 10
		sizeRelated = SizeRelated.getInstance();
		pDisplay = PropertyDisplay.getInstance();
		mainPanel = new JPanel(null);
		mainPanel.setLayout(null);
		getContentPane().add(mainPanel);
		mainPanel.add(pDisplay);
		pDisplay.setVisible(false);
		btnExit = new JButton("X");
		btnExit.setBounds(sizeRelated.getScreenW()-50, 0, 50, 50);
		mainPanel.add(btnExit);
		addExitListener(isHost);
		initUserInfoWindow();
		selectMortgage = new JComboBox();
		sellConfirm = new JButton("Mortgage");
		sellCancel = new JButton("Cancel");
		pleaseSelectMortgage =  new JLabel("Please select a property to mortgage:");
		pleaseSelectMortgage.setBounds(75,20,250,20);	//SUBJECT TO CHANGE/////////////////////////////////////	
		pleaseSelectMortgage.setFont(new Font("Serif",Font.BOLD,16));
		selectMortgage.setBounds(90, 50, 200, 20);  //SUBJECT TO CHANGE/////////////////////////////////////
		sellConfirm.setBounds(30,100,120,30); 	//SUBJECT TO CHANGE/////////////////////////////////////
		sellCancel.setBounds(230, 100, 120, 30); //SUBJECT TO CHANGE/////////////////////////////////////
		sellConfirm.setFont(new Font("Serif",Font.BOLD,16));
		sellCancel.setFont(new Font("Serif",Font.BOLD,16));
		mortgageWindow.add(sellConfirm);
		mortgageWindow.add(pleaseSelectMortgage);
		mortgageWindow.add(selectMortgage);
		mortgageWindow.add(sellCancel);
		//mortgageWindow
		mLabels = MoneyLabels.getInstance();
		mLabels.initLabels(playerInfo, insets, players,totalPlayers);
		loadingProgress = 10;
		dicePanel = new DicePanel(players, mLabels);
		loadingProgress = 20;
		boardPanel = new BoardPanel(players,dicePanel);
		dicePanel.setPlayerPiecesUp(mainPanel, boardPanel.getX() + boardPanel.getWidth()+20);
		addShowMoneyButton();
		addMortgageButton();
		setupMortgage();
		addButtonListeners();
		mainPanel.add(showInfo);
		mainPanel.add(showMortgage);
		mainPanel.add(boardPanel);
		mainPanel.add(giveJailFreeCard);
		addMuteMusic();
		addMuteSounds();
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);
		
		//Sounds.buildingHouse.toggleMuteSounds(); // DEBUG
		
		
		Random r = new Random();
		scheduledMusic = r.nextInt(NUMBER_OF_MUSIC);
		
		
	}
	private void addMuteMusic() {
		ImageIcon imgOn, imgOff;
		imgOn = new ImageIcon("src/Images/music_on.png");
		imgOff = new ImageIcon("src/Images/music_off.png");
		//muteMusic = new JCheckBox(imgOff); 	// DEBUG
		muteMusic = new JCheckBox(imgOff); 	// DEBUG
		muteMusic.setBorder(null);
		muteMusic.setBounds(40, 0, 40, 40);
		mainPanel.add(muteMusic);
		muteMusic.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1){ // left click
					Music.music1.toggleMuteMusic();
					if (Music.music1.getIsMuted()){
						muteMusic.setIcon(imgOff);
					}
					else{
						playScheduledMusic();
						muteMusic.setIcon(imgOn);
					}
					muteMusic.setBorder(null);
				}
				else if (e.getButton() == 3){ // right click
					scheduledMusic = (scheduledMusic + 1) % NUMBER_OF_MUSIC;
					if (!Music.music1.getIsMuted()){
						Music.music1.stopMusic();
						playScheduledMusic();
					}
					
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				
			}
		});
	}
	
	private void addMuteSounds(){
		ImageIcon imgOn, imgOff;
		imgOn = new ImageIcon("src/Images/sound_on.png");
		imgOff = new ImageIcon("src/Images/sound_off.png");
		//muteSounds = new JCheckBox(imgOff);	// DEBUG
		muteSounds = new JCheckBox(imgOn);	// DEBUG
		muteSounds.setBounds(0, 0, 40, 40);
		mainPanel.add(muteSounds);
		muteSounds.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 1){
					Sounds.buildingHouse.toggleMuteSounds();
					if (Sounds.buildingHouse.getIsMuted()){
						muteSounds.setIcon(imgOff);
					}
					else{
						muteSounds.setIcon(imgOn);
					}
				}
				else if (e.getButton() == 3){
					Sounds.landedOnJail.playSound();
				}
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				
			}
			
		});
	}
	
	
	public void initUserInfoWindow()
	{
		playerInfo = new JDialog();
		playerInfo.setLayout(null);
		playerInfo.setSize(850,1000);
        playerInfo.setTitle("Player Info!");
        insets = playerInfo.getInsets();
        
        mortgageWindow = new JDialog();
        mortgageWindow.setLayout(null);
        mortgageWindow.setSize(400,200);
        mortgageWindow.setTitle("Mortgage Property!");
	}
	
	public void setupMortgage()
	{
		for (int g = 0; g < players[0].getOwnedProperties().size(); g++)	//NEED TO GET PLAYER VALUE
		{
			selectMortgage.addItem(players[0].getOwnedProperties().get(g).getName());	//NEED TO GET PLAYER VALUE
		}
	}
	public void addShowMoneyButton()
	{
		JLabel buttonLabel1 = new JLabel("SHOW ME");
		JLabel buttonLabel2 = new JLabel("THE $$$");
		showInfo = new JButton();
		showInfo.setLayout(new BorderLayout());
		showInfo.setBounds(boardPanel.getX() + boardPanel.getWidth() + 10, myComp_height/2 - 100, 100, 50);
		showInfo.add(BorderLayout.NORTH, buttonLabel1);
		showInfo.add(BorderLayout.SOUTH, buttonLabel2);
		showInfo.setVisible(true);
		giveJailFreeCard = new JButton("GIVE JAIL-FREE CARD");
		giveJailFreeCard.setBounds(boardPanel.getX() + boardPanel.getWidth() + 10, myComp_height/2 - 25, 200, 50);
	}
	public void addMortgageButton()
	{
		showMortgage = new JButton("Mortgage Property!");
		showMortgage.setLayout(new BorderLayout());
		showMortgage.setBounds(boardPanel.getX() + boardPanel.getWidth() + 10, myComp_height/2 + 50, 150, 50);
		showMortgage.setVisible(true);
	}
	
//	public static void main(String[] args) {
//		GameScreen game = new GameScreen();
//	}

	
	private void playScheduledMusic(){
		switch (scheduledMusic){
		case 0:
			Music.music1.playMusic();
			break;
		case 1:
			Music.music2.playMusic();
			break;
		case 2:
			Music.music3.playMusic();
			break;
		case 3:
			Music.music4.playMusic();
			break;
		case 4:
			Music.music5.playMusic();
			break;
		}
	}
	
	private void addHost(){
		
		Timer t = new Timer();
		t.schedule(new TimerTask(){

			@Override
			public void run() {
				try {

					new MHost(dicePanel,players);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}, 0);

	}
	private void addClient(String ip, int port) throws UnknownHostException, IOException{
			client = new MClient(ip,port,false,dicePanel,players);
	}
	public int getLoadingProgress() {
		return loadingProgress;
	}
}