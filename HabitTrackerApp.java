import java.io.*;
import java.util.*;

class Habit {
    private String name;
    private String description;
    private String frequency;
    private List<Boolean> completionStatus;
    private int streak;
    
    public Habit(String name, String description, String frequency) {
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.completionStatus = new ArrayList<>();
        this.streak = 0;
    }

    public void markCompletion(boolean completed) {
        completionStatus.add(completed);
        if (completed) {
            streak++;
        } else {
            streak = 0;
        }
    }

    public double calculateStrength() {
        int completedDays = 0;
        for (boolean status : completionStatus) {
            if (status) completedDays++;
        }
        return completionStatus.isEmpty() ? 0 : (completedDays * 100.0) / completionStatus.size();
    }

    public String getFeedback() {
        double strength = calculateStrength();
        if (strength >= 80) return "Great job! You're consistently keeping up with your habit.";
        else if (strength >= 50) return "Good job! You're making progress, but there's room for improvement.";
        else return "Keep going! Try to be more consistent in completing your habit.";
    }

    public String getName() { return name; }
    public int getStreak() { return streak; }
    public double getStrength() { return calculateStrength(); }

    public String toFileString() {
        return name + "," + description + "," + frequency + "," + getStrength() + "," + streak;
    }

    public static Habit fromFileString(String line) {
        String[] parts = line.split(",");
        Habit habit = new Habit(parts[0], parts[1], parts[2]);
        return habit;
    }

    public void displaySummary() {
        System.out.println("Habit: " + name);
        System.out.println("Description: " + description);
        System.out.println("Frequency: " + frequency);
        System.out.printf("Strength: %.2f%%\n", calculateStrength());
        System.out.println("Streak: " + streak + " days");
        System.out.println("Feedback: " + getFeedback());
        System.out.println("---------------------------");
    }
}

public class HabitTrackerApp {
    private static final String FILE_NAME = "habits.txt";
    private static List<Habit> habits = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadHabits();
        while (true) {
            System.out.println("\n📋 Habit Strength Tracker");
            System.out.println("1. Create New Habit");
            System.out.println("2. Mark Habit Completion");
            System.out.println("3. View Habit Summary");
            System.out.println("4. Save & Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> createHabit();
                case 2 -> markCompletion();
                case 3 -> viewSummary();
                case 4 -> {
                    saveHabits();
                    System.out.println("✅ Data saved. Goodbye!");
                    return;
                }
                default -> System.out.println("❌ Invalid choice. Try again.");
            }
        }
    }

    private static void createHabit() {
        System.out.print("Enter habit name: ");
        String name = scanner.nextLine();
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        System.out.print("Enter frequency (Daily/Weekly): ");
        String frequency = scanner.nextLine();
        habits.add(new Habit(name, description, frequency));
        System.out.println("✅ Habit created successfully.");
    }

    private static void markCompletion() {
        if (habits.isEmpty()) {
            System.out.println("⚠️ No habits found.");
            return;
        }
        for (int i = 0; i < habits.size(); i++) {
            System.out.println((i + 1) + ". " + habits.get(i).getName());
        }
        System.out.print("Select habit to mark completion: ");
        int index = scanner.nextInt() - 1;
        scanner.nextLine(); // consume newline
        if (index >= 0 && index < habits.size()) {
            System.out.print("Did you complete the habit today? (yes/no): ");
            String input = scanner.nextLine().toLowerCase();
            habits.get(index).markCompletion(input.equals("yes"));
            System.out.println("✅ Completion recorded.");
        } else {
            System.out.println("❌ Invalid habit selection.");
        }
    }

    private static void viewSummary() {
        if (habits.isEmpty()) {
            System.out.println("⚠️ No habits to display.");
            return;
        }
        for (Habit habit : habits) {
            habit.displaySummary();
        }
    }

    private static void saveHabits() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Habit habit : habits) {
                writer.write(habit.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("❌ Error saving habits: " + e.getMessage());
        }
    }

    private static void loadHabits() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                habits.add(Habit.fromFileString(line));
            }
        } catch (IOException e) {
            System.out.println("❌ Error loading habits: " + e.getMessage());
        }
    }
}
