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
        ScraperLoop scraperLoopObj;
        File iniFileObj;
        Ini iniObj;
        Handler[] existingHandlers;
        ConsoleHandler handlerObj;
        Options optionsObj;
        CommandLineParser commandLineParserObj;
        CommandLine commandLineObj;

        optionsObj = new Options();
        optionsObj.addOption("p", false, "fetch profiles only, disregard following & followers pages");
        optionsObj.addOption("r", false, "fetch both profiles and relations");
        optionsObj.addOption("H", false, "load unfetched handles from the `handles` table");
        optionsObj.addOption("R", false, "load unfetched handles from the `relations` table");
        commandLineParserObj = new DefaultParser();

        try {
            commandLineObj = commandLineParserObj.parse(optionsObj, args);
        } catch (Exception exceptionObj) {
            System.out.println("error in parsing commandline options: " + exceptionObj.getMessage());
            System.exit(0);
            return;
        }

        if (!commandLineObj.hasOption("p") && !commandLineObj.hasOption("r")) {
            System.out.println("please specify one of either the -p flag or the -r flag on the commandline to choose the scraping mode");
            System.exit(0);
        }

        if (!commandLineObj.hasOption("H") && !commandLineObj.hasOption("R")) {
            System.out.println("please specify one of either the -H flag or the -R flag on the commandline to indicate where to load pending handles from");
            System.exit(0);
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

        if (commandLineObj.hasOption("H")) {
            unfetchedHandlesQueue = new ConcurrentLinkedQueue<Handle>(dataStoreObj.retrieveUnfetchedHandlesFromRelations());
        } else {
            unfetchedHandlesQueue = new ConcurrentLinkedQueue<Handle>(dataStoreObj.retrieveUnfetchedHandlesFromHandles());
        }

        loggerObj.log(Level.INFO, "loaded unfetched handles list, " + unfetchedHandlesQueue.size() + " entries");

        scraperLoopObj = new ScraperLoop(instanceMap, unfetchedHandlesQueue, host, username, password, database);

        if (commandLineObj.hasOption("p")) {
            scraperLoopObj.executeMainLoop(false);
        } else {
            scraperLoopObj.executeMainLoop(true);
        }
    }
}
