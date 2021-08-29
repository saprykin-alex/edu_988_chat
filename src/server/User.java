package server;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class User {
    private Socket socket;
    private String userName;
    private static int userNumbers = 0;
    private int userNumber;

    private ObjectOutputStream oos;

    public User(Socket socket) {

        this.socket = socket;
        userNumbers++;
        this.userNumber = userNumbers;
    }

    public int getUserNumber() {
        return userNumber;
    }

    public Socket getSocket() {

        return socket;
    }
    public String getUserName() {

        return userName;
    }
    public void setUserName(String userName) {

        this.userName = userName;
    }

    public ObjectOutputStream getOos() {

        return oos;
    }

    public void setOos(ObjectOutputStream oos) {

        this.oos = oos;
    }


}