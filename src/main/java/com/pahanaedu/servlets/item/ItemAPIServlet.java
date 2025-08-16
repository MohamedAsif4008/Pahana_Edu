package com.pahanaedu.servlets.item;

import com.pahanaedu.models.Item;
import com.pahanaedu.service.interfaces.ItemService;
import com.pahanaedu.service.impl.ItemServiceImpl;
import com.pahanaedu.servlets.common.BaseServlet;
import com.pahanaedu.util.ValidationUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.util.List;

/**
 * Simple REST API for Item operations
 * Demonstrates RESTful design pattern
 */
@WebServlet(name = "ItemAPIServlet", urlPatterns = {"/api/items", "/api/items/*"})
public class ItemAPIServlet extends BaseServlet {

    private ItemService itemService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.itemService = new ItemServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isUserLoggedIn(request)) {
            sendApiError(response, 401, "Authentication required");
            return;
        }

        String pathInfo = request.getPathInfo();
        String itemId = extractItemId(pathInfo);

        try {
            if (itemId != null) {
                getItem(response, itemId);
            } else {
                getItems(request, response);
            }
        } catch (Exception e) {
            sendApiError(response, 500, "Internal server error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isUserLoggedIn(request)) {
            sendApiError(response, 401, "Authentication required");
            return;
        }

        try {
            String jsonBody = readRequestBody(request);
            Item item = parseItemFromJson(jsonBody);

            if (item == null) {
                sendApiError(response, 400, "Invalid item data");
                return;
            }

            boolean created = itemService.createItem(item);

            if (created) {
                Item createdItem = itemService.findItemById(item.getItemId());
                sendApiSuccess(response, 201, "Item created", itemToJson(createdItem));
            } else {
                sendApiError(response, 400, "Failed to create item");
            }

        } catch (Exception e) {
            sendApiError(response, 500, "Internal server error");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isUserLoggedIn(request)) {
            sendApiError(response, 401, "Authentication required");
            return;
        }

        String pathInfo = request.getPathInfo();
        String itemId = extractItemId(pathInfo);

        if (itemId == null) {
            sendApiError(response, 400, "Item ID required");
            return;
        }

        try {
            Item existingItem = itemService.findItemById(itemId);
            if (existingItem == null) {
                sendApiError(response, 404, "Item not found");
                return;
            }

            String jsonBody = readRequestBody(request);
            Item updateData = parseItemFromJson(jsonBody);

            if (updateData == null) {
                sendApiError(response, 400, "Invalid item data");
                return;
            }

            // Update fields
            existingItem.setName(updateData.getName());
            existingItem.setCategory(updateData.getCategory());
            existingItem.setPrice(updateData.getPrice());
            existingItem.setStockQuantity(updateData.getStockQuantity());
            existingItem.setDescription(updateData.getDescription());

            boolean updated = itemService.updateItem(existingItem);

            if (updated) {
                Item updatedItem = itemService.findItemById(itemId);
                sendApiSuccess(response, 200, "Item updated", itemToJson(updatedItem));
            } else {
                sendApiError(response, 400, "Failed to update item");
            }

        } catch (Exception e) {
            sendApiError(response, 500, "Internal server error");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isUserLoggedIn(request)) {
            sendApiError(response, 401, "Authentication required");
            return;
        }

        String pathInfo = request.getPathInfo();
        String itemId = extractItemId(pathInfo);

        if (itemId == null) {
            sendApiError(response, 400, "Item ID required");
            return;
        }

        try {
            boolean deleted = itemService.deactivateItem(itemId);

            if (deleted) {
                sendApiSuccess(response, 200, "Item deleted", null);
            } else {
                sendApiError(response, 400, "Failed to delete item");
            }

        } catch (Exception e) {
            sendApiError(response, 500, "Internal server error");
        }
    }

    private void getItem(HttpServletResponse response, String itemId) throws IOException {
        Item item = itemService.findItemById(itemId);

        if (item != null) {
            sendApiSuccess(response, 200, "Item found", itemToJson(item));
        } else {
            sendApiError(response, 404, "Item not found");
        }
    }

    private void getItems(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String search = getSanitizedParameter(request, "search");
        List<Item> items;

        if (ValidationUtils.isNotEmpty(search)) {
            items = itemService.searchItemsByName(search);
        } else {
            items = itemService.getAllActiveItems();
        }

        String itemsJson = itemsToJson(items);
        sendApiSuccess(response, 200, "Items retrieved", itemsJson);
    }

    private String extractItemId(String pathInfo) {
        if (pathInfo == null || pathInfo.length() <= 1) {
            return null;
        }
        String path = pathInfo.substring(1);
        return ValidationUtils.isValidItemId(path) ? path : null;
    }

    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }
        return body.toString();
    }

    private Item parseItemFromJson(String json) {
        try {
            if (!ValidationUtils.isNotEmpty(json)) return null;

            Item item = new Item();

            String itemId = extractJsonValue(json, "itemId");
            String name = extractJsonValue(json, "name");
            String category = extractJsonValue(json, "category");
            String priceStr = extractJsonValue(json, "price");
            String stockStr = extractJsonValue(json, "stock");
            String description = extractJsonValue(json, "description");

            if (!ValidationUtils.isNotEmpty(name)) return null;

            item.setItemId(itemId);
            item.setName(name);
            item.setCategory(category);
            item.setDescription(description);

            if (ValidationUtils.isNotEmpty(priceStr)) {
                item.setPrice(new BigDecimal(priceStr));
            }

            if (ValidationUtils.isNotEmpty(stockStr)) {
                item.setStockQuantity(Integer.parseInt(stockStr));
            }

            return item;

        } catch (Exception e) {
            return null;
        }
    }

    private String extractJsonValue(String json, String key) {
        try {
            String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]*)\"|\"" + key + "\"\\s*:\\s*([^,}\\s]+)";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            return m.find() ? (m.group(1) != null ? m.group(1) : m.group(2)) : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String itemToJson(Item item) {
        if (item == null) return "null";
        return String.format(
                "{\"itemId\":\"%s\",\"name\":\"%s\",\"category\":\"%s\",\"price\":%s,\"stock\":%d,\"description\":\"%s\",\"isActive\":%s}",
                item.getItemId(), escapeJson(item.getName()), escapeJson(item.getCategory()),
                item.getPrice(), item.getStockQuantity(), escapeJson(item.getDescription()), item.isActive()
        );
    }

    private String itemsToJson(List<Item> items) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) json.append(",");
            json.append(itemToJson(items.get(i)));
        }
        json.append("]");
        return json.toString();
    }

    private void sendApiSuccess(HttpServletResponse response, int statusCode, String message, String data) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        StringBuilder json = new StringBuilder();
        json.append("{\"success\":true,\"message\":\"").append(message).append("\"");
        if (data != null) {
            json.append(",\"data\":").append(data);
        }
        json.append("}");
        response.getWriter().write(json.toString());
    }

    private void sendApiError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        String json = String.format("{\"success\":false,\"error\":\"%s\",\"code\":%d}", message, statusCode);
        response.getWriter().write(json);
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    @Override
    public void destroy() {
        super.destroy();
        this.itemService = null;
    }
}