package guru.bubl.service.resources.group_relation;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.center_graph_element.CenterGraphElementOperatorFactory;
import guru.bubl.module.model.graph.GraphElementOperator;
import guru.bubl.module.model.graph.edge.EdgeOperator;
import guru.bubl.module.model.graph.fork.ForkOperator;
import guru.bubl.module.model.graph.group_relation.GroupRelationFactory;
import guru.bubl.module.model.graph.subgraph.UserGraph;
import guru.bubl.service.resources.edge.EdgeResource;
import guru.bubl.service.resources.fork.ForkResource;

import java.net.URI;

public class GroupRelationResource implements ForkResource, EdgeResource {

    @Inject
    private GroupRelationFactory groupRelationFactory;
    @Inject
    private CenterGraphElementOperatorFactory centerGraphElementOperatorFactory;

    private UserGraph userGraph;

    @AssistedInject
    public GroupRelationResource(
            @Assisted UserGraph userGraph
    ) {
        this.userGraph = userGraph;
    }

    @Override
    public URI getUriFromShortId(String shortId) {
        return new UserUris(userGraph.user()).groupRelationUriFromShortId(shortId);
    }

    @Override
    public ForkOperator getForkOperatorFromURI(URI uri) {
        return groupRelationFactory.withUri(uri);
    }

    @Override
    public GraphElementOperator getOperatorFromShortId(String shortId) {
        return groupRelationFactory.withUri(
                getUriFromShortId(shortId)
        );
    }

    @Override
    public UserGraph getUserGraph() {
        return userGraph;
    }

    @Override
    public CenterGraphElementOperatorFactory getCenterOperatorFactory() {
        return centerGraphElementOperatorFactory;
    }

    @Override
    public EdgeOperator getEdgeOperatorFromShortId(String shortId) {
        return groupRelationFactory.withUri(
                getUriFromShortId(shortId)
        );
    }
}
