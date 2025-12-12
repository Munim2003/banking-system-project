package group_5.banking_system_application.Ui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Service class to manage banking suggestions/tips.
 * Loads suggestions from banking_suggestions.txt file.
 * Can be extended to fetch from AI or database in the future.
 */
public class BankingSuggestionService {
    
    private static final List<String> BANKING_SUGGESTIONS = new ArrayList<>();
    private static final Random random = new Random();
    private static boolean loaded = false;
    
    static {
        loadSuggestionsFromFile();
    }
    
    /**
     * Loads banking suggestions from the banking_suggestions.txt file.
     */
    private static void loadSuggestionsFromFile() {
        if (loaded) {
            return;
        }
        
        try {
            InputStream inputStream = BankingSuggestionService.class
                .getResourceAsStream("/group_5/banking_system_application/banking_suggestions.txt");
            
            if (inputStream == null) {
                System.err.println("Warning: banking_suggestions.txt not found, using default suggestions");
                addDefaultSuggestions();
                loaded = true;
                return;
            }
            
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        BANKING_SUGGESTIONS.add(line);
                    }
                }
            }
            
            if (BANKING_SUGGESTIONS.isEmpty()) {
                System.err.println("Warning: No suggestions loaded from file, using defaults");
                addDefaultSuggestions();
            }
            
            loaded = true;
            System.out.println("Loaded " + BANKING_SUGGESTIONS.size() + " banking suggestions from file");
            
        } catch (Exception e) {
            System.err.println("Error loading banking suggestions: " + e.getMessage());
            e.printStackTrace();
            addDefaultSuggestions();
            loaded = true;
        }
    }
    
    /**
     * Adds default suggestions if file loading fails.
     */
    private static void addDefaultSuggestions() {
        BANKING_SUGGESTIONS.add("💡 Tip: Set up automatic savings transfers to build your emergency fund");
        BANKING_SUGGESTIONS.add("🔒 Security: Enable two-factor authentication for enhanced account protection");
        BANKING_SUGGESTIONS.add("📊 Insight: Review your monthly spending patterns to identify savings opportunities");
        BANKING_SUGGESTIONS.add("💰 Smart Move: Consider investing in a high-yield savings account for better returns");
        BANKING_SUGGESTIONS.add("🎯 Goal: Create a budget to track your expenses and achieve financial goals");
    }
    
    /**
     * Gets a random banking suggestion from the pre-generated list.
     * @return A random banking suggestion string
     */
    public static String getRandomSuggestion() {
        if (BANKING_SUGGESTIONS.isEmpty()) {
            return "Welcome to Vaultiq Banking!";
        }
        return BANKING_SUGGESTIONS.get(random.nextInt(BANKING_SUGGESTIONS.size()));
    }
    
    /**
     * Gets all suggestions in a shuffled order.
     * Useful for creating a continuous ticker.
     * @return List of all suggestions in random order
     */
    public static List<String> getShuffledSuggestions() {
        List<String> shuffled = new ArrayList<>(BANKING_SUGGESTIONS);
        Collections.shuffle(shuffled, random);
        return shuffled;
    }
    
    /**
     * Gets a formatted ticker text with multiple suggestions.
     * @param count Number of suggestions to include
     * @return Formatted string with suggestions separated by separators
     */
    public static String getTickerText(int count) {
        List<String> suggestions = getShuffledSuggestions();
        StringBuilder ticker = new StringBuilder();
        
        int actualCount = Math.min(count, suggestions.size());
        for (int i = 0; i < actualCount; i++) {
            if (i > 0) {
                ticker.append("  •  ");
            }
            ticker.append(suggestions.get(i));
        }
        
        return ticker.toString();
    }
    
    /**
     * Gets a continuous ticker text that loops.
     * This creates a longer string for smooth scrolling animation.
     * @return Long formatted string with multiple suggestions
     */
    public static String getContinuousTickerText() {
        List<String> suggestions = getShuffledSuggestions();
        StringBuilder ticker = new StringBuilder();
        
        // Repeat suggestions 3 times for continuous loop effect
        for (int repeat = 0; repeat < 3; repeat++) {
            for (String suggestion : suggestions) {
                if (ticker.length() > 0) {
                    ticker.append("  •  ");
                }
                ticker.append(suggestion);
            }
        }
        
        return ticker.toString();
    }
}

