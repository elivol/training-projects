package flashcards;

import java.io.*;
import java.util.*;

public class Main {

    private static Map<String, String> map;
    private static Scanner scanner;
    private static Map<String, Integer> mistakesMap;
    private static List<String> log;
    private static String exportFileName;

    public static void main(String[] args) {

        scanner = new Scanner(System.in);
        map = new LinkedHashMap<>();
        mistakesMap = new HashMap<>();
        log = new ArrayList<>();

        init(args);
        String action = "";
        String line = "Input the action (add, remove, import, export, ask, hardest card, reset stats, log, exit):";

        while (!"exit".equals(action)) {
            System.out.println(line);
            log.add(line + "\n");
            action = scanner.nextLine();
            log.add(action + "\n");

            switch (action) {
                case "add":
                    add();
                    break;
                case "remove":
                    remove();
                    break;
                case "import":
                    importFlashcards();
                    break;
                case "export":
                    exportFlashcards();
                    break;
                case "ask":
                    ask();
                    break;
                case "hardest card":
                    hardestCard();
                    break;
                case "reset stats":
                    resetStats();
                    break;
                case "log":
                    log();
                    break;
                case "exit":
                    exit();
                    break;
            }

            System.out.println();
            log.add("\n\n");
        }
    }

    public static void init(String[] args){
        for (int i = 0; i < args.length; i += 2) {
            if ("-import".equals(args[i])) {
                String line = readFromFile(args[i + 1]);
                System.out.println(line);
                log.add("\n" + line);
            } else if("-export".equals(args[i])) {
                exportFileName = args[i + 1];
            }
        }
    }

    public static void add() {
        String line = "The card:";
        StringBuilder collector = new StringBuilder(line);

        System.out.println(line);
        String card = scanner.nextLine();
        collector.append("\n").append(card);

        if (map.containsKey(card)) {
            line = "The card \"" + card + "\" already exists.";
            System.out.println(line);
            collector.append("\n").append(line);
            return;
        }

        line = "The definition of the card:";
        System.out.println(line);
        String definition = scanner.nextLine();
        collector.append("\n").append(line).append("\n").append(definition);
        if (map.containsValue(definition)) {
            line = "The definition \"" + definition + "\" already exists.";
            System.out.println(line);
            collector.append("\n").append(line);
            return;
        }

        map.put(card, definition);
        line = "The pair (\"" + card + "\":\"" + definition + "\") has been added.";
        System.out.println(line);
        collector.append("\n").append(line);
        log.add(collector.toString());
    }

    public static void remove() {
        String line = "The card:";
        StringBuilder collector = new StringBuilder(line);

        System.out.println(line);
        String card = scanner.nextLine();
        collector.append("\n").append(card);

        if (map.containsKey(card)) {
            map.remove(card);
            mistakesMap.remove(card);
            line = "The card has been removed.";
        } else {
            line = "Can't remove \"" + card + "\": there is no such card.";
        }
        System.out.println(line);
        collector.append("\n").append(line);

        log.add(collector.toString());
    }

    public static void exportFlashcards() {
        String line = "File name:";
        StringBuilder collector = new StringBuilder(line);

        System.out.println(line);
        String filename = scanner.nextLine();
        collector.append("\n").append(filename);

        line = writeToFile(filename);

        System.out.println(line);
        collector.append("\n").append(line);
        log.add(collector.toString());
    }

    private static String writeToFile(String filename) {
        try(FileWriter fileWriter = new FileWriter(filename)) {
            for (Map.Entry<String, String> entry: map.entrySet()) {
                fileWriter.append(entry.getKey());
                fileWriter.append('\n');
                fileWriter.append(entry.getValue());
                fileWriter.append('\n');
                fileWriter.append((mistakesMap.getOrDefault(entry.getKey(), 0)).toString());
                fileWriter.append('\n');
            }
            return map.size() + " cards have been saved.";
        } catch (IOException e) {
            return  "File not found.";
        }
    }


    public static void importFlashcards() {
        String line = "File name:";
        StringBuilder collector = new StringBuilder(line);

        System.out.println(line);
        String filename = scanner.nextLine();
        collector.append("\n").append(filename);

        line = readFromFile(filename);

        System.out.println(line);
        collector.append("\n").append(line);
        log.add(collector.toString());

    }

