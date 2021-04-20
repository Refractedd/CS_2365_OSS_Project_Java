import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

public class logOn implements ActionListener {
    fileParser fileParser = new fileParser(); //Aids in checking valid logins and creating new accounts

    //Window variables
    private static JLabel loginResponseLabel; //Tells the OP whether their login is invalid or not
    private static JLabel createAccountResponseLabel; //Tells the OP whether or not their create account call is invalid or not

    public static JFrame frame = new JFrame(); //Frame displaying everything. Root.

    //Buttons
    public JButton loginButton; //Submit a login attempt

    //Login variables
    public JPanel loginPanel = new JPanel(); //Panel holding all of the login display components
    public JPanel createAccountPanel = new JPanel(); //Panel holding all of the create account display components
    private static JTextField loginUserText; //On the login panel, holds what username the OP types
    private static JPasswordField loginPasswordText; //On the login panel, holds what password the OP types

    private static JTextField emailText; //On the Create Account panel, holds the email the OP types
    private static JPasswordField createAccountPasswordText; //On the Create Account panel, holds the password the OP types
    private static JTextField cardNumberText; //On the Create Account panel, holds the card number the OP types
    private static JTextField cardExpirationDateText; //On the Create Account panel, holds the card expiration date the OP types
    private static JTextField cardCVVText; //On the Create Account panel, holds the card cvv the OP types

    Buffer buff;

    //Panel Constructs
    public logOn(Buffer buff){
        this.buff = buff;
    }

