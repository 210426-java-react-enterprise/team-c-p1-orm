package com.revature.assigments.p1.repos;

import com.revature.assigments.p1.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class ClassDAO {

    public boolean saveInstance(Connection conn, ArrayList<String> objectMapSequence,
                                TreeMap<String, ArrayList<String>> objectMapped,
                                TreeMap<String, ArrayList<String>> instanceMapped){

        //1.-Check if @Entity table is in the DB >> Select * from @Entity

        //1.1-Drop Entity table

        //2.-Create @Entity Table

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

        //3.-Insert Instance value in @Entity table
        //3.1.-Build insert query
        StringBuilder insertTableQuery = new StringBuilder("insert");

        insertTableQuery.append(" into table "+objectMapped.get("TABLE").get(0)+" (");
        for (String fieldKey: objectMapSequence){
            insertTableQuery.append(fieldKey+", ");
        }
        insertTableQuery.delete((objectMapSequence.size())-1,objectMapSequence.size());
        insertTableQuery.append(") values (");
        for (String fieldKey : objectMapSequence){
            insertTableQuery.append("?,");
        }
        insertTableQuery.delete((objectMapSequence.size()),objectMapSequence.size());
        insertTableQuery.append(");");


        //HERE
        String sqlInsertIntoTable=String.valueOf(insertTableQuery);
        try{
            PreparedStatement pstmt = conn.prepareStatement(sqlInsertIntoTable);
            for(String fieldKey :objectMapSequence){
                int i=0;
                for(String str : instanceMapped.get(fieldKey)){
                    i++;
                    if (i> 1){
                        String dataType = instanceMapped.get(fieldKey).get(0);
                        String strValue;
                        switch (dataType){
                            case "java.lang.String":
                                strValue=instanceMapped.get(fieldKey).get(1);
                                pstmt.setString(i,strValue);
                                break;
                            case "int":
                                strValue=instanceMapped.get(fieldKey).get(1);
                                pstmt.setInt(i,Integer.valueOf(instanceMapped.get(fieldKey).get(1)));
                                break;
                            case "float":
                                strValue=instanceMapped.get(fieldKey).get(1);
                                pstmt.setFloat(i,Float.parseFloat(instanceMapped.get(fieldKey).get(1)));
                                break;
                            case "double":
                                strValue=instanceMapped.get(fieldKey).get(1);
                                pstmt.setDouble(i,Double.valueOf(instanceMapped.get(fieldKey).get(1)));
                                break;
                            case "boolean":
                                strValue=instanceMapped.get(fieldKey).get(1);
                                pstmt.setBoolean(i,Boolean.valueOf(instanceMapped.get(fieldKey).get(1)));
                                break;
                            default:
                                break;
                        }
                    }

                }
            }

            int rowInserted = pstmt.executeUpdate();
            if((rowInserted!=0)){
                //4.-Send status of the process
                return true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //4.-Send status of the process
        return false;
    }

}
