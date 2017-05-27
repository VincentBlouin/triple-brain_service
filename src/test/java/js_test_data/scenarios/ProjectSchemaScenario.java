
/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package js_test_data.scenarios;

import com.google.gson.Gson;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperatorFactory;
import guru.bubl.module.model.graph.*;
import guru.bubl.module.model.graph.GraphFactory;
import guru.bubl.module.model.graph.edge.EdgeFactory;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.graph.identification.IdentifierPojo;
import guru.bubl.module.model.graph.subgraph.SubGraphPojo;
import guru.bubl.module.model.graph.schema.SchemaJson;
import guru.bubl.module.model.search.VertexSearchResult;
import guru.bubl.test.module.utils.ModelTestScenarios;
import guru.bubl.module.model.search.GraphElementSearchResult;
import guru.bubl.module.model.search.GraphSearch;
import js_test_data.JsTestScenario;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.schema.SchemaOperator;
import guru.bubl.module.model.graph.vertex.VertexFactory;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.graph.SubGraphJson;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.schema.SchemaFactory;

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
     * some project -- impact 1 on society --> impact 1 bubble
     * some project -- impact 2 on society --> impact 2 bubble
     * some project -- impact 3 --> impact 3 bubble
     * impact on society relations are identified to property "impact on society"
     * impact 3 is identified to "Impact on society" and "Impact on the individual"
     * impact 2 bubble has been centered and so has 1 nb of visits
     */


    @Inject
    protected GraphFactory graphFactory;

    @Inject
    protected VertexFactory vertexFactory;

    @Inject
    protected EdgeFactory edgeFactory;

    @Inject
    protected SchemaFactory schemaFactory;

    @Inject
    protected CenterGraphElementOperatorFactory centerGraphElementOperatorFactory;

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
        List<GraphElementSearchResult> searchResultsForProject = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "project",
                user
        );
        List<GraphElementSearchResult> resultsForImpact = graphSearch.searchRelationsPropertiesSchemasForAutoCompletionByLabel(
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
        List<GraphElementSearchResult> searchResultsForProjectAfterIdentificationAdded = graphSearch.searchForAnyResourceThatCanBeUsedAsAnIdentifier(
                "project",
                user
        );

        List<VertexSearchResult> searchResultsForImpactBubbles = graphSearch.searchOnlyForOwnVerticesForAutoCompletionByLabel(
                "impact",
                user
        );
        SubGraphPojo someProjectGraph = userGraph.graphWithDepthAndCenterBubbleUri(
                1,
                someProject.uri()
        );
        try {
            return new JSONObject().put(
                    "schema",
                    SchemaJson.toJson(
                            userGraph.schemaPojoWithUri(project.uri())
                    )
            ).put(
                    "searchResultsForProject",
                    new JSONArray(
                            new Gson().toJson(
                                    searchResultsForProject
                            )
                    )
            ).put(
                    "searchResultsForProjectAfterIdentificationAdded",
                    new JSONArray(
                            new Gson().toJson(
                                    searchResultsForProjectAfterIdentificationAdded
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
                    "impactVerticesSearchResults",
                    new JSONArray(
                            new Gson().toJson(
                                    searchResultsForImpactBubbles
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
        someProject.addMeta(
                projectIdentification()
        );

        EdgeOperator impact1Relation = edgeFactory.withUri(
                someProject.addVertexAndRelation().uri()
        );
        impact1Relation.label("impact 1 on society");
        impact1Relation.destinationVertex().label("impact 1 bubble");
        impact1Relation.addMeta(
                impactOnSocietyIdentification()
        );

        EdgeOperator impact2Relation = edgeFactory.withUri(
                someProject.addVertexAndRelation().uri()
        );
        impact2Relation.label("impact 2 on society");
        VertexOperator impact2Bubble = impact2Relation.destinationVertex();
        impact2Bubble.label("impact 2 bubble");
        centerGraphElementOperatorFactory.usingFriendlyResource(impact2Bubble).incrementNumberOfVisits();
        impact2Relation.addMeta(
                impactOnSocietyIdentification()
        );

        EdgeOperator impact3Relation = edgeFactory.withUri(
                someProject.addVertexAndRelation().uri()
        );
        impact3Relation.label("impact 3");
        impact3Relation.destinationVertex().label("impact 3 bubble");
        impact3Relation.addMeta(
                impactOnSocietyIdentification()
        );
        impact3Relation.addMeta(
                impactOnTheIndividualIdentification()
        );
    }

    private IdentifierPojo projectIdentification(){
        return new IdentifierPojo(
                project.uri(),
                new FriendlyResourcePojo(
                        "Project"
                )
        );
    }

    private IdentifierPojo impactOnSocietyIdentification(){
        return new IdentifierPojo(
                impactOnSocietyProperty.uri(),
                new FriendlyResourcePojo(
                        "Impact on society"
                )
        );
    }

    private IdentifierPojo impactOnTheIndividualIdentification(){
        return new IdentifierPojo(
                impactOnTheIndividualProperty.uri(),
                new FriendlyResourcePojo(
                        "Impact on the individual"
                )
        );
    }
}
