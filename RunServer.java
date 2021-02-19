package test;

public class RunServer {
    public static void main(String[] args) throws Exception {
        Server server= new Server(6400,null);
        server.start();
        Thread.sleep(2*60*1000);
        server.stop();
    }
}
