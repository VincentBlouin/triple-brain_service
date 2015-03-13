/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.resources.test;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.rest.graphdb.query.QueryEngine;
import org.triple_brain.module.model.graph.GraphElementType;
import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.repository.user.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;

@Path("test/users")
@Singleton
public class UserResourceTestUtils {

    @Inject
    UserRepository userRepository;

    @Inject
    protected QueryEngine queryEngine;

    @Path("{email}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response emailExists(@PathParam("email") String email)throws Exception{
        return Response.ok(
                userRepository.emailExists(email).toString()
        ).build();
    }

    @Path("/")
    @DELETE
    @GraphTransactional
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteAllUsers()throws Exception{
        queryEngine.query(
                "START n=node:node_auto_index('type:user') DELETE n",
                Collections.EMPTY_MAP
        );
        return Response.noContent().build();
    }

}
