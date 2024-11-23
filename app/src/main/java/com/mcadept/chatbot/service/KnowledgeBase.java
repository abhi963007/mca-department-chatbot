package com.mcadept.chatbot.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class KnowledgeBase {
    private static final Map<String, String> knowledgeMap = new HashMap<>();

    static {
        initializeKnowledgeBase();
    }

    private static void initializeKnowledgeBase() {
        // Course Information
        knowledgeMap.put("course", "MCA (Master of Computer Applications) is a 2-year professional postgraduate program focusing on computer applications and software development.");
        knowledgeMap.put("duration", "The MCA program duration is 2 years, divided into 4 semesters.");
        knowledgeMap.put("syllabus", "The MCA syllabus includes subjects like Advanced Java, Python, Data Structures, Database Management, Web Technologies, and Software Engineering.");
        
        // Admission
        knowledgeMap.put("eligibility", "To be eligible for MCA, candidates must have:\n- Bachelor's degree with Mathematics\n- Minimum 50% marks in graduation\n- Valid entrance test score");
        knowledgeMap.put("admission", "The admission process includes:\n1. Online application\n2. Entrance test\n3. Document verification\n4. Merit list declaration\n5. Counseling");
        
        // Faculty
        knowledgeMap.put("faculty", "Our department has experienced faculty members specializing in various domains of Computer Science and Applications.");
        
        // Facilities
        knowledgeMap.put("facilities", "The department provides:\n- Modern computer labs\n- High-speed internet\n- Digital library\n- Research facilities\n- Seminar hall");
        knowledgeMap.put("labs", "We have multiple computer labs equipped with latest hardware and software for practical learning.");
        
        // Placements
        knowledgeMap.put("placements", "Our department has excellent placement records with students placed in top IT companies. We have dedicated placement cell to assist students.");
        knowledgeMap.put("companies", "Our students are placed in companies like:\n- TCS\n- Infosys\n- Wipro\n- Tech Mahindra\n- And many more");
        
        // Research
        knowledgeMap.put("research", "The department actively conducts research in areas like:\n- Artificial Intelligence\n- Machine Learning\n- Cloud Computing\n- Cybersecurity");
        
        // Contact
        knowledgeMap.put("contact", "You can contact the department at:\nEmail: mca@department.edu\nPhone: +91-XXXXXXXXXX\nAddress: Department of MCA, College Campus");
    }

    @NonNull
    public static String getResponse(@Nullable String query) {
        if (query == null || query.trim().isEmpty()) {
            return getDefaultResponse();
        }
        
        String normalizedQuery = query.toLowerCase().trim();
        
        // Handle greetings
        if (containsAny(normalizedQuery, "hi", "hello", "hey")) {
            return "Hello! How can I help you with information about the MCA department?";
        }
        
        if (containsAny(normalizedQuery, "thank", "thanks")) {
            return "You're welcome! Feel free to ask any other questions about the MCA department.";
        }
        
        // Check for keywords in the knowledge base
        for (Map.Entry<String, String> entry : knowledgeMap.entrySet()) {
            if (normalizedQuery.contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }
        
        // Handle unknown queries
        return getDefaultResponse();
    }
    
    private static boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    @NonNull
    private static String getDefaultResponse() {
        return "I'm not sure about that. You can ask me about:\n" +
               "- Course information and syllabus\n" +
               "- Admission process and eligibility\n" +
               "- Faculty and facilities\n" +
               "- Placements and companies\n" +
               "- Research activities\n" +
               "- Contact information";
    }
}
