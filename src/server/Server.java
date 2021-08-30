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
                    User currentUser = new User(socket);
                    System.out.println("Клиент №" + currentUser.getUserNumber() + " подключился");
                    users.add(currentUser);
                    DataInputStream in = new DataInputStream(currentUser.getSocket().getInputStream()); // Поток ввода
                    DataOutputStream out = new DataOutputStream(currentUser.getSocket().getOutputStream()); // Поток вывода
//                    ObjectOutputStream oos= new ObjectOutputStream(currentUser.getSocket().getOutputStream());//С серилизацией потока не работает даже если на клиенте сделана десерилизация
//                    currentUser.setOos(oos);

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                out.writeUTF("Добро пожаловать на сервер!");
                                //Ждём от клиента ввода имени и проверяем его на уникальность
                                String userName;
                                do {
                                    out.writeUTF("Введите ваше имя: ");
                                    userName = in.readUTF(); // Ожидаем имя от клиента
                                }while (existUserName(users, userName, out));//Если true, то заново просим ввести имя
                                currentUser.setUserName(userName);
                                usersName.add(currentUser.getUserName());
                                //Рассылка сообщений всем клиентам о подключении нового пользователя
                                searchForUsers(users, currentUser, "К чату подключены только Вы", "К чату подключены: Вы", " присоединился к беседе");
                                while (true){
                                    String request = in.readUTF(); // Ждём сообщение от пользователя
                                    //Распарсивание сообщения
                                    String[] splited = request.split(" ");
                                        if (splited[0].equals("/m") && splited.length>2) {
                                            User user = existUser(splited[1], users);
                                            if (user!=null){
                                                DataOutputStream out = new DataOutputStream(user.getSocket().getOutputStream());
                                                String str = "";
                                                for (int i = 2; i < splited.length; i++) {
                                                    str+=splited[i];
                                                }
                                                out.writeUTF(currentUser.getUserName()+": "+str);
                                            }
                                        }else {
                                            System.out.println(currentUser.getUserName()+": "+request);
                                            for (User user : users) {
                                                if(users.indexOf(user) == users.indexOf(currentUser)) continue;
                                                DataOutputStream out = new DataOutputStream(user.getSocket().getOutputStream());
                                                out.writeUTF(currentUser.getUserName()+": "+request);
                                            }
                                        }
                                }
                            }catch (IOException e){
                                //Рассылка всем клиентам об отключении пользователя
                                searchForUsers(users, currentUser, "Вы покинули чат", "Вы покинули чат, но в нём остались: ", " покинул чат");
                                users.remove(currentUser);
                                usersName.remove(currentUser.getUserName());
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
    //Метод для рассылки о входе и выходе из чата
    private static void searchForUsers(ArrayList<User> users, User currentUser, String onlineUsers1, String onlineUsers2, String str) {
        int i = 0;
        String onlineUsers = "";
            for (User user : users) {         //Перебор всех клиентов
                if (user.getUserName() != null) {       //Если клиент подключился, но не ввёл своё имя, то не отправляем ему сообщение
                    try {
                        DataOutputStream out = new DataOutputStream(user.getSocket().getOutputStream());
                        if (!user.equals(currentUser)) {
                            out.writeUTF(currentUser.getUserName() + str);         //Отправляем сообщение всем клиентам, кроме текущего
                            onlineUsers += ", " + user.getUserName();   //Список клиентов в строке
                            i++;
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        try {
            DataOutputStream out = new DataOutputStream(currentUser.getSocket().getOutputStream());     //Текущему пользователю сообщаем кто ещё есть в чате
                if (i > 0) {
                    onlineUsers=onlineUsers2+onlineUsers;
                } else {
                    onlineUsers = onlineUsers1;
                }
            out.writeUTF(onlineUsers);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    //Функция проверки уникальности имени подключающегося клиента
    private static boolean existUserName(ArrayList<User> users, String userName, DataOutputStream out){
        User user = existUser(userName, users);
        if(user!=null){
            try {
                out.writeUTF("Такой никнейм уже присутствует в чате!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }

    private static User existUser(String userName, ArrayList<User> users){
        for (User user:users) {
            if(user.getUserName()!=null) {
                if (user.getUserName().equals(userName)) return user;
            }
        }
        return null;
    }

}

