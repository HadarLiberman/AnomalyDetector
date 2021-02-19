package test;

import java.io.InputStream;
import java.io.OutputStream;

public interface ClientHandler {

    void communicate(InputStream inFromClient, OutputStream outToClient);

}
