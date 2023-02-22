package exchange.DTO;

public class ExchangeRatesDTO {
    private int id;
    private CurrenciesDTO baseCurrencyId;
    private CurrenciesDTO targetCurrencyId;
    private double rate;

    public ExchangeRatesDTO(int id, CurrenciesDTO baseCurrencyId, CurrenciesDTO targetCurrencyId, double rate) {
        this.id = id;
        this.baseCurrencyId = baseCurrencyId;
        this.targetCurrencyId = targetCurrencyId;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CurrenciesDTO getBaseCurrencyId() {
        return baseCurrencyId;
    }

    public void setBaseCurrencyId(CurrenciesDTO baseCurrencyId) {
        this.baseCurrencyId = baseCurrencyId;
    }

    public CurrenciesDTO getTargetCurrencyId() {
        return targetCurrencyId;
    }

    public void setTargetCurrencyId(CurrenciesDTO targetCurrencyId) {
        this.targetCurrencyId = targetCurrencyId;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
