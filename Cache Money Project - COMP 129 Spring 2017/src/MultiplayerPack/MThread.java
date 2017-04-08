package MultiplayerPack;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class MThread extends Thread{
	private String name;
	private ArrayList<OutputStream> usersOutput;
	private InputStream readFromUser;
	private int pos;
	private byte[] msg;
	private boolean serverDisconnected;
	private MByteUnpack mUnpack;
	private MBytePack mPack;
	private UnicodeForServer ufs;
	private Integer playerNum;
	private int disconnectPlayer;
	private byte[] tempMsg;
	private int numPlayer;
	private int specialCode;
	private boolean exitCode;
	public MThread(ArrayList<OutputStream> usersOutput, int numPlayer, InputStream inputStream){
		this.playerNum = playerNum;
		this.usersOutput = usersOutput;
		this.numPlayer = numPlayer;
		mPack = MBytePack.getInstance();
		mUnpack = MByteUnpack.getInstance();
		ufs = UnicodeForServer.getInstance();
		readFromUser = inputStream;
		msg=new byte[512];
		/*
		try {
			System.out.println("Connection from : " + s.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}
	public void disconnectServer(){
		serverDisconnected = true;
	}
	public void run(){
		try{
			sendPlayerNum(mPack.packPlayerNumber(ufs.PLAYER_NUM, usersOutput.size()-1));
			
			playerNum = playerNum.intValue()+1;
			while(!exitCode){
				getMsg();
				
				specialCode = mUnpack.isSpecalCode(msg[3]);
				if(specialCode == 0){
					showMsgToUsers(msg);
				}
				else if(specialCode == 1){
					// To do : get rid of all the property this owner owns.
					disconnectPlayer = (Integer)mUnpack.getResult(msg).get(1);
					System.out.println("Player " + (disconnectPlayer+1) + " is disconnected");
					usersOutput.set(disconnectPlayer,null);
//					disconnectedUser();
					showMsgToUsers(mPack.packPlayerNumber(ufs.DISCONNECTED, disconnectPlayer));
					exitCode = true;
					break;
				}else if(specialCode == 2){
//					System.out.println("Server is disconnected");
//					usersOutput.set(myNum,null);
//					startPlayers.set(myNum, null);
//					showMsgToUsers(mPack.packSimpleRequest(ufs.HOST_DISCONNECTED));
					exitCode = true;
					break;
				}else {
//					System.out.println("Called");
				}
				
					
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
		}
	}
	private void getMsg(){
		try {
			
			readFromUser.read(msg);
//			sendingQ.enqueue(msg);
		} catch (IOException e) {
		}
	}
	private void showMsgToUsers(byte[] msg) throws SocketException{
		for(OutputStream output:usersOutput){
			try {
				if(output != null)
					output.write(msg);
			} catch (Exception e) {
				e.printStackTrace();
				
			}
		}
	}
	private void sendPlayerNum(byte[] msg){
		try {
			usersOutput.get(usersOutput.size()-1).write(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
