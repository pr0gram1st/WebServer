package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Processor of HTTP request.
 */
public class Processor {
    private final Socket socket;
    private final HttpRequest request;
    public String response = "";

    public Processor(Socket socket, HttpRequest request) {
        this.socket = socket;
        this.request = request;
    }

    void PrintFiles(File[] arr, int index, int level) {
        if (index == arr.length) return;
        for (int i = 0; i < level; i++) this.response += "&nbsp;&nbsp;&nbsp;";
        if (arr[index].isFile()) this.response += arr[index].getName() + "<br>";
        else if (arr[index].isDirectory()) {
            this.response += "[" + arr[index].getName() + "]<br>";
            PrintFiles(arr[index].listFiles(), 0, level + 1);
        }
        PrintFiles(arr, ++index, level);
    }

    public void process() throws IOException {
        System.out.println("Got request:");
        System.out.println(request.toString());
        System.out.flush();

        String s = request.toString();
        if(s.contains("create")){
            String name = "";
            int pos = 12;
            while(s.charAt(pos) != ' '){
                char c = s.charAt(pos);
                name += c;
                pos++;
            }
            File myObj = new File(name);
            if (myObj.createNewFile()) {
                this.response += ("File created: " + myObj.getName());
            }
            else {
                this.response += "File already exists.";
            }
        }
        else if(s.contains("write")){
            String filename = "";
            int pos = 11;
            while(s.charAt(pos) != '/'){
                char c = s.charAt(pos);
                filename += c;
                pos++;
            }
            pos++;
            String text = "";
            while(s.charAt(pos) != ' '){
                char c = s.charAt(pos);
                text += c;
                pos++;
            }
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write(text);
            myWriter.close();
            this.response += "Successfully wrote to the file.";
        }
        else if(s.charAt(5) == 'c' && s.charAt(6) == 'a' && s.charAt(7) == 't'){
            String filename = "";
            int pos = 9;
            while(s.charAt(pos) != ' '){
                char c = s.charAt(pos);
                filename += c;
                pos++;
            }
            BufferedReader myReader = new BufferedReader(new FileReader(filename));
            String strCurrentLine = "";
            while ((strCurrentLine = myReader.readLine()) != null) {
                this.response += strCurrentLine;
            }
            myReader.close();
        }
        else if(s.charAt(5) == 't' && s.charAt(6) == 'r' && s.charAt(7) == 'e' && s.charAt(8) == 'e'){
            String maindirpath = "/Users/adiletkemelkhan/IdeaProjects/WebServer";
            File maindir = new File(maindirpath);
            if (maindir.exists() && maindir.isDirectory()) {
                File arr[] = maindir.listFiles();
                this.response += "**********************************************<br>";
                this.response += "Files from main directory : " + maindir + "<br>";
                this.response += "**********************************************<br>";
                PrintFiles(arr, 0, 0);
            }
        }
        PrintWriter output = new PrintWriter(socket.getOutputStream());

        // We are returning a simple web page now.
        output.println("HTTP/1.1 200 OK");
        output.println("Content-Type: text/html; charset=utf-8");
        output.println();
        output.println("<html>");
        output.println("<head><title>Hello</title></head>");
        output.println("<body><p>" + this.response + "</p></body>");
        output.println("</html>");
        output.flush();
        socket.close();
        this.response = "";
    }
}
