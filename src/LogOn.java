import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogOn implements ActionListener {

    private static JTextField userText;
    private static JPasswordField passwordText;
    private static JLabel success;
    public JFrame frame = new JFrame();
    public JPanel panel = new JPanel();

    public void display() {
        frame.setSize(350,200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Login");

        frame.add(panel);
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Username");
        userLabel.setBounds(10,20,80,25);
        panel.add(userLabel);

        userText = new JTextField(20);
        userText.setBounds(100,20,165,25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(10,50,80,25);
        panel.add(passwordLabel);

        passwordText = new JPasswordField(20);
        passwordText.setBounds(100,50,165,25);
        panel.add(passwordText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(10,80,80,25);
        loginButton.addActionListener(new LogOn());
        panel.add(loginButton);

        success = new JLabel("");
        success.setBounds(10,110,300,25);
        panel.add(success);

        frame.setVisible(true);
        //this.toggleVisible(this);
    }
    private static void close(){
        frame.setVisible(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String user = userText.getText();
        String password = passwordText.getText();

        if(user.equals("Ben") && password.equals("password")){
            success.setText("Login Successful");
            //frame.setVisible(false);
            LogOn.close();
        } else {
            success.setText("Incorrect ID or Password");
        }

    }
}
