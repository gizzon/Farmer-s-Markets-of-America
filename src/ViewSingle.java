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
 * Servlet implementation class ViewSingle. The ViewSingle
 * class is responsible for displaying a single Farmers Market in the 
 * SQL Database. This will display the name of the selected Farmer's
 * Market along with more information about it such as their social media,
 * and information about which types of items are available for purchase.
 * 
 */
@WebServlet("/FarmersMarkets/viewSingle")
public class ViewSingle extends HttpServlet {
    private static final long serialVersionUID = 1L;
      

    /**
     * The getQueryParams function is responsible for taking the
     * current page's url and returning the current Farmer's Market ID
     * the user is requesting. This is done by first isolating
     * the query string, and then returning the ID number that
     * is found at the end. 
     * 
     * @param url the current URL of the page
     * @return The Farmer's Market ID the user is requesting to see. 
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
     * The doGet function is responsible for displaying the single Farmers market. This is  done by
     * utilizing the HTTPServletResponse to write HTML to the page. The View Single page displays
     * the logo of the website, along with information about the Farmer's Market the user requested. 
     * 
     * @param request the request that is sent to the server
     * @param response the response that is recieved from the server
     * @throws IOException If the PrintWriter cannot be established
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
      //The code below is used to display the menu bar at the top of the page, along with the logo for the website
        out.print("<style> ul { list-syle-type: none; margin: 0; padding: 0; overflow: hidden; background-color: #B7BDBD;}");
        out.print("li {float: left;}");
        out.print("li a {display: inline-block; color: white; text-align: center; padding: 14px 16px; text-decoration: none;}");
        out.print("li a: hover{ background-color: #111;}");
        out.print(".active{background-color: #2d2a3d;} </style>");
        out.print("<body> <ul>");
        out.print("<li><a href=\"home\">Home</a></li>");
        out.print("<li><a href=\"viewall\">View All</a></li>");
        out.print("<li> <a href=\"search\">Search</a></li> </ul> </body>");
        out.print("<html><body><div align='center'><img src='https://i.imgur.com/gY5BAUT.jpg' height = '200' width = '308'/></div></body></html>");
        
        
      //Establishes connection to SQL database. 
		String url = "jdbc:mysql://localhost:3306/hw05?serverTimezone=UTC";
		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(url, "swagmaster", "Password123*");
			
			//The code below is used to grab the entire URL of the page
			StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
		    String queryString = request.getQueryString();

		    if (queryString == null) {
		        requestURL.toString();
		    } else {
		        requestURL.append('?').append(queryString).toString();
		    }
		    //Utilizes the getQueryParams() function to get the current Farmer's Market ID number.
			int farmerID = getQueryParams(requestURL.toString());
			
			//Query below is used to grab the specific farmers market to be displayed.
			String query = String.format("SELECT * FROM hw05.FarmersMarkets WHERE (FMID = ?)");
			
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
			out.print("<div align = 'center'> <table style='width:90%'> <tr></tr>");
		    
			//Code below executes the query to grab the Farmer's Market
			try(PreparedStatement statement = con.prepareStatement(query))
			{
				statement.setInt(1, farmerID);
				try(ResultSet rs = statement.executeQuery())
				{
				   while (rs.next()) {
					   //The code below prints out all of the information provided in the SQL 
					   //database for a specific Farmer's Market. This will also provide links
					   //to the social media and websites of the specific market (if they exist)
					   out.print("<tr> <td>Market Name</td> <td>" + rs.getString("MarketName") + "</td></tr>");
					   if(rs.getString("Website") != null)
						   out.print("<tr> <td>Website</td> <td>" + "<a href='" + rs.getString("Website") + "'>" + "Link" + "</a></td></tr>");
					   if(rs.getString("Facebook") != null)
						   out.print("<tr> <td>Facebook</td> <td>" + "<a href='" + rs.getString("Facebook") + "'>" + "Link" + "</a></td></tr>");
					   if(rs.getString("Twitter") != null)
						   out.print("<tr> <td>Twitter</td> <td>" + "<a href='" + rs.getString("Twitter") + "'>" + "Link" + "</a></td></tr>");
					   out.print("<tr> <td>Location</td> <td>" + rs.getString("street") + "		" + rs.getString("city")+ ", " + rs.getString("State") + " " + rs.getString("zip")+ "</td></tr>");
					   out.print("<tr> <td>Credit Card Accepted</td> <td>" + rs.getString("Credit") + "</td></tr>");
					   out.print("<tr> <td>Cash Accepted</td> <td>" + rs.getString("WICcash") + "</td></tr>");
					   out.print("<tr> <td>Organic</td> <td>" + rs.getString("Organic") + "</td></tr>");
					   out.print("<tr> <td>Baked Goods</td> <td>" + rs.getString("Bakedgoods") + "</td></tr>");
					   
					   out.print("<tr> <td>Cheese</td> <td>" + rs.getString("Cheese") + "</td></tr>");
					   out.print("<tr> <td>Crafts</td> <td>" + rs.getString("Crafts") + "</td></tr>");
					   out.print("<tr> <td>Flowers</td> <td>" + rs.getString("Flowers") + "</td></tr>");
					   out.print("<tr> <td>Eggs</td> <td>" + rs.getString("Eggs") + "</td></tr>");
					   out.print("<tr> <td>Seafood</td> <td>" + rs.getString("Seafood") + "</td></tr>");
					   out.print("<tr> <td>Herbs</td> <td>" + rs.getString("Herbs") + "</td></tr>");
					   out.print("<tr> <td>Vegetables</td> <td>" + rs.getString("Vegetables") + "</td></tr>");
					   out.print("<tr> <td>Honey</td> <td>" + rs.getString("Honey") + "</td></tr>");
					   out.print("<tr> <td>Jams</td> <td>" + rs.getString("Jams") + "</td></tr>");
					   out.print("<tr> <td>Maple</td> <td>" + rs.getString("Maple") + "</td></tr>");
					   out.print("<tr> <td>Meat</td> <td>" + rs.getString("Meat") + "</td></tr>");
					   out.print("<tr> <td>Nursery</td> <td>" + rs.getString("Nursery") + "</td></tr>");
					   out.print("<tr> <td>Nuts</td> <td>" + rs.getString("Nuts") + "</td></tr>");
					   out.print("<tr> <td>Plants</td> <td>" + rs.getString("Plants") + "</td></tr>");
					   out.print("<tr> <td>Poultry</td> <td>" + rs.getString("Poultry") + "</td></tr>");
					   out.print("<tr> <td>Prepared</td> <td>" + rs.getString("Prepared") + "</td></tr>");
					   out.print("<tr> <td>Soap</td> <td>" + rs.getString("Soap") + "</td></tr>");
					   out.print("<tr> <td>Trees</td> <td>" + rs.getString("Trees") + "</td></tr>");
					   out.print("<tr> <td>Wine</td> <td>" + rs.getString("Wine") + "</td></tr>");
					   out.print("<tr> <td>Coffee</td> <td>" + rs.getString("Coffee") + "</td></tr>");
					   out.print("<tr> <td>Beans</td> <td>" + rs.getString("Beans") + "</td></tr>");
					   out.print("<tr> <td>Fruits</td> <td>" + rs.getString("Fruits") + "</td></tr>");
					   out.print("<tr> <td>Grains</td> <td>" + rs.getString("Grains") + "</td></tr>");
					   out.print("<tr> <td>Juices</td> <td>" + rs.getString("Juices") + "</td></tr>");
					   out.print("<tr> <td>Mushrooms</td> <td>" + rs.getString("Mushrooms") + "</td></tr>");
					   out.print("<tr> <td>Pet Food</td> <td>" + rs.getString("PetFood") + "</td></tr>");
					   out.print("<tr> <td>Tofu</td> <td>" + rs.getString("Tofu") + "</td></tr>");
					   out.print("<tr> <td>Wild Harvested</td> <td>" + rs.getString("WildHarvested") + "</td></tr>");
					   
				   }
			    out.print("</table>");
				}
				out.print("</div>");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }

}