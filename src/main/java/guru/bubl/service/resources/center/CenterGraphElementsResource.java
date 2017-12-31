/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.center;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.common_utils.NoEx;
import guru.bubl.module.model.User;
import guru.bubl.module.model.center_graph_element.CenterGraphElementPojo;
import guru.bubl.module.model.center_graph_element.CenterGraphElementsOperatorFactory;
import guru.bubl.module.model.json.CenterGraphElementsJson;
import org.codehaus.jettison.json.JSONArray;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CenterGraphElementsResource {

    private User user;

    private CenterGraphElementsOperatorFactory centerGraphElementsOperatorFactory;

    @AssistedInject
    CenterGraphElementsResource(
            CenterGraphElementsOperatorFactory centerGraphElementsOperatorFactory,
            @Assisted User user
    ) {
        this.centerGraphElementsOperatorFactory = centerGraphElementsOperatorFactory;
        this.user = user;
    }

    @GET
    public Response get() {
        return Response.ok().entity(
                CenterGraphElementsJson.toJson(
                        centerGraphElementsOperatorFactory.forUser(user).getPublicAndPrivate()
                )
        ).build();
    }

    @DELETE
    public Response delete(JSONArray uris) {
        Set<CenterGraphElementPojo> centers = new HashSet<>();
        NoEx.wrap(()->{
            for (int i = 0; i < uris.length(); i++) {
                centers.add(
                        new CenterGraphElementPojo(
                                URI.create(
                                        uris.getString(i)
                                )
                        )
                );
            }
            return centers;
        }).get();
        centerGraphElementsOperatorFactory.forUser(
                user
        ).removeCenterGraphElements(centers);
        return Response.noContent().build();
    }

}
