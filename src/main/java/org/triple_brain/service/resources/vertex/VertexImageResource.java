package org.triple_brain.service.resources.vertex;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.imgscalr.Scalr;
import org.triple_brain.module.common_utils.Urls;
import org.triple_brain.module.model.ExternalFriendlyResource;
import org.triple_brain.module.model.ExternalFriendlyResourcePersistenceUtils;
import org.triple_brain.module.model.Image;
import org.triple_brain.module.model.graph.Vertex;
import org.triple_brain.service.ExternalResourceServiceUtils;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

/*
* Copyright Mozilla Public License 1.1
*/
public class VertexImageResource {

    private Vertex vertex;

    public static final String IMAGES_FOLDER_PATH = "/var/lib/triple_brain/image";

    @Inject
    ExternalResourceServiceUtils externalResourceServiceUtils;

    @Inject
    ExternalFriendlyResourcePersistenceUtils externalFriendlyResourcePersistenceUtils;

    @AssistedInject
    public VertexImageResource(
            @Assisted Vertex vertex
    ) {
        this.vertex = vertex;
    }

    @GET
    @Path("/{imageId}/small")
    @Produces("application/octet-stream")
    public byte[] getSmall(@PathParam("imageId") String imageId) {
        return makeImageSmall(
                new File(IMAGES_FOLDER_PATH + "/" + imageId)
        );
    }

    @GET
    @Produces("application/octet-stream")
    @Path("/{imageId}/big")
    public byte[] getBig(@PathParam("imageId") String imageId) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(
                    ImageIO.read(
                            new File(IMAGES_FOLDER_PATH + "/" + imageId)
                    ),
                    "png",
                    baos
            );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException e) {
            throw new WebApplicationException(
                    Response.Status.INTERNAL_SERVER_ERROR
            );
        }
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("text/plain")
    @Path("/")
    public Response add(@Context HttpServletRequest request) {
        Set<Image> uploadedImages = new HashSet<>();
        if (ServletFileUpload.isMultipartContent(request)) {
            final FileItemFactory factory = new DiskFileItemFactory();
            final ServletFileUpload fileUpload = new ServletFileUpload(factory);
            try {
                /*
                                 * parseRequest returns a list of FileItem
                                 * but in old (pre-java5) style
                                 */
                final List items = fileUpload.parseRequest(request);

                if (items != null) {
                    final Iterator iter = items.iterator();
                    while (iter.hasNext()) {
                        final FileItem item = (FileItem) iter.next();
                        String imageId = UUID.randomUUID().toString();
                        final File savedFile = new File(
                                IMAGES_FOLDER_PATH
                                        + File.separator +
                                        imageId
                        );
                        System.out.println("Saving the file: " + savedFile.getName());
                        item.write(savedFile);
                        String url = request.getRequestURL() + "/" + imageId + "/";
                        uploadedImages.add(
                                Image.withUrlForSmallAndBigger(
                                        Urls.get(url + "/small"),
                                        Urls.get(url + "big")
                                )
                        );
                    }
                }
            } catch (FileUploadException fue) {
                fue.printStackTrace();
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            } catch (Exception e) {
                e.printStackTrace();
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
        }
        externalFriendlyResourcePersistenceUtils.addImages(
                ExternalFriendlyResource.fromGraphElement(vertex),
                uploadedImages
        );
        return Response.ok().build();
    }

    private byte[] makeImageSmall(File image) {
        try {
            BufferedImage originalImage = ImageIO.read(
                    image
            );
            originalImage = Scalr.resize(
                    originalImage,
                    Scalr.Method.QUALITY,
                    Scalr.Mode.FIT_TO_WIDTH,
                    60,
                    60
            );
            //To save with original ratio uncomment next line and comment the above.
            //originalImage= Scalr.resize(originalImage, 153, 128);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(
                    originalImage,
                    "png",
                    baos
            );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
