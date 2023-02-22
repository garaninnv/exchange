package exchange.AllServlet;

import exchange.ServletService.ActionService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/currencies")
public class GettingListOfCurrenciesServlet extends HttpServlet {
    ActionService actionService = new ActionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        actionService.getListOfCurrencies(resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        actionService.addNewCurrency(req, resp);
    }
}