    private static String readFromFile(String filename) {
        File file = new File(filename);
        try(Scanner fileScanner = new Scanner(file)) {
            int n = 0;
            while (fileScanner.hasNextLine()) {
                String card = fileScanner.nextLine();
                String definition = fileScanner.nextLine();
                Integer mistakes = Integer.parseInt(fileScanner.nextLine());
                map.put(card, definition);
                mistakesMap.put(card, mistakes);
                n++;
            }
            return n + " cards have been loaded.";
        } catch (FileNotFoundException e) {
            return "File not found.";
        }
    }

    public static void ask() {
        String line = "How many times to ask?";
        StringBuilder collector = new StringBuilder(line);

        System.out.println(line);
        int n = Integer.parseInt(scanner.nextLine());
        collector.append("\n").append(n);

        while (n > 0) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                line = "Print the definition of \"" + entry.getKey() + "\":";
                System.out.println(line);
                String answer = scanner.nextLine();
                collector.append("\n").append(line).append("\n").append(answer);

                if (map.containsValue(answer)) {
                    if (answer.equals(entry.getValue())) {
                        line = "Correct!";
                    } else {
                        line = "Wrong. The right answer is \"" + entry.getValue() + "\", " +
                                "but your definition is correct for \"" + findCardByAnswer(answer) +
                                "\".";
                        incMistake(entry.getKey());
                    }
                } else {
                    line = "Wrong. The right answer is \"" + entry.getValue() + "\".";
                    incMistake(entry.getKey());
                }

                System.out.println(line);
                collector.append("\n").append(line);
                n--;
                if (n == 0) {
                    break;
                }
            }
        }
        log.add(collector.toString());
    }

    private static String findCardByAnswer(String answer){
        String card = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(answer)) {
                card = entry.getKey();
                break;
            }
        }
        return card;
    }

    private static void incMistake(String card) {
        if (mistakesMap.containsKey(card)) {
            mistakesMap.put(card, mistakesMap.get(card) + 1);
        } else {
            mistakesMap.put(card, 1);
        }
    }

    public static void hardestCard() {
        String line;
        StringBuilder collector = new StringBuilder();

        int max = 0;
        for (Map.Entry<String, Integer> entry : mistakesMap.entrySet()) {
            max = Math.max(max, entry.getValue());
        }

        List<String> cards = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : mistakesMap.entrySet()) {
            if (entry.getValue() == max) {
                cards.add(entry.getKey());
            }
        }

        if (cards.size() == 1) {
            line = "The hardest card is \"" + cards.get(0) + "\". ";
            System.out.print(line);
            collector.append(line);

            line = "You have " + max + " errors answering it.";
            System.out.println(line);
            collector.append("\n").append(line);
        } else if (cards.size() == 0) {
            line = "There are no cards with errors.";
            System.out.println(line);
            collector.append("\n").append(line);
        } else {
            line = "The hardest cards are ";
            System.out.print(line);
            collector.append(line);

            for (int i = 0; i < cards.size(); i++) {
                if (i == cards.size() - 1) {
                    line = "\"" + cards.get(i) + "\". ";
                } else {
                    line = "\"" + cards.get(i) + "\", ";
                }
                System.out.print(line);
                collector.append(line);
            }
            line = "You have " + max + " errors answering it.";
            System.out.println(line);
            collector.append("\n").append(line);
        }

        log.add(collector.toString());
    }

    public static void resetStats() {
        mistakesMap.clear();
        String line = "Card statistics has been reset.\n";
        System.out.print(line);
        log.add(line);
    }

    public static void log() {
        String line = "File name:";
        StringBuilder collector = new StringBuilder(line);
        System.out.println(line);

        String filename = scanner.nextLine();
        collector.append("\n").append(filename);
        log.add(collector.toString());

        try(FileWriter writer = new FileWriter(filename)) {
            for (String logLine : log) {
                writer.append(logLine);
            }
            line = "The log has been saved.";
        } catch (IOException e) {
            line = "File not found.";
        }
        System.out.println(line);
    }

    public static void exit() {
        String line = "\nBye bye!";
        System.out.println(line);
        log.add(line);
        if (exportFileName != null) {
            line = writeToFile(exportFileName);
            System.out.println(line);
            log.add("\n" + line);
        }
    }
}

