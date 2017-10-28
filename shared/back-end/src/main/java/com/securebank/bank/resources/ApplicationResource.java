package com.securebank.bank.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.securebank.bank.model.Login;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;

@Component
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApplicationResource {
    private @Value("${git.loc}")
    String gitRepoLocation;

    @GET
    public Response login(Login login){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("status","ok");
        return Response.status(Response.Status.OK).entity(objectNode).type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/version")
    public Response version() throws IOException {
        Repository existingRepo = new FileRepositoryBuilder()
                .setGitDir(new File(gitRepoLocation))
                .readEnvironment()
                .findGitDir() .build();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("status",existingRepo.getRef("HEAD").getObjectId().getName());
        return Response.status(Response.Status.OK).entity(objectNode).type(MediaType.APPLICATION_JSON).build();
    }
}
