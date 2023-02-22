package exchange.AllServlet;

import exchange.ServletService.ActionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchangeRate/*")
public class ObtainingSpecificExchangeRateServlet extends HttpServlet {
    ActionService actionService = new ActionService();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        actionService.getSpecificExchangeRate(req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (!method.equals("PATCH")) {
            super.service(req, resp);
        } else {
            this.doPatch(req, resp);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) {
        actionService.updateExistingExchangeRate(req, resp);
    }
}
