package oh_heaven.game;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PropertiesLoader {
    private static final String DEFAULT_DIRECTORY_PATH = "properties/";
    private static Properties properties;

    public static void setProperties(String[] args)
    {
        if (args == null || args.length == 0)
            loadPropertiesFile(null);
        else
            loadPropertiesFile(args[0]);
    }
    private static void loadPropertiesFile(String propertiesFile) {
        if (propertiesFile == null) {
            try (InputStream input = new FileInputStream(DEFAULT_DIRECTORY_PATH + "runmode.properties")) {

                Properties prop = new Properties();

                // load a properties file
                prop.load(input);

                propertiesFile = DEFAULT_DIRECTORY_PATH + prop.getProperty("current_mode");
                System.out.println(propertiesFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        try (InputStream input = new FileInputStream(propertiesFile)) {

            Properties prop = new Properties();

            // load a properties file
            prop.load(input);

            properties = prop;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static int getNbRounds()
    {
        return Integer.parseInt(properties.getProperty("rounds"));
    }

    public static int getNbStartCards()
    {
        return Integer.parseInt(properties.getProperty("nbStartCards"));
    }

    public static int getSeed()
    {
        return Integer.parseInt(properties.getProperty("seed"));
    }

    public static boolean getEnforceRules()
    {
        return Boolean.parseBoolean(properties.getProperty("enforceRules"));
    }
}

