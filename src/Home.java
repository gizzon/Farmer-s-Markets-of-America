import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class HelloWorldServlet
 * 
 * The Home class of this project is used to display the
 * "Home" page of the Farmer's Market Website. From this
 * home page the user is able to access other pages on the
 * website such as viewing all of the markets, and searching
 * for specific markets. 
 */
@WebServlet("/FarmersMarkets/home")
public class Home extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     * 
     * The doGet function is responsible for displaying the home page. This is  done by
     * utilizing the HTTPServletResponse to write HTML to the page. The home page displays
     * the logo of the website, along with links to the "View All" page, and to the "Search"
     * page.
     * 
     * @param request the request that is sent to the server
     * @param response the response that is recieved from the server
     * @throws IOException If the PrintWriter cannot be established
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
      //The code below is used to display the menu bar at the top of the page, along with the logo for the website
        out.print("<style> ul { list-syle-type: none; margin: 0; padding: 0; overflow: hidden; background-color: #B7BDBD;}");
        out.print("li {float: left;}");
        out.print("li a {display: inline-block; color: white; text-align: center; padding: 14px 16px; text-decoration: none;}");
        out.print("li a: hover{ background-color: #111;}");
        out.print(".active{background-color: #2d2a3d;} </style>");
        out.print("<body> <ul>");
        out.print("<li><a href=\"home\" class=\"active\">Home</a></li>");
        out.print("<li><a href=\"viewall\">View All</a></li>");
        out.print("<li> <a href=\"search\">Search</a></li> </ul> </body>");
        out.print("<html><body><div align='center'><img src='https://i.imgur.com/gY5BAUT.jpg' height = '400' width = '616'/></div></body></html>");
        
		
    }

}