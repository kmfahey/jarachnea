package jarachnea;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogManager;

import org.ini4j.Ini;

public class ScraperMain {
    private static final String INI_FILE_PATH = "/home/kmfahey/Workspace/jarachnea_mysql_secrets.ini";
    private static Thread[] threadsArray = new Thread[8];
    private static ConcurrentHashMap<String, Instance> instanceMap;
    private static ConcurrentLinkedQueue<Handle> unfetchedHandlesQueue;
    private static DataStore dataStoreObj;
    private static String host;
    private static String username;
    private static String password;
    private static String database;
    private static Logger loggerObj;

    public static void main(final String[] args) throws IOException, SQLException, InterruptedException {
        ScraperRunnable scraperRunnableObj;
        File iniFileObj;
        Ini iniObj;
        Handler[] existingHandlers;
        ConsoleHandler handlerObj;

        iniFileObj = new File(INI_FILE_PATH);
        iniObj = new Ini(iniFileObj);

        LogManager.getLogManager().reset();
        loggerObj = Logger.getLogger("scraper_main");
        loggerObj.setLevel(Level.INFO);
        handlerObj = new ConsoleHandler();
        handlerObj.setFormatter(new LogFormatter());
        loggerObj.addHandler(handlerObj);

        host = iniObj.get("Production").get("host");
        username = iniObj.get("Production").get("username");
        password = iniObj.get("Production").get("password");
        database = iniObj.get("Production").get("database");

        dataStoreObj = new DataStore(host, username, password, database);

        instanceMap = new ConcurrentHashMap<String, Instance>(dataStoreObj.retrieveBadInstances());
        loggerObj.log(Level.INFO, "loaded bad instances list, " + instanceMap.size() + " entries");

        unfetchedHandlesQueue = new ConcurrentLinkedQueue<Handle>(dataStoreObj.retrieveUnfetchedHandles());
        loggerObj.log(Level.INFO, "loaded unfetched handles list, " + unfetchedHandlesQueue.size() + " entries");
        scraperRunnableObj = new ScraperRunnable(instanceMap, unfetchedHandlesQueue, host, username, password, database);
        scraperRunnableObj.run();
    }
}
