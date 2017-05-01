package ScreenPack;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import GamePack.ImageRelated;
import GamePack.PathRelated;
import GamePack.Property;
import GamePack.SizeRelated;
import InterfacePack.Sounds;
import MultiplayerPack.MBytePack;
import MultiplayerPack.PlayingInfo;
import MultiplayerPack.SqlRelated;
import MultiplayerPack.UnicodeForServer;

public class MainGameArea extends JPanel{
	private HashMap<Long,JButton> rooms;
	private JButton createNewRoom;
	private JButton loadGame;
	private GridLayout gLayout;
	private Container container;
	private int oldContainerRow;
	private int oldContainerCol;
	private JLabel jLabel;
	private JLabel lblFriend;
	
	private WaitingArea waitingArea;
	private PlayingInfo playingInfo;
	private MBytePack mPack;
	private infoThatScrolls friendList;
	private infoThatScrolls onlineUsers;
	private ChatScreen chatScreen;
	private JComboBox<Integer> loadingList;
	private JPanel chatAndFriends;
	private JPanel controlPanel;
	private JPanel mainPanel;
	private SqlRelated sqlRelated;
	private ResultSet friends;
	private ArrayList<ArrayList<Integer>> loadingListInt;
	public MainGameArea(final Container container) {
		this.container = container;
		init();
		addListener();
	}
	private void init(){
		createChatAndFriendsPanel();
		createOnlineUserAndCreateRoomPanel();
		playingInfo = PlayingInfo.getInstance();
		mPack = MBytePack.getInstance();
		if (Property.isSQLEnabled){
			sqlRelated = SqlRelated.getInstance();
		}
		rooms = new HashMap<>();
		waitingArea = new WaitingArea(container, this);
		
		gLayout = new GridLayout(12, 4);
		setLayout(gLayout);
		//setPreferredSize(new Dimension(SizeRelated.getInstance().getScreenW()/4, SizeRelated.getInstance().getScreenH()/10));	
		setBackground(Color.black);		
	}
	
	private void createChatAndFriendsPanel(){
		chatScreen = new ChatScreen(UnicodeForServer.CHAT_LOBBY);
		friendList = new infoThatScrolls(false);
		friendList.setScrollingPaneVisible(true);
		
		chatAndFriends = new JPanel();
		GridLayout gl = new GridLayout(2,1);
		gl.setVgap(10);
		chatAndFriends.setLayout(gl);
		chatAndFriends.add(chatScreen);

		JPanel temp = new JPanel();
		temp.setLayout(new GridBagLayout());
		lblFriend = new JLabel("Friend list:");
		
		
		chatAndFriends.add(lblFriend);
		chatAndFriends.add(friendList.getScrollingPanel());
	}
	
	private void createOnlineUserAndCreateRoomPanel(){
		controlPanel = new JPanel();
		controlPanel.setPreferredSize(new Dimension(SizeRelated.getInstance().getScreenW()/3, SizeRelated.getInstance().getScreenH()));
		controlPanel.setLayout(new GridBagLayout());
		loadGame = new JButton();
		addLoadGameButton();
		onlineUsers = new infoThatScrolls(false);
		onlineUsers.setScrollingPaneVisible(true);
		createNewRoom = new JButton();
		addCreateNewRoomButton();
		jLabel = new JLabel("Online users:");
		loadingList = new JComboBox<>();
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,1,30,5);
			
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 4;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weighty = 0.;
		gbc.weightx = 1;
		controlPanel.add(createNewRoom, gbc);
		
		
		
		
		
		gbc.insets = new Insets(5,1,30,5);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 4;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weighty = 0.;
		gbc.weightx = 1;
		controlPanel.add(loadingList, gbc);
		
		gbc.insets = new Insets(5,1,30,5);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 4;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weighty = 0.;
		gbc.weightx = 1;
		controlPanel.add(loadGame, gbc);
		
		gbc.insets = new Insets(1,1,0,5);
		
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridheight = 1;
		gbc.gridwidth = 4;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weighty = 0;
		gbc.weightx = 1;
		controlPanel.add(jLabel, gbc);
		
		gbc.insets = new Insets(10,1,500,5);
		
