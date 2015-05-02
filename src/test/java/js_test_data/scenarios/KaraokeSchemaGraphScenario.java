/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.GraphElementOperator;
import org.triple_brain.module.model.graph.GraphFactory;
import org.triple_brain.module.model.graph.ModelTestScenarios;
import org.triple_brain.module.model.graph.UserGraph;
import org.triple_brain.module.model.graph.schema.SchemaOperator;
import org.triple_brain.module.model.json.graph.SchemaJson;
import org.triple_brain.module.neo4j_graph_manipulator.graph.graph.schema.SchemaFactory;

import javax.inject.Inject;

public class KaraokeSchemaGraphScenario implements JsTestScenario {
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
        return SchemaJson.toJson(
                userGraph.schemaPojoWithUri(karaoke.uri())
        );
    }
}
