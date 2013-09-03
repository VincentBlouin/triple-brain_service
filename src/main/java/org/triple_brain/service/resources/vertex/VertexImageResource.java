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
import org.triple_brain.module.model.FriendlyResourceFactory;
import org.triple_brain.module.model.Image;
import org.triple_brain.module.model.graph.Vertex;
import org.triple_brain.service.ResourceServiceUtils;

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
import java.net.URI;
import java.util.*;

/*
* Copyright Mozilla Public License 1.1
*/
public class VertexImageResource {

    private Vertex vertex;

    public static final String IMAGES_FOLDER_PATH = "/var/lib/triple_brain/image";

    @Inject
    ResourceServiceUtils resourceServiceUtils;

    @Inject
    FriendlyResourceFactory friendlyResourceFactory;

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
        return resizedSmallImage(
                new File(IMAGES_FOLDER_PATH + "/" + imageId)
        );
    }

    @GET
    @Produces("application/octet-stream")
    @Path("/{imageId}/big")
    public byte[] getBig(@PathParam("imageId") String imageId) {
        return resizedBigImage(
                new File(IMAGES_FOLDER_PATH + "/" + imageId)
        );
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("text/plain")
    @Path("/")
    public Response add(@Context HttpServletRequest request) {
        Set<Image> uploadedImages = new HashSet<>();
        String imageBaseUrl = "";
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
                        imageBaseUrl = request.getRequestURL() + "/" + imageId + "/";
                        uploadedImages.add(
                                Image.withUrlForSmallAndBigger(
                                        Urls.get(imageBaseUrl + "small"),
                                        Urls.get(imageBaseUrl + "big")
                                )
                        );
                    }
                }
                vertex.addImages(
                        uploadedImages
                );
                return Response.created(
                       URI.create(
                               imageBaseUrl
                       )
                ).build();
            } catch (FileUploadException fue) {
                fue.printStackTrace();
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            } catch (Exception e) {
                e.printStackTrace();
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
        }
        throw new WebApplicationException(
                Response.Status.INTERNAL_SERVER_ERROR
        );
    }

    private byte[] resizedSmallImage(File image) {
        return resizeImageToMaxWidth(
                image,
                60
        );
    }

    private byte[] resizedBigImage(File image) {
        return resizeImageToMaxWidth(
                image,
                600
        );
    }

    private byte[] resizeImageToMaxWidth(File image, Integer width) {
        try {
            BufferedImage originalImage = ImageIO.read(
                    image
            );
            originalImage = originalImage.getWidth() > width ?
                    Scalr.resize(
                            originalImage,
                            Scalr.Method.QUALITY,
                            Scalr.Mode.FIT_TO_WIDTH,
                            width,
                            width
                    ) :
                    ImageIO.read(
                            image
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
