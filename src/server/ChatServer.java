package server;

public class ChatServer {
    public static void main(String[] args){
        Server server = new Server();
        server.start(4444);
    }
}
