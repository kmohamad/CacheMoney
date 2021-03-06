package MiniGamePack;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import InterfacePack.Sounds;
import MultiplayerPack.UnicodeForServer;

public class RockScissorPaperGame extends MiniGame{
	private RockScissorPaper rsp;
	private KeyListener listener;
	private char enteredChar;
	private int[] selected;
	private int[] ascii;
	private boolean[] decided;
	private ArrayList<JLabel> lblsForThis;
	
	public RockScissorPaperGame(JPanel miniPanel) {
		super(miniPanel);
		initExtra();
		initLabels();
		initKeyListener();
	}
	private void initLabels(){
		lblsForThis = new ArrayList<>();
		lblsForThis.add(new JLabel("q,w,e for owner : i,o,p for guest"));
		lblsForThis.add(new JLabel("Make your decision in : "));
		lblsForThis.add(new JLabel("6"));
		lblsForThis.add(new JLabel(""));
		lblsForThis.add(new JLabel(""));
		lblsForThis.add(new JLabel(imgs.getRspImg(6)));
		lblsForThis.add(new JLabel(imgs.getRspImg(7)));
		lbls.get(0).setBounds(dpWidth*2/9, 0, dpWidth*2/3, dpHeight*1/7);
		lbls.get(1).setBounds(dpWidth*2/9, dpHeight*1/7-20, dpWidth*5/7, dpHeight*1/7);
		lblsForThis.get(0).setBounds(dpWidth*2/9, dpHeight*1/7+20, dpWidth-dpWidth*2/9, dpHeight*1/7);
		lblsForThis.get(1).setBounds(dpWidth*2/9, dpHeight*1/7+40, dpWidth*4/7, dpHeight*1/7);
		lblsForThis.get(2).setBounds(dpWidth*2/9+140, dpHeight*1/7+40, dpWidth*1/7, dpHeight*1/7);
		lblsForThis.get(3).setBounds(0, dpHeight*1/7+70, 100, 100);
		lblsForThis.get(4).setBounds(dpWidth-100, dpHeight*1/7+70, 100, 100);
		lblsForThis.get(5).setBounds(100, dpHeight-100, 100, 100);
		lblsForThis.get(6).setBounds(200, dpHeight-100, 100, 100);
	}
	private void initExtra(){
		
		rsp = new RockScissorPaper();
		selected = new int[2];
		ascii = new int[128];
		decided = new boolean[2];
		ascii['q'] = ascii['i'] = 1;
		ascii['w'] = ascii['o'] = 2;
		ascii['e'] = ascii['p'] = 3;
	}
	public void addGame(){
		GAME_NUM = 3;
		super.addGame();
		setTitleAndDescription("Rock Scissor Paper Game!", "Order of rock,scissor,paper:");
		setVisibleForTitle(true);
		lblsForThis.get(3).setIcon(imgs.getPieceImg(owner.getPlayerNum()));
		lblsForThis.get(4).setIcon(imgs.getPieceImg(guest.getPlayerNum()));
		lblsForThis.get(5).setVisible(false);
		lblsForThis.get(6).setVisible(false);
		initGameSetting();
	}
	public void addActionToGame(int decision, boolean isOwner){
		selectDecision(decision, isOwner);
	}
	protected void initGameSetting(){
		super.initGameSetting();
		for(int i=0; i<lblsForThis.size(); i++)
			miniPanel.add(lblsForThis.get(i));
		miniPanel.repaint();
		miniPanel.revalidate();
		
	}
	private void ifNotSelected(){
		for(int i=0; i<2; i++)
			if(!decided[i]){
				if(pInfo.isSingle())
					selectDecision(rand.nextInt(3)+1, i==0);
				else
					pInfo.sendMessageToServer(mPack.packIntBoolean(UnicodeForServer.RSP_MINI_GAME_DECISION, rand.nextInt(3)+1, i==0));
			}
				
	}
	private void uncoverResult(){
		for(int i=0; i<2; i++){
			lblsForThis.get(i+5).setIcon(imgs.getRspImg(selected[i]+(3*i)));
		}
		Sounds.waitingRoomJoin.playSound();
		lbls.get(1).setText((getWinner() ? "OWNER":"GUEST") + " WINS!");
	}
	public void play(){
		super.play();
		
		(new PlayGame()).start();
		
	}
	private void initKeyListener(){
		listener = new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				enteredChar = Character.toLowerCase(e.getKeyChar());
				if((enteredChar == 'q' || enteredChar == 'w' || enteredChar == 'e') && (isOwner || pInfo.isSingle())){
					Sounds.buttonPress.playSound();
					if(pInfo.isSingle())
						selectDecision(ascii[enteredChar], true);
					else
						pInfo.sendMessageToServer(mPack.packIntBoolean(UnicodeForServer.RSP_MINI_GAME_DECISION, ascii[enteredChar], true));
				}else if((enteredChar == 'i' || enteredChar == 'o' || enteredChar == 'p') && (isGuest || pInfo.isSingle())){
					Sounds.buttonPress.playSound();
					if(pInfo.isSingle())
						selectDecision(ascii[enteredChar], false);
					else
						pInfo.sendMessageToServer(mPack.packIntBoolean(UnicodeForServer.RSP_MINI_GAME_DECISION, ascii[enteredChar], false));
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				
			}
			@Override
			public void keyPressed(KeyEvent e) {}
				
			};
	}
	private void addKeyListener(){
		miniPanel.addKeyListener(listener);
		miniPanel.setFocusable(true);
		miniPanel.requestFocusInWindow();
	}
	private void removeKeyListner(){
		miniPanel.removeKeyListener(listener);
		miniPanel.setFocusable(false);
	}
	private void selectDecision(int charValue, boolean isOwner){
		selected[isOwner?0:1] = charValue-1;
		decided[isOwner?0:1] = true;
		lblsForThis.get(isOwner?5:6).setIcon(imgs.getRspImg(isOwner?6:7));
		lblsForThis.get(isOwner?5:6).setVisible(true);
	}
	protected void cleanUp(){
		lblsForThis.get(2).setText("10");
		for(int i=0; i<2; i++){
			decided[i] = false;
			lblsForThis.get(i+5).setVisible(false);
		}
		miniPanel.removeAll();
		miniPanel.repaint();
		miniPanel.revalidate();
		isGameEnded = true;
	}
	public boolean isGameEnded(){
		return isGameEnded;
	}
	public boolean getWinner(){
		return rsp.isOwnerWin(selected[0], selected[1]);
	}
	class PlayGame extends Thread{
		public void run(){
			addKeyListener();
			if(isUnavailableToPlay())
				removeKeyListner();
			for(int i=10; i>=0; --i){
				lblsForThis.get(2).setText(i +"");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			removeKeyListner();
			ifNotSelected();
			uncoverResult();
			forEnding();
		}
	}
}
