/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import com.google.common.collect.Sets;
import guru.bubl.module.model.graph.*;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;
import guru.bubl.module.model.Image;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.graph.SubGraphJson;

import javax.inject.Inject;
import java.net.URI;


public class GroupRelationWithImageScenario implements JsTestScenario {

    /*
    * some project-idea for 1->idea 1
    * some project-idea for 2->idea 2
    * relation "idea for" is identified to idea which has an image
    * some project-has component 1->component 1
    * some project-has component 2->component 2
    * has component is identified to component which has no images
    * some project-other relation->-other bubble
    * some project-other relation->-other bubble 2
    * some project-other relation->-other bubble 3
    */

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    private VertexOperator
            someProject,
            idea1,
            idea2,
            component1,
            component2,
            otherBubble,
            otherBubble2,
            otherBubble3;

    User user = User.withEmailAndUsername("a", "b");

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        createVertices();
        createEdges();
        SubGraphPojo subGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                someProject.uri()
        );
        return SubGraphJson.toJson(
                subGraph
        );
    }

    private void createVertices() {
        someProject = vertexFactory.createForOwnerUsername(
                user.username()
        );
        someProject.label("some project");
        idea1 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        idea1.label("idea 1");
        idea2 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        idea2.label("idea 2");
        component1 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        component1.label("component 1");
        component2 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        component2.label("component 2");
        otherBubble = vertexFactory.createForOwnerUsername(
                user.username()
        );
        otherBubble.label("other bubble");
        otherBubble2 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        otherBubble2.label("other bubble 2");
        otherBubble3 = vertexFactory.createForOwnerUsername(
                user.username()
        );
        otherBubble3.label("other bubble 3");
    }

    private void createEdges() {
        FriendlyResourcePojo ideaFriendlyResource = new FriendlyResourcePojo(
                "idea"
        );
        ideaFriendlyResource.setImages(Sets.newHashSet(
                Image.withBase64ForSmallAndUriForBigger(
                        "dummy_base_64",
                        URI.create("big_url")
                )
        ));
        IdentifierPojo ideaIdentification = new IdentifierPojo(
                URI.create(
                        "http://external-uri.com/idea"
                ),
                ideaFriendlyResource
        );
        EdgeOperator rIdea1 = someProject.addRelationToVertex(idea1);
        rIdea1.label("idea for 1");
        rIdea1.addMeta(ideaIdentification);

        EdgeOperator rIdea2 = someProject.addRelationToVertex(idea2);
        rIdea2.label("idea for 2");
        rIdea2.addMeta(ideaIdentification);

        IdentifierPojo componentIdentification = new IdentifierPojo(
                URI.create(
                        "http://external-uri.com/component"
                ),
                new FriendlyResourcePojo(
                        "component"
                )
        );
        EdgeOperator rComponent1 = someProject.addRelationToVertex(component1);
        rComponent1.label("has component 1");
        rComponent1.addMeta(componentIdentification);

        EdgeOperator rComponent2 = someProject.addRelationToVertex(component2);
        rComponent2.label("has component 2");
        rComponent2.addMeta(componentIdentification);

        someProject.addRelationToVertex(
                otherBubble
        ).label("other relation");

        someProject.addRelationToVertex(
                otherBubble2
        ).label("other relation");

        someProject.addRelationToVertex(
                otherBubble3
        ).label("other relation");
    }
}
