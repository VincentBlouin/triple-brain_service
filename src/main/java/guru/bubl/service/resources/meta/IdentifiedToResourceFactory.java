/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.meta;

import guru.bubl.module.model.User;

public interface IdentifiedToResourceFactory {
    IdentifiedToResource forAuthenticatedUser(User user);
}
