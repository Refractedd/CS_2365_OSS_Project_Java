import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;


public class store implements ActionListener {
    Buffer buff;
    JFrame frame;
    int customerID;
    TextArea order;
    JComboBox shippingList;
    boolean shipping = false;
    boolean firstOrder = false;
    public JPanel storePanel = new JPanel();
    public JPanel accountInfo = new JPanel();
    public JPanel loginPanel;
    fileParser fileParser = new fileParser();

    JScrollPane scrollFrame = new JScrollPane(accountInfo);

    int total;
    ArrayList<item> cart = new ArrayList<>();

    store(JFrame frame, int customerID, Buffer buff, JPanel loginPanel){
        this.frame = frame; //Display
        this.customerID = customerID; //The customer currently logged in
        this.buff = buff; //the buffer for communicating with the banking system
        this.loginPanel = loginPanel; //Panel containing the login view. Used if the customer logs out
    }

    /**
     * Called when a button is pressed within the frame. This includes both the storePanel AND the accountInformationPanel.
     *  Possible buttons include, but are not limited to addToCart, checkOut, clearCart, etc... Set via a Object.setActionCommand("");
     * @param e : ActionEvent of the button clicked. Reference the button clicked via e,getSource()
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = "";

        //If a button was clicked...
        try{
            actionCommand = ((JButton) e.getSource()).getActionCommand();

        }
        catch(ClassCastException ignored){

        }

        //If a button was not clicked, but a comboBox (drop down) was changed
        if(actionCommand.equals(""))
            try{
                actionCommand = ((JComboBox) e.getSource()).getActionCommand();
            }

            catch(ClassCastException ignored){
            }

        //If the customer cleared their cart...
        if(actionCommand.equals("Clear Cart")){
            try {
                clearCart();
            } catch (IOException | ParseException ioException) {
                ioException.printStackTrace();
            }
        }

        //If the customer is ready to check out AND there is something in the cart...
        else if(actionCommand.equals("Check Out") && cart.size() > 0){
            try {
                checkout();
            } catch (IOException | ParseException ioException) {
                ioException.printStackTrace();
            }
        }

        //If the customer wants to view their account information...
        else if(actionCommand.equals("View Account Info")){
            try {
                changeToAccountInfoFrame();
            } catch (IOException | ParseException ioException) {
                ioException.printStackTrace();
            }
        }

        //If the customer wants to switch back to shopping from viewing their account information...
        else if(actionCommand.equals("View Store")){
            changeToStoreFrame();
        }

        //If the customer wants to log out...
        else if(actionCommand.equals("Log Out")){
            try {
                changeToLoginFrame();
            } catch (IOException | ParseException ioException) {
                ioException.printStackTrace();
            }

        }

        //Should only be the name of an item to add to the cart. If anything else, no errors, or actions, will occur
        else{
            addItemToCart(actionCommand);
        }
    }

    /**
     * Constructs the frame responsible for the store view. This includes the items available for purchase, the cart view,
     *  and a button to transition to the account information view
     * @throws IOException : wrong file path
     * @throws ParseException : wrong json file
     */
    public void storeFrame() throws IOException, ParseException {
        //Get the frame ready
        frame.setSize(450,900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Store");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        storePanel.setLayout(new GridBagLayout());
        storePanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        accountInfoFrame(); //construct the accountInfoPanel, but do make it the active one until the customer clicks the appropriate button

        //Add the button(s) at the top
        addAccountInfoButton(gbc);

        //For each item currently available for purchase add a row detailing the name, price, and give a button to addToCart
        ArrayList<item> items = fileParser.getItems();
        for(int i = 0; i < items.size(); i++){
            addProductRow(gbc, items.get(i), i+1);
        }

        //Add order control buttons (shipping method, clear cart, checkout, etc...)
        addShippingMethodBox(gbc, items.size()+1);
        addCheckoutButton(gbc, items.size()+1);
        addClearCartButton(gbc, items.size()+1);
        addCartTextArea(gbc, items.size()+2);

        frame.setContentPane(storePanel);

        frame.setVisible(true);
    }

    /**
     * Constructs the frame responsible for the account information view. This includes the email, password, card information, all orders placed by only this customer
     *  and a button to transition to the store view
     * @throws IOException : wrong file path
     * @throws ParseException : wrong json file
     */
    public void accountInfoFrame() throws IOException, ParseException {
        //Get the frame ready
        int y_level = 0;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        accountInfo.setLayout(new GridBagLayout());
        accountInfo.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        addViewStoreButton(gbc, 0);
        addLogOutButton(gbc, 0);
        y_level++;

        //For only the account corresponding with this customer's unique id, display the information (email, password, card information)
        ArrayList<account> accounts = fileParser.getAccounts();
        for(account account : accounts){
            if(account.getId() == this.customerID){
                addAccountInfo(gbc, y_level, account);
                break;
            }
        }

        y_level += 5;
        //For the orders placed by this customer id, display all appropriate infromation (Total, date, items purchased, etc...)
        ArrayList<order> orders = fileParser.getOrders();

        addFillerRow(gbc, y_level, "");
        y_level++;
        addFillerRow(gbc, y_level, "");
        y_level++;

        JLabel ordersLabel = new JLabel("Orders :");
        gbc.gridx = 0;
        gbc.gridy = y_level;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        accountInfo.add(ordersLabel, gbc);

        y_level++;

        for(order order : orders){
            if(order.getCustomerId().equals("" + this.customerID)){
                addOrderDescription(gbc, y_level, order);
                y_level+=5;
                for(item item : order.getItems()){
                    addProductPurchased(gbc, item, y_level);
                    y_level++;
                }

                addOrderTotal(gbc, y_level, order.getOrderTotal());
                addFillerRow(gbc, y_level, "");
                y_level++;
                addFillerRow(gbc, y_level, "..................................................................................");
                y_level++;
            }
        }

        //Add a label in the bottom right cell for resizing purposes
        addEmptyLabel(gbc, y_level);


    }

    /**
     * If this is the first order placed by this customer, charge an additional $40 'membership' fee
     * @throws IOException : wrong file path
     * @throws ParseException : wrong json file
     */
    public void checkFirstOrder() throws IOException, ParseException {
        ArrayList<order> orders = fileParser.getOrders();

        for(order order : orders){
            if(Integer.parseInt(order.getCustomerId()) == this.customerID){
                firstOrder = false;
                return;
            }
        }

        firstOrder = true;
    }

    /**
     * If the customer wants to ship their order, charge an addition $3. If pickup, charge $0. If no choice is selected
     *  FORCE THEM TO CHOOSE
     * @throws IOException : wrong file
     * @throws ParseException : wrong json
     */
    public void checkShipping() throws IOException, ParseException {
        String shipping = Objects.requireNonNull(shippingList.getSelectedItem()).toString();

        switch (shipping) {
            case "Shipping ($3)" -> this.shipping = true;
            case "Pick-Up" -> this.shipping = false;
        }
    }

    /**
     * Clear the cart. This includes the display, saved items, and quantityPurchased within the items classes
     * @throws IOException : wrong file
     * @throws ParseException : wrong JSON
     */
    public void clearCart() throws IOException, ParseException {
        order.setText("");
        cart = new ArrayList<>();
        total = 0;

        ArrayList<item> items = fileParser.getItems();
        for(item item : items){
            item.quantityPurchased = 0;
        }
    }

    /**
     * When an 'addToCart' button is clicked, this method is called. Adds the item name and price to the display, and either adds
     *  the corresponding items class to the cart or increments the quantityPurchased by 1
     * @param actionCommand : name of the item to add to the cart
     */
    public void addItemToCart(String actionCommand){
        total = 0;

        try {
            ArrayList<item> items = fileParser.getItems();

            boolean alreadyInCart = false;
            for(item item : items){
                if(item.getName().equals(actionCommand)){
                    for(item itemInCart : cart){
                        if(itemInCart.getName().equals(item.getName())){
                            itemInCart.setQuantityPurchased(itemInCart.getQuantityPurchased()+1);

                            alreadyInCart = true;
                            break;
                        }
                    }
                    if(!alreadyInCart){
                        cart.add(item);
                        item.quantityPurchased++;
                    }
                    break;
                }
            }
        } catch (IOException | ParseException ioException) {
            ioException.printStackTrace();
        }

        order.setText("");

        for(item inCart : cart){
            for(int i = 0; i < inCart.getQuantityPurchased(); i++){
                order.append(inCart.getName() + "........$" + (inCart.getPrice() / 100.0) + "\n");
                total += inCart.getPrice();
            }
        }

        order.append("................................\n");

        try {
            checkFirstOrder();
            checkShipping();
        } catch (IOException | ParseException ioException) {
            ioException.printStackTrace();
        }

        if(shipping){
            order.append("Shipping : $3\n");
            total += 300;
        }

        if(firstOrder){
            order.append("Membership Fee : $40\n");
            total += 4000;
        }

        order.append("Total : $" + (total/100.0));
    }

    /**
     * Checks-out the customer GIVEN NEW CARD INFO. This method is only called if the card on file is denied by the banking system.
     *  If this checkout is successful (i.e. the banking system returns a authorization number) then the new card info is saved
     * @param cardNumber : 16 digit card number
     * @param cardExpiration : MM/YY format date
     * @param cardCVV : 3 digit card security number
     * @throws IOException : wrong file path
     * @throws ParseException : wrong json
     */
    public void checkout(String cardNumber, String cardExpiration, String cardCVV) throws IOException, ParseException {
        String message = "" + total
                + "," + cardNumber
                + "," + cardExpiration
                + "," + cardCVV;

        buff.send(message);
        String[] reply = buff.receiveReply().split(",");

        if(reply[0].equals("false")){
            JTextField cardNumberField = new JTextField();
            JTextField cardExpirationField = new JTextField();
            JTextField cardCVVField = new JTextField();

            Object[] inputs = {
                    "Charge declined. Please input a new card number or click 'Cancel' to cancel the order.",
                    "Card Number: ", cardNumberField,
                    "Card Expiration Date: ", cardExpirationField,
                    "Card CVV: ", cardCVVField
            };

            int option = JOptionPane.showConfirmDialog(null, inputs, "Login", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) {
                clearCart();

            }
        }

        else if(reply[0].equals("true")){
            JOptionPane.showConfirmDialog(null, "Order successfully placed! $" + (total/100.0) + " has been charged.", "Order", JOptionPane.OK_CANCEL_OPTION);
            fileParser.changeCardInfo("" + customerID, cardNumber, cardExpiration, cardCVV);

            addNewOrder(reply[1], Objects.requireNonNull(shippingList.getSelectedItem()).toString());
            clearCart();
        }

        else{
            JOptionPane.showConfirmDialog(null, "This application has encountered an error. Please restart and try again.", "Order", JOptionPane.OK_CANCEL_OPTION);
        }
    }

    /**
     * Checks-out the customer WITH THE CARD INFORMATION ON FILE. If this purchase is denied by the banking system, then
     *  the customer is prompted to either cancel the order or enter new information. If new card information is entered,
     *  then a different checkout() method is called.
     * @throws IOException : wrong file
     * @throws ParseException : wrong json
     */
    public void checkout() throws IOException, ParseException {
        String message = "" + total ;

        ArrayList<account> accounts = fileParser.getAccounts();
        for(account account : accounts){
            if(account.getId() == this.customerID){
                message += "," + account.getCardNumber()
                        + "," + account.getCardExpiration()
                        + "," + account.getCardCVV();
                buff.send(message);
                String[] reply = buff.receiveReply().split(",");

                if(reply[0].equals("false")){
                    JTextField cardNumber = new JTextField();
                    JTextField cardExpiration = new JTextField();
                    JTextField cardCVV = new JTextField();

                    Object[] inputs = {
                            "Charge declined. Please input a new card number or click 'Cancel' to cancel the order.",
                            "Card Number: ", cardNumber,
                            "Card Expiration Date: ", cardExpiration,
                            "Card CVV: ", cardCVV
                    };

                    int option = JOptionPane.showConfirmDialog(null, inputs, "Login", JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        checkout(cardNumber.getText(), cardExpiration.getText(), cardCVV.getText());
                    } else {
                        clearCart();
                    }
                }

                else if(reply[0].equals("true")){
                    JOptionPane.showConfirmDialog(null, "Order successfully placed! $" + (total/100.0) + " has been charged.", "Order", JOptionPane.OK_CANCEL_OPTION);

                    addNewOrder(reply[1], Objects.requireNonNull(shippingList.getSelectedItem()).toString());

                    clearCart();
                }

                else{
                    JOptionPane.showConfirmDialog(null, "This application has encountered an error. Please restart and try again.", "Order", JOptionPane.OK_CANCEL_OPTION);
                }

                return;
            }
        }

    }

    /**
     * When the banking system successfully charges a customer's card, then a new order must be added to the orders.json file
     * @param purchaseAuthorizationNumber : 8 digit confirmation code from the banking system
     * @throws IOException : wrong file
     * @throws ParseException : wrong json
     */
    public void addNewOrder(String purchaseAuthorizationNumber, String shippingMethod) throws IOException, ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        fileParser.addNewOrder("" + customerID, total, "Ordered", purchaseAuthorizationNumber, dateFormat.format(date), shippingMethod, this.cart);
    }

