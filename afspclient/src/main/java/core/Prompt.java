package core;

import config.Configuration;
import process.ProcessResult;

import java.util.Scanner;

public class Prompt {

    private final PromptResponse promptResponse = new PromptResponse();
    private final PromptHandler promptHandler;
    private boolean firstPrompt = true;
    private boolean ready = false;

    public Prompt(Configuration conf) {
        this.promptHandler = new PromptHandler(conf);
    }

    public void start(String folder) {
        do {
            prompt(folder);

            if(ready) {
                promptHandler.handle(promptResponse);
                firstPrompt = false;
            }

        } while (promptResponse.getAction() != Action.EXIT);
    }

    public void prompt(String folder) {
        Action[] actions = Action.values();

        if (firstPrompt) {
            System.out.println("Select an option:");
        } else {
            System.out.println("Press enter to continue...");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            System.out.println("Select another option:");
        }

        for (int i = 0; i < actions.length; i++) {
            System.out.println((i + 1) + ". " + actions[i].getLabel());
        }

        int selectedAction = 0;
        boolean validOption = false;
        Scanner scanner = new Scanner(System.in); // create a new instance of the Scanner class
        while (!validOption) {
            System.out.print("Enter option number: ");

            if (scanner.hasNextInt()) {
                selectedAction = scanner.nextInt();
                if (selectedAction >= 1 && selectedAction <= actions.length) {
                    validOption = true;
                } else {
                    System.out.println("Invalid option. Please enter a number between 1 and " + actions.length);
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }

        promptResponse.setAction(actions[selectedAction - 1]);

        System.out.println("You selected: " + promptResponse.getAction().getLabel());

        if(promptResponse.getAction() == Action.SYNC_FILES_TO_LOCAL_FOLDER) {
            System.out.println("Which files do you want to?");
            scanner.nextLine();

            if(scanner.hasNextInt()) {
                ProcessResult.setFileChoice(scanner.nextInt() - 1);
            }
        }

        ready = true;
    }


}
