package MultiplayerPack;

public class CredentialManager {

	
	public static boolean authenticate(String username, String password) {
        //TODO Tie to DB for authentication. Devin this whole class is dedicated to you <3
        return SqlRelated.checkingLogin(username, password);
    }
	
	//TODO Send new account data to DB
	public static boolean createUser(String firstName, String lastName, String username, String password){
		//TODO SQL method to return whether successful or not?
		SqlRelated.insertNewUser(username, password, firstName, lastName);
		return true;
	}
	
}