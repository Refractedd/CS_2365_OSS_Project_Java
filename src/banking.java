import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

public class banking extends Thread
{
    private final Buffer buff;
    fileParser fileParser = new fileParser();

    public banking(Buffer buff)
    {
        this.buff = buff;
    }

    public void run()
    {
        //msg.split(",") = [total*100, 16-digit card number, MM-YY format expiration date, three digit security code ]
        String[] msg;

        while(true)
        {
            msg = buff.receive().split(",");

            //See if the card information sent is valid (including if there is enough available balance)
            Boolean validCard = false;
            try {
                validCard = checkCardInformation(Integer.parseInt(msg[0]), msg[1], msg[2], msg[3]);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }

            //If there is no matching card, or insufficient funds, send the denied reply
            if(!validCard) {
                buff.sendReply("false");
                return;
            }

            //If there is a matching card with enough balance, charge the card and generate a confirmation number
            try {
                chargeCard(Integer.parseInt(msg[0]), msg[1]);
                String confirmationNumber = generateConfirmationNumber();

                buff.sendReply(true + "," + confirmationNumber);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }

            System.out.println("[BANK] " + Arrays.toString(msg));
        }
    }

    public Boolean checkCardInformation(int total, String cardNumber, String cardExpiration, String cardCVV) throws IOException, ParseException {
        ArrayList<card> cards = fileParser.getCards();

        for (card card : cards) {
            if(card.getCardNumber().equals(cardNumber) &&
                    card.getCardExpiration().equals(cardExpiration) &&
                    card.getCardCVV().equals(cardCVV) &&
                    (card.getRemainingBalance() - total >= 0))
                return true;
        }
        return false;
    }

    public void chargeCard(int total, String cardNumber) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("../config_files/cards.json"));
        Iterator iterator = jsonArray.iterator();
        while(iterator.hasNext()) {
            //Grab the next object and save appropriate types
            JSONObject card = (JSONObject) iterator.next();

            if(cardNumber.equals("" + card.get("cardNumber"))){
                int oldTotal = ((Long)card.get("remainingBalance")).intValue();
                card.put("remainingBalance", oldTotal-total);
            }
        }

        PrintWriter pw = new PrintWriter("../config_files/cards.json");
        pw.write(jsonArray.toJSONString()
            .replace("[", "[\n")
            .replace("{", "\t{")
            .replace("},", "},\n")
            .replace("]", "\n]")
        );

        pw.flush();
        pw.close();
    }

    public String generateConfirmationNumber() throws IOException, ParseException {
        StringBuilder code= new StringBuilder();
        Random rand=new Random();//Generate random numbers
        for(int a=0;a<8;a++){
            code.append(rand.nextInt(10));//Generate 6-digit verification code

        }

       return code.toString();
    }
}
