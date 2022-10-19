package jarachnea;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.LogManager;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

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

    public static void main(final String[] args) throws IOException, SQLException, InterruptedException, ParseException {
        ScraperRunnableProfiles scraperRunnableProfilesObj;
        ScraperRunnableRelations scraperRunnableRelationsObj;
        File iniFileObj;
        Ini iniObj;
        Handler[] existingHandlers;
        ConsoleHandler handlerObj;
        Options optionsObj;
        CommandLineParser commandLineParserObj;
        CommandLine commandLineObj;

        optionsObj = new Options();
        optionsObj.addOption("p", false, "fetch profiles only, disregard following & followers pages");
        commandLineParserObj = new DefaultParser();

        try {
            commandLineObj = commandLineParserObj.parse(optionsObj, args);
        } catch (Exception exceptionObj) {
            System.out.println("error in parsing commandline options: " + exceptionObj.getMessage());
            System.exit(0);
            return;
        }

        iniFileObj = new File(INI_FILE_PATH);
        iniObj = new Ini(iniFileObj);

        LogManager.getLogManager().reset();
        loggerObj = Logger.getLogger("scraper_main");
        loggerObj.setLevel(Level.INFO);
        handlerObj = new ConsoleHandler();
        handlerObj.setFormatter(new LogFormatter());
        loggerObj.addHandler(handlerObj);

        if (commandLineObj.hasOption("p")) {
            loggerObj.log(Level.INFO, "received -p flag on the commandline, executing Profiles-only mode");
        } else {
            loggerObj.log(Level.INFO, "received no -p flag on the commandline, executing Profiles + Relations mode");
        }

        host = iniObj.get("Production").get("host");
        username = iniObj.get("Production").get("username");
        password = iniObj.get("Production").get("password");
        database = iniObj.get("Production").get("database");

        dataStoreObj = new DataStore(host, username, password, database);

        instanceMap = new ConcurrentHashMap<String, Instance>(dataStoreObj.retrieveBadInstances());
        loggerObj.log(Level.INFO, "loaded bad instances list, " + instanceMap.size() + " entries");

        if (commandLineObj.hasOption("p")) {
            unfetchedHandlesQueue = new ConcurrentLinkedQueue<Handle>(dataStoreObj.retrieveUnfetchedHandlesFromRelations());
            loggerObj.log(Level.INFO, "loaded unfetched handles list, " + unfetchedHandlesQueue.size() + " entries");

            scraperRunnableProfilesObj = new ScraperRunnableProfiles(instanceMap, unfetchedHandlesQueue, host, username, password, database);
            scraperRunnableProfilesObj.run();
        } else {
            unfetchedHandlesQueue = new ConcurrentLinkedQueue<Handle>(dataStoreObj.retrieveUnfetchedHandlesFromHandles());
            loggerObj.log(Level.INFO, "loaded unfetched handles list, " + unfetchedHandlesQueue.size() + " entries");

            scraperRunnableRelationsObj = new ScraperRunnableRelations(instanceMap, unfetchedHandlesQueue, host, username, password, database);
            scraperRunnableRelationsObj.run();
        }
    }
}
