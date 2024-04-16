package com.group.eBookManagementSystem.GUI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group.eBookManagementSystem.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterWindow {
    private static final Logger LOG = LoggerFactory.getLogger(LoginWindow.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final SingletonWindow singletonWindow;
    private final JTextField usernameField;
    private final JTextField firstnameField;
    private final JTextField lastnameField;
    private final JPasswordField passwordField;
    private final JTextField emailField;
    private final JButton submitButton;
    private final JTextField alarmField;

    // define a constructor to initialize the RegisterWindow
    public RegisterWindow() {
        // define an instance of the SingletonWindow
        singletonWindow = SingletonWindow.getInstance();
        singletonWindow.getContentPane().removeAll(); // remove all the components from the content pane of the singleton window
        singletonWindow.getContentPane().setLayout(new BorderLayout()); // set the layout of the content pane of the singleton window to BorderLayout
        singletonWindow.repaint(); // repaint the window

        // create a panel
        JPanel panel = new JPanel();
        // create labels, text fields and buttons
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        JLabel firstnameLabel = new JLabel("Firstname:");
        firstnameField = new JTextField(20);
        JLabel lastnameLabel = new JLabel("Lastname:");
        lastnameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        JLabel emailLabel = new JLabel("E-mail:");
        emailField = new JTextField(20);
        submitButton = new JButton("Submit");
        alarmField = new JTextField(20);
        alarmField.setBorder(null);
        alarmField.setBackground(null);
        alarmField.setForeground(Color.red);

        // add an ActionListener to the submitButton to handle the request
        submitButton.addActionListener(new ActionListener() {
            @Override
            // call the submit() method
            public void actionPerformed(ActionEvent e) {
                submit();
            }
        });

        // add the labels, text fields, button, and error message field to the panel
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(firstnameLabel);
        panel.add(firstnameField);
        panel.add(lastnameLabel);
        panel.add(lastnameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(submitButton);
        panel.add(alarmField);

        // add the panel to the singleton window and set its properties
        singletonWindow.add(panel, BorderLayout.CENTER);
        singletonWindow.setSize(300, 350);
        singletonWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        singletonWindow.setLocationRelativeTo(null);
        singletonWindow.setVisible(true);
    }

    // define a method which submits the user details as a JSON payload to the server using a POST request.
    public void submit() {
        String userName = usernameField.getText();
        String firstName = firstnameField.getText();
        String lastName = lastnameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        // initialize variables to store HTTP response
        int statusCode = -1;
        StringBuilder response = new StringBuilder();

        try {
            // create a new Customer object with the input fields
            Customer customer = new Customer();
            customer.setUserName(userName);
            customer.setPassword(password);
            customer.setEmail(email);
            customer.setLastName(lastName);
            customer.setFirstName(firstName);

            // set up an HTTP connection to the REST API endpoint
            URL url = new URL("http://localhost:8080/addCustomer");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // convert the Customer object to a JSON payload and send it in the request body
            String payload = objectMapper.writeValueAsString(customer);
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            out.write(payload);
            out.close();

            // read the HTTP response from the server
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            // append the response to a StringBuilder object
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            statusCode = con.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // log the HTTP response
        // logs that the registration was successful and then opens a new LoginWindow if the response status code is 200 (OK).
        // otherwise, sets the text of the alarmField to "Failed!".
        LOG.info(String.format("Response %s", response));
        if (statusCode == 200) {
            LOG.info("Registered!");
            javax.swing.SwingUtilities.invokeLater(LoginWindow::new);
        } else {
            alarmField.setText("Failed!");
        }
    }

}

