package oh_heaven.game;

public class Oh_Heaven
{
	public static void main(String[] args)
	{
		PropertiesLoader.setProperties(args);
		new GameManager();
	}
}
