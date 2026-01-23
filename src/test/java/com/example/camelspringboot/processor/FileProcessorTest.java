package com.example.camelspringboot.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.io.FileUtils;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class FileProcessorTest {

    private static final long DURATION_MILIS = 10000;
    private static final String ROOT_FOLDER = "src/test/resources/fileprocessor";
    private static final String INPUT_FOLDER = ROOT_FOLDER + "/in";
    private static final String OUTPUT_FOLDER = ROOT_FOLDER + "/out";

    @AfterAll
    static void tearDown() throws IOException {
        Path currentPath = Path.of("").toAbsolutePath();
        FileUtils.deleteQuietly(currentPath.resolve(ROOT_FOLDER).toFile());
    }

    @Test
    public void givenJavaDSLRoute_whenCamelStart_thenMoveFolderContent() throws Exception {
        CamelContext camelContext = new DefaultCamelContext();
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file:" + INPUT_FOLDER)
                        .process(new FileProcessor())
                        .to("file:" + OUTPUT_FOLDER);
            }
        });
        camelContext.start();

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        File sourceFile = new File(INPUT_FOLDER + "/" + "File1.txt");
        File destinationFile = new File(OUTPUT_FOLDER + "/" + dateFormat.format(date) + "File1.txt");

        sourceFile.createNewFile();
        Awaitility.await().atMost(DURATION_MILIS, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            assertThat(destinationFile.exists()).isTrue();
        });
        camelContext.stop();
    }
}
