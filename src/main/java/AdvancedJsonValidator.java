import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.*;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class AdvancedJsonValidator {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Dotenv dotenv = Dotenv.configure().load();

    public static void main(String[] args) {

        String schemaPath = dotenv.get("SCHEMA_PATH");
        String jsonDataPath = dotenv.get("JSON_DATA_PATH");
        if (schemaPath == null || jsonDataPath == null) {
            System.out.println("Error: Please set SCHEMA_PATH and JSON_DATA_PATH in .env file");
            System.exit(1);
        }
        try {
            System.out.println("Start validating");
            System.out.println("Schema: " + schemaPath);
            System.out.println("Data: " + jsonDataPath);
            System.out.println("-".repeat(50));

            boolean isValid = validateJson(schemaPath, jsonDataPath);

            if (isValid) {
                System.out.println("Success! JSON is Valid!");
            } else {
                System.out.println("JSON not valid!");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean validateJson(String schemaPath, String dataPath) throws IOException {
        JsonNode schemaNode = mapper.readTree(new File(schemaPath));
        JsonNode dataNode = mapper.readTree(new File(dataPath));

        JsonSchema schema = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(schemaNode);

        Set<ValidationMessage> errors = schema.validate(dataNode);

        if (!errors.isEmpty()) {
            System.out.println("Found errors:");
            for (ValidationMessage error : errors) {
                System.out.println("- " + error.getMessage());
                System.out.println("  path: " + error.getInstanceLocation());
                System.out.println("---");
            }
            return false;
        }

        return true;
    }
}