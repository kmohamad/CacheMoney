package ScreenPack;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import GamePack.Board;
import GamePack.ImageRelated;
import GamePack.PathRelated;
import GamePack.Piece;
import GamePack.Player;
import GamePack.SizeRelated;
import GamePack.Space;
import GamePack.Wildcard;


/*
 Size Formula:
w = 1.5h
2w + 9h = screen_height * .95
Therefore, 3h+9h = screen_height * .95
	12h = screen_height*.95
	h = screen_height*.95/12
	w = 1.5h
 */
public class BoardPanel extends JLayeredPane{



	private final static int NUM_ROW = 11;
	private final static int NUM_COL = 11;
	private int ROW_SPACE_WIDTH ;
	private int ROW_SPACE_HEIGHT;
	private int COL_SPACE_WIDTH ;
	private int COL_SPACE_HEIGHT;
	private final static int START_X = 0;
	private final static int START_Y = 0;

	private final static String IMAGE_PATH = "src/Images/";
	private final static String CHANCE_PATH = "Chance/";
	private final static String COMMUNITY_PATH = "CommunityChest/";
	private final static String SPACE_IMG_PATH = "src/SpaceImages/";
	private final static String SPACE_IMG_TOP = "TopRow/";
	private final static String SPACE_IMG_LEFT = "LeftCol/";
	private final static String SPACE_IMG_RIGHT = "RightCol/";
	private final static String SPACE_IMG_BOT = "BotRow/";
	private final static String SPACE_IMG_CORNER = "Corners/";
	private double screen_w;
	private double screen_h;
	private int max_w;
	private int max_h;

	private PathRelated paths;
	private SizeRelated sizeRelated;

	private ImageIcon spaceImgsTop[];
	private ImageIcon spaceImgsLeft[];
	private ImageIcon spaceImgsRight[];
	private ImageIcon spaceImgsBot[];
	private ImageIcon spaceImgsCorner[];
	private ImageIcon communityChestImg;
	private ImageIcon chanceImg;
	private Space[][] spaces;
	private Random rand;
	private DicePanel dicePanel;

	private Wildcard chance;
	private Wildcard communityChest;

