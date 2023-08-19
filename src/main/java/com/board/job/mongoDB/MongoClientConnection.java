package com.board.job.mongoDB;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MongoClientConnection {
    public static void connectToDB(String connection) {
        try(MongoClient mongoClient = MongoClients.create(connection)) {
            MongoDatabase database = mongoClient.getDatabase("messages");
//            MongoCollection<Message> messagesBoardMongoCollection = database.getCollection("messages_job_board", Message.class);

            // it`s for having all-time limited number of objects, and not added already existing objects which have differences only with an id.
//            messagesBoardMongoCollection.deleteMany(new Document());
            log.info("Pinged your deployment. You successfully connected to MongoDB!");
        }catch (MongoException e) {
            log.error("Error connecting to MongoDB: {}", e.getMessage(), e);
        }
    }
}
