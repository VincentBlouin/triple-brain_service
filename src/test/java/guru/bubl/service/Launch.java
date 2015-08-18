/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service;
import guru.bubl.module.neo4j_graph_manipulator.graph.Neo4jModule;
import guru.bubl.service.usage_log.H2DataSource;
import guru.bubl.service.usage_log.SQLConnection;

public class Launch {
    public static void before(){
        SQLConnection.createTablesUsingDataSource(
                new H2DataSource()
        );
         Neo4jModule.clearDb();
    }
    public static void after(){}
}
