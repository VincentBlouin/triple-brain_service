package org.triple_brain.service.resources;

import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.model.graph.IdentificationOperator;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.TEXT_PLAIN)
public class GraphElementIdentificationDescriptionResource {
    private IdentificationOperator identificationOperator;
    public GraphElementIdentificationDescriptionResource(IdentificationOperator identificationOperator){
        this.identificationOperator = identificationOperator;
    }

    @PUT
    @GraphTransactional
    @Path("/")
    public Response add(String description) {
        identificationOperator.comment(
                description
        );
        return Response.noContent().build();
    }

}
