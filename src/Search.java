
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Servlet implementation class Search.
 * The Search class is responsible for allowing the user to look
 * up specific markets that meet their specifications. The user can
 * provide specifications such as State, Town name, Zip Code, or look
 * up all of the markets within a certain radius of their zip code. 
 * The Search class also uses the ViewSingle class which allows the user to
 * click on a farmers market and view more details about that individual market. 
 * The user may use the left and right arrow keys for general searches, but
 * the keys are not used when displaying zip code radius searches. 
 */
@WebServlet("/FarmersMarkets/search")
public class Search extends HttpServlet {
	public int pageNum = 0;
	public boolean submitted = false;
	public boolean zip_submitted = false;
	public String sub_city = "";
	public String sub_state = "";
	public String sub_zip = "";
	public String distance = "";
	public double lat = 0.0;
	public double lon = 0.0;
	public int counter = 0;
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
     * The doGet function is responsible for displaying the Search results. This is  done by
     * utilizing the HTTPServletResponse to write HTML to the page. The Search page displays
     * the logo of the website, input boxes that allow the user to search for desired specifications,
     * and displays the results of these searches.
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
        out.print("<li><a href=\"viewall\">View All</a></li>");
        out.print("<li> <a href=\"search\" class = \"active\">Search</a></li> </ul> </body>");
        out.print("<html><body><div align='center'><img src='https://i.imgur.com/gY5BAUT.jpg' height = '200' width = '308' vspace='30'/></div></body></html>");
        
        
        //Code below established the URL
		String url = "jdbc:mysql://localhost:3306/hw05?serverTimezone=UTC";
		Connection con = null;
		try {
			//code below establishes a connect to the SQL database.
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(url, "swagmaster", "Password123*");
			
			//The html code below is used to implement the text boxes where the
			//user can input the locations to search by
			out.print("<body>");
			out.print("<div align = 'center'>");
			out.print("<form method = 'post' action = 'http://localhost:8080/Homework5/FarmersMarkets/search'>");
			out.print("City: <input type='text' name='city'>");
			out.print("  State: <input type='text' name='state'>");
			out.print("  Zipcode: <input type='text' name='zip'>");
			out.print("  Range: <input type='text' name='range'>");
			out.print("<input type='submit' value='Submit'></form>");
			
			
			out.print("</body> </div>");
			
			//Starts creating the SQL Query to look for specific markets
			String query = "SELECT * " + 
					  "FROM hw05.FarmersMarkets";
			
			int size = 0;
			//Code below finds the number of markets found with specific specs
			if (submitted == true)
			{
				query = "SELECT * FROM hw05.FarmersMarkets "; 
				ArrayList<String> conditions = new ArrayList<String>();
				if (sub_city != "" || sub_state != "" || sub_zip != "" )
					query += ("as f WHERE ");
				if (sub_city != "")
					conditions.add("f.city LIKE '" + sub_city + "'");
				if (sub_state != "")
					conditions.add("f.State LIKE '" + sub_state + "'");
				if (sub_zip != "")
					conditions.add("f.zip LIKE '" + sub_zip + "'");
				if (sub_city != "" || sub_state != "" || sub_zip != "" )
				{
					String result = conditions.parallelStream().collect(Collectors.joining(" AND "));
					query += result;
				}
				
				try(PreparedStatement statement = con.prepareStatement(query))
				{
					try (ResultSet rs = statement.executeQuery()) {
						while(rs.next())
						{
							size++;
						}
					}
				}
			}
					
			
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
			pageNum = getQueryParams(requestURL.toString());
			
			
			//Code below is used to implement the query below that will grab the next 100 farmers markets
			//That meet the desired specs and display them.
			if (submitted == true && zip_submitted == false)
			{
				query = "SELECT * FROM hw05.FarmersMarkets "; 
				ArrayList<String> conditions = new ArrayList<String>();
				if (sub_city != "" || sub_state != "" || sub_zip != "" )
					query += ("as f WHERE ");
				if (sub_city != "")
					conditions.add("f.city LIKE '" + sub_city + "'");
				if (sub_state != "")
					conditions.add("f.State LIKE '" + sub_state + "'");
				if (sub_zip != "")
					conditions.add("f.zip LIKE '" + sub_zip + "'");
				if (sub_city != "" || sub_state != "" || sub_zip != "" )
				{
					String result = conditions.parallelStream().collect(Collectors.joining(" AND "));
					query += result;
				}
				query += " LIMIT ? OFFSET ?";
				
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
					statement.setInt(1, pageSize);
					statement.setInt(2, pageNum*pageSize);
					try(ResultSet rs = statement.executeQuery())
					{
					   while (rs.next()) 
					   {
						 //Prints out the Name of the market, along with the City and State
						   //Name of the market is a link to a page that allows the user to see more detail about the individual market
							out.print("<tr>");
							out.print("<td> <a href='http://localhost:8080/Homework5/FarmersMarkets/viewSingle?page="+ rs.getInt("FMID") + "'>" + rs.getString("MarketName") + "</a> </td>");
							out.print("<td>" + rs.getString("city") +  "</td> <td>" + rs.getString("State") + "</td></tr>");
					   }	
						out.print("</table>");
						out.print("<div style = 'width: 15%;'>");
						//Code below implements the Left and Right arrow of the page. The buttons update the URL Query String which allows them to see
						//The next or previous 100 farmers markets. 
						if (pageNum != 0)
							out.print("<a style = 'float: left;' href = 'http://localhost:8080/Homework5/FarmersMarkets/search?page=" + (pageNum-1) +"'> &#10229 </a>");
						if (pageNum < size/100)
							out.print("<a style = 'float: right;' href = 'http://localhost:8080/Homework5/FarmersMarkets/search?page=" + (pageNum+1) + "'> &#10230 </a>");
						out.print("</div></div>");
					}
				}	
			}
			
