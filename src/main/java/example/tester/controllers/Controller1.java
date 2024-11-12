package example.tester.controllers;

import example.annotations.Controller;
import example.annotations.GET;
import example.annotations.POST;
import example.annotations.Path;

@Controller
@Path("/controller1")
public class Controller1 {
    @GET
    @Path("/test")
    public void testGet() {
        System.out.println("GET /controller1/test");
    }

    @POST
    @Path("/test1")
    public void testPost() {
        System.out.println("POST /controller1/test1");
    }

}
