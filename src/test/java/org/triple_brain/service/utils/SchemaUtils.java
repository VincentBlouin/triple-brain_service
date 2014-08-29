package org.triple_brain.service.utils;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.NewCookie;
import java.net.URI;

/**
 * Copyright Mozilla Public License 1.1
 */
public class SchemaUtils {
    private WebResource resource;
    private NewCookie authCookie;
    private GraphRestTestUtils graphUtils;
    public static SchemaUtils withWebResourceAndAuthCookie(WebResource resource, NewCookie authCookie, GraphRestTestUtils graphUtils){
        return new SchemaUtils(resource, authCookie, graphUtils);
    }
    protected SchemaUtils(WebResource resource, NewCookie authCookie, GraphRestTestUtils graphUtils){
        this.resource = resource;
        this.authCookie = authCookie;
        this.graphUtils = graphUtils;
    }

    public ClientResponse createSchema() {
        return resource
                .path(graphUtils.getCurrentGraphUri())
                .path("schema")
                .cookie(authCookie)
                .post(ClientResponse.class);
    }

    public URI uriOfCreatedSchema(){
        return graphUtils.getElementUriInResponse(
                createSchema()
        );
    }
}
