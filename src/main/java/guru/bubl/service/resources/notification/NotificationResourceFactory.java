package guru.bubl.service.resources.notification;

import guru.bubl.module.model.User;

public interface NotificationResourceFactory {
    NotificationResource ofUser(User user);
}
