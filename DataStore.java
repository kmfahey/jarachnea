package jarachnea;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DataStore {
    private static final String[] BAD_INSTANCES_COLUMNS = {"instance", "issue"};
    private static final String[] HANDLES_COLUMNS_WITHOUT_HANDLE_ID = {"username", "instance"};
    private static final String[] HANDLES_COLUMNS_WITH_HANDLE_ID = {"handle_id", "username", "instance"};
    private static final String[] PROFILES_COLUMNS = {"profile_handle_id", "username", "instance", "considered", "profile_snippet"};
    private static final String[] RELATIONS_COLUMNS = {"profile_handle_id", "profile_username", "profile_instance", "relation_handle_id",
                                                       "relation_type", "relation_page_number", "relation_username", "relation_instance"};

    private String host;
    private String username;
    private String password;
    private String database;
    private Connection dbConnection;

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public DataStore(final String hostString, final String usernameString, final String passwordString,
                     final String databaseString) throws SQLException {
        String mysqlUrlString;

        host = hostString;
        username = usernameString;
        password = passwordString;
        database = databaseString;

        mysqlUrlString = "jdbc:mysql://localhost:3306/" + databaseString;

        dbConnection = DriverManager.getConnection(mysqlUrlString, username, password);
    }

    public ArrayList<Handle> retrieveUnfetchedHandles() throws SQLException {
        ArrayList<Handle> retrievedHandlesList;
        PreparedStatement statementObj;
        ResultSet retrievalResults;

        statementObj = dbConnection.prepareStatement("SELECT handles.handle_id, handles.username, handles.instance "
                                                     + "FROM handles LEFT JOIN profiles "
                                                     + "ON handles.handle_id = profiles.profile_handle_id "
                                                     + "WHERE profiles.profile_handle_id IS NULL;", HANDLES_COLUMNS_WITH_HANDLE_ID);
        retrievalResults = statementObj.executeQuery();

        retrievedHandlesList = new ArrayList<Handle>();

        while (retrievalResults.next()) {
            String handleId;
            String usernameString;
            String instanceString;
            Handle handleObj;

            handleId = retrievalResults.getString("handle_id");
            usernameString = retrievalResults.getString("username");
            instanceString = retrievalResults.getString("instance");
            handleObj = new Handle(Integer.valueOf(handleId), usernameString, instanceString);

            retrievedHandlesList.add(handleObj);
        }

        return retrievedHandlesList;
    }

    public HashMap<String, Instance> retrieveBadInstances() throws SQLException {
        HashMap<String, Instance> retrievedInstancesMap;
        PreparedStatement statementObj;
        ResultSet retrievalResults;

        statementObj = dbConnection.prepareStatement("SELECT instance, issue FROM bad_instances;", BAD_INSTANCES_COLUMNS);
        retrievalResults = statementObj.executeQuery();

        retrievedInstancesMap = new HashMap<String, Instance>();

        while (retrievalResults.next()) {
            String instanceHostString;
            String instanceIssueString;
            int instanceIssueFlag;
            Instance instanceObj;

            instanceHostString = retrievalResults.getString("instance");
            instanceIssueString = retrievalResults.getString("issue");

            if (instanceIssueString.equals("suspended")) {
                instanceIssueFlag = Instance.SUSPENDED;
            } else if (instanceIssueString.equals("malfunctioning")) {
                instanceIssueFlag = Instance.MALFUNCTIONING;
            } else if (instanceIssueString.equals("unparseable")) {
                instanceIssueFlag = Instance.UNPARSEABLE;
            } else {
                instanceIssueFlag = Instance.IN_GOOD_STANDING;
            }

            instanceObj = new Instance(instanceHostString, instanceIssueFlag);
            retrievedInstancesMap.put(instanceHostString, instanceObj);
        }

        return retrievedInstancesMap;
    }

    public boolean storeHandle(final Handle handleObj) throws SQLException {
        Formatter formatterObj;
        String[] instanceValuesWithoutHandleId = {null, null};
        String[] instanceValuesWithHandleId = {null, null, null};
        
        if (handleObj.getHandleId() == -1) {
            instanceValuesWithoutHandleId[0] = handleObj.getUsername();
            instanceValuesWithoutHandleId[1] = handleObj.getInstance();

            return insertIntoTableKeysAndValues("handles", HANDLES_COLUMNS_WITHOUT_HANDLE_ID, instanceValuesWithoutHandleId);
        } else {
            instanceValuesWithHandleId[0] = ((Integer) handleObj.getHandleId()).toString();
            System.out.println(instanceValuesWithHandleId[0]);
            instanceValuesWithHandleId[1] = handleObj.getUsername();
            instanceValuesWithHandleId[2] = handleObj.getInstance();

            return insertIntoTableKeysAndValues("handles", HANDLES_COLUMNS_WITH_HANDLE_ID, instanceValuesWithHandleId);
        }
    }

    public boolean storeInstance(final Instance instanceObj) throws ProcessingException, SQLException {
        Formatter formatterObj;
        String[] instanceValues = {null, null};
        int instanceFlag;

        instanceValues[0] = instanceObj.getInstanceHostname();
        instanceFlag = instanceObj.getInstanceStatus();

        if (((Integer) instanceFlag).equals(Instance.SUSPENDED)) {
            instanceValues[1] = "suspended";
        } else if (((Integer) instanceFlag).equals(Instance.MALFUNCTIONING)) {
            instanceValues[1] = "malfunctioning";
        } else if (((Integer) instanceFlag).equals(Instance.UNPARSEABLE)) {
            instanceValues[1] = "unparseable";
        } else if (((Integer) instanceFlag).equals(Instance.IN_GOOD_STANDING)) {
            throw new ProcessingException("table `bad_instances` does not store instances in good standing");
        }

        return insertIntoTableKeysAndValues("bad_instances", BAD_INSTANCES_COLUMNS, instanceValues);
    }

    public boolean insertIntoTableKeysAndValues(final String tableName, final String[] sqlKeys, final String[] sqlValues) throws SQLException {
        PreparedStatement statementObj;
        Formatter formatterObj;
        String keysStatement;
        String valuesStatement;
        String sqlInsertStatement;

        formatterObj = new Formatter();

        keysStatement = "(";
        valuesStatement = "(";

        for (int keysIndex = 0; keysIndex < sqlKeys.length; keysIndex++) {
            keysStatement += sqlKeys[keysIndex];
            if (keysIndex < sqlKeys.length - 1) {
                keysStatement += ", ";
            }
        }

        keysStatement += ")";
        valuesStatement = "(";

        for (int valuesIndex = 0; valuesIndex < sqlValues.length; valuesIndex++) {
            valuesStatement += '"'+sqlValues[valuesIndex]+'"';
            if (valuesIndex < sqlValues.length - 1) {
                valuesStatement += ", ";
            }
        }

        valuesStatement += ")";

        sqlInsertStatement = formatterObj.format("INSERT INTO %s %s VALUES %s", tableName, keysStatement, valuesStatement).toString();

        statementObj = dbConnection.prepareStatement(sqlInsertStatement);
        return statementObj.execute();
    }

    public boolean clearTable(final String tableName) throws SQLException {
        PreparedStatement statementObj;
        ResultSet retrievalResults;
        Formatter formatterObj;
        String sqlDeleteStatement;

        formatterObj = new Formatter();

        sqlDeleteStatement = formatterObj.format("DELETE FROM %s;", tableName).toString();
        statementObj = dbConnection.prepareStatement(sqlDeleteStatement);
        return statementObj.execute();
    }
}
