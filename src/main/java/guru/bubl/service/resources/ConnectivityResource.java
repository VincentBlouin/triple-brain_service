/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/connectivity")
public class ConnectivityResource {

    @GET
    @Path("/")
    public Response getConnectivity() {
        return Response.ok().build();
    }
}
