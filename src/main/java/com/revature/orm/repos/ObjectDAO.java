package com.revature.orm.repos;

import com.revature.orm.exceptions.ObjectNotFoundInDB;
import com.revature.orm.services.ObjectService;
import com.revature.orm.util.ObjectMapper;

import java.sql.*;
import java.util.*;


public class ObjectDAO {
    
    private static final ObjectDAO objectDAO = new ObjectDAO();
    
    public static ObjectDAO getInstance(){return objectDAO;}
    
    /**
     *  This method build the query to insert the object
     * @param conn
     * @param fieldSequence -- This sequence help to build first part of the query
     * @param classMap -- This map contains the class info to create the table
     * @param objectMap -- This map contains the object info to be saved
     * @return -- TRUE if the object was saved and FALSE if the object wasn't saved
     */
    public boolean saveObject(Connection conn,
                              ArrayList<String> fieldSequence,
                              HashMap<String, ArrayList<String>> classMap,
                              HashMap<String, ArrayList<String>> objectMap) {

        //1.0.- Check if the Object Table exists in the DB
        
        if (!checkObjectTableInDB(conn, classMap)) {
            //1.1-Build create table query
            
            //1.1.1-Select [table_name]
            StringBuilder createTableQuery = new StringBuilder("create table ");
            createTableQuery.append(classMap.get("TABLE").get(0));
            createTableQuery.append("( ");
            
            //1.1.2.-Add columns with their respective specs >> check object map
            for (String fieldKey : fieldSequence) {
                createTableQuery.append(fieldKey + " ");
                for (int i = 0; i < classMap.get(fieldKey).size() - 2; i++) {
                    createTableQuery.append(classMap.get(fieldKey).get(i)+" ");
                }

                createTableQuery.append(", ");
            }
            //1.1.3.-Add constraints for PK and FK
            createTableQuery.append(" primary key (" +classMap.get("ID").get(0)+")");
            createTableQuery.append(" );");
            
            //1.1.4-Execute the create table query
            String sqlCreateTable = String.valueOf(createTableQuery);
            try {
                PreparedStatement pstmt = conn.prepareStatement(sqlCreateTable);
                pstmt.execute();
    
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        
        //2.-Insert Instance value in @Entity table
        //2.1.-Build insert query
        StringBuilder insertTableQuery = new StringBuilder("insert");
        
        insertTableQuery.append(" into " + classMap.get("TABLE").get(0) + " (");
        for (String fieldKey : fieldSequence) {
            insertTableQuery.append(fieldKey + ", ");
        }
        insertTableQuery.delete((insertTableQuery.length() - 2),
                                insertTableQuery.length());
        insertTableQuery.append(") values (");
        for (String fieldKey : fieldSequence) {
            insertTableQuery.append("?,");
        }
        insertTableQuery.delete((insertTableQuery.length() - 1),
                                insertTableQuery.length());
        insertTableQuery.append(");");
        
        
        String sqlInsertIntoTable = String.valueOf(insertTableQuery);
        try {
            PreparedStatement pstmt = conn.prepareStatement(sqlInsertIntoTable);
            int i = 0;
            for (String fieldKey : fieldSequence) {
                i++;
                String dataType = findDataType(classMap,
                                               fieldKey);
                String dataValue = findDataValue(objectMap,
                                                 fieldKey);
    
                switch (dataType) {
                    case "java.lang.String":
                        pstmt.setString(i, dataValue);
                        break;
                    case "int":
                        pstmt.setInt(i,
                                     Integer.valueOf(dataValue));
                        break;
                    case "float":
                        pstmt.setFloat(i,
                                       Float.parseFloat(dataValue));
                        break;
                    case "double":
                        pstmt.setDouble(i,
                                        Double.valueOf(dataValue));
                        break;
                    case "boolean":
                        pstmt.setBoolean(i,
                                         Boolean.valueOf(dataValue));
                        break;
                    default:
                        pstmt.setString(i,
                                        dataValue);
                        break;
                }
    
            }
    
            int rowInserted = pstmt.executeUpdate();
            if ((rowInserted != 0)) {
                //3.-Send status of the process
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        //3.-Send status of the process
        return false;
    }
    
    /**
     * this method is responsible to update the object in DB, It's takes the entire object and with the ID proceed
     * to update
     * @param conn
     * @param fieldSequence -- This is the sequence use it to build the query
     * @param classMap
     * @param objectMap
     * @return -- TRUE if the object was updated it and FALSE wasn't updated
     */
    public boolean updateObject (Connection conn,
                                 ArrayList<String> fieldSequence,
                                 HashMap<String, ArrayList<String>> classMap,
                                 HashMap<String, ArrayList<String>> objectMap){
        
        //1.-Update object in db
        //1.-Build update query
        StringBuilder updateQuery = new StringBuilder();
        updateQuery.append("update ").append(classMap.get("TABLE").get(0)).append(" set ");
        fieldSequence.forEach(field -> {
            if (!field.contains("id")){
                updateQuery.append(field).append("=").append("'"+objectMap.get(field).get(1)+"', ");
            }
        });
        updateQuery.delete((updateQuery.length() - 2),
                                updateQuery.length());
        
        updateQuery.append("where ").append(classMap.get("ID").get(0)+"=").append(objectMap.get(classMap.get("ID").get(0)).get(1)).append(";");
        String updateQueryString = updateQuery.toString();
        //1.2.- Execute the query
        try{
        PreparedStatement pstmt = conn.prepareStatement(updateQueryString);
        int rowInserted = pstmt.executeUpdate();
        if ((rowInserted != 0)) {
            //2.-Send status of the process
            return true;
            }
        } catch (SQLException e) {
        e.printStackTrace();
        }
        //2.-Send status of the process
        
        return false;
    }
    
    /**
     * This method is responsible to check if the object is already in the DB
     * @param conn
     * @param objectMapped
     * @return - TRUE is the object is in the DB and FALSE is not in the DB
     */
    private boolean checkObjectTableInDB(Connection conn, HashMap<String, ArrayList<String>> objectMapped) {
        
        StringBuilder selectQuery = new StringBuilder("select * from ");
        selectQuery.append(objectMapped.get("TABLE").get(0)).append(";");
        
        try {
            PreparedStatement pstmt = conn.prepareStatement(selectQuery.toString());
            ResultSet rs = pstmt.executeQuery();
            
        } catch (SQLException e) {
            return false;
        }
        
        return true;
    }
    
    public boolean deleteObject (Connection conn,
                                 HashMap<String, ArrayList<String>> classMap,
                                 HashMap<String, ArrayList<String>> objectMap){
        
        //1.-Update object in db
        //1.-Build delete query
        StringBuilder deleteQuery = new StringBuilder();
        deleteQuery.append("delete from ")
                   .append(classMap.get("TABLE").get(0))
                   .append(" where ")
                   .append(classMap.get("ID")
                   .get(0)).append("=")
                   .append(objectMap.get(classMap.get("ID").get(0)).get(1))
                   .append(";");
        
        String deleteQueryString = deleteQuery.toString();
        //1.2.- Execute the query
        try{
            PreparedStatement pstmt = conn.prepareStatement(deleteQueryString);
            int rowInserted = pstmt.executeUpdate();
            if ((rowInserted != 0)) {
                //2.-Send status of the process
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //2.-Send status of the process
        
        return false;
    }
    
    /**
     * This methedo find the Data Type inside the class map
     * @param classMap
     * @param key
     * @return - The Data Type for the key
     */
    private String findDataType(HashMap<String, ArrayList<String>> classMap, String key) {
        
        return classMap.get(key).get(4);
    }
    
    /**
     * This method find the Data Value inside the class map
     * @param objectMap
     * @param key
     * @return - The Data Value for the key
     */
    private String findDataValue(HashMap<String, ArrayList<String>> objectMap, String key) {
        return objectMap.get(key).get(1);
    }
    
    /**
     * This method is responsible to bring the instance data from DB and return it as map
     *
     * @param conn
     * @param object           -- this is the object use to build the select statement
     * @param objectField      -- This is the column to be include in the select statement where clause
     * @param objectFieldValue -- This is the value that we'll use to filter the select statement
     * @param classMap
     * @param <T>
     * @param <E>
     * @return -- Map with the requestedObject
     * @throws ObjectNotFoundInDB
     */
    public <T, E> Map<?, ?> requestObjectDataByField(Connection conn,
                                                     T object,
                                                     E objectField,
                                                     E objectFieldValue,
                                                     HashMap<String, ArrayList<String>> classMap) throws ObjectNotFoundInDB {
    
    
        //Mapping the instance MAP
        HashMap<String, String> objectRequestedMap = (HashMap<String, String>) ObjectMapper.createObjectMap(object);
    
    
        //1-Create Select Statement to bring object data
        StringBuilder selectQuery = new StringBuilder("select * from ").append(classMap.get("TABLE").get(0))
                .append(" where " + objectField + "= '").append(objectFieldValue)
                .append("';");
        try {
            PreparedStatement pstmt = conn.prepareStatement(String.valueOf(selectQuery));
            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData rsMetaData = rs.getMetaData();
    
            //2- Check if the select statement brought data from DB
            while (rs.next()) {
                //3.- Populate the object Map with result from query
                for (int i=1; i <= rsMetaData.getColumnCount(); i++){
                    int finalI = i;
                    classMap.forEach((key, value)->{
                        int pos = finalI;
                        try {
                            String columnName = rsMetaData.getColumnName(pos);
                            if(columnName.equals(key)){
                                switch (value.get(4)){
                                    case "int":
                                        objectRequestedMap.put(key, Integer.toString(rs.getInt(key)));
                                        break;
                                    default:
                                        objectRequestedMap.put(key, rs.getString(key));
                                        break;
                                }
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    });
                }
                
                
//                //Change by Sequence MAP 05.30.21
//                //3.- Populate the instance Map with result from query
//                for (int i = 0; i < rsMetaData.getColumnCount(); i++) {
//                    try {
//                        switch (rsMetaData.getColumnType(i)) {
//                            case "int":
//                                objectRequestedMapped.get().add(Integer.toString(rs.getInt(value)));
//                                break;
//                            default:
//                                objectRequestedMapped.get(value).add(rs.getString(value));
//                                break;
//                        }
//                    } catch (SQLException throwables) {
//                        throwables.printStackTrace();
//                    }

//                classMapSequence.forEach((key,value)-> {
//                    try {
//                        switch (key){
//                            case "int":
//                                int intSupport = rs.getInt(value);
//                                objectRequestedMapped.get(value).add(Integer.toString(rs.getInt(value)));
//                                break;
//                            case "double":
//                                objectRequestedMapped.get(value).add(Double.toString(rs.getDouble(value)));
//                                break;
//                            default:
//                                objectRequestedMapped.get(value).add(rs.getString(value));
//                                break;
//                        }
//
//                    } catch (SQLException throwables) {
//                        throwables.printStackTrace();
//                    }
//                });

//                    for (String str : classMapSequence) {
//                        objectRequestedMapped.get(str).add(rs.getString(str));
//                    }
//                }
//
//            } catch(SQLException e){
//
//            }
//
//            //4.-Return the instance map with DB data
//            return objectRequestedMapped;
//        }
    
            }
        } catch (SQLException throwables) {
            throw new ObjectNotFoundInDB("The object >>> " + object.getClass().toString() + " <<< not found in DB");
           }
        
        //4.-Return the instance map with DB data
            return objectRequestedMap;
    }
}