package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        {
            try {
                Socket socket = new Socket("localhost", 8188);
                System.out.println("Успешно подключен к серверу");

                DataInputStream in = new DataInputStream(socket.getInputStream());      // Поток ввода
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());  // Поток вывода
                String response = in.readUTF();
                System.out.println(response);
                System.out.println(response);
                Scanner scanner = new Scanner(System.in);
                while (true){
                    String consoleText = scanner.nextLine();
                    out.writeUTF(consoleText);
                    response = in.readUTF();
                    System.out.println("Ответ сервера: " + response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
