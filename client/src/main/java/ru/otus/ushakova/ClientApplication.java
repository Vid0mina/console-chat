package ru.otus.ushakova;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientApplication {

    private static String clientName;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (
                Socket socket = new Socket("localhost", 8189);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Клиент подключился к серверу.");
            new Thread(() -> {
                try {
                    while (true) {
                        String inMessage = in.readUTF();
                        System.out.println(inMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            while (true) {
                String msg = scanner.nextLine();
                if (msg.startsWith("/w")) {
                    out.writeUTF(parseMessage(msg));
                } else if (msg.equals("/exit")) {
                    break;
                } else {
                    out.writeUTF(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String parseMessage(String msg) {
        String result = "";
        if (msg.startsWith("/w")) {
            String[] part = msg.split("\\s");
            String message = "";
            for (int i = 2; i < part.length; i++) {
                message += part[i] + " ";
            }
            result = part[0] + "#" + part[1] + "#" + message.trim();
            return result;
        } else {
            return result;
        }
    }

}