		gbc.gridx = 0;
		gbc.gridy = 7;
		gbc.gridheight = 2;
		gbc.gridwidth = 4;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 0.1;
		controlPanel.add(onlineUsers.getScrollingPanel(), gbc);
		
		
		
	}
	
	private void addLoadGameButton() {
		loadGame.setSize(50, 50);
		loadGame.setIcon(ImageRelated.getInstance().resizeImage(PathRelated.getButtonImgPath() + "LoadMultiplayer.png", loadGame.getWidth(), loadGame.getHeight()));
		loadGame.setContentAreaFilled(false);
		loadGame.setBorder(null);
	}
	private void addCreateNewRoomButton() {
		createNewRoom.setSize(50, 50);
		createNewRoom.setIcon(ImageRelated.getInstance().resizeImage(PathRelated.getButtonImgPath() + "CreateRoomButton.png", loadGame.getWidth(), loadGame.getHeight()));
		createNewRoom.setContentAreaFilled(false);
		createNewRoom.setBorder(null);
	}
	public WaitingArea getWaiting(){
		return waitingArea;
	}
	public void switchToWaiting(){
		chatScreen.clearArea();
		container.removeAll();
		waitingArea.setComponents();
		container.repaint();
		container.revalidate();
	}
	public void setComponents(){
		mainPanel = new JPanel();
		GridLayout gl = new GridLayout(1,3);
		gl.setHgap(20);
		mainPanel.setLayout(gl);
		mainPanel.add(this,BorderLayout.WEST);
		mainPanel.add(chatAndFriends,BorderLayout.CENTER);
		mainPanel.add(controlPanel,BorderLayout.EAST);
		
		container.add(mainPanel);
		loadFriendList();
		getLoadingGames();
		/*container.add(this,BorderLayout.WEST);
		container.add(chatAndFriends,BorderLayout.CENTER);
		container.add(controlPanel, BorderLayout.EAST);*/
	}
	private void loadFriendList(){
		friendList.clearList();
		friends = sqlRelated.getFriend(playingInfo.getLoggedInId());
		try {
			while(friends.next())
				friendList.addObject(friends.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void getLoadingGames(){
		loadingListInt = sqlRelated.getLoadingGameList(playingInfo.getLoggedInId());
		loadGame.setEnabled(loadingListInt.size()!=0);
		loadingList.removeAllItems();
		for(int gameNum : loadingListInt.get(0)){
			loadingList.addItem(gameNum);
		}
	}
	public void updatelist(ArrayList<Object> userList){
		onlineUsers.clearList();
		System.out.println("Update user lists : . size:" + userList.get(0));
		for(int i=1; i<userList.size(); i++){
			System.out.println((String)userList.get(i));
			onlineUsers.addObject((String)userList.get(i));
		}
		//onlineUsers.refresh();
	}
	private void addListener(){
		createNewRoom.addMouseListener(new MouseListener() {

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
				Sounds.buttonConfirm.playSound();
				switchToWaiting();
				playingInfo.sendMessageToServer(mPack.packSimpleRequest(UnicodeForServer.CREATE_ROOM));

			}
		});
		loadGame.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				Sounds.buttonConfirm.playSound();
				switchToWaiting();
				System.out.println("Sending load # " +(Integer)loadingList.getSelectedItem() + "numPpl:" + loadingListInt.get(1).get(loadingList.getSelectedIndex()));
				playingInfo.sendMessageToServer(mPack.packIntArray(UnicodeForServer.LOADING_GAME, new int[]{(Integer)loadingList.getSelectedItem(), loadingListInt.get(1).get(loadingList.getSelectedIndex())}));

			}
		});
	}
	private void addListenerToRoom(JButton room, long roomNum){
		room.addMouseListener(new MouseListener() {

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
				if(room.isEnabled()){
					Sounds.buttonConfirm.playSound();
					switchToWaiting();
					playingInfo.sendMessageToServer(mPack.packLong(UnicodeForServer.JOIN_ROOM, roomNum));
				}


			}
		});
	}
	public void updateRooms(ArrayList<Object> list){
		//		removeAll();
		System.out.println("size"+list.size());

		makeNewRoom((Long)list.get(1));

		repaint();
		revalidate();
	}
	public void makeNewRoom(long roomNum){
		System.out.println("MAKE ROOM");
		rooms.put(roomNum,new JButton("Room : " + roomNum + "  1/4"));
		addListenerToRoom(rooms.get(roomNum),roomNum);
		add(rooms.get(roomNum));
		System.out.println(rooms.size());
	}
	public void updateRoom(long roomNum, int numPpl, boolean isStarting){
		System.out.println("UPDATE ROOM");
		if(isStarting){
			remove(rooms.get(roomNum));
			rooms.remove(roomNum);
			revalidate();
			repaint();
		}else{
			System.out.print("room:"+roomNum + " num:" + numPpl);
			rooms.get(roomNum).setEnabled(numPpl < 4);
			setRoomText(rooms.get(roomNum), roomNum, numPpl);
		}

	}
	private void setRoomText(JButton b, long roomNum, int numPpl){
		b.setText("Room : " + roomNum + "  " + numPpl+ "/4");
	}
	public void receiveMsg(String id, String msg){
		chatScreen.receiveMsg(id, msg);
	}
}
