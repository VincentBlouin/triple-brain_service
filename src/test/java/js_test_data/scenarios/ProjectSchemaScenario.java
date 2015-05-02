
/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import com.google.gson.Gson;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.triple_brain.module.model.User;
import org.triple_brain.module.model.graph.*;
import org.triple_brain.module.model.graph.edge.EdgeOperator;
import org.triple_brain.module.model.graph.schema.SchemaOperator;
import org.triple_brain.module.model.graph.vertex.VertexFactory;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.model.json.graph.SubGraphJson;
import org.triple_brain.module.neo4j_graph_manipulator.graph.graph.schema.SchemaFactory;
import org.triple_brain.module.search.GraphElementSearchResult;
import org.triple_brain.module.search.GraphSearch;
import org.triple_brain.module.search.VertexSearchResult;

import javax.inject.Inject;
import java.util.List;

public class ProjectSchemaScenario implements JsTestScenario {

    /*
     * schema project with comment 'A project is defined as a collaborative enterprise ...'
     * project -> impact on the individual
     * project -> impact on society with comment 'impact on society comment'
     * project -> has objective
     * project -> has component
     *
     * another bubble labeled impact
     *
     * Relation in 2 groups test
     * bubble "some project" identified to project
     * some project-impact 1 on society->impact 1 bubble
     * some project-impact 2 on society->impact 2 bubble
     * some project-impact 3->impact 3 bubble
     * impact on society relations are identified to property "impact on society"
     * impact 3 is identified to "impact on society" and "impact on the individual"
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

    SchemaOperator project;
    GraphElementOperator impactOnSocietyProperty;
    GraphElementOperator impactOnTheIndividualProperty;

    VertexOperator someProject;

    UserGraph userGraph;

    @Override
    public JSONObject build() {
        userGraph = graphFactory.createForUser(user);
        buildSchema();
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
        GraphElementSearchResult projectSearchDetails = graphSearch.getDetails(
                project.uri(),
                user
        );
        GraphElementSearchResult impactOnSocietySearchDetails = graphSearch.getDetails(
                impactOnSocietyProperty.uri(),
                user
        );
        buildSomeProject();
        SubGraphPojo someProjectGraph = userGraph.graphWithDepthAndCenterVertexId(
                1,
                someProject.uri()
        );
        try {
            return new JSONObject().put(
                    "searchResultsForProject",
                    new JSONArray(
                            new Gson().toJson(
                                    resultsForProject
                            )
                    )
            ).put(
                    "searchResultsForImpact",
                    new JSONArray(
                            new Gson().toJson(
                                    resultsForImpact
                            )
                    )
            ).put(
                    "projectSearchDetails",
                    new JSONObject(
                            new Gson().toJson(
                                    projectSearchDetails
                            )
                    )
            ).put(
                    "impactOnSocietySearchDetails",
                    new JSONObject(
                            new Gson().toJson(
                                    impactOnSocietySearchDetails
                            )
                    )
            ).put(
                    "someProjectGraph",
                    SubGraphJson.toJson(someProjectGraph)
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void buildSchema() {
        project = schemaFactory.createForOwnerUsername(
                user.username()
        );
        project.label("project");
        project.comment("A project is defined as a collaborative enterprise ...");
        impactOnTheIndividualProperty = project.addProperty();
        impactOnTheIndividualProperty.label("impact on the individual");
        impactOnSocietyProperty = project.addProperty();
        impactOnSocietyProperty.label("impact on society");
        impactOnSocietyProperty.comment("impact on society comment");
        project.addProperty().label("has objective");
        project.addProperty().label("has component");
    }

    private void buildSomeProject() {
        someProject = vertexFactory.createForOwnerUsername(user.username());
        someProject.label("some project");
        someProject.addGenericIdentification(
                projectIdentification()
        );

        EdgeOperator impact1Relation = someProject.addVertexAndRelation();
        impact1Relation.label("impact 1 on society");
        impact1Relation.destinationVertex().label("impact 1 bubble");
        impact1Relation.addSameAs(
                impactOnSocietyIdentification()
        );

        EdgeOperator impact2Relation = someProject.addVertexAndRelation();
        impact2Relation.label("impact 2 on society");
        impact2Relation.destinationVertex().label("impact 2 bubble");
        impact2Relation.addSameAs(
                impactOnSocietyIdentification()
        );

        EdgeOperator impact3Relation = someProject.addVertexAndRelation();
        impact3Relation.label("impact 3");
        impact3Relation.destinationVertex().label("impact 3 bubble");
        impact3Relation.addSameAs(
                impactOnSocietyIdentification()
        );
        impact3Relation.addSameAs(
                impactOnTheIndividualIdentification()
        );
    }

    private IdentificationPojo projectIdentification(){
        return new IdentificationPojo(
                project.uri(),
                new FriendlyResourcePojo(
                        "Project"
                )
        );
    }

    private IdentificationPojo impactOnSocietyIdentification(){
        return new IdentificationPojo(
                impactOnSocietyProperty.uri(),
                new FriendlyResourcePojo(
                        "Impact on society"
                )
        );
    }

    private IdentificationPojo impactOnTheIndividualIdentification(){
        return new IdentificationPojo(
                impactOnTheIndividualProperty.uri(),
                new FriendlyResourcePojo(
                        "Impact on the individual"
                )
        );
    }
}
