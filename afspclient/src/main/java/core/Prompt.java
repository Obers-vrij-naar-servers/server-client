package core;

import config.Configuration;
import java.util.Scanner;
import static util.PathHelper.isFile;

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

        if(firstPrompt) {
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

        Scanner scanner = new Scanner(System.in);
        int selectedAction = 0;
        boolean validOption = false;
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

        try {
            followUpQuestions(scanner, actions[selectedAction - 1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid option. Please enter a number between 1 and " + actions.length);
        }

        promptResponse.setAction(actions[selectedAction - 1]);

        try {
            if(isFile(folder)) {
                promptResponse.setRequestPath(folder);
                promptResponse.setIsFile(true);
            } else {
                promptResponse.setRequestPath(folder);
                promptResponse.setIsFile(false);
            };

        } catch (Exception e) {
            System.out.println("No such file or directory");
        }

        System.out.println("You selected: " + promptResponse.getAction().getLabel());
        ready = true;
    }

    private void followUpQuestions(Scanner scanner, Action selectedAction) {
        if (selectedAction == Action.SHOW_ALL_FILES) {

        }
    }



}
