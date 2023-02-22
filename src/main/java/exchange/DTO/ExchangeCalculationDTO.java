package exchange.DTO;

public class ExchangeCalculationDTO {
    private CurrenciesDTO baseCurrency;
    private CurrenciesDTO targetCurrency;
    private double rate;
    private double amount;
    private double convertedAmount;

    public ExchangeCalculationDTO(CurrenciesDTO baseCurrency, CurrenciesDTO targetCurrency, double rate, double amount) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.amount = amount;
        this.convertedAmount = amount * rate;
    }

    public CurrenciesDTO getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(CurrenciesDTO baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public CurrenciesDTO getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(CurrenciesDTO targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(double convertedAmount) {
        this.convertedAmount = convertedAmount;
    }
}
