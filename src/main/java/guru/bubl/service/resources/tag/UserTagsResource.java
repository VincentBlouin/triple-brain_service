/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.tag;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.tag.TagJson;
import guru.bubl.module.model.tag.UserTagsOperatorFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON)
public class UserTagsResource {

    @Inject
    protected UserTagsOperatorFactory userTagsOperatorFactory;

    private User user;

    @AssistedInject
    public UserTagsResource(
            @Assisted User user
    ) {
        this.user = user;
    }

    @GET
    public Response get(){
        return Response.ok().entity(
                TagJson.toJsonSet(
                        userTagsOperatorFactory.forUser(user).get()
                )
        ).build();
    }
}
