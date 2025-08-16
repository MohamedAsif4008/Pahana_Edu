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
import java.util.List;

/**
 * Simple Item Search Servlet
 * Demonstrates search functionality and AJAX support
 */
@WebServlet(name = "ItemSearchServlet", urlPatterns = {"/items/search"})
public class ItemSearchServlet extends BaseServlet {

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

        String action = getParameter(request, PARAM_ACTION, "search");

        try {
            switch (action) {
                case "search":
                    performSearch(request, response);
                    break;
                case "ajax":
                    performAjaxSearch(request, response);
                    break;
                default:
                    performSearch(request, response);
            }
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    private void performSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchTerm = getSanitizedParameter(request, "q");
        String searchType = getParameter(request, "type", "name");

        if (!ValidationUtils.isNotEmpty(searchTerm)) {
            setErrorMessage(request, "Please enter a search term");
            redirectTo(response, request.getContextPath() + "/items");
            return;
        }

        List<Item> items = null;

        switch (searchType) {
            case "name":
                items = itemService.searchItemsByName(searchTerm);
                break;
            case "category":
                items = itemService.getItemsByCategory(searchTerm);
                break;
            case "id":
                Item item = itemService.findItemById(searchTerm);
                items = item != null ? List.of(item) : List.of();
                break;
            default:
                items = itemService.searchItemsByName(searchTerm);
        }

        request.setAttribute("items", items);
        request.setAttribute("searchTerm", searchTerm);
        request.setAttribute("searchType", searchType);
        request.setAttribute("resultCount", items.size());

        logAction(request, "ITEM_SEARCH", "Term: " + searchTerm + ", Results: " + items.size());

        forwardToJSP(request, response, "item/item-search-results.jsp");
    }

    private void performAjaxSearch(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String searchTerm = getSanitizedParameter(request, "q");

        if (!ValidationUtils.isNotEmpty(searchTerm) || searchTerm.length() < 2) {
            sendJsonResponse(response, "[]");
            return;
        }

        try {
            List<Item> items = itemService.searchItemsByName(searchTerm);

            // Limit results for AJAX
            if (items.size() > 10) {
                items = items.subList(0, 10);
            }

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                        .append("\"itemId\":\"").append(item.getItemId()).append("\",")
                        .append("\"name\":\"").append(escapeJson(item.getName())).append("\",")
                        .append("\"category\":\"").append(escapeJson(item.getCategory())).append("\",")
                        .append("\"price\":").append(item.getPrice()).append(",")
                        .append("\"stock\":").append(item.getStockQuantity())
                        .append("}");
            }
            json.append("]");

            sendJsonResponse(response, json.toString());

        } catch (Exception e) {
            sendJsonResponse(response, "[]");
        }
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