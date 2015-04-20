/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package js_test_data.scenarios;

import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.GraphFactory;
import org.triple_brain.module.model.graph.SubGraphPojo;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.edge.EdgeOperator;
import org.triple_brain.module.model.graph.edge.EdgePojo;
import org.triple_brain.module.model.graph.vertex.VertexFactory;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.model.json.graph.SubGraphJson;

import javax.inject.Inject;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

public class DeepGraphWithCircularityScenario implements JsTestScenario {

    /*
    * Keep relations order
    * b3<-r1-b2
    * b4-r2->b1
    * b1-r3->b2
    */

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    private VertexOperator
            b1,
            b2,
            b3,
            b4;

    private EdgeOperator
            r1,
            r2,
            r3;

    User user = User.withEmailAndUsername("a", "b");

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.createForUser(user);
        createVertices();
        createRelations();
        SubGraphPojo subGraphPojo = userGraph.graphWithDepthAndCenterVertexId(
                3,
                b3.uri()
        );
        Map<URI, EdgePojo> relations = new LinkedHashMap<>();
        relations.put(
                r1.uri(),
                (EdgePojo) subGraphPojo.edgeWithIdentifier(r1.uri())
        );
        relations.put(
                r2.uri(),
                (EdgePojo) subGraphPojo.edgeWithIdentifier(r2.uri())
        );
        relations.put(
                r3.uri(),
                (EdgePojo) subGraphPojo.edgeWithIdentifier(r3.uri())
        );
        SubGraphPojo subGraphPojoWithSpecificRelationsOrder = SubGraphPojo.withVerticesAndEdges(
                subGraphPojo.vertices(),
                relations
        );

        return SubGraphJson.toJson(subGraphPojoWithSpecificRelationsOrder);
    }

    private void createVertices() {
        b1 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b1.label("b1");
        b2 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b2.label("b2");
        b3 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b3.label("b3");
        b4 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        b4.label("b4");
    }

    private void createRelations() {
        r1 = b2.addRelationToVertex(b3);
        r1.label("r1");
        r2 = b4.addRelationToVertex(b1);
        r2.label("r2");
        r3 = b1.addRelationToVertex(b2);
        r3.label("r3");
    }
}
