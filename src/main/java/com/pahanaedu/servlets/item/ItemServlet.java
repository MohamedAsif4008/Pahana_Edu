package com.pahanaedu.servlets.item;

import com.pahanaedu.models.Item;
import com.pahanaedu.models.User;
import com.pahanaedu.service.interfaces.ItemService;
import com.pahanaedu.service.impl.ItemServiceImpl;
import com.pahanaedu.servlets.common.BaseServlet;
import com.pahanaedu.util.ValidationUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Servlet for Item CRUD operations
 * Demonstrates MVC pattern and basic web functionality
 */
@WebServlet(name = "ItemServlet", urlPatterns = {"/items", "/item"})
public class ItemServlet extends BaseServlet {

    private ItemService itemService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.itemService = new ItemServiceImpl();
        System.out.println("=== ItemServlet initialized ===");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== ItemServlet.doGet() called ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Query String: " + request.getQueryString());
        System.out.println("User logged in: " + isUserLoggedIn(request));

        if (!isUserLoggedIn(request)) {
            System.out.println("User not logged in, redirecting to login");
            redirectTo(response, request.getContextPath() + "/login");
            return;
        }

        String action = getParameter(request, PARAM_ACTION, "list");
        System.out.println("Action: " + action);

        try {
            switch (action) {
                case "list":
                    System.out.println("Executing: showItemList");
                    showItemList(request, response);
                    break;
                case "view":
                    System.out.println("Executing: showItemDetails");
                    showItemDetails(request, response);
                    break;
                case "create":
                    System.out.println("Executing: showCreateForm");
                    showCreateForm(request, response);
                    break;
                case "edit":
                    System.out.println("Executing: showEditForm");
                    showEditForm(request, response);
                    break;
                default:
                    System.out.println("Default action: showItemList");
                    showItemList(request, response);
            }
        } catch (Exception e) {
            System.err.println("=== ERROR in ItemServlet.doGet() ===");
            System.err.println("Action: " + action);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            handleException(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== ItemServlet.doPost() called ===");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("User logged in: " + isUserLoggedIn(request));

        if (!isUserLoggedIn(request)) {
            System.out.println("User not logged in, sending unauthorized response");
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        String action = getParameter(request, PARAM_ACTION, "create");
        System.out.println("Action: " + action);

        try {
            switch (action) {
                case "create":
                    System.out.println("Executing: createItem");
                    createItem(request, response);
                    break;
                case "update":
                    System.out.println("Executing: updateItem");
                    updateItem(request, response);
                    break;
                case "delete":
                    System.out.println("Executing: deleteItem");
                    deleteItem(request, response);
                    break;
                default:
                    System.out.println("Invalid action: " + action);
                    setErrorMessage(request, "Invalid action");
                    showItemList(request, response);
            }
        } catch (Exception e) {
            System.err.println("=== ERROR in ItemServlet.doPost() ===");
            System.err.println("Action: " + action);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            handleException(request, response, e);
        }
    }

    private void showItemList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== showItemList() called ===");
        List<Item> items = itemService.getAllActiveItems();
        System.out.println("Total items found: " + items.size());

        request.setAttribute("items", items);
        request.setAttribute("totalItems", items.size());
        request.setAttribute("csrfToken", generateCSRFToken(request));

        System.out.println("Forwarding to: item/list.jsp");
        forwardToJSP(request, response, "item/list.jsp");
    }

    private void showItemDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== showItemDetails() called ===");
        String itemId = getSanitizedParameter(request, PARAM_ID);
        System.out.println("Item ID: " + itemId);

        if (!ValidationUtils.isValidItemId(itemId)) {
            System.out.println("Invalid item ID, redirecting to list");
            setErrorMessage(request, "Invalid item ID");
            showItemList(request, response);
            return;
        }

        Item item = itemService.findItemById(itemId);
        System.out.println("Item found: " + (item != null));

        if (item == null) {
            System.out.println("Item not found, redirecting to list");
            setErrorMessage(request, "Item not found");
            showItemList(request, response);
            return;
        }

        System.out.println("Item details: " + item.toString());
        request.setAttribute("item", item);
        forwardToJSP(request, response, "item/view.jsp");
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== showCreateForm() called ===");
        String nextItemId = itemService.generateNextItemId();
        System.out.println("Next item ID: " + nextItemId);
        List<String> categories = itemService.getAllCategories();
        System.out.println("Total categories found: " + categories.size());

        request.setAttribute("nextItemId", nextItemId);
        request.setAttribute("categories", categories);
        request.setAttribute("csrfToken", generateCSRFToken(request));

        System.out.println("Forwarding to: item/create.jsp");
        forwardToJSP(request, response, "item/create.jsp");
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== showEditForm() called ===");
        
        String itemId = getSanitizedParameter(request, PARAM_ID);
        System.out.println("Item ID: " + itemId);

        Item item = itemService.findItemById(itemId);
        System.out.println("Item found: " + (item != null));
        
        if (item != null) {
            System.out.println("Item details: " + item.toString());
        }

        if (item == null) {
            System.out.println("Item not found, redirecting to list");
            setErrorMessage(request, "Item not found");
            showItemList(request, response);
            return;
        }

        List<String> categories = itemService.getAllCategories();
        System.out.println("Categories loaded: " + categories.size());
        
        request.setAttribute("item", item);
        request.setAttribute("categories", categories);
        request.setAttribute("csrfToken", generateCSRFToken(request));

        System.out.println("Forwarding to: item/edit.jsp");
        forwardToJSP(request, response, "item/edit.jsp");
    }

