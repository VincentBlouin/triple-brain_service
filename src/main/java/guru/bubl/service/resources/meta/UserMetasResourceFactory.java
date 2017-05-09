/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.meta;

import guru.bubl.module.model.User;

public interface UserMetasResourceFactory {
    UserMetasResource forUser(User user);
}
