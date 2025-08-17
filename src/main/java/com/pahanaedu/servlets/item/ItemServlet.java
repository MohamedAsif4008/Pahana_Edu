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
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isUserLoggedIn(request)) {
            redirectTo(response, request.getContextPath() + "/login");
            return;
        }

        String action = getParameter(request, PARAM_ACTION, "list");

        try {
            switch (action) {
                case "list":
                    showItemList(request, response);
                    break;
                case "view":
                    showItemDetails(request, response);
                    break;
                case "create":
                    showCreateForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                default:
                    showItemList(request, response);
            }
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isUserLoggedIn(request)) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        String action = getParameter(request, PARAM_ACTION, "create");

        try {
            switch (action) {
                case "create":
                    createItem(request, response);
                    break;
                case "update":
                    updateItem(request, response);
                    break;
                case "delete":
                    deleteItem(request, response);
                    break;
                default:
                    setErrorMessage(request, "Invalid action");
                    showItemList(request, response);
            }
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    private void showItemList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Item> items = itemService.getAllActiveItems();

        request.setAttribute("items", items);
        request.setAttribute("totalItems", items.size());
        request.setAttribute("csrfToken", generateCSRFToken(request));

        forwardToJSP(request, response, "item/list.jsp");
    }

    private void showItemDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String itemId = getSanitizedParameter(request, PARAM_ID);
        if (!ValidationUtils.isValidItemId(itemId)) {
            setErrorMessage(request, "Invalid item ID");
            showItemList(request, response);
            return;
        }

        Item item = itemService.findItemById(itemId);
        if (item == null) {
            setErrorMessage(request, "Item not found");
            showItemList(request, response);
            return;
        }

        request.setAttribute("item", item);
        forwardToJSP(request, response, "item/view.jsp");
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nextItemId = itemService.generateNextItemId();
        List<String> categories = itemService.getAllCategories();

        request.setAttribute("nextItemId", nextItemId);
        request.setAttribute("categories", categories);
        request.setAttribute("csrfToken", generateCSRFToken(request));

        forwardToJSP(request, response, "item/create.jsp");
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String itemId = getSanitizedParameter(request, PARAM_ID);
        Item item = itemService.findItemById(itemId);

        if (item == null) {
            setErrorMessage(request, "Item not found");
            showItemList(request, response);
            return;
        }

        List<String> categories = itemService.getAllCategories();
        request.setAttribute("item", item);
        request.setAttribute("categories", categories);
        request.setAttribute("csrfToken", generateCSRFToken(request));

        forwardToJSP(request, response, "item/edit.jsp");
    }

    private void createItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!validateRequiredParams(request, "name", "category", "price")) {
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

            if (created) {
                setSuccessMessage(request, "Item created successfully");
                logAction(request, "CREATE_ITEM", "Item: " + itemId);
                redirectTo(response, request.getContextPath() + "/items");
            } else {
                setErrorMessage(request, "Failed to create item");
                showCreateForm(request, response);
            }

        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid price format");
            showCreateForm(request, response);
        }
    }

    private void updateItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!validateRequiredParams(request, "itemId", "name", "category", "price")) {
            setErrorMessage(request, "All fields are required");
            showItemList(request, response);
            return;
        }

        try {
            String itemId = getSanitizedParameter(request, "itemId");
            Item item = itemService.findItemById(itemId);

            if (item == null) {
                setErrorMessage(request, "Item not found");
                showItemList(request, response);
                return;
            }

            String name = getSanitizedParameter(request, "name");
            String category = getSanitizedParameter(request, "category");
            String priceStr = request.getParameter("price");
            int stockQuantity = getIntParameter(request, "stockQuantity", 0);
            String description = getSanitizedParameter(request, "description");
            boolean isActive = "on".equals(request.getParameter("isActive"));

            item.setName(name);
            item.setCategory(category);
            item.setPrice(new BigDecimal(priceStr));
            item.setStockQuantity(stockQuantity);
            item.setDescription(description);
            item.setActive(isActive);

            boolean updated = itemService.updateItem(item);

            if (updated) {
                setSuccessMessage(request, "Item updated successfully");
                logAction(request, "UPDATE_ITEM", "Item: " + itemId);
                redirectTo(response, request.getContextPath() + "/items?action=view&id=" + itemId);
            } else {
                setErrorMessage(request, "Failed to update item");
                showEditForm(request, response);
            }

        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid price format");
            showEditForm(request, response);
        }
    }

    private void deleteItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String itemId = getSanitizedParameter(request, "itemId");
        if (!ValidationUtils.isValidItemId(itemId)) {
            setErrorMessage(request, "Invalid item ID");
            showItemList(request, response);
            return;
        }

        boolean deleted = itemService.deactivateItem(itemId);

        if (deleted) {
            setSuccessMessage(request, "Item deactivated successfully");
            logAction(request, "DELETE_ITEM", "Item: " + itemId);
        } else {
            setErrorMessage(request, "Failed to deactivate item");
        }

        redirectTo(response, request.getContextPath() + "/items");
    }

    @Override
    public void destroy() {
        super.destroy();
        this.itemService = null;
    }
}