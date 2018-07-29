import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;

class TCPClient {

    public boolean connectedToServer = false;

    private BufferedReader inFromUser;
    private Socket clientSocket;
    private DataOutputStream outToServer;
    private BufferedReader inFromServer;
    private boolean loggedIn = false;

    public TCPClient() throws IOException {
        String clientCommand;
        String[] parsedCommand;
        String serverResponse = "";

        inFromUser = new BufferedReader(new InputStreamReader(System.in));

        String connectResponse = connectToServer();
        System.out.println(connectResponse);

        //server is connected
        while(true) {
            //busy wait for client to input command, first command should be login
            clientCommand = inFromUser.readLine();

            //TODO: Remove this, just exits client process if requested
            if (checkExit(clientCommand)){
                if (clientSocket != null) {
                    clientSocket.close();
                }
                break;
            }

            if(goodCommand(clientCommand, loggedIn)){
                sendCommand(clientCommand);
                //busy wait for server response
                serverResponse = getServerCommand(inFromServer);
                //print response
                System.out.println(serverResponse);
                if (serverResponse.charAt(0) == '!'){
                    loggedIn = true;
                }
            } else {
                System.out.println("Bad command, you're either not logged in or you don't know what you're doing");
            }

        }

    }

    private String connectToServer() throws IOException {
        while(!connectedToServer){
            //continue trying to connect until welcome string is accepted
            clientSocket = new Socket("localhost", 42069);
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            inFromServer = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            //busy wait for welcome string
            String serverResponse = getServerCommand(inFromServer);
            //if positive we can move on, if not stay in loop and try connect again
            if(serverResponse.charAt(0) == '+'){
                connectedToServer = true;
                return serverResponse;
            } else {
                //ask if they want to try connect again
                System.out.println(serverResponse);
                System.out.println("Server not available");
            }

        }
        return "";
    }

    private boolean goodCommand(String userCommand, boolean loggedIn){
        String command = userCommand.substring(0, Math.min(userCommand.length(), 4));
        List<String> logInCommands = Arrays.asList("USER", "ACCT", "PASS");
        List<String> goodCommands = Arrays.asList("USER", "ACCT", "PASS", "TYPE",
                "LIST", "CDIR", "KILL", "NAME", "DONE", "RETR", "STOR");
        if(loggedIn){
            return goodCommands.contains(command);
        } else {
            return logInCommands.contains(command);
        }
    }

    private void sendCommand(String command) throws IOException {
        if (this.outToServer != null){
            outToServer.writeBytes(command + "\0");
        }
    }

    private boolean checkExit(String command){
        return (command.equals("EXIT"));
    }

    private String getServerCommand(BufferedReader inFromClient) throws IOException {
        String serverCommand = "";
        int charCount = 0;
        while(true){
            char ch = (char) inFromClient.read();
            //check for terminating null or too many chars
            if ((ch == '\0') || (charCount >= Integer.MAX_VALUE)){
                break;
            } else {
                serverCommand += ch;
                charCount++;
            }
        }
        return serverCommand;
    }

    private String[] parseString(String string){
        return string.split(" ");
    }
} 