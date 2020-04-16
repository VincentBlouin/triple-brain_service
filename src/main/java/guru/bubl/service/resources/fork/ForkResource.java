package guru.bubl.service.resources.fork;

import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.fork.ForkOperator;
import guru.bubl.module.model.graph.fork.NbNeighbors;
import guru.bubl.module.model.graph.relation.RelationJson;
import guru.bubl.module.model.graph.relation.RelationPojo;
import guru.bubl.module.model.graph.vertex.VertexJson;
import guru.bubl.module.model.graph.vertex.VertexPojo;
import guru.bubl.module.model.json.StatementJsonFields;
import guru.bubl.service.resources.GraphElementResource;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.net.URI;

public interface ForkResource extends GraphElementResource {

    @POST
    @Path("/{sourceForkShortId}")
    default Response addVertexAndEdgeToSourceVertex(
            @PathParam("sourceForkShortId") String sourceForkShortId,
            JSONObject options
    ) {
        URI sourceUri = getUriFromShortId(
                sourceForkShortId
        );
        ForkOperator source = getForkOperatorFromURI(
                sourceUri
        );
        RelationPojo newEdge;
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
                    RelationJson.toJson(new RelationPojo(
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

    @POST
    @Path("/{shortId}/childrenIndex")
    default Response saveChildrenIndexes(
            @PathParam("shortId") String shortId,
            JSONObject childrenIndexes
    ) {
        getOperatorFromShortId(shortId).setChildrenIndex(
                childrenIndexes.toString()
        );
        return Response.noContent().build();
    }

    @POST
    @Path("/{shortId}/colors")
    default Response saveColors(
            @PathParam("shortId") String shortId,
            JSONObject colors
    ) {
        getOperatorFromShortId(shortId).setColors(
                colors.toString()
        );
        return Response.noContent().build();
    }

    @POST
    @Path("/{shortId}/font")
    default Response saveFont(
            @PathParam("shortId") String shortId,
            JSONObject font
    ) {
        getOperatorFromShortId(shortId).setFont(
                font.toString()
        );
        return Response.noContent().build();
    }

    @POST
    @Path("/{shortId}/nbNeighbors")
    default Response setNbNeighbors(
            @PathParam("shortId") String shortId,
            JSONObject nbNeighbors
    ) {
        NbNeighbors nbNeighborsOperator = getForkOperatorFromURI(
                getUriFromShortId(shortId)
        ).getNbNeighbors();
        if (nbNeighbors.has("private_")) {
            nbNeighborsOperator.setPrivate(nbNeighbors.optInt("private_", 0));
        }
        if (nbNeighbors.has("friend")) {
            nbNeighborsOperator.setFriend(nbNeighbors.optInt("friend", 0));
        }
        if (nbNeighbors.has("public_")) {
            nbNeighborsOperator.setPublic(nbNeighbors.optInt("public_", 0));
        }
        return Response.noContent().build();
    }

    @Path("{shortId}/shareLevel")
    @POST
    default Response setShareLevel(@PathParam("shortId") String shortId, JSONObject shareLevel) {
        getForkOperatorFromURI(getUriFromShortId(shortId)).setShareLevel(
                ShareLevel.valueOf(
                        shareLevel.optString("shareLevel", ShareLevel.PRIVATE.name()).toUpperCase()
                )
        );
        return Response.noContent().build();
    }

    ForkOperator getForkOperatorFromURI(URI uri);
}
