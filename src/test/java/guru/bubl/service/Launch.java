/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service;
import guru.bubl.module.neo4j_graph_manipulator.graph.Neo4jModule;

public class Launch {
    public static void before(){
        Neo4jModule.clearDb();
    }
    public static void after(){}
}
