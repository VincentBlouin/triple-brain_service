package guru.bubl.service.resources.group_relation;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.fork.ForkOperator;
import guru.bubl.module.model.graph.group_relation.GroupRelationFactory;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.service.resources.fork.ForkResource;

import java.net.URI;

public class GroupRelationResource extends ForkResource {

    @Inject
    private GroupRelationFactory groupRelationFactory;
    private UserGraph userGraph;

    @AssistedInject
    public GroupRelationResource(
            @Assisted UserGraph userGraph
    ) {
        this.userGraph = userGraph;
    }

    @Override
    protected URI getUriFromShortId(String shortId) {
        return new UserUris(userGraph.user()).groupRelationUriFromShortId(shortId);
    }

    @Override
    protected ForkOperator getForkOperatorFromURI(URI uri) {
        return groupRelationFactory.withUri(uri);
    }

    @Override
    protected GraphElementOperator getOperatorFromShortId(String shortId) {
        return groupRelationFactory.withUri(
                getUriFromShortId(shortId)
        );
    }
}
