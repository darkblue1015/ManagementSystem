package com.group.eBookManagementSystem.GUI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group.eBookManagementSystem.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
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

public class AddNewBookWindow {
    private static final Logger LOG = LoggerFactory.getLogger(AddNewBookWindow.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final SingletonWindow singletonWindow;
    private final JTextField bookNameField;
    private final JTextField authorField;
    private final JTextField subjectField;
    private final JTextField contentField;
    private final JButton submitButton;
    private final JTextField alarmField;

    // define a constructor
    public AddNewBookWindow() {
        // initializes the window with the relevant components and sets it to be visible
        singletonWindow = SingletonWindow.getInstance();
        singletonWindow.getContentPane().removeAll();
        singletonWindow.repaint();

        // create a JPanel to hold the components
        JPanel panel = new JPanel();
        // create labels and text fields for the book's details
        JLabel bookNameLabel = new JLabel("Book Name:");
        bookNameField = new JTextField(20);
        JLabel authorLabel = new JLabel("Author:");
        authorField = new JTextField(20);
        JLabel subjectLabel = new JLabel("Subject:");
        subjectField = new JTextField(20);
        JLabel contentLabel = new JLabel("Main Content:");
        contentField = new JTextField(20);
        // create a button to submit the book details
        submitButton = new JButton("Add It");
        // create a text field to display error messages
        alarmField = new JTextField(20);
        alarmField.setBorder(null);
        alarmField.setBackground(null);
        alarmField.setForeground(Color.red);

        // add an action listener to the submit button to handle the request

        submitButton.addActionListener(new ActionListener() {

            @Override
            // call the submit() method and refreshes the ManageBooksWindow
            public void actionPerformed(ActionEvent event) {
                submit();
                // create a new ManageBooksWindow after adding the book
                SwingUtilities.invokeLater(() -> {
                    try {
                        ManageBooksWindow manageBooksWindow = new ManageBooksWindow();
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });

        // add the labels, text fields, button, and error message field to the panel
        panel.add(bookNameLabel);
        panel.add(bookNameField);
        panel.add(authorLabel);
        panel.add(authorField);
        panel.add(subjectLabel);
        panel.add(subjectField);
        panel.add(contentLabel);
        panel.add(contentField);
        panel.add(submitButton);
        panel.add(alarmField);

        // add the panel to the singleton window and set its properties
        singletonWindow.add(panel, BorderLayout.CENTER);
        singletonWindow.setSize(300, 300);
        singletonWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        singletonWindow.setLocationRelativeTo(null);
        singletonWindow.setVisible(true);
    }

    // define a method which submits the book details as a JSON payload to the server using a POST request
    public void submit() {
        String bookName = bookNameField.getText();
        String author = authorField.getText();
        String subject = subjectField.getText();
        String content = contentField.getText();
        // initialize variables to store HTTP response
        int statusCode = -1;
        StringBuilder response = new StringBuilder();

        try {
            // create a new Book object with the input fields
            Book book = new Book();
            book.setBookName(bookName);
            book.setAuthor(author);
            book.setSubject(subject);
            book.setContent(content);

            // set up an HTTP connection to the REST API endpoint
            URL url = new URL("http://localhost:8080/addBook");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            // convert the Book object to a JSON payload and send it in the request body
            String payload = objectMapper.writeValueAsString(book);
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
        LOG.info(String.format("Response %s", response));
    }

}


