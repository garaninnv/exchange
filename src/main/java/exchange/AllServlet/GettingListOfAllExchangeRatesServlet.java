package exchange.AllServlet;

import exchange.ServletService.ActionService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/exchangeRates")
public class GettingListOfAllExchangeRatesServlet extends HttpServlet {
    ActionService actionService = new ActionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        actionService.getListOfAllExchangeRates(resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        actionService.addNewExchangeRate(req, resp);
    }
}
