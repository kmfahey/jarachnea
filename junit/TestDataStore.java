package jarachnea.junit;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.ini4j.Ini;

import jarachnea.DataStore;
import jarachnea.Instance;
import jarachnea.ProcessingException;
import jarachnea.Handle;

public final class TestDataStore extends TestCase {
    private static final Instance TEST_MALFUNCTIONING_INSTANCE = new Instance("100club.social", Instance.MALFUNCTIONING);
    private static final Instance TEST_SUSPENDED_INSTANCE = new Instance("anime.website", Instance.SUSPENDED);
    private static final Instance TEST_UNPARSEABLE_INSTANCE = new Instance("56k.space", Instance.UNPARSEABLE);

    private static final Handle TEST_HANDLE_WITHOUT_HANDLE_ID = new Handle("Gargron", "mastodon.social");
    private static final Handle TEST_HANDLE_WITH_HANDLE_ID = new Handle(71790, "Gargron", "mastodon.social");

    private static final String INI_FILE_PATH = "/home/kmfahey/Workspace/jarachnea_mysql_secrets.ini";

    private String host;
    private String username;
    private String password;
    private String database;
    private DataStore dataStoreObj;
    private String table;

    public void setUp() throws SQLException, IOException {
        File iniFileObj;
        Ini iniObj;

        iniFileObj = new File(INI_FILE_PATH);
        iniObj = new Ini(iniFileObj);

        host = iniObj.get("Testing").get("host");
        username = iniObj.get("Testing").get("username");
        password = iniObj.get("Testing").get("password");
        database = iniObj.get("Testing").get("database");

        dataStoreObj = new DataStore(host, username, password, database);
    }

    public void tearDown() throws SQLException {
        dataStoreObj.clearTable("bad_instances");
        dataStoreObj.clearTable("handles");
    }

    public void testConstructor() {
        assertEquals(dataStoreObj.getHost(), host);
        assertEquals(dataStoreObj.getUsername(), username);
        assertEquals(dataStoreObj.getPassword(), password);
        assertEquals(dataStoreObj.getDatabase(), database);
    }

    public void testInstanceStoreAndRetrieve() throws SQLException, ProcessingException {
        HashMap<String, Instance> instanceMap;
        Instance malfunctioningInstanceObj;
        Instance suspendedInstanceObj;
        Instance unparseableInstanceObj;
        boolean executionOutcome;

        table = "bad_instances";
        
        dataStoreObj.storeInstance(TEST_MALFUNCTIONING_INSTANCE);
        dataStoreObj.storeInstance(TEST_SUSPENDED_INSTANCE);
        dataStoreObj.storeInstance(TEST_UNPARSEABLE_INSTANCE);

        instanceMap = dataStoreObj.retrieveBadInstances();

        malfunctioningInstanceObj = instanceMap.get(TEST_MALFUNCTIONING_INSTANCE.getInstanceHostname());
        unparseableInstanceObj = instanceMap.get(TEST_UNPARSEABLE_INSTANCE.getInstanceHostname());
        suspendedInstanceObj = instanceMap.get(TEST_SUSPENDED_INSTANCE.getInstanceHostname());

        assertEquals(malfunctioningInstanceObj.getInstanceHostname(), TEST_MALFUNCTIONING_INSTANCE.getInstanceHostname());
        assertEquals(malfunctioningInstanceObj.getInstanceStatus(), TEST_MALFUNCTIONING_INSTANCE.getInstanceStatus());
        assertEquals(unparseableInstanceObj.getInstanceHostname(), TEST_UNPARSEABLE_INSTANCE.getInstanceHostname());
        assertEquals(unparseableInstanceObj.getInstanceStatus(), TEST_UNPARSEABLE_INSTANCE.getInstanceStatus());
        assertEquals(suspendedInstanceObj.getInstanceHostname(), TEST_SUSPENDED_INSTANCE.getInstanceHostname());
        assertEquals(suspendedInstanceObj.getInstanceStatus(), TEST_SUSPENDED_INSTANCE.getInstanceStatus());
    }

    public void testHandleStoreAndRetrieve() throws SQLException {
        ArrayList<Handle> handleList;
        Handle retrievedHandle;

        dataStoreObj.storeHandle(TEST_HANDLE_WITHOUT_HANDLE_ID);

        handleList = dataStoreObj.retrieveUnfetchedHandles();

        retrievedHandle = handleList.get(0);

        assertEquals(retrievedHandle.getUsername(), TEST_HANDLE_WITHOUT_HANDLE_ID.getUsername());
        assertEquals(retrievedHandle.getInstance(), TEST_HANDLE_WITHOUT_HANDLE_ID.getInstance());

        dataStoreObj.clearTable("handles");

        dataStoreObj.storeHandle(TEST_HANDLE_WITH_HANDLE_ID);

        handleList = dataStoreObj.retrieveUnfetchedHandles();

        retrievedHandle = handleList.get(0);

        assertEquals(retrievedHandle.getHandleId(), TEST_HANDLE_WITH_HANDLE_ID.getHandleId());
        assertEquals(retrievedHandle.getUsername(), TEST_HANDLE_WITH_HANDLE_ID.getUsername());
        assertEquals(retrievedHandle.getInstance(), TEST_HANDLE_WITH_HANDLE_ID.getInstance());
    }
}
