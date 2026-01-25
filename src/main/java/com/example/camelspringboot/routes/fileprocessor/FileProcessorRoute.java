package com.example.camelspringboot.routes.fileprocessor;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileProcessorRoute extends RouteBuilder {

    @Value("${app.fileprocessor.root_folder}")
    private String rootFolder;

    @Override
    public void configure() {
        from("file:" + rootFolder + "/in")
            .routeId("fileProcessorRoute")
            .process(new FilenameDateAppendProcessor())
            .to("file:" + rootFolder + "/out");

    }
}
