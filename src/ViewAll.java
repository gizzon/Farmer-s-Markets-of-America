import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Servlet implementation class ViewAll. The ViewAll
 * class is responsible for displaying all of the Farmers Markets in the 
 * SQL Database. This class also implements pagination by showing only
 * 100 markets at a time. The user can look at the next or previous 100
 * markets by using the left and right arrow keys on the bottom of the page.
 * The ViewAll class also uses the ViewSingle class which allows the user to
 * click on a farmers market and view more details about that individual market.
 * 
 */
@WebServlet("/FarmersMarkets/viewall")
public class ViewAll extends HttpServlet {
    private static final long serialVersionUID = 1L;
      

    /**
     * The getQueryParams function is responsible for taking the
     * current page's url and returning the current page number
     * the user is requesting. This is done by first isolating
     * the query string, and then returning the page number that
     * is found at the end. 
     * 
     * @param url the current URL of the page
     * @return The page number the user is requesting to see. 
     */
    public static int getQueryParams(String url) {
    	if (url.indexOf("?") == -1)
    	{
    		return 0;
    	}
    	String[] urlParts = url.split("\\?");
        if (urlParts.length > 1) {
            String query = urlParts[1];
            String[] temp = query.split("=");
            if (Integer.parseInt(temp[1]) == -1)
            	return 0;
            return Integer.parseInt(temp[1]);
        }
        return 0;
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     * 
     * The doGet function is responsible for displaying the ViewAll. This is  done by
     * utilizing the HTTPServletResponse to write HTML to the page. The View All page displays
     * the logo of the website, along with a list of all of the farmers markets. The list of 
     * markets is broken down into pages. 
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
        out.print("<li><a href=\"home\">Home</a></li>");
        out.print("<li><a href=\"viewall\" class = \"active\">View All</a></li>");
        out.print("<li> <a href=\"search\">Search</a></li> </ul> </body>");
        out.print("<html><body><div align='center'><img src='https://i.imgur.com/gY5BAUT.jpg' height = '200' width = '308'/></div></body></html>");
        
        
        //Establishes connection to URL
		String url = "jdbc:mysql://localhost:3306/hw05?serverTimezone=UTC";
		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(url, "swagmaster", "Password123*");
			
			//Creates SQL query that will be used to select all of the farmets markets
			String query = "SELECT * " + 
					  "FROM hw05.FarmersMarkets";
			
			int size = 0;
			//Records the size of the number of Markets in the database.
			try(PreparedStatement statement = con.prepareStatement(query))
			{
				try (ResultSet rs = statement.executeQuery()) {
					while(rs.next())
					{
						size++;
					}
				}
			}
			//Sets default number of markets to be shown ona page.
			int pageSize = 100; //length you want one page to be
			
			//The code below is used to grab the entire URL of the page
			StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
		    String queryString = request.getQueryString();

		    if (queryString == null) {
		        requestURL.toString();
		    } else {
		        requestURL.append('?').append(queryString).toString();
		    }
		    
		    //Utilizes the getQueryParams() function to get the current page number.
			int pageNum = getQueryParams(requestURL.toString());
			
			//Query below is used to grab the next 100 farmers markets to be displayed.
			query = "SELECT * FROM hw05.FarmersMarkets LIMIT ?, ?";
			
			//HTML code below is used to set up the formatting for the table that will display the markets
			out.print("<style> <head>");
			out.print("table {");
			out.print("font-family: arial, sans-serif;");
			out.print("border-collapse: collapse;");
			out.print("width: 100%;}");
			out.print(" td, th {");
			out.print("border: 1px solid #7e78a2;");
			out.print("text-align: left;");
			out.print("padding: 8px;}");
			out.print("tr:nth-child(even) {background-color: #7e78a2;}");
			out.print("</style> </head>");
			out.print("<div align = 'center'> <table style='width:90%'> <tr> <th>Market Name</th> <th>City</th> <th>State</th> </tr>");
		    
			//Code below executes the query to grab the 100 Farmer's Markets
			try(PreparedStatement statement = con.prepareStatement(query))
			{
				statement.setInt(1, pageNum*pageSize);
				statement.setInt(2, pageSize);
				try(ResultSet rs = statement.executeQuery())
				{
				   while (rs.next()) {
					   	//Prints out the Name of the market, along with the City and State
					   //Name of the market is a link to a page that allows the user to see more detail about the individual market
						out.print("<tr>");
						out.print("<td> <a href='http://localhost:8080/Homework5/FarmersMarkets/viewSingle?page="+ rs.getInt("FMID") + "'>" + rs.getString("MarketName") + "</a> </td>");
						out.print("<td>" + rs.getString("city") +  "</td> <td>" + rs.getString("State") + "</td></tr>");
				   }
			    out.print("</table>");
				}
				out.print("<div style = 'width: 15%;'>");
				//Code below implements the Left and Right arrow of the page. The buttons update the URL Query String which allows them to see
				//The next or previous 100 farmers markets. 
				if (pageNum != 0)
					out.print("<a style = 'float: left;' href = 'http://localhost:8080/Homework5/FarmersMarkets/viewall?page=" + (pageNum-1) +"'> &#10229 </a>");
				if (pageNum < size/100)
					out.print("<a style = 'float: right;' href = 'http://localhost:8080/Homework5/FarmersMarkets/viewall?page=" + (pageNum+1) + "'> &#10230 </a>");
				out.print("</div></div>");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }

}