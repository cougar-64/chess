package ui;
import java.lang.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;
import server.Server;

public class ServerFacade {
    static {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to CS 240s chess game! Type `help` to get started\n");
        String input = scanner.nextLine().toLowerCase();
    }

    private void help() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("""
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account (please type `register` and then your desired username, password, and email)
                login <USERNAME> <PASSWORD> - to play chess (please type `login` followed by your username and password
                quit - playing chess
                help - with possible commands
                """);
        while(true) {
            String input = scanner.next().toLowerCase();
            String[] words = input.replace(",", "").split("\\s+");
            if (words[0].equals("help")) {
                help();
            } else if (words[0].equals("register")) {
                if (words.length != 4) {
                    System.out.println("Error: Not correct amount of parameters. Please type `register` followed by your desired username, password, and email\n");
                    input = scanner.next().toLowerCase();
                    words = input.replace(",", "").split("\\s+");}
                register(words[1], words[2], words[3]);
                break;
                    }
            else if (words[0].equals("login")) {
                if (words.length != 3) {
                    System.out.println("Error: Not correct amount of parameters. Please type `login` followed by your username and password\n ");
                    input = scanner.next().toLowerCase();
                    words = input.replace(",", "").split("\\s+");
                }
                login(words[1], words[2]);
            }
            else if (words[0].equals("quit")) {
                quit();
            }
            else {
                System.out.println("Error: Request not understood. Defaulting to the `help` window...\n");
                help();
            }
        }
    }

    private void register(String username, String password, String email) {

    }


}
