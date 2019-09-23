/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.pattern;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.center_graph_element.CenterGraphElementsOperatorFactory;
import guru.bubl.module.model.graph.pattern.PatternList;
import guru.bubl.module.model.json.CenterGraphElementsJson;
import guru.bubl.module.model.json.JsonUtils;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/patterns")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class PatternResource {

    @Inject
    PatternList patternList;

    @GET
    public Response get() {
        return Response.ok().entity(
                JsonUtils.getGson().toJson(
                        patternList.get()
                )
        ).build();
    }

}
