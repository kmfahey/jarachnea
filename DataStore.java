package jarachnea;

import java.util.ArrayList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DataStore {
    private String[] BAD_INSTANCES_COLUMNS = {"instance", "issue"};
    private String[] HANDLES_COLUMNS = {"handle_id", "username", "instance"};
    private String[] PROFILES_COLUMNS = {"profile_handle_id", "username", "instance", "considered", "profile_snippet"};
    private String[] RELATIONS_COLUMNS = {"profile_handle_id", "profile_username", "profile_instance", "relation_handle_id", "relation_type",
                                          "relation_page_number", "relation_username", "relation_instance"};

    private String host;
    private String username;
    private String password;
    private String database;
    private Connection dbConnection;

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
        ResultSet retrievalResults;
        PreparedStatement statementObj;

        statementObj = dbConnection.prepareStatement("SELECT handles.handle_id, handles.username, handles.instance " +
                                                     "FROM handles LEFT JOIN profiles " + 
                                                     "ON handles.handle_id = profiles.profile_handle_id " + 
                                                     "WHERE profiles.profile_handle_id IS NULL LIMIT 0;", HANDLES_COLUMNS);
        retrievalResults = statementObj.executeQuery();

        retrievedHandlesList = new ArrayList<Handle>();

        while (retrievalResults.next()) {
            String handleId;
            String username;
            String instance;
            Handle handleObj;

            handleId = retrievalResults.getString("handle_id");
            username = retrievalResults.getString("username");
            instance = retrievalResults.getString("instance");
            handleObj = new Handle(Integer.valueOf(handleId), username, instance);
            
            retrievedHandlesList.add(handleObj);
        }

        return retrievedHandlesList;
    }
}
