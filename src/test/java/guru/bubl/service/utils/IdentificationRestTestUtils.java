/*
 * Copyright Vincent Blouin under the GPL License version 3
 */

package guru.bubl.service.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import guru.bubl.module.common_utils.Uris;
import guru.bubl.module.model.User;
import guru.bubl.module.model.graph.identification.Identification;
import guru.bubl.module.model.search.EdgeSearchResult;
import guru.bubl.module.model.search.VertexSearchResult;

import javax.ws.rs.core.NewCookie;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class IdentificationRestTestUtils {

    private WebResource resource;
    private NewCookie authCookie;
    private User authenticatedUser;
    private static Gson gson = new Gson();

    public static IdentificationRestTestUtils withWebResourceAndAuthCookie(WebResource resource, NewCookie authCookie, User authenticatedUser) {
        return new IdentificationRestTestUtils(
                resource, authCookie, authenticatedUser
        );
    }

    protected IdentificationRestTestUtils(WebResource resource, NewCookie authCookie, User authenticatedUser) {
        this.resource = resource;
        this.authCookie = authCookie;
        this.authenticatedUser = authenticatedUser;
    }


    public Set<VertexSearchResult> getRelatedResourcesForIdentification(Identification identification) {
        return gson.fromJson(
                getRelatedResourcesForIdentificationClientResponse(
                        identification
                ).getEntity(String.class),
                new TypeToken<Set<VertexSearchResult>>() {
                }.getType()
        );
    }

    public Set<EdgeSearchResult> getEdgesRelatedResourcesForIdentification(Identification identification) {
        return gson.fromJson(
                getRelatedResourcesForIdentificationClientResponse(
                        identification
                ).getEntity(String.class),
                new TypeToken<Set<EdgeSearchResult>>() {
                }.getType()
        );
    }

    public ClientResponse getRelatedResourcesForIdentificationClientResponse(Identification identification) {
        return getRelatedResourcesForIdentificationClientResponseForUsername(
                identification,
                authenticatedUser.username()
        );
    }

    public ClientResponse getRelatedResourcesForIdentificationClientResponseForUsername(
            Identification identification,
            String username
    ) {
        return resource
                .path("service")
                .path("users")
                .path(username)
                .path("identification")
                .path(
                        Uris.encodeURL(
                                identification.getExternalResourceUri()
                        )
                )
                .cookie(authCookie)
                .get(ClientResponse.class);
    }
}
