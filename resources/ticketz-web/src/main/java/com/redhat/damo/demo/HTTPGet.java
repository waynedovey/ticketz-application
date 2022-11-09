package com.redhat.damo.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;

public class HTTPGet {
    
    public static String Get(String URL) throws Exception {
                // Providing the website URL
                URL url = new URL(URL);

                // Creating an HTTP connection
                HttpURLConnection MyConn = (HttpURLConnection) url.openConnection();
        
                // Set the request method to "GET"
                MyConn.setRequestMethod("GET");
        
                // Collect the response code
                int responseCode = MyConn.getResponseCode();
                System.out.println("GET Response Code :: " + responseCode);
        
                if (responseCode == MyConn.HTTP_OK) {
                    // Create a reader with the input stream reader.
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            MyConn.getInputStream()));
                    String inputLine;
        
                    // Create a string buffer
                    StringBuffer response = new StringBuffer();
        
                    // Write each of the input line
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    //Show the output
                    System.out.println(response.toString());
                    return response.toString();
                } else {
                    System.out.println("Error found !!!");
                    return null;
                }

    }

    public static String Post(String URL, String content) throws Exception {
        // Providing the website URL
        URL url = new URL(URL);

        // Creating an HTTP connection
        HttpURLConnection MyConn = (HttpURLConnection) url.openConnection();

        // Set the request method to "POST"
        MyConn.setRequestMethod("POST");
        MyConn.setDoOutput(true);
        // Creating an output stream
        OutputStream out = MyConn.getOutputStream();

        // Defining the sting to post
        out.write(content.getBytes());
        // Collect the response code
        int responseCode = MyConn.getResponseCode();

//        if (responseCode > 299) {
//            return null;
//        }
        System.out.print("Value of http created is:" + MyConn.HTTP_CREATED + "\n");

//        if (responseCode == MyConn.HTTP_CREATED) {
            System.out.print("This is the response Code: " + responseCode + "\n");
            System.out.print("This is the response Message fromserver: "
                    + MyConn.getResponseMessage() + "\n");
//        } else {
//            System.out.print("GO HOME, EVERYBODY :( ");
//        }
        // Creating an input stream reader
        InputStreamReader in = new InputStreamReader(MyConn.getInputStream());
        // Creating a buffered reader
        BufferedReader buffer = new BufferedReader(in);
        // Creating a string buffer
        StringBuffer fromServer = new StringBuffer();
        String eachLine = null;

        // Writing each line of the document
        while ((eachLine = buffer.readLine()) != null) {
            fromServer.append(eachLine);
            fromServer.append(System.lineSeparator());
        }
        buffer.close();
        // Printing the html document
        System.out.print("Here is our webpage:\n" + fromServer);
        return fromServer.toString();
}
}
