/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.resources;

import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.model.graph.IdentificationOperator;
import org.triple_brain.module.model.json.ImageJson;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GraphElementIdentificationImageResource {

    IdentificationOperator identification;

    public GraphElementIdentificationImageResource(IdentificationOperator identification){
        this.identification = identification;
    }

    @POST
    @GraphTransactional
    @Path("/")
    public Response add(String images) {
        identification.addImages(
                ImageJson.fromJson(images)
        );
        return Response.noContent().build();
    }
}
