/*
 * Copyright Vincent Blouin under the Mozilla Public License 1.1
 */

package org.triple_brain.service.resources.vertex;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.imgscalr.Scalr;
import org.triple_brain.module.model.FriendlyResourceFactory;
import org.triple_brain.module.model.Image;
import org.triple_brain.module.model.graph.GraphTransactional;
import org.triple_brain.module.model.graph.vertex.VertexOperator;
import org.triple_brain.module.model.json.ImageJson;

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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class VertexImageResource {

    private VertexOperator vertex;

    public static final String IMAGES_FOLDER_PATH = "/var/lib/triple_brain/image";

    @Inject
    FriendlyResourceFactory friendlyResourceFactory;

    @AssistedInject
    public VertexImageResource(
            @Assisted VertexOperator vertex
    ) {
        this.vertex = vertex;
    }


    @GET
    @Produces("application/octet-stream")
    @Path("/{imageId}/big")
    public byte[] getBig(@PathParam("imageId") String imageId) {
        try{
            return Files.readAllBytes(
                    Paths.get(
                            IMAGES_FOLDER_PATH + "/" + imageId  + "_big"
                    )
            );
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @GraphTransactional
    @Produces(MediaType.APPLICATION_JSON)
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
                        saveBigImage(savedFile);
                        String imageBaseUrl = request.getRequestURI() + "/" + imageId + "/";
                        String base64ForSmallImage = Base64.encodeBase64String(
                                resizedSmallImage(savedFile)
                        );
                        uploadedImages.add(
                                Image.withBase64ForSmallAndUriForBigger(
                                        base64ForSmallImage,
                                        URI.create(imageBaseUrl + "big")
                                )
                        );
                    }
                }
                vertex.addImages(
                        uploadedImages
                );

                return Response.ok().entity(
                        ImageJson.toJsonArray(uploadedImages)
                ).build();
            } catch (Exception exception) {
                exception.printStackTrace();
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
        }
        throw new WebApplicationException(
                Response.Status.INTERNAL_SERVER_ERROR
        );
    }

    private void saveBigImage(File rawImageFile){
        try{
            Files.write(
                    Paths.get(
                            rawImageFile.getAbsolutePath() + "_big"
                    ),
                    resizedBigImage(rawImageFile)
            );
        }catch(IOException e){
            throw new RuntimeException(e);
        }
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
