import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;

public class SendMessage {

    private static final String USER_AGENT = "Mozilla/5.0";

    public static void main(String[] args) {
        /**
         Uncomment while loop to endlessly spam the group :)
         */
        //while (true) {
        try {
            sendPost();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //}
    }

    private static void sendPost() throws Exception {
        try (InputStream inputStream = new FileInputStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(inputStream);
            UUID uuid = UUID.randomUUID();
            String groupId = prop.getProperty("groupId");
            String token = prop.getProperty("token");
            String message = prop.getProperty("message");
            URL url = new URL("https://api.groupme.com/v3/groups/" + groupId + "/messages?token=" + token);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setDoOutput(true);
            String jsonInputString = "{\"message\": {\"source_guid\":\"" + uuid.toString() + "\",\"text\": \"" + message + "\"} }";
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        }
    }
}
