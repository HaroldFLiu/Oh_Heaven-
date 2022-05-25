package oh_heaven.game;

public class Oh_Heaven
{
	public static void main(String[] args)
	{
		//System.out.println("Working Directory = " + System.getProperty("user.dir"));
		PropertiesLoader.setProperties(args);
		new GameManager();
	}

}
