import java.io.*;
import java.net.*;

public class Main {


    public static void main(String args[]) throws IOException {
        BufferedReader inFromUser =
                new BufferedReader(new InputStreamReader(System.in));

        TCPClient camsTCPClient = new TCPClient();
        //camsTCPClient.sendCommand(inFromUser.readLine());

    }
}