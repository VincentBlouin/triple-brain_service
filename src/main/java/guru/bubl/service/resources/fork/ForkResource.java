package guru.bubl.service.resources.fork;

import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.edge.EdgeJson;
import guru.bubl.module.model.graph.edge.EdgePojo;
import guru.bubl.module.model.graph.fork.ForkOperator;
import guru.bubl.module.model.graph.vertex.VertexJson;
import guru.bubl.module.model.graph.vertex.VertexOperator;
import guru.bubl.module.model.graph.vertex.VertexPojo;
import guru.bubl.module.model.json.StatementJsonFields;
import guru.bubl.module.neo4j_graph_manipulator.graph.graph.GraphElementSpecialOperatorFactory;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.net.URI;

public abstract class ForkResource {

    @POST
    @Path("/{sourceVertexShortId}")
    public Response addVertexAndEdgeToSourceVertex(
            @PathParam("sourceVertexShortId") String sourceVertexShortId,
            JSONObject options
    ) {
        URI sourceUri = getUriFromShortId(
                sourceVertexShortId
        );
        ForkOperator source = getForkOperatorFromURI(
                sourceUri
        );
        EdgePojo newEdge;
        if (options.has("vertexId") && options.has("edgeId")) {
            newEdge = source.addVertexAndRelationWithIds(
                    options.optString("vertexId"),
                    options.optString("edgeId")
            );
        } else {
            newEdge = source.addVertexAndRelation();
        }
        VertexPojo newVertex = newEdge.getDestinationVertex();
        VertexPojo sourceVertexPojo = new VertexPojo(
                sourceUri
        );
        JSONObject jsonCreatedStatement = new JSONObject();
        try {
            jsonCreatedStatement.put(
                    StatementJsonFields.source_vertex.name(),
                    VertexJson.toJson(
                            sourceVertexPojo
                    )
            );
            jsonCreatedStatement.put(
                    StatementJsonFields.edge.name(),
                    EdgeJson.toJson(new EdgePojo(
                                    newEdge.getGraphElement(),
                                    sourceVertexPojo,
                                    newVertex
                            )
                    )
            );
            jsonCreatedStatement.put(
                    StatementJsonFields.end_vertex.name(),
                    VertexJson.toJson(
                            newVertex
                    )
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        //TODO response should be of type created
        return Response.ok(jsonCreatedStatement).build();
    }

    protected abstract URI getUriFromShortId(String shortId);

    protected abstract ForkOperator getForkOperatorFromURI(URI uri);
}
