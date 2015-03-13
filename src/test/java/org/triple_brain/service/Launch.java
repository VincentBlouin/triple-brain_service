/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service;
import org.triple_brain.module.neo4j_graph_manipulator.graph.Neo4jModule;

public class Launch {
    public static void before(){
        Neo4jModule.clearDb();
    }
    public static void after(){}
}
