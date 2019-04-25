package server.hibernate.models;

import javax.persistence.Table;

public class EntityReader {
    public static String getName(Class entityClass){
        Table tableAnnotation = (Table) entityClass.getAnnotation(Table.class);
        return tableAnnotation.name();
    }
}
