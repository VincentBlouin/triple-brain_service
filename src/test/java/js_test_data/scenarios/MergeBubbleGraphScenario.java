/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package js_test_data.scenarios;

import com.google.common.collect.Sets;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.GraphFactory;
import org.triple_brain.module.model.graph.SubGraphPojo;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.edge.Edge;
import org.triple_brain.module.model.graph.edge.EdgeOperator;
import org.triple_brain.module.model.graph.vertex.Vertex;
import org.triple_brain.module.model.graph.vertex.VertexFactory;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.model.json.graph.SubGraphJson;

import javax.inject.Inject;

public class MergeBubbleGraphScenario implements JsTestScenario {

    /*
             one bubble labeled merge
             merge contains bubbles
             b1-r1->b2
             b2-r2->b3
             b1<-r4-b4
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
            r4;

    User user = User.withEmailAndUsername("a", "b");

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.createForUser(user);
        createVertices();
        createEdges();
        VertexOperator mergeBubble = vertexFactory.createFromGraphElements(
                Sets.<Vertex>newHashSet(
                        b1,
                        b2,
                        b3,
                        b4
                ),
                Sets.<Edge>newHashSet(
                        r1,
                        r2,
                        r4
                )
        );
        mergeBubble.label("merge");
        SubGraphPojo subGraphPojo = userGraph.graphWithDepthAndCenterVertexId(
                1,
                mergeBubble.uri()
        );
        return SubGraphJson.toJson(
                subGraphPojo
        );
    }


    private void createVertices(){
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

    private void createEdges(){
        r1 = b1.addRelationToVertex(b2);
        r1.label("r1");
        r2 = b2.addRelationToVertex(b3);
        r2.label("r2");
        r4 = b4.addRelationToVertex(b1);
        r4.label("r4");
    }
}
