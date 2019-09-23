package guru.bubl.service.resources.pattern;

import guru.bubl.module.model.User;

public interface PatternConsumerResourceFactory {
    PatternConsumerResource withUser(User user);
}
