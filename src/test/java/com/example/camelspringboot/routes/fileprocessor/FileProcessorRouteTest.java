package com.example.camelspringboot.routes.fileprocessor;

import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.commons.io.FileUtils;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class FileProcessorRouteTest {

    @Value("${app.fileprocessor.root_folder}")
    private String rootFolder;

    private static final long DURATION_MILIS = 10000;

    @BeforeEach
    void setup() throws IOException {
        Path currentPath = Path.of("").toAbsolutePath();
        Files.createDirectories(currentPath.resolve(rootFolder + "/in"));
        Files.createDirectories(currentPath.resolve(rootFolder + "/out"));
    }

    @AfterEach
    void tearDown() {
        Path currentPath = Path.of("").toAbsolutePath();
        FileUtils.deleteQuietly(currentPath.resolve(rootFolder).toFile());
    }

    @Test
    public void checkFilenameModification() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        File sourceFile = new File(Path.of(rootFolder).toAbsolutePath() + "/in/" + "File1.txt");
        File destinationFile = new File(Path.of(rootFolder).toAbsolutePath() + "/out/" + dateFormat.format(new Date()) + "File1.txt");

        sourceFile.createNewFile();
        Awaitility.await().atMost(DURATION_MILIS, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            assertThat(destinationFile.exists()).isTrue();
        });
    }
}
