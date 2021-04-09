public class card {
    String cardNumber; //id of the product
    String cardExpiration; //name of the product
    String cardCVV;
    int remainingBalance ;

    public card(String cardNumber, String cardExpiration, String cardCVV, int total) {
        this.cardNumber = cardNumber;
        this.cardExpiration = cardExpiration;
        this.cardCVV = cardCVV;
        this.remainingBalance = total;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardExpiration() {
        return cardExpiration;
    }

    public String getCardCVV() {
        return cardCVV;
    }

    public int getRemainingBalance() {
        return remainingBalance;
    }
}
