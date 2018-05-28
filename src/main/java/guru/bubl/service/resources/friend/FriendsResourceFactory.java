/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.friend;

import guru.bubl.module.model.User;
import guru.bubl.service.resources.fork.ForkResource;

public interface FriendsResourceFactory {
    FriendsResource forUser(User user);
}
