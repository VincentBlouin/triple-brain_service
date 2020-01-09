/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.tag;

import guru.bubl.module.model.User;

public interface UserTagsResourceFactory {
    UserTagsResource forUser(User user);
}
