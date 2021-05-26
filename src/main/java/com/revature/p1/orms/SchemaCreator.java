package com.revature.p1.orms;

import com.revature.p1.annotations.MyColumn;
import com.revature.p1.annotations.MyEntity;

import java.lang.reflect.Field;
import java.util.*;

public class SchemaCreator
{
    private List<Class<?>> classes;

    public SchemaCreator(List<Class<?>> classes)
    {
        this.classes = classes;
    }

    public boolean createSchema()
    {
        Map<Class<?>,List<MyColumn>> classListMap = new HashMap<>();
        for (Class<?> aClass : classes)
        {
            List<MyColumn> columns = new ArrayList<>();
            if (aClass.isAnnotationPresent(MyEntity.class))
            {
                List<Field> fields = new ArrayList<>(Arrays.asList(aClass.getDeclaredFields()));
                for (Field field : fields)
                {
                    if (field.isAnnotationPresent(MyColumn.class))
                    {
                        columns.add(field.getAnnotation(MyColumn.class));
                    }
                }
            }
            classListMap.put(aClass, columns);
        }

        List<String> queries = createQuery(classListMap);
        System.out.println("\n********** createSchema() queries **********");
        queries.forEach(System.out::println);
        System.out.println("********************************************\n");
        return false;
    }

    private List<String> createQuery(Map<Class<?>,List<MyColumn>> classListMap)
    {
//        List<String> queries = new ArrayList<>();
//
//        classListMap.forEach((clazz,columns) -> {
//            StringBuilder sb = new StringBuilder();
//            sb.append("CREATE TABLE IF NOT EXISTS ").append(clazz.getAnnotation(MyEntity.class).name()).append(" (");
//            columns.forEach((column -> {
//                String type = switch (column.type())
//                        {
//                            case VARCHAR -> "VARCHAR(" + column.length() + ") ";
//                            case INT -> "INT";
//                            case SERIAL -> "SERIAL";
//                            case DECIMAL -> "DECIMAL";
//                            case TIME -> "TIME";
//                            case DATE -> "DATE";
//                        };
//                sb.append(column.name()).append(" ").append(type).append(column.nullable() ? "" : " NOT NULL ")
//                .append(column.unique() ? "UNIQUE " : "").append(column.pk() ? "PRIMARY KEY " : "")
//                .append(column.fk() ? "REFERENCES " + column.reference() + " ON DELETE " + column.delete() : "").append(",");
//            }));
//            sb.deleteCharAt(sb.lastIndexOf(",")).append(")");
//            queries.add(sb.toString());
//        });
//        return queries;
        return null;
    }


}
