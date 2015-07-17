/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import js_test_data.JsTestScenario;
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
