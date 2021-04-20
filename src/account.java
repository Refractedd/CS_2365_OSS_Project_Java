public class account {
    String email; //id of the product
    String password; //name of the product
    String cardNumber;
    String cardExpiration;
    String cardCVV;
    String type;
    int id;


    //constructors
    account(){
    }

    public account(String email, String password, String cardNumber, String cardExpiration, String cardCVV, String type, int id) {
        this.email = email;
        this.password = password;
        this.cardNumber = cardNumber;
        this.cardExpiration = cardExpiration;
        this.cardCVV = cardCVV;
        this.type = type;
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
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

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }
}
