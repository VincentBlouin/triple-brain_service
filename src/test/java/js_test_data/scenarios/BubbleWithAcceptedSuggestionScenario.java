/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.UserGraph;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.json.graph.SubGraphJson;
import guru.bubl.test.module.utils.ModelTestScenarios;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;

public class BubbleWithAcceptedSuggestionScenario implements JsTestScenario {

    /*
    * Event-date->2016/01/15
    * Has a generic identification to freebase "Event" http://rdf.freebase.com/rdf/m/02xm94t
    * Has 2 suggestions, one related to Person identification and the other to "date" identification
    */

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    @Inject
    ModelTestScenarios modelTestScenarios;

    User user = User.withEmailAndUsername("a", "b");

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.createForUser(user);
        VertexOperator bubble = vertexFactory.createForOwnerUsername(
                user.username()
        );
        bubble.label("Event");
        bubble.addGenericIdentification(
                modelTestScenarios.event()
        );
        bubble.addSuggestions(
                modelTestScenarios.suggestionsToMap(
                        modelTestScenarios.peopleInvolvedSuggestionFromEventIdentification(user),
                        modelTestScenarios.startDateSuggestionFromEventIdentification(user)
                )
        );
        VertexOperator childBubble = vertexFactory.createForOwnerUsername(
                user.username()
        );
        childBubble.label("2016/01/17");
        childBubble.addGenericIdentification(
                modelTestScenarios.startDateIdentification()
        );
        EdgeOperator edge = bubble.addRelationToVertex(childBubble);
        edge.label("start date");
        edge.addGenericIdentification(modelTestScenarios.startDateIdentification());
        return SubGraphJson.toJson(
                userGraph.graphWithDepthAndCenterVertexId(
                        1,
                        bubble.uri()
                )
        );
    }
}
