package exchange.AllServlet;

import exchange.ServletService.ActionService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/currency/*")
public class GettingCertainCurrencyServlet extends HttpServlet {
    ActionService actionService = new ActionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        actionService.getSpecificCurrency(req, resp);
    }
}