package Nathanael.expense_tracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class ApiCategoryController {

    @GetMapping
    public Map<String, String> getCategories() {
        Map<String, String> icons = new LinkedHashMap<>();
        icons.put("Food & Groceries", "🛒");
        icons.put("Utilities", "🔧");
        icons.put("Shopping", "🛍️");
        icons.put("Housing", "🏠");
        icons.put("Transportation", "🚌");
        icons.put("Healthcare", "🏥");
        icons.put("Entertainment", "🎬");
        icons.put("Career Development", "📈");
        icons.put("Travel", "✈️");
        icons.put("Charity & Gifts", "🎁");
        icons.put("Subscription", "🔁");
        icons.put("Personal Care", "💇");
        icons.put("Pet", "🐾");
        icons.put("Household Supplies", "🧴");
        icons.put("Other", "📦");
        return icons;
    }
}