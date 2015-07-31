/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import com.google.gson.Gson;
import guru.bubl.module.common_utils.NoExRun;
import guru.bubl.module.model.search.GraphSearch;
import guru.bubl.module.model.search.VertexSearchResult;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.test.module.utils.ModelTestScenarios;
import guru.bubl.module.model.graph.UserGraph;
import guru.bubl.module.model.graph.schema.SchemaOperator;
import guru.bubl.module.model.json.graph.SchemaJson;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.schema.SchemaFactory;

import javax.inject.Inject;
import java.util.List;

public class KaraokeSchemaScenario implements JsTestScenario {
    /*
    * karaoke->invitees
    * karaoke->repertoire
    * karaoke->location
    * location identified to Freebase Location
    */

    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected SchemaFactory schemaFactory;

    @Inject
    ModelTestScenarios modelTestScenarios;

    @Inject
    GraphSearch graphSearch;

    User user = User.withEmailAndUsername("a", "b");

    @Override
    public JSONObject build() {
        UserGraph userGraph = graphFactory.createForUser(user);
        SchemaOperator karaoke = schemaFactory.createForOwnerUsername(
                user.username()
        );
        karaoke.label("karaoke");
        GraphElementOperator invitees = karaoke.addProperty();
        invitees.label("invitees");
        GraphElementOperator repertoire = karaoke.addProperty();
        repertoire.label("repertoire");
        GraphElementOperator location = karaoke.addProperty();
        location.label("location");
        location.addSameAs(modelTestScenarios.location());
        List<VertexSearchResult> searchResultsForKaraoke = graphSearch.searchSchemasOwnVerticesAndPublicOnesForAutoCompletionByLabel(
                "karaoke",
                user
        );
        return NoExRun.wrap(() ->
                new JSONObject().put(
                        "schema",
                        SchemaJson.toJson(
                                userGraph.schemaPojoWithUri(karaoke.uri())
                        )
                ).put(
                        "searchResults",
                        new JSONArray(
                                new Gson().toJson(
                                        searchResultsForKaraoke
                                )
                        )
                )).get();
    }
}
