/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.common_utils.NoExRun;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.graph.SubGraphJson;
import guru.bubl.test.module.utils.ModelTestScenarios;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;

public class OneBubbleHavingSuggestionsGraphScenario implements JsTestScenario {

    /*
    * Bubble labeled Event.
    * Has a generic identification to freebase "Event" http://rdf.freebase.com/rdf/m/02xm94t
    * Has 2 suggestions, one related to Person identification and the other to "date" identification
    */

    /*
    * Bubble labeled center.
    * connected to bubble event
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
        bubble.addMeta(
                modelTestScenarios.event()
        );
        bubble.addSuggestions(
                modelTestScenarios.suggestionsToMap(
                        modelTestScenarios.peopleInvolvedSuggestionFromEventIdentification(user),
                        modelTestScenarios.startDateSuggestionFromEventIdentification(user)
                )
        );
        return NoExRun.wrap(() -> {
            JSONObject json = new JSONObject().put(
                    "original",
                    SubGraphJson.toJson(
                            userGraph.graphWithDepthAndCenterBubbleUri(
                                    1,
                                    bubble.uri()
                            )
                    )
            );
            VertexOperator center = vertexFactory.createForOwnerUsername(
                    user.username()
            );
            center.label("center");
            center.addRelationToVertex(bubble);
            json.put(
                    "not_centered",
                    SubGraphJson.toJson(
                            userGraph.graphWithDepthAndCenterBubbleUri(
                                    1,
                                    center.uri()
                            )
                    )
            );
            return json;
        }).get();
    }
}