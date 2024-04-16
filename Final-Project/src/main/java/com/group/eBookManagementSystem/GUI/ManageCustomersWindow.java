package com.group.eBookManagementSystem.GUI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group.eBookManagementSystem.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ManageCustomersWindow {
    private static final Logger LOG = LoggerFactory.getLogger(ManageCustomersWindow.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final SingletonWindow singletonWindow;
    private final JTable userTable;
    private final JTextField userNameField;
    private final JButton deleteButton;
    private final JTextField alarmField;

    // define a constructor to initialize the manage customers window
    public ManageCustomersWindow() throws JsonProcessingException {
        // find all customers and log the information
        String allUsers = findAllCustomer();
        LOG.info("AllUsers:" + allUsers);
        // convert the JSON data into a list of Customer objects
        List<Customer> allCustomers = new ObjectMapper().readValue(allUsers, new TypeReference<List<Customer>>() {
        });
        LOG.info("Size:" + allCustomers.size());

        // initialize an instance of the SingletonWindow and set the layout
        singletonWindow = SingletonWindow.getInstance();
        singletonWindow.getContentPane().removeAll();
        singletonWindow.getContentPane().setLayout(new BorderLayout());
        singletonWindow.repaint();

        // create a JPanel to hold the components of the manage customers window
        JPanel panel = new JPanel();
        // define the column names for the JTable
        String[] columnNames = {"User Name", "Firstname", "Lastname", "Email", "Role"};
        // create a 2D array to hold the data for the JTable
        Object[][] usersData = new Object[allCustomers.size()][5];
        for (int i = 0; i < allCustomers.size(); i++) {
            usersData[i][0] = allCustomers.get(i).getUserName();
            usersData[i][1] = allCustomers.get(i).getFirstName();
            usersData[i][2] = allCustomers.get(i).getLastName();
            usersData[i][3] = allCustomers.get(i).getEmail();
            usersData[i][4] = allCustomers.get(i).getRole();
        }
        // create the JTable with the column names and data
        userTable = new JTable(usersData, columnNames);
        JScrollPane tablePane = new JScrollPane(userTable);
        tablePane.setPreferredSize(new Dimension(400, 300));

        // create a JLabel and a JTextField for the user name
        JLabel userNameLabel = new JLabel("User Name:");
        userNameField = new JTextField(20);

        // create a JButton for deleting customers and add an ActionListener to handle the button click event
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // find the customer by the user name and log the response
                String response = findByUserNameRequest(userNameField.getText());
                LOG.info("res:" + response);
                // display an alert if the response is empty, otherwise delete the customer
                if (response.equals("")) {
                    alarmField.setText("Username does not exist！");
                } else {
                    deleteCustomerByUserName();
                }
            }
        });
        alarmField = new JTextField(20);
        alarmField.setForeground(Color.red);
        alarmField.setBorder(null);
        alarmField.setBackground(null);

        // create a button to go back to the account window
        JButton goBackButton = new JButton("Go Back");
        goBackButton.addActionListener(new ActionListener() {
            @Override
            // get the username of the current user and create a new account window for the user
            public void actionPerformed(ActionEvent event) {
                String userName = getUserName();
                SwingUtilities.invokeLater(() -> {
                    try {
                        AccountWindow accountWindow = new AccountWindow(userName);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });

        // add components to the panel
        panel.add(userNameLabel);
        panel.add(userNameField);
        panel.add(deleteButton);
        panel.add(alarmField);
        panel.add(tablePane);
        panel.add(goBackButton);

        // set properties for the window
        singletonWindow.setSize(500, 450);
        singletonWindow.add(panel);
        singletonWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        singletonWindow.setLocationRelativeTo(null);
        singletonWindow.setVisible(true);
    }

    // define a method to send a request to the server to delete a user by its username
    public String deleteCustomerByUserName() {
        try {
            URL url = new URL("http://localhost:8080/deleteCustomer?userName=" + userNameField.getText());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // read the response from the server and creates a StringBuilder object containing it
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // update the UI by invoking a new ManageCustomersWindow instance
            SwingUtilities.invokeLater(() -> {
                try {
                    ManageCustomersWindow manageCustomersWindow = new ManageCustomersWindow();
                } catch (JsonProcessingException e) {

                    throw new RuntimeException(e);
                }
            });
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();

            return "Error: " + e.getMessage();
        }
    }

    // define a method to send a GET request to the server to find a user by its username
    private static String findByUserNameRequest(String userName) {
        try {
            URL url = new URL(String.format("http://localhost:8080/findCustomerByUserName/%s", userName));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // read the response from the server and creates a StringBuilder object containing it
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // define a method to send a GET request to the server to get a list of all users
    public static String findAllCustomer() {
        try {
            URL url = new URL("http://localhost:8080/listCustomers");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // read the response from the server and creates a StringBuilder object containing it
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // define a method to send a GET request to the server to get the admin's username
    private static String getUserName() {
        try {
            URL url = new URL("http://localhost:8080/getAdminName");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // read the response from the server and creates a StringBuilder object containing it
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

}
