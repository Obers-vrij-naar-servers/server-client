package process;

import config.Configuration;

import java.util.Scanner;

public class ClientPrompt {

    private MenuOption selectedMenuOption;
    private final RequestHandler requestHandler;
    private boolean firstPrompt = true;

    public ClientPrompt(Configuration conf) {
        this.requestHandler = new RequestHandler(conf);
    }

    public void start() {
        do {
            prompt();

            if(selectedMenuOption != null) {
                requestHandler.handle();
            }

            this.firstPrompt = false;

        } while (selectedMenuOption != MenuOption.EXIT);
    }

    public void prompt() {
        MenuOption[] options = MenuOption.values();

        if(this.firstPrompt) {
            System.out.println("Select an option:");
        } else {
            System.out.println("Select another option:");
        }

        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i].getLabel());
        }

        Scanner scanner = new Scanner(System.in);
        int selectedOption = 0;
        boolean validOption = false;
        while (!validOption) {
            System.out.print("Enter option number: ");

            if (scanner.hasNextInt()) {
                selectedOption = scanner.nextInt();
                if (selectedOption >= 1 && selectedOption <= options.length) {
                    validOption = true;
                } else {
                    System.out.println("Invalid option. Please enter a number between 1 and " + options.length);
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }

        this.selectedMenuOption = options[selectedOption - 1];
        System.out.println("You selected: " + selectedMenuOption.getLabel());
    }
}
