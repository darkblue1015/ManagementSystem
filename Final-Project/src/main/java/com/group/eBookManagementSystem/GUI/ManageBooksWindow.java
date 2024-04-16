package com.group.eBookManagementSystem.GUI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group.eBookManagementSystem.model.Book;
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

public class ManageBooksWindow {

    private static final Logger LOG = LoggerFactory.getLogger(ManageBooksWindow.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final SingletonWindow singletonWindow;
    private final JTable userTable;
    private final JTextField bookIDField;
    private final JButton deleteBookButton;
    private final JButton addNewBookButton;
    private final JTextField alarmField;

    // define a ManageBooksWindow() constructor
    public ManageBooksWindow() throws JsonProcessingException {
        // use the ObjectMapper to convert a JSON string into a List<Book> object
        String allBooks = findAllBooks();
        LOG.info("AllBooks:" + allBooks);
        List<Book> bookList = new ObjectMapper().readValue(allBooks, new TypeReference<List<Book>>() {
        });
        LOG.info("Size:" + bookList.size());

        // initialize the SingletonWindow and set its layout
        singletonWindow = SingletonWindow.getInstance();
        singletonWindow.getContentPane().removeAll();
        singletonWindow.getContentPane().setLayout(new BorderLayout());
        singletonWindow.repaint();

        // create a panel for displaying the JTable and other GUI components
        JPanel panel = new JPanel();

        // define the column names and the data for the JTable
        String[] columnNames = {"Book ID", "Book Name", "Author", "Subject", "Main Content", "Rating"};
        Object[][] booksData = new Object[bookList.size()][6];
        for (int i = 0; i < bookList.size(); i++) {
            booksData[i][0] = bookList.get(i).getId();
            booksData[i][1] = bookList.get(i).getBookName();
            booksData[i][2] = bookList.get(i).getAuthor();
            booksData[i][3] = bookList.get(i).getSubject();
            booksData[i][4] = bookList.get(i).getContent();
            booksData[i][5] = bookList.get(i).getRate();
        }
        userTable = new JTable(booksData, columnNames);
        JScrollPane tablePane = new JScrollPane(userTable);
        tablePane.setPreferredSize(new Dimension(400, 300));

        // create GUI components for deleting and adding a book, and for displaying an error message
        JLabel bookIDLabel = new JLabel("Book ID:");
        bookIDField = new JTextField(20);

        deleteBookButton = new JButton("Delete");

        // add action listeners to the buttons
        deleteBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // find the book by its ID and get the response
                String response = findBookByIDRequest(Integer.parseInt(bookIDField.getText()));
                // log the response
                LOG.info("res:" + response);
                // set the alarmField to indicate that the book ID does not exist if the response is empty
                // otherwise, delete the book by its ID
                if (response.equals("")) {
                    alarmField.setText("BookID does not exist！");
                } else {
                    deleteBookByID();
                }
            }
        });

        // create a button for adding a new book to the library
        addNewBookButton = new JButton(("Add a New Book"));
        addNewBookButton.addActionListener(new ActionListener() {
            @Override
            // create a new window to add a new book
            public void actionPerformed(ActionEvent event) {
                javax.swing.SwingUtilities.invokeLater(AddNewBookWindow::new);
            }
        });

        // create a field to display error messages
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
        panel.add(bookIDLabel);
        panel.add(bookIDField);
        panel.add(deleteBookButton);
        panel.add(addNewBookButton);
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

    // define a method to send a request to the server to delete a book by its ID
    public String deleteBookByID() {
        try {
            URL url = new URL("http://localhost:8080/deleteBook?id=" + Integer.parseInt(bookIDField.getText()));
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

            // update the UI by invoking a new ManageBooksWindow instance
            SwingUtilities.invokeLater(() -> {
                try {
                    ManageBooksWindow manageBooksWindow = new ManageBooksWindow();
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

    // define a method to send a GET request to the server to find a book by its ID
    private static String findBookByIDRequest(Integer bookID) {
        try {
            URL url = new URL(String.format("http://localhost:8080/findBookByID/%s", bookID));
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

    // define a method to send a GET request to the server to get a list of all books
    public static String findAllBooks() {
        try {
            URL url = new URL("http://localhost:8080/listBooks");
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
