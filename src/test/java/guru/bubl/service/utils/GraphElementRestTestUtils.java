/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.utils;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import guru.bubl.module.common_utils.NoEx;
import guru.bubl.module.model.User;
import guru.bubl.module.model.UserUris;
import guru.bubl.module.model.graph.ShareLevel;
import guru.bubl.module.model.graph.tag.TagPojo;
import guru.bubl.module.model.tag.TagJson;
import guru.bubl.test.module.utils.ModelTestScenarios;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import java.net.URI;
import java.util.Arrays;

public class GraphElementRestTestUtils {

    private WebResource resource;
    private NewCookie authCookie;
    private User authenticatedUser;

    public static GraphElementRestTestUtils withWebResourceAndAuthCookie(WebResource resource, NewCookie authCookie, User authenticatedUser) {
        return new GraphElementRestTestUtils(resource, authCookie, authenticatedUser);
    }

    protected GraphElementRestTestUtils(WebResource resource, NewCookie authCookie, User authenticatedUser) {
        this.resource = resource;
        this.authCookie = authCookie;
        this.authenticatedUser = authenticatedUser;
    }

    public ClientResponse addIdentificationToGraphElementWithUri(TagPojo identification, URI graphElementUri) {
        return addIdentificationToGraphElementWithUri(
                TagJson.singleToJson(identification),
                graphElementUri
        );
    }

    public ClientResponse addIdentificationToGraphElementWithUri(JSONObject identification, URI graphElementUri) {
        return resource
                .path(graphElementUri.getPath())
                .path("identification")
                .cookie(authCookie)
                .type(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class, identification);
    }

    public ClientResponse addFoafPersonTypeToVertexA() {
        TagPojo identification = new ModelTestScenarios().person();
        identification.setRelationExternalResourceUri(
                ModelTestScenarios.TYPE
        );
        JSONObject personType = TagJson.singleToJson(identification);
        return addIdentificationToGraphElementWithUri(
                personType,
                graphUtils().vertexAUri()
        );

    }


    public TagPojo getIdentificationsFromResponse(ClientResponse response) {
        return TagJson.fromJson(
                response.getEntity(String.class)
        ).values().iterator().next();
    }

    public ClientResponse makePublicGraphElementsWithUri(URI... vertexUri) {
        return this.setShareLevelOfCollection(
                ShareLevel.PUBLIC,
                authCookie,
                vertexUri
        );
    }

    public ClientResponse makePrivateGraphElementsWithUri(URI... vertexUri) {
        return this.setShareLevelOfCollection(
                ShareLevel.PRIVATE,
                authCookie,
                vertexUri
        );
    }

    public ClientResponse setShareLevelOfCollection(ShareLevel shareLevel, NewCookie cookie, URI... graphElementUri) {
        return NoEx.wrap(() ->
                resource
                        .path(this.baseGraphElementUri().toString())
                        .path("collection")
                        .path("share-level")
                        .cookie(cookie)
                        .post(
                                ClientResponse.class,
                                new JSONObject().put(
                                        "shareLevel",
                                        shareLevel.name()
                                ).put(
                                        "graphElementsUri",
                                        new JSONArray(Arrays.asList(graphElementUri))
                                )
                        )
        ).get();
    }
    public URI baseGraphElementUri() {
        return new UserUris(
                authenticatedUser
        ).baseGraphElementUri();
    }

    private GraphRestTestUtils graphUtils() {
        return GraphRestTestUtils.withWebResourceAndAuthCookie(
                authCookie,
                authenticatedUser
        );
    }
}