    private void createItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== createItem() called ===");
        if (!validateRequiredParams(request, "name", "category", "price")) {
            System.out.println("Validation failed - missing required parameters");
            setErrorMessage(request, "Name, category, and price are required");
            showCreateForm(request, response);
            return;
        }

        try {
            String itemId = getSanitizedParameter(request, "itemId");
            String name = getSanitizedParameter(request, "name");
            String category = getSanitizedParameter(request, "category");
            String priceStr = request.getParameter("price");
            int stockQuantity = getIntParameter(request, "stockQuantity", 0);
            String description = getSanitizedParameter(request, "description");

            BigDecimal price = new BigDecimal(priceStr);

            Item item = new Item(itemId, name, category, price, stockQuantity);
            item.setDescription(description);

            boolean created = itemService.createItem(item);
            System.out.println("Item creation result: " + created);

            if (created) {
                System.out.println("Item created successfully, redirecting");
                setSuccessMessage(request, "Item created successfully");
                logAction(request, "CREATE_ITEM", "Item: " + itemId);
                redirectTo(response, request.getContextPath() + "/items");
            } else {
                System.out.println("Failed to create item");
                setErrorMessage(request, "Failed to create item");
                showCreateForm(request, response);
            }

        } catch (NumberFormatException e) {
            System.err.println("NumberFormatException: " + e.getMessage());
            setErrorMessage(request, "Invalid price format");
            showCreateForm(request, response);
        } catch (Exception e) {
            System.err.println("Unexpected error in createItem: " + e.getMessage());
            e.printStackTrace();
            setErrorMessage(request, "An error occurred while creating the item");
            showCreateForm(request, response);
        }
    }

    private void updateItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== updateItem() called ===");
        
        // Log all request parameters
        System.out.println("Request parameters:");
        request.getParameterMap().forEach((key, values) -> {
            System.out.println("  " + key + ": " + String.join(", ", values));
        });

        if (!validateRequiredParams(request, "itemId", "name", "category", "price")) {
            System.out.println("Validation failed - missing required parameters");
            setErrorMessage(request, "All fields are required");
            showItemList(request, response);
            return;
        }

        try {
            String itemId = getSanitizedParameter(request, "itemId");
            System.out.println("Updating item with ID: " + itemId);
            
            Item item = itemService.findItemById(itemId);
            System.out.println("Original item found: " + (item != null));

            if (item == null) {
                System.out.println("Item not found, redirecting to list");
                setErrorMessage(request, "Item not found");
                showItemList(request, response);
                return;
            }

            // Log original values
            System.out.println("Original item values:");
            System.out.println("  Name: " + item.getName());
            System.out.println("  Category: " + item.getCategory());
            System.out.println("  Price: " + item.getPrice());
            System.out.println("  Stock: " + item.getStockQuantity());
            System.out.println("  Active: " + item.isActive());

            String name = getSanitizedParameter(request, "name");
            String category = getSanitizedParameter(request, "category");
            String priceStr = request.getParameter("price");
            int stockQuantity = getIntParameter(request, "stockQuantity", 0);
            String description = getSanitizedParameter(request, "description");
            boolean isActive = "true".equals(request.getParameter("active"));

            System.out.println("New values:");
            System.out.println("  Name: " + name);
            System.out.println("  Category: " + category);
            System.out.println("  Price: " + priceStr);
            System.out.println("  Stock: " + stockQuantity);
            System.out.println("  Active: " + isActive);

            item.setName(name);
            item.setCategory(category);
            item.setPrice(new BigDecimal(priceStr));
            item.setStockQuantity(stockQuantity);
            item.setDescription(description);
            item.setActive(isActive);

            System.out.println("Calling itemService.updateItem()");
            boolean updated = itemService.updateItem(item);
            System.out.println("Update result: " + updated);

            if (updated) {
                System.out.println("Item updated successfully, redirecting");
                setSuccessMessage(request, "Item updated successfully");
                logAction(request, "UPDATE_ITEM", "Item: " + itemId);
                redirectTo(response, request.getContextPath() + "/items?action=view&id=" + itemId);
            } else {
                System.out.println("Failed to update item");
                setErrorMessage(request, "Failed to update item");
                showItemList(request, response);
            }

        } catch (NumberFormatException e) {
            System.err.println("NumberFormatException: " + e.getMessage());
            setErrorMessage(request, "Invalid price format");
            showItemList(request, response);
        } catch (Exception e) {
            System.err.println("Unexpected error in updateItem: " + e.getMessage());
            e.printStackTrace();
            setErrorMessage(request, "An error occurred while updating the item");
            showItemList(request, response);
        }
    }

    private void deleteItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== deleteItem() called ===");
        
        // Try both parameter names
        String itemId = getSanitizedParameter(request, "itemId");
        if (itemId == null || itemId.trim().isEmpty()) {
            itemId = getSanitizedParameter(request, "id");
        }
        
        System.out.println("Item ID for deletion: " + itemId);

        if (!ValidationUtils.isValidItemId(itemId)) {
            System.out.println("Invalid item ID, redirecting to list");
            setErrorMessage(request, "Invalid item ID");
            showItemList(request, response);
            return;
        }

        // Check permission
        User currentUser = getCurrentUser(request);
        if (!currentUser.hasPermission("ITEM_MANAGEMENT")) {
            setErrorMessage(request, "You don't have permission to delete items");
            showItemList(request, response);
            return;
        }

        boolean deleted = itemService.deactivateItem(itemId);
        System.out.println("Item deactivation result: " + deleted);

        if (deleted) {
            System.out.println("Item deactivated successfully, redirecting");
            setSuccessMessage(request, "Item deactivated successfully");
            logAction(request, "DELETE_ITEM", "Item: " + itemId);
        } else {
            System.out.println("Failed to deactivate item");
            setErrorMessage(request, "Failed to deactivate item");
        }

        redirectTo(response, request.getContextPath() + "/items");
    }

    @Override
    public void destroy() {
        super.destroy();
        this.itemService = null;
        System.out.println("=== ItemServlet destroyed ===");
    }
}