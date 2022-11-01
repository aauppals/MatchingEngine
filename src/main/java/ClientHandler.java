import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private BufferedReader inFromClient;
    private UnifiedOrderBook unifiedOrderBook;
    private MessageParser messageParser;

    public ClientHandler(Socket client, UnifiedOrderBook book) throws IOException {
        this.inFromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
        unifiedOrderBook = book;
        messageParser = new MessageParser();

    }

    @Override
    public void run() {
        try {
            String clientRequest = inFromClient.readLine();
            processRequest(clientRequest);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inFromClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processRequest(String clientRequest) {
        //ToDo: complete this method
    }
}


