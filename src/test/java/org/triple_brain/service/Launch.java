/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package org.triple_brain.service;
import org.triple_brain.module.neo4j_graph_manipulator.graph.Neo4jModule;

public class Launch {
    public static void before(){
        Neo4jModule.clearDb();
    }
    public static void after(){}
}
