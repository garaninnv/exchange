package exchange.ServletService;

import com.fasterxml.jackson.databind.ObjectMapper;
import exchange.ConnectionDB;
import exchange.DTO.CurrenciesDTO;
import exchange.DTO.ExchangeCalculationDTO;
import exchange.DTO.ExchangeRatesDTO;
import exchange.DTO.MessageDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ActionService {
    private Statement statement;
    private ObjectMapper objectMapper = new ObjectMapper();
    private PrintWriter out;

    //получить список валют
    public void getListOfCurrencies(HttpServletResponse resp) {
        ConnectionDB connectionDB = new ConnectionDB();
        String query = "select * from Currencies";

        try {
            statement = connectionDB.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            List<CurrenciesDTO> currenciesDTOList = new ArrayList<>();
            while (resultSet.next()) {
                currenciesDTOList.add(new CurrenciesDTO(resultSet.getInt(1), resultSet.getString(2)
                        , resultSet.getString(3), resultSet.getString(4)));
            }
            resp.setContentType("application/json; charset=UTF-8");
            out = resp.getWriter();
            String out1 = objectMapper.writeValueAsString(currenciesDTOList);
            out.println(out1);
            statement.close();
            resultSet.close();
            connectionDB.closeDB();
        } catch (SQLException | IOException e) {
            resp.setStatus(500);
            showError(resp, "База данных не доступна");
        }
    }

    // получить конкретную валюту
    public void getSpecificCurrency(HttpServletRequest req, HttpServletResponse resp) {
        ConnectionDB connectionDB = new ConnectionDB();
        String url = req.getRequestURI();
        String[] str = url.split("/");
        String query = "select * from Currencies where Code='" + str[str.length - 1].toUpperCase() + "'";
        if (str[str.length - 2].equals("currency") && str[str.length - 1].length() != 3 || str[str.length - 1].equals("currency")) {
            resp.setStatus(400);
            showError(resp, "Код валюты отсутствует в адресе");
        } else {
            try {
                statement = connectionDB.getConnection().createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                String out1 = null;
                if (resultSet.next()) {
                    resp.setContentType("application/json; charset=UTF-8");
                    out = resp.getWriter();
                    out1 = objectMapper.writeValueAsString(new CurrenciesDTO(resultSet.getInt(1), resultSet.getString(2)
                            , resultSet.getString(3), resultSet.getString(4)));
                    out.println(out1);
                }
                if (out1 == null) {
                    resp.setStatus(404);
                    showError(resp, "Валюта не найдена");
                }
                statement.close();
                resultSet.close();
                connectionDB.closeDB();
            } catch (SQLException | IOException e) {
                resp.setStatus(500);
                showError(resp, "База данных не доступна");
            }
        }
    }

    //добавить новую валюту
    public void addNewCurrency(HttpServletRequest req, HttpServletResponse resp) {
        ConnectionDB connectionDB = new ConnectionDB();
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");
        String queryInsert = "INSERT INTO Currencies (Code, FullName, Sign) VALUES ('"
                + code + "', '" + name + "', '" + sign + "')";
        String querySelect = "select * from Currencies where Code='" + code + "'";
        if ("".equals(name) || "".equals(code) || "".equals(sign) || name == null || code == null || sign == null) {
            resp.setStatus(400);
            showError(resp, "Отсутствует нужное поле формы");
        } else if (findCurrencyIdByCode(code) != 0) {
            resp.setStatus(409);
            showError(resp, "Валюта с таким кодом уже существует");
        } else {
            try {
                statement = connectionDB.getConnection().createStatement();
                statement.executeUpdate(queryInsert);
                ResultSet resultSet = statement.executeQuery(querySelect);
                if (resultSet.next()) {
                    resp.setContentType("application/json; charset=UTF-8");
                    out = resp.getWriter();
                    String out1 = objectMapper.writeValueAsString(
                            new CurrenciesDTO(resultSet.getInt(1), resultSet.getString(2)
                                    , resultSet.getString(3), resultSet.getString(4)));
                    out.println(out1);
                }
                statement.close();
                resultSet.close();
                connectionDB.closeDB();
            } catch (SQLException | IOException e) {
                resp.setStatus(500);
                showError(resp, "База данных не доступна");
            }
        }
    }

    //Получить списка всех обменных курсов
    public void getListOfAllExchangeRates(HttpServletResponse resp) {
        ConnectionDB connectionDB = new ConnectionDB();
        String query = "select * from ExchangeRates";

        try {
            statement = connectionDB.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            List<ExchangeRatesDTO> exchangeRatesDTOList = new ArrayList<>();
            while (resultSet.next()) {
                exchangeRatesDTOList.add(new ExchangeRatesDTO(resultSet.getInt(1)
                        , findCurrencyById(resultSet.getInt(2))
                        , findCurrencyById(resultSet.getInt(3)), resultSet.getDouble(4)));
            }
            resp.setContentType("application/json; charset=UTF-8");
            out = resp.getWriter();
            String out1 = objectMapper.writeValueAsString(exchangeRatesDTOList);
            out.println(out1);
            statement.close();
            resultSet.close();
            connectionDB.closeDB();
        } catch (SQLException | IOException e) {
            resp.setStatus(500);
            showError(resp, "База данных не доступна");
        }
    }

    //получить конкретный обменный курс
    public void getSpecificExchangeRate(HttpServletRequest req, HttpServletResponse resp) {
        ConnectionDB connectionDB = new ConnectionDB();
        String url = req.getRequestURI();
        String[] str = url.split("/");
        String baseCurrency = str[str.length - 1].substring(0, 3);
        String targetCurrency = str[str.length - 1].substring(3);
        String query = "select * from ExchangeRates where (BaseCurrencyId='"
                + findCurrencyIdByCode(baseCurrency.toUpperCase()) + "'"
                + " AND TargetCurrencyId='"
                + findCurrencyIdByCode(targetCurrency.toUpperCase()) + "')";
        try {
            statement = connectionDB.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            String out1 = null;
            if (resultSet.next()) {
                resp.setContentType("application/json; charset=UTF-8");
                resp.setStatus(200);
                out = resp.getWriter();
                out1 = objectMapper.writeValueAsString(new ExchangeRatesDTO(resultSet.getInt(1)
                        , findCurrencyById(resultSet.getInt(2))
                        , findCurrencyById(resultSet.getInt(3)), resultSet.getDouble(4)));
                out.println(out1);
            }
            if (("exchangeRate").equals(str[str.length - 2]) && str[str.length - 1].length() != 6 || ("exchangeRate").equals(str[str.length - 1])) {
                resp.setStatus(400);
                showError(resp, "Коды валют пары отсутствуют в адресе");
            } else if (out1 == null) {
                resp.setStatus(404);
                showError(resp, "Обменный курс для пары не найден");
            }
            statement.close();
            resultSet.close();
            connectionDB.closeDB();
        } catch (SQLException | IOException e) {
            resp.setStatus(500);
            showError(resp, "База данных не доступна");
        }
    }

    //добавить новый обменный курс
    public void addNewExchangeRate(HttpServletRequest req, HttpServletResponse resp) {
        ConnectionDB connectionDB = new ConnectionDB();
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        if ("".equals(baseCurrencyCode) || "".equals(targetCurrencyCode) || "".equals(req.getParameter("rate"))
                || baseCurrencyCode == null || targetCurrencyCode == null || req.getParameter("rate") == null) {
            resp.setStatus(400);
            showError(resp, "Отсутствует нужное поле формы");
        } else if (findExchangeRateByCode(findCurrencyIdByCode(baseCurrencyCode), findCurrencyIdByCode(targetCurrencyCode))) {
            resp.setStatus(409);
            showError(resp, "ОВалютная пара с таким кодом уже существует");
        } else {
            double rate = Double.parseDouble(req.getParameter("rate"));
            String queryInsert = "INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES ("
                    + findCurrencyIdByCode(baseCurrencyCode) + ", "
                    + findCurrencyIdByCode(targetCurrencyCode) + ", " + rate + ")";
            String querySelect = "select * from ExchangeRates WHERE (BaseCurrencyId ="
                    + findCurrencyIdByCode(baseCurrencyCode) + " AND TargetCurrencyId="
                    + findCurrencyIdByCode(targetCurrencyCode) + ")";
            try {
                statement = connectionDB.getConnection().createStatement();
                statement.executeUpdate(queryInsert);
                ResultSet resultSet = statement.executeQuery(querySelect);
                if (resultSet.next()) {
                    resp.setContentType("application/json; charset=UTF-8");
                    out = resp.getWriter();
                    String out1 = objectMapper.writeValueAsString(
                            new ExchangeRatesDTO(resultSet.getInt(1), findCurrencyById(resultSet.getInt(2))
                                    , findCurrencyById(resultSet.getInt(3)), resultSet.getDouble(4)));
                    out.println(out1);
                }
                statement.close();
                resultSet.close();
                connectionDB.closeDB();
            } catch (SQLException | IOException e) {
                resp.setStatus(500);
                showError(resp, "База данных не доступна");
            }
        }
    }

    //обновить существующий обменный курс
    public void updateExistingExchangeRate(HttpServletRequest req, HttpServletResponse resp) {
        ConnectionDB connectionDB = new ConnectionDB();
        String url = req.getRequestURI();
        String[] str = url.split("/");
        String baseCurrency = str[str.length - 1].substring(0, 3);
        String targetCurrency = str[str.length - 1].substring(3);
        String stringRate = getParameter(req);
        if ("".equals(stringRate) || stringRate == null) {
            resp.setStatus(400);
            showError(resp, "Отсутствует нужное поле формы");
        } else if (!findExchangeRateByCode(findCurrencyIdByCode(baseCurrency), findCurrencyIdByCode(targetCurrency))) {
            resp.setStatus(404);
            showError(resp, "Валютная пара отсутствует в базе данных");
        } else {
            double rate = Double.parseDouble(stringRate);
            String queryUpdate = "UPDATE ExchangeRates SET Rate = " + rate + " WHERE (BaseCurrencyId="
                    + findCurrencyIdByCode(baseCurrency) + " AND TargetCurrencyId="
                    + findCurrencyIdByCode(targetCurrency) + ")";
            String querySelect = "select * from ExchangeRates WHERE (BaseCurrencyId ="
                    + findCurrencyIdByCode(baseCurrency) + " AND TargetCurrencyId="
                    + findCurrencyIdByCode(targetCurrency) + ")";
            try {
                statement = connectionDB.getConnection().createStatement();
                statement.executeUpdate(queryUpdate);
                ResultSet resultSet = statement.executeQuery(querySelect);
                if (resultSet.next()) {
                    resp.setContentType("application/json; charset=UTF-8");
                    out = resp.getWriter();
                    String out1 = objectMapper.writeValueAsString(
                            new ExchangeRatesDTO(resultSet.getInt(1), findCurrencyById(resultSet.getInt(2))
                                    , findCurrencyById(resultSet.getInt(3)), resultSet.getDouble(4)));
                    out.println(out1);
                }
                statement.close();
                resultSet.close();
                connectionDB.closeDB();
            } catch (SQLException | IOException e) {
                resp.setStatus(500);
                showError(resp, "База данных не доступна");
            }
        }
    }

    //сделать обмен валют
    public void makeCurrencyExchange(HttpServletRequest req, HttpServletResponse resp) {
        ConnectionDB connectionDB = new ConnectionDB();
        String BASE_CURRENCY_CODE = req.getParameter("from");
        String TARGET_CURRENCY_CODE = req.getParameter("to");
        double $AMOUNT = Double.parseDouble(req.getParameter("amount"));
        String query = "select * from ExchangeRates where (BaseCurrencyId="
                + findCurrencyIdByCode(BASE_CURRENCY_CODE.toUpperCase())
                + " AND TargetCurrencyId="
                + findCurrencyIdByCode(TARGET_CURRENCY_CODE.toUpperCase()) + ")";
        try {
            statement = connectionDB.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            out = resp.getWriter();
            //проверяем прямой курс
            if (resultSet.next()) {
                resp.setContentType("application/json; charset=UTF-8");
                ExchangeCalculationDTO ob = new ExchangeCalculationDTO(findCurrencyById(resultSet.getInt(2)),
                        findCurrencyById(resultSet.getInt(3)), resultSet.getDouble(4), $AMOUNT);
                String out1 = objectMapper.writeValueAsString(ob);
                out.println(out1);
            } else {
                //формируем запрос и проверяем обратный курс
                query = "select * from ExchangeRates where (BaseCurrencyId="
                        + findCurrencyIdByCode(TARGET_CURRENCY_CODE.toUpperCase())
                        + " AND TargetCurrencyId="
                        + findCurrencyIdByCode(BASE_CURRENCY_CODE.toUpperCase()) + ")";
                resultSet = statement.executeQuery(query);
                if (resultSet.next()) {
                    resp.setContentType("application/json; charset=UTF-8");
                    ExchangeCalculationDTO ob = new ExchangeCalculationDTO(findCurrencyById(resultSet.getInt(3)),
                            findCurrencyById(resultSet.getInt(2)), 1 / resultSet.getDouble(4), $AMOUNT);
                    String out1 = objectMapper.writeValueAsString(ob);
                    out.println(out1);
                } else {
                    //формируем запросы и проверяем курс через USD
                    Statement statement1 = connectionDB.getConnection().createStatement();
                    double courceUSDA = 0;
                    double courceUSDB = 0;
                    String queryUSDA = "select * from ExchangeRates where (BaseCurrencyId=" + findCurrencyIdByCode("USD")
                            + " AND TargetCurrencyId="
                            + findCurrencyIdByCode(BASE_CURRENCY_CODE.toUpperCase()) + ")";
                    resultSet = statement1.executeQuery(queryUSDA);
                    if (resultSet.next()) {
                        courceUSDA = Double.parseDouble(resultSet.getString(4));
                    }
                    String queryUSDB = "select * from ExchangeRates where (BaseCurrencyId=" + findCurrencyIdByCode("USD")
                            + " AND TargetCurrencyId="
                            + findCurrencyIdByCode(TARGET_CURRENCY_CODE.toUpperCase()) + ")";
                    resultSet = statement1.executeQuery(queryUSDB);
                    if (resultSet.next()) {
                        courceUSDB = Double.parseDouble(resultSet.getString(4));
                    }
                    resp.setContentType("application/json; charset=UTF-8");
                    if (courceUSDB != 0 && courceUSDA != 0) {
                        ExchangeCalculationDTO ob = new ExchangeCalculationDTO(findCurrencyByCode(BASE_CURRENCY_CODE),
                                findCurrencyByCode(TARGET_CURRENCY_CODE), courceUSDB / courceUSDA, $AMOUNT);
                        String out1 = objectMapper.writeValueAsString(ob);
                        out.println(out1);
                    } else {
                        resp.setStatus(404);
                        out.println("Нет курсов для расчета");
                    }
                }
            }
            statement.close();
            resultSet.close();
            connectionDB.closeDB();
        } catch (SQLException | IOException e) {
            resp.setStatus(500);
            showError(resp, "База данных не доступна");
        }
    }

    private CurrenciesDTO findCurrencyById(int id) {
        ConnectionDB connectionDB = new ConnectionDB();
        String query = "select * from Currencies where id='" + id + "'";
        CurrenciesDTO out;
        try {
            Statement statement = connectionDB.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            out = new CurrenciesDTO(resultSet.getInt(1), resultSet.getString(2)
                    , resultSet.getString(3), resultSet.getString(4));
            statement.close();
            resultSet.close();
            connectionDB.closeDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    private int findCurrencyIdByCode(String code) {
        ConnectionDB connectionDB = new ConnectionDB();
        String query = "select * from Currencies where Code='" + code + "'";
        int out;
        try {
            Statement statement = connectionDB.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            out = resultSet.getInt(1);
            statement.close();
            resultSet.close();
            connectionDB.closeDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    private boolean findExchangeRateByCode(int baseCurrencyCode, int targetCurrencyCode) {
        ConnectionDB connectionDB = new ConnectionDB();
        String query = "select * from ExchangeRates where (BaseCurrencyId=" + baseCurrencyCode + " AND" +
                " TargetCurrencyId=" + targetCurrencyCode + ")";
        boolean out;
        try {
            Statement statement = connectionDB.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            out = resultSet.next();
            statement.close();
            resultSet.close();
            connectionDB.closeDB();
            return out;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private CurrenciesDTO findCurrencyByCode(String code) {
        ConnectionDB connectionDB = new ConnectionDB();
        String query = "select * from Currencies where Code='" + code + "'";
        CurrenciesDTO out;
        try {
            Statement statement = connectionDB.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            resultSet.next();
            out = new CurrenciesDTO(resultSet.getInt(1), resultSet.getString(2)
                    , resultSet.getString(3), resultSet.getString(4));
            statement.close();
            resultSet.close();
            connectionDB.closeDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    public static String getParameter(HttpServletRequest request) {
        BufferedReader br;
        String[] par;

        InputStreamReader reader;
        try {
            reader = new InputStreamReader(
                    request.getInputStream());
            br = new BufferedReader(reader);
            String data = br.readLine();
            par = data.split("=");
            if (par.length == 1) {
                return "";
            } else {
                return par[1];
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showError(HttpServletResponse resp, String message) {
        try {
            resp.setContentType("application/json; charset=UTF-8");
            out = resp.getWriter();
            out.println(objectMapper.writeValueAsString(new MessageDTO(message)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}