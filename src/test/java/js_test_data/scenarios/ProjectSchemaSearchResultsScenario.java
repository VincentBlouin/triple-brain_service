/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package js_test_data.scenarios;

import com.google.gson.Gson;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.GraphFactory;
import org.triple_brain.module.model.graph.ModelTestScenarios;
import org.triple_brain.module.model.graph.schema.SchemaOperator;
import org.triple_brain.module.model.graph.vertex.VertexFactory;
import org.triple_brain.module.neo4j_graph_manipulator.graph.graph.schema.SchemaFactory;
import org.triple_brain.module.search.GraphElementSearchResult;
import org.triple_brain.module.search.GraphSearch;
import org.triple_brain.module.search.VertexSearchResult;

import javax.inject.Inject;
import java.util.List;

public class ProjectSchemaSearchResultsScenario implements JsTestScenario {

    /*
     * schema project
     * project -> impact on the individual
     * project -> impact on society
     * project -> has objective
     * project -> has component
     *
     * another bubble labeled impact
     */


    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    @Inject
    protected SchemaFactory schemaFactory;

    @Inject
    ModelTestScenarios modelTestScenarios;

    @Inject
    GraphSearch graphSearch;

    User user = User.withEmailAndUsername("a", "b");

    @Override
    public JSONObject build() {
        graphFactory.createForUser(user);
        SchemaOperator project = schemaFactory.createForOwnerUsername(
                user.username()
        );
        project.label("project");
        project.addProperty().label("impact on the individual");
        project.addProperty().label("impact on society");
        project.addProperty().label("has objective");
        project.addProperty().label("has component");
        vertexFactory.createForOwnerUsername(
                user.username()
        ).label("impact");
        List<VertexSearchResult> resultsForProject = graphSearch.searchSchemasOwnVerticesAndPublicOnesForAutoCompletionByLabel(
                "project",
                user
        );
        List<GraphElementSearchResult> resultsForImpact = graphSearch.searchRelationsPropertiesOrSchemasForAutoCompletionByLabel(
                "impact",
                user
        );
        try {
            return new JSONObject().put(
                    "getForProject",
                    new JSONArray(
                            new Gson().toJson(
                                    resultsForProject
                            )
                    )
            ).put(
                    "resultsForImpact",
                    new JSONArray(
                            new Gson().toJson(
                                    resultsForImpact
                            )
                    )
            );
        }catch(JSONException e){
            throw new RuntimeException(e);
        }
    }
}
