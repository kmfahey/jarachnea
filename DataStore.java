package jarachnea;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DataStore {
    private static final String[] BAD_INSTANCES_COLUMNS = {"instance", "issue"};
    private static final String[] HANDLES_COLUMNS_WITHOUT_HANDLE_ID = {"username", "instance"};
    private static final String[] HANDLES_COLUMNS_WITH_HANDLE_ID = {"handle_id", "username", "instance"};
    private static final String[] HANDLES_COLUMNS_HANDLE_ID = {"handle_id"};
    private static final String[] PROFILES_COLUMNS = {"profile_handle_id", "username", "instance", "considered", "profile_snippet"};
    private static final String[] PROFILES_HANDLE_COLUMNS = {"profile_handle_id", "username", "instance"};
    private static final String[] RELATIONS_HANDLE_COLUMNS = {"relation_handle_id", "relation_username", "relation_instance"};
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

    public ArrayList<Handle> retrieveUnfetchedHandlesFromRelationsVsProfiles() throws SQLException {
        ArrayList<Handle> retrievedHandlesList;
        PreparedStatement statementObj;
        ResultSet retrievalResults;

        statementObj = dbConnection.prepareStatement("SELECT DISTINCT relation_handle_id, relation_username, relation_instance FROM relations "
                                                     + "LEFT JOIN profiles ON relations.relation_handle_id = profiles.profile_handle_id "
                                                     + "WHERE profiles.profile_handle_id IS NULL;", RELATIONS_HANDLE_COLUMNS);

        retrievalResults = statementObj.executeQuery();

        retrievedHandlesList = new ArrayList<Handle>();

        while (retrievalResults.next()) {
            String handleId;
            String usernameString;
            String instanceString;
            Handle handleObj;

            handleId = retrievalResults.getString("relation_handle_id");
            usernameString = retrievalResults.getString("relation_username");
            instanceString = retrievalResults.getString("relation_instance");
            handleObj = new Handle(Integer.valueOf(handleId), usernameString, instanceString);

            retrievedHandlesList.add(handleObj);
        }

        return retrievedHandlesList;
    }

    public ArrayList<Handle> retrieveUnfetchedHandlesFromProfilesVsRelations() throws SQLException {
        ArrayList<Handle> retrievedHandlesList;
        PreparedStatement statementObj;
        ResultSet retrievalResults;

        statementObj = dbConnection.prepareStatement("SELECT profiles.profile_handle_id, username, instance FROM profiles "
                                                     + "LEFT JOIN relations ON profiles.profile_handle_id = relations.profile_handle_id "
                                                     + "WHERE relations.profile_handle_id IS NULL ORDER BY RAND();", PROFILES_HANDLE_COLUMNS);
        retrievalResults = statementObj.executeQuery();

        retrievedHandlesList = new ArrayList<Handle>();

        while (retrievalResults.next()) {
            String handleId;
            String usernameString;
            String instanceString;
            Handle handleObj;

            handleId = retrievalResults.getString("profile_handle_id");
            usernameString = retrievalResults.getString("username");
            instanceString = retrievalResults.getString("instance");
            handleObj = new Handle(Integer.valueOf(handleId), usernameString, instanceString);

            retrievedHandlesList.add(handleObj);
        }

        return retrievedHandlesList;
    }
    public ArrayList<Handle> retrieveUnfetchedHandlesFromHandlesVsProfiles() throws SQLException {
        ArrayList<Handle> retrievedHandlesList;
        PreparedStatement statementObj;
        ResultSet retrievalResults;

        statementObj = dbConnection.prepareStatement("SELECT handles.handle_id, handles.username, handles.instance "
                                                     + "FROM handles LEFT JOIN profiles "
                                                     + "ON handles.handle_id = profiles.profile_handle_id "
                                                     + "WHERE profiles.profile_handle_id IS NULL ORDER BY RAND();", HANDLES_COLUMNS_WITH_HANDLE_ID);
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
        ArrayList<String[]> resultsList;

        resultsList = selectFromTable("bad_instances", BAD_INSTANCES_COLUMNS);
        retrievedInstancesMap = new HashMap<String, Instance>();

        for (int index = 0; index < resultsList.size(); index++) {
            String instanceHostString;
            String instanceIssueString;
            int instanceIssueFlag;
            Instance instanceObj;

            instanceHostString = resultsList.get(index)[0];
            instanceIssueString = resultsList.get(index)[1];

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

    public Profile retrieveProfile(final Handle profileHandleObj) throws SQLException, MalformedURLException, ProcessingException {
        Profile profileObj;
        String[] sqlWhereKeys = {"username", "instance"};
        String[] sqlWhereValues = new String[2];
        ArrayList<String[]> profilesResults;
        ArrayList<String[]> relationsTypesAndPages;
        String[] relationsDistinctColumns = {"relation_type", "relation_page_number"};
        String[] resultLine;

        sqlWhereValues[0] = profileHandleObj.getUsername();
        sqlWhereValues[1] = profileHandleObj.getInstance();

        profilesResults = selectFromTable("profiles", PROFILES_COLUMNS, sqlWhereKeys, sqlWhereValues);
        resultLine = profilesResults.get(0);
        profileObj = new Profile(new Handle(Integer.valueOf(resultLine[0]), resultLine[1], resultLine[2]),
                                 resultLine[3].equals("1") ? true : false, resultLine[4]);

        relationsTypesAndPages = selectWithStatement("SELECT DISTINCT relation_type, relation_page_number FROM relations "
                                                     + "WHERE profile_username = 'Gargron' AND profile_instance = 'mastodon.social' "
                                                     + "ORDER BY relation_type, relation_page_number;", relationsDistinctColumns);
        for (int index = 0; index < relationsTypesAndPages.size(); index++) {
            RelationSet relationSetObj;
            int relationType;
            int relationPageNumber;

            relationType = (relationsTypesAndPages.get(index)[0].equals("following")) ? Relation.IS_FOLLOWED_BY : Relation.IS_FOLLOWER_OF;
            relationPageNumber = Integer.valueOf(relationsTypesAndPages.get(index)[1]);
            relationSetObj = retrieveRelationSet(profileObj.getProfileHandle(), relationType, relationPageNumber);

            if (((Integer) relationType).equals(Relation.IS_FOLLOWED_BY)) {
                profileObj.getFollowingMap().put(relationPageNumber, relationSetObj);
            } else if (((Integer) relationType).equals(Relation.IS_FOLLOWER_OF)) {
                profileObj.getFollowersMap().put(relationPageNumber, relationSetObj);
            } else {
                throw new ProcessingException("unknown relation type flag " + relationSetObj.getRelationType());
            }
        }

        return profileObj;
    }

    public void storeProfile(final Profile profileObj) throws SQLException {
        ArrayList<HashMap<Integer, RelationSet>> relationsMapList;
        String[] profileValues;
        Handle profileHandle;

        relationsMapList = new ArrayList<HashMap<Integer, RelationSet>>();
        profileValues = new String[5];
        profileHandle = profileObj.getProfileHandle();

        if (profileHandle.getHandleId() == -1) {
            updateHandleWithHandleId(profileHandle);
        }

        profileValues[0] = String.valueOf(profileHandle.getHandleId());
        profileValues[1] = profileHandle.getUsername();
        profileValues[2] = profileHandle.getInstance();
        profileValues[3] = profileObj.getProfileConsidered() ? "1" : "0";
        profileValues[4] = profileObj.getProfileSnippet();

        insertIntoTable("profiles", PROFILES_COLUMNS, profileValues);

        relationsMapList.add(profileObj.getFollowingMap());
        relationsMapList.add(profileObj.getFollowersMap());

        for (int index = 0; index < relationsMapList.size(); index++) {
            HashMap<Integer, RelationSet> relationMap;
            String relationString;
            Iterator relationSetValueMapIter;

            relationMap = relationsMapList.get(index);
            relationSetValueMapIter = relationMap.values().iterator();

            while (relationSetValueMapIter.hasNext()) {
                RelationSet relationSetObj;

                relationSetObj = (RelationSet) relationSetValueMapIter.next();
                storeRelationSet(relationSetObj);
            }
        }
    }

    public void updateHandleWithHandleId(final Handle handleObj) throws SQLException {
        ArrayList<String[]> handleIdSelectResult;
        String[] handleValues = new String[2];
        int handleId;

        handleValues[0] = handleObj.getUsername();
        handleValues[1] = handleObj.getInstance();

        handleIdSelectResult = selectFromTable("handles", HANDLES_COLUMNS_HANDLE_ID, HANDLES_COLUMNS_WITHOUT_HANDLE_ID, handleValues);
        if (handleIdSelectResult.size() == 0) {
            insertIntoTable("handles", HANDLES_COLUMNS_WITHOUT_HANDLE_ID, handleValues);
            handleIdSelectResult = selectFromTable("handles", HANDLES_COLUMNS_HANDLE_ID, HANDLES_COLUMNS_WITHOUT_HANDLE_ID, handleValues);
        }
        handleId = Integer.valueOf(handleIdSelectResult.get(0)[0]);

        handleObj.setHandleId(handleId);
    }

    public RelationSet retrieveRelationSet(final Handle profileHandleObj, final int relationType, final int relationPageNumber) throws SQLException {
        ArrayList<String[]> relationsResults;
        RelationSet relationSetObj;
        String[] sqlWhereKeys = {"profile_username", "profile_instance", "relation_type", "relation_page_number"};
        String[] sqlWhereValues = new String[4];

        relationSetObj = new RelationSet(profileHandleObj, relationType, relationPageNumber);

        sqlWhereValues[0] = profileHandleObj.getUsername();
        sqlWhereValues[1] = profileHandleObj.getInstance();
        sqlWhereValues[2] = (relationType == Relation.IS_FOLLOWED_BY) ? "following" : "followers";
        sqlWhereValues[3] = String.valueOf(relationPageNumber);

        relationsResults = selectFromTable("relations", RELATIONS_COLUMNS, sqlWhereKeys, sqlWhereValues);

        for (int index = 0; index < relationsResults.size(); index++) {
            Relation newRelationObj;
            Handle relationHandleObj;
            String[] relationResult;

            relationResult = relationsResults.get(index);
            relationHandleObj = new Handle(Integer.valueOf(relationResult[3]), relationResult[6], relationResult[7]);
            newRelationObj = new Relation(profileHandleObj, relationHandleObj, relationType, relationPageNumber);
            relationSetObj.add(newRelationObj);
        }

        return relationSetObj;
    }

    public void storeRelationSet(final RelationSet relationSetObj) throws SQLException {
        Iterator relationSetIter;

        relationSetIter = relationSetObj.iterator();
        while (relationSetIter.hasNext()) {
            Relation relationObj;
            Handle profileHandle;
            Handle relationHandle;
            String[] relationsInsertValues = new String[8];

            relationObj = (Relation) relationSetIter.next();
            profileHandle = relationObj.getProfileHandle();
            relationHandle = relationObj.getRelationHandle();

            if (profileHandle.getHandleId() == -1) {
                updateHandleWithHandleId(profileHandle);
            }
            if (relationHandle.getHandleId() == -1) {
                updateHandleWithHandleId(relationHandle);
            }

            relationsInsertValues[0] = String.valueOf(profileHandle.getHandleId());
            relationsInsertValues[1] = profileHandle.getUsername();
            relationsInsertValues[2] = profileHandle.getInstance();
            relationsInsertValues[3] = String.valueOf(relationHandle.getHandleId());
            relationsInsertValues[4] = (relationObj.getRelationType() == Relation.IS_FOLLOWED_BY) ? "following" : "followers";
            relationsInsertValues[5] = String.valueOf(relationObj.getRelationPageNumber());
            relationsInsertValues[6] = relationHandle.getUsername();
            relationsInsertValues[7] = relationHandle.getInstance();

            insertIntoTable("relations", RELATIONS_COLUMNS, relationsInsertValues);
        }
    }

    public void storeHandle(final Handle handleObj) throws SQLException {
        Formatter formatterObj;
        String[] instanceValuesWithoutHandleId = {null, null};
        String[] instanceValuesWithHandleId = {null, null, null};

        if (handleObj.getHandleId() == -1) {
            instanceValuesWithoutHandleId[0] = handleObj.getUsername();
            instanceValuesWithoutHandleId[1] = handleObj.getInstance();
            insertIntoTable("handles", HANDLES_COLUMNS_WITHOUT_HANDLE_ID, instanceValuesWithoutHandleId);
            updateHandleWithHandleId(handleObj);
        } else {
            instanceValuesWithHandleId[0] = ((Integer) handleObj.getHandleId()).toString();
            instanceValuesWithHandleId[1] = handleObj.getUsername();
            instanceValuesWithHandleId[2] = handleObj.getInstance();
            insertIntoTable("handles", HANDLES_COLUMNS_WITH_HANDLE_ID, instanceValuesWithHandleId);
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

        return insertIntoTable("bad_instances", BAD_INSTANCES_COLUMNS, instanceValues);
    }

    public ArrayList<String[]> selectFromTable(final String tableName, final String[] sqlColumns, final String[] sqlWhereKeys, final String[] sqlWhereValues) throws SQLException {
        PreparedStatement statementObj;
        ResultSet statementResult;
        Formatter formatterObj;
        ArrayList<String[]> resultLines;
        String columnsStatement;
        String whereStatement;
        String sqlSelectStatement;

        formatterObj = new Formatter();
        resultLines = new ArrayList<String[]>();

        columnsStatement = "";
        for (int index = 0; index < sqlColumns.length; index++) {
            columnsStatement += sqlColumns[index] + ((index < sqlColumns.length - 1) ? ", " : "");
        }

        whereStatement = "";
        for (int index = 0; index < sqlWhereKeys.length; index++) {
            String conditionSql;

            conditionSql = sqlWhereKeys[index] + " = " + '"' + String.valueOf(sqlWhereValues[index]) + '"';
            whereStatement += ((index != 0) ? " AND " : "") + conditionSql;
        }

        sqlSelectStatement = formatterObj.format("SELECT %s FROM %s WHERE %s;", columnsStatement, tableName, whereStatement).toString();

        return selectWithStatement(sqlSelectStatement, sqlColumns);
    }

    public ArrayList<String[]> selectFromTable(final String tableName, final String[] sqlKeys) throws SQLException {
        PreparedStatement statementObj;
        ResultSet statementResult;
        Formatter formatterObj;
        ArrayList<String[]> resultLines;
        String keysStatement;
        String sqlSelectStatement;

        formatterObj = new Formatter();
        resultLines = new ArrayList<String[]>();
        keysStatement = "";

        for (int keysIndex = 0; keysIndex < sqlKeys.length; keysIndex++) {
            keysStatement += sqlKeys[keysIndex];
            if (keysIndex < sqlKeys.length - 1) {
                keysStatement += ", ";
            }
        }

        sqlSelectStatement = formatterObj.format("SELECT %s FROM %s;", keysStatement, tableName).toString();

        return selectWithStatement(sqlSelectStatement, sqlKeys);
    }

    private ArrayList<String[]> selectWithStatement(final String sqlStatement, final String[] sqlKeys) throws SQLException {
        PreparedStatement statementObj;
        ResultSet statementResult;
        ArrayList<String[]> resultLines;

        statementObj = dbConnection.prepareStatement(sqlStatement, sqlKeys);
        statementResult = statementObj.executeQuery();
        resultLines = new ArrayList<String[]>();

        while (statementResult.next()) {
            String[] resultLineValues = new String[sqlKeys.length];

            for (int keyIndex = 0; keyIndex < sqlKeys.length; keyIndex += 1) {
                resultLineValues[keyIndex] = statementResult.getString(sqlKeys[keyIndex]);
            }

            resultLines.add(resultLineValues);
        }

        return resultLines;
    }

    public boolean insertIntoTable(final String tableName, final String[] sqlKeys, final String[] sqlValues) throws SQLException {
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
            valuesStatement += '"' + sqlValues[valuesIndex].replace("\"", "\\\"") + '"';
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