	private ImageRelated imageRelated;
	private Board board;
	private Piece[] pieces;
	private Player[] players;

	
	public BoardPanel(){
		sizeRelated = SizeRelated.getInstance();
		setSize();
		init();
		importImgs();
		tempInitPiece();
		board = new Board(spaces, pieces, 4);

		addDiceBoard();
		addWildcards();
	}
	private void tempInitPiece(){
		players = new Player[4];
		pieces = new Piece[4];
		for(int i=0; i<4; i++)
		{
			players[i] = new Player();
			pieces[i] = new Piece(i);
			players[i].setPlayerPiece(pieces[i]);
		}
	}
	private void setSize(){
		ROW_SPACE_WIDTH = COL_SPACE_HEIGHT = sizeRelated.getSpaceRowWidth();
		COL_SPACE_WIDTH = ROW_SPACE_HEIGHT = sizeRelated.getSpaceRowHeight();
	}
	private void init(){
		
		paths = PathRelated.getInstance();
		imageRelated = ImageRelated.getInstance();
		setBackground(new Color(202, 232, 224));
        setBounds(100,10,START_X + COL_SPACE_WIDTH + ROW_SPACE_WIDTH * 9 + COL_SPACE_WIDTH, START_Y + ROW_SPACE_HEIGHT + COL_SPACE_HEIGHT * 9 + ROW_SPACE_HEIGHT);
        
        setLayout(null);
        
        rand = new Random();
	}
	private void importImgs(){
		spaceImgsTop = new ImageIcon[8];
        spaceImgsLeft = new ImageIcon[8];
        spaceImgsRight = new ImageIcon[8];
        spaceImgsBot = new ImageIcon[8];
        spaceImgsCorner = new ImageIcon[4];
        spaces = new Space[NUM_ROW][NUM_COL];
        
        communityChestImg = new ImageIcon(IMAGE_PATH + CHANCE_PATH + ".png");
        chanceImg = new ImageIcon(IMAGE_PATH + COMMUNITY_PATH + ".png");
        
        chance = new Wildcard(chanceImg, 100, 100);
        communityChest = new Wildcard(communityChestImg, 200, 200);
        
        
        for(int i=0; i<8; i++){
        	spaceImgsTop[i] = resizedImgs(paths.getSpaceImgTopPath()+i+".png",0);
        	spaceImgsLeft[i] = resizedImgs(paths.getSpaceImgLeftPath()+i+".png",1);
        	spaceImgsRight[i] = resizedImgs(paths.getSpaceImgRightPath()+i+".png",1);
        	spaceImgsBot[i] = resizedImgs(paths.getSpaceImgBotPath()+i+".png",0);
        }
        for(int i=0; i<4; i++){
        	spaceImgsCorner[i] = resizedImgs(paths.getSpaceImgCornerPath()+i+".png",2);
        }
        for(int i=0; i<NUM_ROW;i++){
        	for(int j=0; j<NUM_COL; j++){
        		if(i == 0){
        			if(j==0){
        				spaces[i][j] = new Space(spaceImgsCorner[0]);
        				spaces[i][j].setBounds(START_X, START_Y, COL_SPACE_WIDTH, ROW_SPACE_HEIGHT);
        			}else if(j==10){
        				spaces[i][j] = new Space(spaceImgsCorner[1]);
        				spaces[i][j].setBounds(START_X + COL_SPACE_WIDTH + (j-1)*ROW_SPACE_WIDTH, START_Y, COL_SPACE_WIDTH, ROW_SPACE_HEIGHT);
        			}else{
            			spaces[i][j] = new Space(spaceImgsTop[rand.nextInt(8)]);
            			spaces[i][j].setBounds(START_X + COL_SPACE_WIDTH + (j-1)*ROW_SPACE_WIDTH, START_Y, ROW_SPACE_WIDTH, ROW_SPACE_HEIGHT);
        			}
        			add(spaces[i][j]);
        		}
        		else if(i == 10){
        			if(j==0){
        				spaces[i][j] = new Space(spaceImgsCorner[2]);
        				spaces[i][j].setBounds(START_X, START_Y + ROW_SPACE_HEIGHT + COL_SPACE_HEIGHT * 9, COL_SPACE_WIDTH, ROW_SPACE_HEIGHT);
        			}else if(j==10){
        				spaces[i][j] = new Space(spaceImgsCorner[3]);
        				spaces[i][j].setBounds(START_X+ COL_SPACE_WIDTH + (j-1)*ROW_SPACE_WIDTH, START_Y + ROW_SPACE_HEIGHT + COL_SPACE_HEIGHT * 9, COL_SPACE_WIDTH, ROW_SPACE_HEIGHT);
        			}else{
            			spaces[i][j] = new Space(spaceImgsBot[rand.nextInt(8)]);
            			spaces[i][j].setBounds(START_X + COL_SPACE_WIDTH + (j-1)*ROW_SPACE_WIDTH, START_Y + ROW_SPACE_HEIGHT + COL_SPACE_HEIGHT * 9, ROW_SPACE_WIDTH, ROW_SPACE_HEIGHT);
        			}
        			add(spaces[i][j]);
        		}
        		else if(j == 0){
    				spaces[i][j] = new Space(spaceImgsLeft[rand.nextInt(8)]);
    				spaces[i][j].setBounds(START_X, START_Y + ROW_SPACE_HEIGHT + (i-1)*COL_SPACE_HEIGHT, COL_SPACE_WIDTH, COL_SPACE_HEIGHT);
    				add(spaces[i][j]);
        		}
        		else if(j == 10){
    				spaces[i][j] = new Space(spaceImgsRight[rand.nextInt(8)]);
    				spaces[i][j].setBounds(START_X + COL_SPACE_WIDTH + ROW_SPACE_WIDTH * 9, START_Y + ROW_SPACE_HEIGHT + (i-1)*COL_SPACE_HEIGHT, COL_SPACE_WIDTH, COL_SPACE_HEIGHT);
    				add(spaces[i][j]);
        		}
        	}
        }
        
	}
	private ImageIcon resizedImgs(String path, int type){
		int width,height;
		//rows
		if(type == 0){
			width = ROW_SPACE_WIDTH;
			height = ROW_SPACE_HEIGHT;
		}
		//cols
		else if(type == 1){
			width = COL_SPACE_WIDTH;
			height = COL_SPACE_HEIGHT;
		}
		//corners
		else{
			width = COL_SPACE_WIDTH;
			height = ROW_SPACE_HEIGHT;
		}
		return imageRelated.resizeImage(path, width, height);
		
	}
	private void addDiceBoard(){
		dicePanel = new DicePanel(this,board);
		add(dicePanel);
	}
	
	private void addWildcards(){
		add(chance);
		add(communityChest);
	}
}

