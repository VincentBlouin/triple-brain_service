/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import guru.bubl.module.common_utils.NoEx;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.SubGraphJson;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.test.module.utils.ModelTestScenarios;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;

public class WithAcceptedSuggestionScenario implements JsTestScenario {

    /*
     * Event-start date->2016/01/15
     * Event-people involved->Jeremy
     * Event-people involved->Noemi
     * Event has a generic identification to freebase "Event" http://rdf.freebase.com/rdf/m/02xm94t
     * Event has 3 suggestions:
     *   - People Involved,
     *   - Start Date
     *   - Venue
     *
     * People involved is identified to Freebase
     * Start date is identified to Freebase
     */

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    @Inject
    ModelTestScenarios modelTestScenarios;

    User user = User.withEmailAndUsername("a", "b");

    private VertexOperator event,
            startDate,
            jeremy,
            noemi;

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.loadForUser(user);
        buildBubbles();
        buildRelations();
        return NoEx.wrap(() -> {
            JSONObject json = new JSONObject().put(
                    "original",
                    SubGraphJson.toJson(
                            userGraph.aroundVertexUriInShareLevels(
                                    event.uri(),
                                    ShareLevel.allShareLevelsInt
                            )
                    )
            );
            VertexOperator center = vertexFactory.createForOwner(
                    user.username()
            );
            center.label("center");
            center.addRelationToVertex(event);
            json.put(
                    "not_centered",
                    SubGraphJson.toJson(
                            userGraph.aroundVertexUriInShareLevels(
                                    center.uri(),
                                    ShareLevel.allShareLevelsInt
                            )
                    )
            );
            return json;
        }).get();
    }

    private void buildBubbles() {
        event = vertexFactory.createForOwner(
                user.username()
        );
        event.label("Event");
        event.addMeta(
                modelTestScenarios.event()
        );
        event.addSuggestions(
                modelTestScenarios.suggestionsToMap(
                        modelTestScenarios.peopleInvolvedSuggestionFromEventIdentification(user),
                        modelTestScenarios.startDateSuggestionFromEventIdentification(user),
                        modelTestScenarios.venueSuggestionFromEventIdentification(user)
                )
        );

        startDate = vertexFactory.createForOwner(
                user.username()
        );
        startDate.label("2016/01/17");
        startDate.addMeta(
                modelTestScenarios.startDateIdentification()
        );

        jeremy = vertexFactory.createForOwner(
                user.username()
        );
        jeremy.label("Jemery");
        jeremy.addMeta(
                modelTestScenarios.person()
        );

        noemi = vertexFactory.createForOwner(
                user.username()
        );
        noemi.label("Noemi");
        noemi.addMeta(
                modelTestScenarios.person()
        );
    }

    private void buildRelations() {
        EdgeOperator startDateEdge = event.addRelationToVertex(startDate);
        startDateEdge.label("start date");
        startDateEdge.addMeta(modelTestScenarios.startDateIdentification());

        EdgeOperator jeremyEdge = event.addRelationToVertex(jeremy);
        jeremyEdge.label("people involved");
        jeremyEdge.addMeta(modelTestScenarios.personFromFreebase());

        EdgeOperator noemiEdge = event.addRelationToVertex(noemi);
        noemiEdge.label("people involved");
        noemiEdge.addMeta(modelTestScenarios.personFromFreebase());
    }
}
