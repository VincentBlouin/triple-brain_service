package guru.bubl.service.resources.tree_copier;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.Tree;
import guru.bubl.module.model.graph.tree_copier.TreeCopierFactory;
import guru.bubl.module.model.json.JsonUtils;
import guru.bubl.module.repository.user.UserRepository;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Map;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TreeCopierResource {

    private User copier;

    @Inject
    private TreeCopierFactory treeCopierFactory;

    @Inject
    private UserRepository userRepository;

    @AssistedInject
    public TreeCopierResource(
            @Assisted User copier
    ) {
        this.copier = copier;
    }

    @POST
    @Path("/")
    public Response copyForSelf(JSONObject options) {
        return _copy(
                options,
                copier
        );
    }

    @POST
    @Path("/{copiedUserName}")
    public Response copyForOtherUser(@PathParam("copiedUserName") String copiedUserName, JSONObject options) {
        return _copy(
                options,
                userRepository.findByUsername(copiedUserName)
        );
    }

    private Response _copy(JSONObject options, User copiedUser) {
        Map<URI, URI> map = treeCopierFactory.forCopier(copier).copyTreeOfUser(
                JsonUtils.getGson().fromJson(options.optString("copiedTree"), Tree.class),
                copiedUser
        );
        return Response.ok(
                JsonUtils.getGson().toJson(map)
        ).build();
    }
}