package guru.bubl.service.resources.notification;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.module.model.json.JsonUtils;
import guru.bubl.module.model.notification.NotificationOperator;
import guru.bubl.service.resources.GraphElementTagResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

public class NotificationResource {

    @Inject
    NotificationOperator notificationOperator;

    private User user;

    @AssistedInject
    public NotificationResource(
            @Assisted User user
    ) {
        this.user = user;
    }

    @Path("/")
    @GET
    public Response getNotifications() {
        return Response.ok(JsonUtils.getGson().toJson(
                notificationOperator.listForUser(user)
        )).build();
    }
}
