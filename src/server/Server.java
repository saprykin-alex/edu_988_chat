package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

public class Server {
    public static void main(String[] args) {
        {
            ArrayList<User> users = new ArrayList<>();
            ArrayList<String> usersName = new ArrayList<>();
            try {
                ServerSocket serverSocket = new ServerSocket(8188); // Создаём серверный сокет
                System.out.println("Сервер запущен");
                while (true){ // Бесконечный цикл для ожидания подключения клиентов
                    Socket socket = serverSocket.accept(); // Ожидаем подключения клиента
                    System.out.println("Клиент подключился");
                    User currentUser = new User(socket);
                    users.add(currentUser);
                    DataInputStream in = new DataInputStream(currentUser.getSocket().getInputStream()); // Поток ввода
                    DataOutputStream out = new DataOutputStream(currentUser.getSocket().getOutputStream()); // Поток вывода
                    ObjectOutputStream oos= new ObjectOutputStream(currentUser.getSocket().getOutputStream());
                    currentUser.setOos(oos);
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                currentUser.getOos().writeObject("Добро пожаловать на сервер");
                                currentUser.getOos().writeObject("Введите ваше имя: ");
//                                out.writeUTF("Добро пожаловать на сервер");
//                                out.writeUTF("Введите ваше имя: ");
                                String userName = in.readUTF(); // Ожидаем имя от клиента

                                //Сюда код по распарсиванию имени

                                currentUser.setUserName(userName);
                                usersName.add(currentUser.getUserName());
                                String onlineUsers="onlineUsers";
                                for (User user:users) {
                                    onlineUsers+="//"+user.getUserName();
                                }

                                for (User user : users) {
                                    DataOutputStream out = new DataOutputStream(user.getSocket().getOutputStream());
                                    out.writeUTF(currentUser.getUserName()+" присоединился к беседе");
                                    out.writeUTF(onlineUsers);
                                }
                                while (true){
                                    String request = in.readUTF(); // Ждём сообщение от пользователя
                                    System.out.println(currentUser.getUserName()+": "+request);
                                    for (User user : users) {
                                        if(users.indexOf(user) == users.indexOf(currentUser)) continue;
                                        DataOutputStream out = new DataOutputStream(user.getSocket().getOutputStream());
                                        out.writeUTF(currentUser.getUserName()+": "+request);
                                    }
                                }
                            }catch (IOException e){
                                users.remove(currentUser);
                                String onlineUsers="onlineUsers";
                                for (User user:users) {
                                    onlineUsers+="//"+user.getUserName();
                                }

                                for (User user : users) {
                                    try {
                                        DataOutputStream out = new DataOutputStream(user.getSocket().getOutputStream());
                                        out.writeUTF(currentUser.getUserName()+" покинул чат");
                                        out.writeUTF(onlineUsers);
                                    } catch (IOException ioException) {
                                        ioException.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                    thread.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

