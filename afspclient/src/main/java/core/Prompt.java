package core;

import config.Configuration;
import config.ConfigurationManager;
import util.AfspFileHandler;
import java.util.Scanner;

public class Prompt {

    private final PromptResponse promptResponse = new PromptResponse();
    private final AfspFileHandler fileHandler = new AfspFileHandler(ConfigurationManager.getInstance().getCurrentConfiguration().getFolder());

    private final PromptHandler promptHandler;
    private boolean firstPrompt = true;
    private boolean ready = false;

    public Prompt(Configuration conf) {
        this.promptHandler = new PromptHandler(conf);
    }

    public void start(String folder) {
        do {
            prompt(folder);

            if (ready) {
                promptHandler.handle(promptResponse);
                firstPrompt = false;
            }

        } while (promptResponse.getAction() != Action.EXIT);
    }

    public void prompt(String folder) {
        Action[] actions = Action.values();
        Scanner scannerInput = new Scanner(System.in);
        int selectedAction = 0;

        if (firstPrompt) {
            promptResponse.setAction(actions[0]);
        } else {
            System.out.println();
            System.out.println("\u001B[33m" + "Press enter to continue... " + "\u001B[0m");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            System.out.println("\u001B[36m" + "Select another option: " + "\u001B[0m");

            for (int i = 0; i < actions.length; i++) {
                System.out.println((i + 1) + ". " + actions[i].getLabel());
            }

            boolean validOption = false;
            while (!validOption) {
                System.out.println("\u001B[34m" + "Enter option by number: " + "\u001B[0m");
                System.out.println();

                if (scannerInput.hasNextInt()) {
                    selectedAction = scannerInput.nextInt();
                    if (selectedAction >= 1 && selectedAction <= actions.length) {
                        validOption = true;
                    } else {
                        System.out.println("\\u001B[31m\" +Invalid option. Please enter a number between 1 and " + actions.length + "\u001B[0m");
                    }
                } else {
                    System.out.println("\\u001B[31m\"Invalid input. Please enter a number." + "\u001B[0m");
                    scannerInput.next();
                }
            }
            promptResponse.setAction(actions[selectedAction - 1]);
        }

        if (promptResponse.getAction() == Action.Download_FILE) {
            downloadFollowUp(scannerInput);
        }

        if (promptResponse.getAction() == Action.DELETE_FILE_FROM_SERVER) {
            deleteFollowUp(scannerInput);
        }

        ready = true;
    }

    private void deleteFollowUp(Scanner scanner) {
        System.out.println("\u001B[34m" + "Select a file to delete by number: " + "\u001B[0m");
        System.out.println();
        for (int i = 0; i < AfspFileHandler.getTargetFiles().size(); i++) {
            System.out.println((i + 1) + ". " + AfspFileHandler.getTargetFiles().get(i).getFileName());
        }
        scanner.nextLine();

        if (scanner.hasNextInt()) {
            AfspFileHandler.setFileChoice(scanner.nextInt() - 1);
        }
    }


    private void downloadFollowUp(Scanner scanner) {
        System.out.println("\u001B[34m" + "Select a file to download by number: " + "\u001B[0m");
        System.out.println();
        for (int i = 0; i < AfspFileHandler.getTargetFiles().size(); i++) {
            System.out.println((i + 1) + ". " + AfspFileHandler.getTargetFiles().get(i).getFileName());
        }
        scanner.nextLine();

        if (scanner.hasNextInt()) {
            AfspFileHandler.setFileChoice(scanner.nextInt() - 1);
        }
    }


}