    /**
     * Constructs the login and createAccount panels. Displays the loginPanel by default.
     */
    public void loginFrame() {
        frame.setSize(350,200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Login");
        loginPanel.setLayout(null);

        loginPanel.add(this.createUsernameLabel());
        loginPanel.add(this.createUsernameTextField());
        loginPanel.add(this.createPasswordLabel());
        loginPanel.add(this.createPasswordField());
        loginPanel.add(this.createLoginButton());
        loginPanel.add(this.createCreateAccountButton());
        loginPanel.add(this.createLoginResponseLabel());

        createAccountFrame();
        frame.setContentPane(loginPanel);

        loginPanel.setVisible(true);
        createAccountPanel.setVisible(false);

        frame.setVisible(true);
    }

    /**
     * Constructs the createAccountPanel. What is displayed when creating an account
     * @return : returns the createAccountPanel... Global variable of type JPanel
     */
    public JPanel createAccountFrame() {
        createAccountPanel.setLayout(null);

        createAccountPanel.add(this.createCreateAccountEmailLabel());
        createAccountPanel.add(this.createCreateAccountEmailTextField());
        createAccountPanel.add(this.createCreateAccountPasswordLabel());
        createAccountPanel.add(this.createCreateAccountPasswordTextField());
        createAccountPanel.add(this.createCreateAccountCardNumberLabel());
        createAccountPanel.add(this.createCreateAccountCardNumberTextField());
        createAccountPanel.add(this.createCreateAccountCardExpirationDateLabel());
        createAccountPanel.add(this.createCreateAccountCardExpirationDateTextField());
        createAccountPanel.add(this.createCreateAccountCardCVVLabel());
        createAccountPanel.add(this.createCreateAccountCardCVVTextField());
        createAccountPanel.add(this.createCreateAccountSubmitButton());
        createAccountPanel.add(this.createBackToLoginButton());
        createAccountPanel.add(this.createCreateAccountResponseLabel());

        return createAccountPanel;
    }

    //Button Processing

    /**
     * Transitions Frame from displaying LoginPanel to displaying CreateAccountPanel. Switch from a login view to a create an account view
     */
    public void moveToCreateAccountPanel(){
        frame.setTitle("Create Account");
        frame.setSize(450,300);

        loginPanel.setVisible(false);
        createAccountPanel.setVisible(true);

        frame.setContentPane(createAccountPanel);
    }

    /**
     * Transitions Frame from displaying CreateAccountPanel to displaying LoginPanel
     */
    public void moveToLoginPanel(){
        frame.setTitle("Login");
        frame.setSize(350,200);

        loginPanel.setVisible(true);
        createAccountPanel.setVisible(false);

        frame.setContentPane(loginPanel);
    }

    /**
     * Checks if a login is valid. If !valid: notify the OP. if valid: call the store gui
     * @throws IOException : incorrect file path
     * @throws ParseException : incorrect json file
     */
    public void checkLogin() throws IOException, ParseException {
        String user = loginUserText.getText();
        String password = loginPasswordText.getText();

        ArrayList<account> accounts = fileParser.getAccounts();

        for (account account : accounts) {
            if (account.getEmail().equals(user) && account.getPassword().equals(password)) {
                int customerID = account.getId();
                loginPanel.setVisible(false);

                if(account.getType().equals("supplier")){
                    supplier supplier = new supplier(frame);
                    supplier.frame();
                    
                    // add supplier view here
//                    SupplierView.GUI(null);
//                    loginFrame();
                }
                else{
                    store store = new store(frame, customerID, buff, loginPanel);
                    store.storeFrame();
                }

                return;
            }
        }

        loginResponseLabel.setText("Incorrect ID or Password");
    }

    /**
     * Checks if account information is valid. If !valid: notify OP. If valid: parse the new account to the JSON file and forward customer id to store gui
     * @throws IOException : wrong file path
     * @throws ParseException : wrong json file
     */
    public void createAccount() throws IOException, ParseException {
       String email = emailText.getText();
       String password = createAccountPasswordText.getText();
       String cardNumber = cardNumberText.getText();
       String cardExpirationDate = cardExpirationDateText.getText();
       String cardCVV = cardCVVText.getText();

       if(!email.matches("^(.+)@(.+)$")
               || !(cardNumber.length() == 16)
               || !cardExpirationDate.matches("^\\d{2}-\\d{2}$")
               || !(cardCVV.length() == 3)
       )
           createAccountResponseLabel.setText("Please enter valid credentials.");
       else {
           createAccountResponseLabel.setText("Creating account...");

           int customerID = fileParser.createAccount(email, password, cardNumber, cardExpirationDate, cardCVV, "Customer");
           createAccountPanel.setVisible(false);

           store store = new store(frame, customerID, buff, loginPanel);
           frame.setContentPane(null);
           store.storeFrame();
       }

    }

    //Login Panel Components
    public JLabel createUsernameLabel(){
        JLabel userLabel = new JLabel("Username");
        userLabel.setBounds(10,20,80,25);
        return userLabel;
    }

    public JTextField createUsernameTextField(){
        loginUserText = new JTextField(20);
        loginUserText.setBounds(100,20,165,25);
        return loginUserText;
    }

    public JLabel createPasswordLabel(){
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(10,50,80,25);
        return passwordLabel;
    }

    public JPasswordField createPasswordField(){
        loginPasswordText = new JPasswordField(20);
        loginPasswordText.setBounds(100,50,165,25);

        loginPasswordText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if(evt.getKeyCode() == KeyEvent.VK_ENTER){
                    loginButton.doClick();
                }
            }
        });

        return loginPasswordText;
    }

    public JButton createLoginButton(){
        loginButton = new JButton("Login");
        loginButton.setBounds(10,80,80,25);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    checkLogin();
                } catch (IOException | ParseException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        return loginButton;
    }


    public JLabel createLoginResponseLabel(){
        loginResponseLabel = new JLabel("");
        loginResponseLabel.setBounds(10,110,300,25);
        return loginResponseLabel;
    }

    public JButton createCreateAccountButton(){
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setBounds(100,80,125,25);

        createAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveToCreateAccountPanel();
            }
        });

        return createAccountButton;
    }

    //Create Account Panel Components
    public JLabel createCreateAccountEmailLabel(){
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setBounds(10,20,80,25);
        return emailLabel;
    }

    public JTextField createCreateAccountEmailTextField(){
        emailText = new JTextField(20);
        emailText.setBounds(195,20,165,25);
        return emailText;
    }

    public JLabel createCreateAccountPasswordLabel(){
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(10,50,80,25);
        return passwordLabel;
    }

    public JTextField createCreateAccountPasswordTextField(){
        createAccountPasswordText = new JPasswordField(20);
        createAccountPasswordText.setBounds(195,50,165,25);
        return createAccountPasswordText;
    }

    public JLabel createCreateAccountCardNumberLabel(){
        JLabel cardNumberLabel = new JLabel("Card Number");
        cardNumberLabel.setBounds(10,80,80,25);
        return cardNumberLabel;
    }

    public JTextField createCreateAccountCardNumberTextField(){
        cardNumberText = new JTextField(20);
        cardNumberText.setBounds(195,80,165,25);
        return cardNumberText;
    }

    public JLabel createCreateAccountCardExpirationDateLabel(){
        JLabel cardExpirationDate = new JLabel("Card Expiration Date (MM-YY)");
        cardExpirationDate.setBounds(10,110,175,25);
        return cardExpirationDate;
    }

    public JTextField createCreateAccountCardExpirationDateTextField(){
        cardExpirationDateText = new JTextField(20);
        cardExpirationDateText.setBounds(195,110,165,25);
        return cardExpirationDateText;
    }

    public JLabel createCreateAccountCardCVVLabel(){
        JLabel cardCVV = new JLabel("Card CVV");
        cardCVV.setBounds(10,140,80,25);
        return cardCVV;
    }

    public JTextField createCreateAccountCardCVVTextField(){
        cardCVVText = new JTextField(20);
        cardCVVText.setBounds(195,140,165,25);
        return cardCVVText;
    }

    public JLabel createCreateAccountResponseLabel(){
        createAccountResponseLabel = new JLabel("");
        createAccountResponseLabel.setBounds(10,200,300,25);
        return createAccountResponseLabel;
    }

    public JButton createCreateAccountSubmitButton(){
        JButton createAccountSubmitButton = new JButton("Create Account");
        createAccountSubmitButton.setBounds(100,170,125,25);

        createAccountSubmitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    createAccount();
                } catch (IOException | ParseException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        return createAccountSubmitButton;
    }

    public JButton createBackToLoginButton() {
        JButton backToLogin = new JButton("Back");
        backToLogin.setBounds(10,170,80,25);

        backToLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveToLoginPanel();
            }
        });

        return backToLogin;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //ActionListeners for buttons declared above, in the create methods
    }
}