    /**
     * Transitions the currently displayed from the store to the account information. This method reconstructs the accountInfo
     *  panel to pickup any new orders placed by the customer
     * @throws IOException : wrong file
     * @throws ParseException : wrong json
     */
    public void changeToAccountInfoFrame() throws IOException, ParseException {
        accountInfo = new JPanel();
        accountInfoFrame();

        scrollFrame = new JScrollPane(accountInfo);

        frame.setContentPane(scrollFrame);
        frame.setTitle("Account Information");
        frame.setSize(450,901);
    }

    /**
     * Transitions the currently displayed from account information to the store
     */
    public void changeToStoreFrame(){
        frame.setContentPane(storePanel);
        frame.setTitle("Store");
        frame.setSize(450,900);
    }

    /**
     * Transitions from the account information page to the login page. Used for logout events.
     * @throws IOException : wrong file
     * @throws ParseException : wrong json
     */
    public void changeToLoginFrame() throws IOException, ParseException {
        clearCart();
        frame.setTitle("Login");
        loginPanel.setVisible(true);
        frame.setSize(350,200);
        frame.setContentPane(loginPanel);
    }

    //Store Components
    public void addAccountInfoButton(GridBagConstraints gbc){
        JButton clearCart = new JButton("View Account Info");
        clearCart.setActionCommand("View Account Info");
        clearCart.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        storePanel.add(clearCart, gbc);
    }

