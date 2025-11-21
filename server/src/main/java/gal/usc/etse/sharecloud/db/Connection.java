package gal.usc.etse.sharecloud.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Connection {

    private MongoClient client;

    public Connection() {
        String connectionString = "mongodb+srv://user:OtQ9oEmYVYq6RwbR@sharecloud.b57dn98.mongodb.net/?retryWrites=true&w=majority&appName=ShareCloud";
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                // Send a ping to confirm a successful connection
                client = mongoClient;
                MongoDatabase database = mongoClient.getDatabase("root");
                database.runCommand(new Document("ping", 1));
                System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
    }
    public void closeConnection() {
        if (client != null) {
            client.close();
        }
    }
}
