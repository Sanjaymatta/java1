import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginGUI extends JFrame implements ActionListener {

    private JPanel panel;
    private JLabel userLabel, passwordLabel, messageLabel;
    private JTextField userText;
    private JPasswordField passwordText;
    private JButton loginButton, resetButton;

    public LoginGUI() {
        setTitle("Login");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panel = new JPanel();
        panel.setLayout(null);

        userLabel = new JLabel("Username");
        userLabel.setBounds(40, 30, 80, 25);
        panel.add(userLabel);

        userText = new JTextField(20);
        userText.setBounds(130, 30, 160, 25);
        panel.add(userText);

        passwordLabel = new JLabel("Password");
        passwordLabel.setBounds(40, 60, 80, 25);
        panel.add(passwordLabel);

        passwordText = new JPasswordField(20);
        passwordText.setBounds(130, 60, 160, 25);
        panel.add(passwordText);

        loginButton = new JButton("Login");
        loginButton.setBounds(40, 100, 80, 25);
        loginButton.addActionListener(this);
        panel.add(loginButton);

        resetButton = new JButton("Reset");
        resetButton.setBounds(210, 100, 80, 25);
        resetButton.addActionListener(this);
        panel.add(resetButton);

        messageLabel = new JLabel("");
        messageLabel.setBounds(40, 130, 300, 25);
        panel.add(messageLabel);

        add(panel);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == loginButton) {
            String user = userText.getText();
            String password = new String(passwordText.getPassword());
            if (user.equals("admin") && password.equals("admin123")) {

                JOptionPane.showMessageDialog(this, "Login successful!");
                dispose();
                // FoodOrderApp f1 = new FoodOrderApp();
                RestaurantBillApp r1=new RestaurantBillApp();
                r1.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,"Invalid username or password");
            }
        } else if (ae.getSource() == resetButton) {
            userText.setText("");
            passwordText.setText("");
            messageLabel.setText("");
        }
    }

    public static void main(String[] args) {
        new LoginGUI();
    }
}