    public void addClearCartButton(GridBagConstraints gbc, int y_level){
        JButton clearCart = new JButton("Clear Cart");
        clearCart.setActionCommand("Clear Cart");
        clearCart.addActionListener(this);
        gbc.gridx = 2;
        gbc.gridy = y_level;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        storePanel.add(clearCart, gbc);
    }

    public void addShippingMethodBox(GridBagConstraints gbc, int y_level){
        String[] shippingMethods = {"Shipping ($3)", "Pick-Up"};

        shippingList = new JComboBox(shippingMethods);
        shippingList.setSelectedIndex(0);
        shippingList.setActionCommand("Delivery Change");
        shippingList.addActionListener(this);

        gbc.gridx = 0;
        gbc.gridy = y_level;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        storePanel.add(shippingList, gbc);
    }

    public void addCheckoutButton(GridBagConstraints gbc, int y_level){
        JButton checkOut = new JButton("Check Out");
        checkOut.setActionCommand("Check Out");
        checkOut.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridy = y_level;
        gbc.anchor = GridBagConstraints.WEST;
        storePanel.add(checkOut, gbc);
    }

    public void addCartTextArea(GridBagConstraints gbc, int y_level){
        order = new TextArea();
        order.setColumns(2);
        order.setSize(new Dimension(0, 0));
        order.setEditable(false);
        gbc.gridx = 0;
        gbc.gridy = y_level;
        gbc.gridwidth = 3;
        gbc.gridheight = 3;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        storePanel.add(order, gbc);
    }

