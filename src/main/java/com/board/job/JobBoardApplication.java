package com.board.job;

import com.board.job.mongoDB.MongoClientConnection;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@AllArgsConstructor
@SpringBootApplication
public class JobBoardApplication {
    private static final String CONNECTION_MONGO = System.getenv("mongoDB_url");

    private static void setToProperties() {
        String username = System.getenv("username");
        String password = System.getenv("password");

        if (Objects.isNull(username) || Objects.isNull(password)) {
            log.warn("You need to write your username and password in Environment Variables!");
            return;
        }

        getAndSetProperties(username, password);
    }

    private static void getAndSetProperties(String username, String password) {
        Properties properties = new Properties();
        String filePath = "src/main/resources/application.properties";

        try {
            FileInputStream inputStream = new FileInputStream(filePath);
            properties.load(inputStream);
            inputStream.close();

            properties.setProperty("spring.datasource.username", username);
            properties.setProperty("spring.datasource.password", password);
            properties.setProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/job_board");
            properties.setProperty("spring.data.mongodb.uri", CONNECTION_MONGO);

            FileOutputStream outputStream = new FileOutputStream(filePath);
            properties.store(outputStream, null);

            outputStream.flush();
            outputStream.close();

            log.info("Username and password was successfully written in file application.properties.");
        } catch (IOException ioException) {
            log.error("Error: writing in property file {}", ioException.getMessage());
        }
    }

    public static void main(String[] args) {
        setToProperties();
        MongoClientConnection.connectToDB(CONNECTION_MONGO);

        SpringApplication.run(JobBoardApplication.class, args);
    }
}
