package GamePack;

public final class PathRelated {
	private final static PathRelated PATH_RELATED = new PathRelated();
	private final static String SPACE_IMG_PATH = "src/SpaceImages/";
	private final static String SPACE_IMG_TOP = SPACE_IMG_PATH+"TopRow/";
	private final static String SPACE_IMG_LEFT = SPACE_IMG_PATH+"LeftCol/";
	private final static String SPACE_IMG_RIGHT = SPACE_IMG_PATH+"RightCol/";
	private final static String SPACE_IMG_BOT = SPACE_IMG_PATH+"BotRow/";
	private final static String SPACE_IMG_CORNER = SPACE_IMG_PATH+"Corners/";
	private final static String DICE_PATH="src/DiceImages/";
	private final static String PIECE_PATH="src/PieceImages/";
	private final static String IMG_PATH="src/Images/";
	
	private PathRelated(){
		
	}
	public static PathRelated getInstance(){
		return PATH_RELATED;
	}
	public String getSpaceImgTopPath(){
		return SPACE_IMG_TOP;
	}
	public String getSpaceImgBotPath(){
		return SPACE_IMG_BOT;
	}
	public String getSpaceImgLeftPath(){
		return SPACE_IMG_LEFT;
	}
	public String getSpaceImgRightPath(){
		return SPACE_IMG_RIGHT;
	}
	public String getSpaceImgCornerPath(){
		return SPACE_IMG_CORNER;
	}
	public String getDiceImgPath(){
		return DICE_PATH;
	}
	public String getImagePath(){
		return IMG_PATH;
	}
	public String getPieceImgPath(){
		return PIECE_PATH;
	}
}
