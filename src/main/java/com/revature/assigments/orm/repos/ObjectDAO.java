package com.revature.assigments.orm.repos;

import com.revature.assigments.orm.exceptions.ObjectNotFoundInDB;
import com.revature.assigments.orm.util.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class ObjectDAO {


    public boolean saveObject(Connection conn,
                                ArrayList<String> objectMapSequence,
                                TreeMap<String, ArrayList<String>> objectMapped,
                                HashMap<String, ArrayList<String>> instanceMapped){

        //1.-Check if @Entity table is in the DB >> Select * from @Entity

        //1.1-Drop Entity table

        //2.-Create @Entity Table

        //2.0.- Check if the Object Table exists in the DB

        if (!checkObjectTableInDB(conn, objectMapped)){
            //2.1-Build create table query

            //2.1.1-Select [table_name]
            StringBuilder createTableQuery= new StringBuilder("create table ");
            createTableQuery.append(objectMapped.get("TABLE").get(0));
            createTableQuery.append("( ");

            //2.1.2.-Add columns with their respective specs >> check object map
            for (String fieldKey: objectMapSequence) {
                createTableQuery.append(fieldKey+" ");
                for(String spec : objectMapped.get(fieldKey)){
                    if (!spec.isEmpty()){
                        createTableQuery.append(spec+" ");
                    }
                }
                createTableQuery.append(", ");
            }
            //2.1.3.-Add constraints for PK and FK
            createTableQuery.append(" constraint pk_"+objectMapped.get("TABLE").get(0)+" primary key ("+objectMapped.get("ID").get(0)+")");

            createTableQuery.append(" );");

            //2.1.4-Execute the create table query
            String sqlCreateTable=String.valueOf(createTableQuery);
            try{
                PreparedStatement pstmt = conn.prepareStatement(sqlCreateTable);
                pstmt.execute();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        //3.-Insert Instance value in @Entity table
        //3.1.-Build insert query
        StringBuilder insertTableQuery = new StringBuilder("insert");

        insertTableQuery.append(" into "+objectMapped.get("TABLE").get(0)+" (");
        for (String fieldKey: objectMapSequence){
            insertTableQuery.append(fieldKey+", ");
        }
        insertTableQuery.delete((insertTableQuery.length()-2), insertTableQuery.length());
        insertTableQuery.append(") values (");
        for (String fieldKey : objectMapSequence){
            insertTableQuery.append("?,");
        }
        insertTableQuery.delete((insertTableQuery.length()-1), insertTableQuery.length());
        insertTableQuery.append(");");


        String sqlInsertIntoTable=String.valueOf(insertTableQuery);
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlInsertIntoTable);
            int i=0;
            for(String fieldKey :objectMapSequence){
                i++;
                String dataType = findDataType(instanceMapped, fieldKey);
                String dataValue = findDataValue(instanceMapped,fieldKey);

                    switch (dataType){
                        case "java.lang.String":
                            pstmt.setString(i,dataValue);
                            break;
                        case "int":
                            pstmt.setInt(i,Integer.valueOf(dataValue));
                            break;
                        case "float":
                                pstmt.setFloat(i,Float.parseFloat(dataValue));
                                break;
                        case "double":
                                pstmt.setDouble(i,Double.valueOf(dataValue));
                                break;
                        case "boolean":
                                pstmt.setBoolean(i,Boolean.valueOf(dataValue));
                                break;
                        default:
                                break;
                        }

                }

                int rowInserted = pstmt.executeUpdate();
                if((rowInserted!=0)){
                    //4.-Send status of the process
                    return true;
                }
            } catch (SQLException e) {
            e.printStackTrace();
        }

        //4.-Send status of the process
        return false;
    }

    private boolean checkObjectTableInDB(Connection conn, TreeMap<String, ArrayList<String>> objectMapped){

        StringBuilder selectQuery = new StringBuilder("select * from ");
        selectQuery.append(objectMapped.get("TABLE").get(0) + ";");

        try{
            PreparedStatement pstmt = conn.prepareStatement(selectQuery.toString());
            ResultSet rs = pstmt.executeQuery();

        } catch (SQLException e) {
            return false;
        }

        return true;
    }

    private String findDataType(HashMap<String, ArrayList<String>> instanceMapped, String key){

        return instanceMapped.get(key).get(0);
    }

    private String findDataValue(HashMap<String, ArrayList<String>> instanceMapped, String key){
        return instanceMapped.get(key).get(1);
    }

    /**
     * This method is responsible to bring the instance data from DB and return it as map
     * @param conn
     * @param object -- this is the object use to build the select statement
     * @param objectField -- This is the column to be include in the select statement where clause
     * @param objectFieldValue -- This is the value that we'll use to filter the select statement
     * @param objectMapSequence
     * @param objectMapped
     * @param <T>
     * @param <E>
     * @return -- Map with the requestedObject
     * @throws ObjectNotFoundInDB
     */
    public <T,E> Map<?,?>requestObjectDataByField( Connection conn,
                                                   T object,
                                                   E objectField,
                                                   E objectFieldValue,
                                                   ArrayList<String> objectMapSequence,
                                                   TreeMap<String, ArrayList<String>> objectMapped) throws ObjectNotFoundInDB {



        //Mapping the instance MAP
        HashMap<String, ArrayList<String>> objectRequestedMapped = (HashMap<String, ArrayList<String>>) ObjectMapper.createObjectMapForDB(object);


        //1-Create Select Statement to bring object data
        StringBuilder selectQuery = new StringBuilder("select * from ").append(objectMapped.get("TABLE").get(0))
                .append(" where "+objectField+"= '").append(objectFieldValue)
                .append("';");
        try{
            PreparedStatement pstmt = conn.prepareStatement(String.valueOf(selectQuery));
            ResultSet rs = pstmt.executeQuery();
            //2- Check if the select statement brought data from DB
            while (rs.next()){
                //3.- Populate the instance Map with result from query
                for (String str : objectMapSequence){
                    objectRequestedMapped.get(str).add(rs.getString(str));
                }
            }

            } catch (SQLException e) {
                throw new ObjectNotFoundInDB("The object >>> " + object.getClass().toString() + " <<< not found in DB");
            }

        //4.-Return the instance map with DB data
        return objectRequestedMapped;
    }

}
