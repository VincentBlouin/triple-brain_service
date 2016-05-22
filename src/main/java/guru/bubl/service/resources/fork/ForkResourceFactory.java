/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.resources.fork;

import guru.bubl.module.model.User;

public interface ForkResourceFactory {
    ForkResource forUser(User user);
}