			//If the user is searching for markets within a certain radius of a zip code
			if (submitted == true && zip_submitted == true)
			{
				//Find the lat. and lon. for specific zip code in the Zip Code database.
				query = "SELECT * FROM hw05.zipcodes where zip_code = ?";
				try(PreparedStatement statement = con.prepareStatement(query))
				{
					statement.setInt(1, Integer.parseInt(sub_zip));
					try(ResultSet rs = statement.executeQuery())
					{
						//Save the lat and lon. to the global variables.
						rs.next();
						lat = rs.getDouble("latitude");
						lon = rs.getDouble("longitude");
					}
				}
				//Search for every market in the databse. 
				query = "SELECT * FROM hw05.FarmersMarkets";
				try(PreparedStatement statement = con.prepareStatement(query))
				{
					try(ResultSet rs = statement.executeQuery())
					{
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
						
						while (rs.next()) {
							//Calculates the distance between the current market and the zip code that was entered. 
							double R = 3958.7559;
							double dLat = Math.toRadians(rs.getDouble("y") - lat);
					        double dLon = Math.toRadians(rs.getDouble("x") - lon);
					        double lat1 = Math.toRadians(lat);
					        double lat2 = Math.toRadians(rs.getDouble("y"));
					 
					        double a = Math.pow(Math.sin(dLat / 2),2) + Math.cos(lat1)*Math.cos(lat2)*Math.pow(Math.sin(dLon / 2), 2); 
					        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)); 
					        double result =  R * c;
					        
					        //If the market is within the entered radius. 
					        if (result <= Double.parseDouble(distance))
					        {
					        	//Prints out the Name of the market, along with the City and State
								   //Name of the market is a link to a page that allows the user to see more detail about the individual market
								out.print("<tr>");
								out.print("<td> <a href='http://localhost:8080/Homework5/FarmersMarkets/viewSingle?page="+ rs.getInt("FMID") + "'>" + rs.getString("MarketName") + "</a> </td>");
								out.print("<td>" + rs.getString("city") +  "</td> <td>" + rs.getString("State") + "</td></tr>");
					        }
							
						}
						out.print("</table></div>");
					}
					
				   }
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
    }
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     * The doPost function is responsible for grabbing and saving the submitted city, state,
     * zip code and distance when the user clicks the "Submit" function which calls the post
     * function. This post function will be used ot save values to variables and flags which
     * allows the program to run and output the correct results. 
     * 
     * @param request the request that is sent to the server
     * @param response the response that is recieved from the server
     * @throws IOException If the PrintWriter cannot be established
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        submitted = true;
        pageNum = 0;
        sub_city = "";
        sub_state = "";
        sub_zip = "";
        distance = "";
        sub_city = request.getParameter("city");
        sub_state = request.getParameter("state");
        sub_zip =request.getParameter("zip");
        distance = request.getParameter("range");
        
        //If the user is searching for markets within a certain radius of a given zipcode
        if (sub_zip != "" && distance != "" && sub_city == "" && sub_state == "")
        	zip_submitted = true;
        //general search
        else
        	zip_submitted = false;
        
        //Go back to main code. 
        doGet(request, response);
    }

}