    public void addProductRow(GridBagConstraints gbc, item item, int y_level){
        //Img   Name   Price   Add to cart
        JLabel name = new JLabel(item.getName());
        JLabel price = new JLabel("$" + (item.getPrice()/100.0));
        JButton button = new JButton("Add to Cart");
        button.setActionCommand(item.getName());
        button.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = y_level;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        storePanel.add(name, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        storePanel.add(price, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.WEST;
        storePanel.add(button, gbc);
    }

    public void addLogOutButton(GridBagConstraints gbc, int y_level){
        JButton logOut = new JButton("Log Out");
        logOut.setActionCommand("Log Out");
        logOut.addActionListener(this);
        gbc.gridx = 1;
        gbc.gridy = y_level;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        accountInfo.add(logOut, gbc);
    }

    public void addViewStoreButton(GridBagConstraints gbc, int y_level){
        JButton viewStore = new JButton("View Store");
        viewStore.setActionCommand("View Store");
        viewStore.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = y_level;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        accountInfo.add(viewStore, gbc);
    }

    public void addAccountInfo(GridBagConstraints gbc, int y_level, account account){
        JLabel email = new JLabel("Email: " );
        JLabel password = new JLabel("Password: ");
        JLabel cardNumber = new JLabel("Card Number: ");
        JLabel cardExpiration = new JLabel("Card Expiration Date: ");
        JLabel cardCVV = new JLabel("Card CVV: ");

        JLabel emailField = new JLabel(account.getEmail());
        JLabel passwordField = new JLabel(account.getPassword());
        JLabel cardNumberField = new JLabel(account.getCardNumber());
        JLabel cardExpirationField = new JLabel(account.getCardExpiration());
        JLabel cardCVVField = new JLabel(account.getCardCVV());

        gbc.gridx = 0;
        gbc.gridy = y_level;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        accountInfo.add(email, gbc);

        gbc.gridx = 1;
        accountInfo.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = y_level+1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        accountInfo.add(password, gbc);

        gbc.gridx = 1;
        accountInfo.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = y_level+2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        accountInfo.add(cardNumber, gbc);

        gbc.gridx = 1;
        accountInfo.add(cardNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = y_level+3;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        accountInfo.add(cardExpiration, gbc);

        gbc.gridx = 1;
        accountInfo.add(cardExpirationField, gbc);

        gbc.gridx = 0;
        gbc.gridy = y_level+4;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        accountInfo.add(cardCVV, gbc);

        gbc.gridx = 1;
        accountInfo.add(cardCVVField, gbc);

    }

    public void addProductPurchased(GridBagConstraints gbc, item item, int y_level){
        //Img   Name   Price   Add to cart
        JLabel name = new JLabel("    " + item.getName());
        JLabel price = new JLabel("    $" + (item.getPrice()/100.0));

        gbc.gridx = 0;
        gbc.gridy = y_level;
        accountInfo.add(name, gbc);

        gbc.gridx = 1;
        accountInfo.add(price, gbc);

    }

    public void addEmptyLabel(GridBagConstraints gbc, int y_level){
        JLabel empty = new JLabel("");
        gbc.gridx = 2;
        gbc.gridy = y_level;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        accountInfo.add(empty, gbc);
    }

    public void addOrderTotal(GridBagConstraints gbc, int y_level, int total){
        JLabel orderTotal = new JLabel("Total :");
        JLabel orderTotalField = new JLabel("$" + (total/100.0));


        gbc.gridx = 0;
        gbc.gridy = y_level;
        accountInfo.add(orderTotal, gbc);

        gbc.gridx = 1;
        accountInfo.add(orderTotalField, gbc);
    }

    public void addFillerRow(GridBagConstraints gbc, int y_level, String content){
        JLabel empty = new JLabel(content);
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.gridy = y_level;
        accountInfo.add(empty, gbc);
        gbc.gridwidth = 1;
    }

    public void addOrderDescription(GridBagConstraints gbc, int y_level, order order){
        JLabel status = new JLabel("Status:");
        JLabel date = new JLabel("Date:");
        JLabel shippingMethod = new JLabel("Delivery Method:");
        JLabel purchaseAuthorizationNumber = new JLabel("Confirmation #:");
        JLabel items = new JLabel("Items Purchased :");
        JLabel statusField = new JLabel(order.getOrderStatus());
        JLabel dateField = new JLabel(order.getOrderDate());
        JLabel shippingField = new JLabel(order.getShippingMethod());
        JLabel purchaseAuthorizationNumberField = new JLabel(order.getPurchaseAuthorizationNumber());

        gbc.gridx = 0;
        gbc.gridy = y_level;
        accountInfo.add(status, gbc);

        gbc.gridx = 1;
        accountInfo.add(statusField, gbc);

        gbc.gridx = 0;
        gbc.gridy = y_level+1;
        accountInfo.add(date, gbc);

        gbc.gridx = 1;
        accountInfo.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = y_level+2;
        accountInfo.add(shippingMethod, gbc);

        gbc.gridx = 1;
        accountInfo.add(shippingField, gbc);

        gbc.gridx = 0;
        gbc.gridy = y_level+3;
        accountInfo.add(purchaseAuthorizationNumber, gbc);

        gbc.gridx = 1;
        accountInfo.add(purchaseAuthorizationNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = y_level+4;
        accountInfo.add(items, gbc);
    }
}
