package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

public class Server {
    public static void main(String[] args) {
        {
            try {
                int i=0;
                ArrayList<ServerThread> threads = new ArrayList<>();
                ServerSocket serverSocket = new ServerSocket(8188);  // Создаём серверный сокет
                System.out.println("Сервер запущен");
                while (true){           //Бесконечный цикл для ожидания подключения клиентов
                    Socket socket = serverSocket.accept();  //Ожидаем подключения
                    threads.add(new ServerThread(socket, i+1));         //Создаём новый поток для подключения к сокету
                    threads.get(i).start();                 //Стартуем нового клиента в новом потоке
                    i++;
                    System.out.println("Клиент №" + i + " подключился");     //Выводим на сервере сообщение о подключении нового клиента
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ServerThread extends Thread{
    private Socket socket;
    private int i;
    private String userName;
    //Передаём в Поток параметры: сокет и номер подключения
    ServerThread(Socket socket, int i){
        super();
        this.socket = socket;
        this.i = i;
        this.userName = userName;
    }



    @Override
    public void run(){
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());     // Поток ввода
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());  // Поток вывода
            out.writeUTF("Добро пожаловать на сервер. Вы подключились как клиент №" + i);
            out.writeUTF("Введите ваше имя: ");
            userName = in.readUTF();
            while (true) {
                // Ожидаем строку от клиента
                String request = in.readUTF();
                System.out.println("Получил запрос от клиента №" +i);
                out.writeUTF(request.toUpperCase(Locale.ROOT));
                //System.out.println("Передал ответ клиенту №" +i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
