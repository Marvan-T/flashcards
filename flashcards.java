
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        Map<String, String> flashCards = new LinkedHashMap<>(); //stores flashcards
        Map<String, Integer> flashCardDifficulty = new HashMap<>(); //flashcards and the number of mistakes made when answering
        List<String> logs = new ArrayList<>(); //log file that contains all the input/output shown at the terminal

        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].contains("-import")) {
                    importCards(flashCards, flashCardDifficulty, args[i + 1], logs);
                    break;
                }
            }
        }

        boolean programOn = true;

        while (programOn) {
            System.out.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            String uChoice = scanner.nextLine();
            logs.add(uChoice);

            switch (uChoice) {
                case "add":
                    addCard(scanner, flashCards, flashCardDifficulty, logs);
                    break;

                case "remove":
                    System.out.println("The card:");
                    logs.add("The card:");
                    removeCard(flashCards, flashCardDifficulty, scanner.nextLine(), logs);
                    break;

                case "import":
                    importCards(flashCards, flashCardDifficulty, args[1], logs);
                    System.out.println("File name:");
                    logs.add("File name:");
                    importCards(flashCards, flashCardDifficulty, scanner.nextLine(), logs);
                    break;

                case "export":
                    System.out.println("File name:");
                    logs.add("File name:");
                    export(flashCards, flashCardDifficulty, scanner.nextLine(), logs);
                    break;

                case "ask":
                    System.out.println("How many times to ask?");
                    logs.add("How many times to ask?");
                    ask(flashCards, flashCardDifficulty, Integer.parseInt(scanner.nextLine()), scanner, logs);
                    break;

                case "exit":
                    System.out.println("Bye bye!");
                    logs.add("Bye bye!");
                    if (args.length > 0) {
                        for (int i = 0; i < args.length; i++) {
                            if (args[i].contains("-export")) {
                                export(flashCards, flashCardDifficulty, args[i + 1], logs);
                            }
                        }
                    }
                    programOn = false;
                    break;

                case "log":
                    System.out.println("File name:");
                    logs.add("File name:");
                    saveLog(logs, scanner.nextLine());
                    break;

                case "hardest card":
                    hardestCard(flashCards, flashCardDifficulty, logs);
                    break;

                case "reset stats":
                    resetStats(flashCardDifficulty, logs);
                    break;


                default:
                    System.out.println("Unknown action");
                    logs.add("Unknown action");
                    break;
            }
        }
    }

    /**
     * Adds flashcards to the collection if their are unique (i.e. the term and the definition should be unique)
     * @param scanner scanner object to take the input from the user
     * @param flashCards collection containing flashcards and their definitions
     * @param flashCardDifficulty collection containing flashcard terms and mistakes made when answering them
     * @param log log file that contains the input/output made at terminal
     */
    public static void addCard(Scanner scanner, Map<String, String> flashCards, Map<String, Integer> flashCardDifficulty, List<String> log) {

        System.out.println("The card:");
        log.add("The card:");
        String term = scanner.nextLine();
        log.add(term);

        if (flashCards.containsKey(term)) {
            System.out.println("The card " + "\"" + term + "\" already exists");
            log.add("The card " + "\"" + term + "\" already exists");

        } else {
            System.out.println("The definition of the card:");
            log.add("The definition of the card:");
            String definition = scanner.nextLine();
            log.add(definition);

            if (flashCards.containsValue(definition)) {
                System.out.println("The definition "  + "\"" + definition + "\" already exists.");
                log.add("The definition "  + "\"" + definition + "\" already exists.");

            } else {
                flashCards.put(term, definition);
                flashCardDifficulty.put(term, 0);
                System.out.println("The pair (" + "\"" + term + "\":" + "\"" + definition + "\") has been added.");
                log.add("The pair (" + "\"" + term + "\":" + "\"" + definition + "\") has been added.");
            }
        }
    }

    /**
     * remove a flashcard from the collection
     * @param flashCards collection containing flashcards and their definitions
     * @param flashCardsDifficulty collection containing flashcard terms and mistakes made when answering them
     * @param cardToRemove user input that says which card to remove
     * @param log log file that contains the input/output made at terminal
     */
    public static void removeCard(Map<String, String> flashCards, Map<String, Integer> flashCardsDifficulty, String cardToRemove, List<String> log) {

        log.add(cardToRemove);

        if (flashCards.containsKey(cardToRemove)) {
            flashCards.remove(cardToRemove);
            flashCardsDifficulty.remove(cardToRemove);
            System.out.println("The card has been removed.");
            log.add("The card has been removed.");
        } else {
            System.out.println("Can't remove " + "\"" + cardToRemove + "\": there is no such card");
            log.add("Can't remove " + "\"" + cardToRemove + "\": there is no such card");
        }
    }

    /**
     * import a set of flashcards that is saved in the location that the user specifies
     * @param flashCards collection containing flashcards and their definitions
     * @param flashCardsDifficulty collection containing flashcard terms and mistakes made when answering them
     * @param fileName filename to import the flashcards from (could be either as an argument at command line) or through the scanner
     * @param log log file that contains the input/output made at terminal
     */
    public static void importCards(Map<String, String> flashCards, Map<String, Integer> flashCardsDifficulty, String fileName, List<String> log) {
        log.add(fileName);
        File file = new File("./" + fileName);
        int count = 0;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String[] aLine = scanner.nextLine().split(":"); //0 - term, 1 - definition, 2 - no.of mistakes
                flashCards.put(aLine[0], aLine[1]);
                flashCardsDifficulty.put(aLine[0], Integer.parseInt(aLine[2]));
                count++;
            }
            System.out.println(count + " cards have been loaded.");
            log.add(count + " cards have been loaded.");
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            log.add("File not found.");
        }
    }

    /**
     * exports the flashcards from the collection to a text file
     * @param flashCards collection containing flashcards and their definitions
     * @param flashCardsDifficulty collection containing flashcard terms and mistakes made when answering the
     * @param fileName filename to export the flashcards to (could be either as an argument at command line) or through the scanner
     * @param log
     */
    public static void export(Map<String, String> flashCards, Map<String, Integer> flashCardsDifficulty, String fileName, List<String> log) {
        log.add(fileName);
        File file = new File("./" + fileName);
        int count = 0;

        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (var entry:flashCards.entrySet()) {
                printWriter.println(entry.getKey() + ":" + entry.getValue() + ":" + flashCardsDifficulty.get(entry.getKey()));
                count++;
            }
        } catch (IOException e) {
            System.out.println("Exception occurred: " + e.getMessage());
            log.add("Exception occurred: " + e.getMessage());
        }
        System.out.println(count + " cards have been saved.");
        log.add(count + " cards have been saved.");
    }


    /**
     * Randomly asks the flashcards for their definition from the user (based on the number of times they wish to be asked)
     * @param flashCards collection containing flashcards and their definitions
     * @param flashCardsDifficulty collection containing flashcard terms and mistakes made when answering them
     * @param numOfTimes number of times the user wish to be asked
     * @param scanner scanner object to take the input from the user
     * @param log log file that contains the input/output made at terminal
     */
    public static void ask(Map<String, String> flashCards, Map<String, Integer> flashCardsDifficulty, int numOfTimes, Scanner scanner, List<String> log) {
        log.add(Integer.toString(numOfTimes));
        ArrayList<String> terms = new ArrayList<>(flashCards.keySet());
        Random random = new Random();

        for (int i = 0; i < numOfTimes ; i++) {
            String randomTerm = terms.get(random.nextInt(terms.size()));
            System.out.println("Print the definition of " + "\"" + randomTerm + "\"");
            log.add("Print the definition of " + "\"" + randomTerm + "\"");
            String userAnswer = scanner.nextLine();
            log.add(userAnswer);

            if (Objects.equals(flashCards.get(randomTerm), userAnswer)) {
                System.out.println("Correct answer.");
                log.add("Correct answer.");

            } else if (flashCards.containsValue(userAnswer)) {
                flashCards.forEach((aTerm, aDefinition) -> {
                    if (Objects.equals(userAnswer, aDefinition)) {
                        System.out.println("Wrong answer. The correct one is "  + "\"" + flashCards.get(randomTerm) + "\", you've just written the definition of " + "\"" + aTerm + "\" (ignoring case)");
                        log.add("Wrong answer. The correct one is "  + "\"" + flashCards.get(randomTerm) + "\", you've just written the definition of " + "\"" + aTerm + "\" (ignoring case)");
                        int currentMistakes = flashCardsDifficulty.get(randomTerm);
                        flashCardsDifficulty.put(randomTerm, currentMistakes + 1); //increasing the no.of mistakes
                    }
                });

            } else {
                System.out.println("Wrong answer. The correct one is "  + "\"" + flashCards.get(randomTerm) + "\"");
                log.add("Wrong answer. The correct one is "  + "\"" + flashCards.get(randomTerm) + "\"");
                int currentMistakes = flashCardsDifficulty.get(randomTerm);
                flashCardsDifficulty.put(randomTerm, currentMistakes + 1); //increasing the no.of mistakes
            }
        }
    }

    /**
     * Loops through the collection "flashCardsDifficulty" to find which card/s has the highest number of mistakes when answering them
     * @param flashCards collection containing flashcards and their definitions
     * @param flashCardsDifficulty collection containing flashcard terms and mistakes made when answering them
     * @param log log file that contains the input/output made at terminal
     */
    public static void hardestCard(Map<String, String> flashCards, Map<String, Integer> flashCardsDifficulty, List<String> log) {
        int highestMistakes = 0;
        List<String> hardestCards = new ArrayList<>();

        for (var entry:flashCardsDifficulty.entrySet()) {
            if (entry.getValue() > highestMistakes) {  //if mistakes of the current card higher than the highest mistake made
                hardestCards.clear();
                hardestCards.add(entry.getKey());
                highestMistakes = entry.getValue();
            } else if (entry.getValue() >= 1 && entry.getValue() == highestMistakes) {
                hardestCards.add(entry.getKey());
            }
        }

        if (hardestCards.size() == 0) {
            System.out.println("There are no cards with errors.");
            log.add("There are no cards with errors.");
        } else if (hardestCards.size() == 1) {
            System.out.println("The hardest card is " + "\"" + hardestCards.get(0) + "\"." + "You have " + highestMistakes + " errors answering it.");
            log.add("The hardest card is " + "\"" + hardestCards.get(0) + "\"." + "You have " + highestMistakes + " errors answering it.");
        } else {
            StringBuilder errorCards = new StringBuilder();
            hardestCards.forEach(term -> errorCards.append("\"").append(term).append("\", "));
            System.out.println("The hardest cards are " + errorCards.toString() + ". You have " + highestMistakes + " errors answering them");
            log.add("The hardest cards are " + errorCards.toString() + ". You have " + highestMistakes + " errors answering them");
        }

    }


    /**
     * Saves the log to a text file
     * @param log log file that contains the input/output made at terminal
     * @param fileName name of the log file that will be saved
     */
    public static void saveLog(List<String> log, String fileName) {
        log.add(fileName);
        File file = new File("./" + fileName);

        try (PrintWriter printWriter = new PrintWriter(file)) {
            log.forEach(printWriter::println);
            System.out.println("The log has been saved.");
            log.add("The log has been saved");
        } catch (IOException e) {
            System.out.println("An exception occurred: " + e.getMessage());
        }

    }

    /**
     * Reset the number of mistakes that the user made on flashcards to 0
     * @param flashCardsDifficulty collection containing flashcard terms and mistakes made when answering them
     * @param log log file that contains the input/output made at terminal
     */
    public static void resetStats(Map<String, Integer> flashCardsDifficulty, List<String> log) {
        flashCardsDifficulty.forEach((term,mistake) -> flashCardsDifficulty.put(term, 0));
        System.out.println("Card statistics has been reset.");
        log.add("Card statistics has been reset.");
    }


}
