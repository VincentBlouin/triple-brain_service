package guru.bubl.service.resources.notification;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.User;
import guru.bubl.module.model.json.JsonUtils;
import guru.bubl.module.model.notification.NotificationOperator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
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
    public Response getNotifications(
            @QueryParam("nbSkip") Integer nbSkip
    ) {
        return Response.ok(JsonUtils.getGson().toJson(
                notificationOperator.listForUserAndNbSkip(user, nbSkip)
        )).build();
    }
}
