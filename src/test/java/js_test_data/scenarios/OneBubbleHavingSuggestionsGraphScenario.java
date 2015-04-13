/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package js_test_data.scenarios;

import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.GraphFactory;
import org.triple_brain.module.model.graph.ModelTestScenarios;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.vertex.VertexFactory;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.model.json.graph.SubGraphJson;

import javax.inject.Inject;

public class OneBubbleHavingSuggestionsGraphScenario implements JsTestScenario {

    /*
    * Bubble labeled Event.
    * Has a generic identification to freebase "Event" http://rdf.freebase.com/rdf/m/02xm94t
    * Has 2 suggestions related to "Event" identification
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
        return SubGraphJson.toJson(
                userGraph.graphWithDepthAndCenterVertexId(
                        1,
                        bubble.uri()
                )
        );
    }
}
