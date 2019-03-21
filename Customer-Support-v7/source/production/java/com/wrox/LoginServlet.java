/*
 * Elbekov Fozil
 * 3/21/2019
 * */
package com.wrox;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.sql.*;

@WebServlet(
        name = "loginServlet",
        urlPatterns = "/login"
)
public class LoginServlet extends HttpServlet
{
    private static final Map<String, String> userDatabase = new Hashtable<>();

    static {
        //userDatabase.put("Nicholas", "password");
        //userDatabase.put("Sarah", "drowssap");
        //userDatabase.put("Mike", "wordpass");
        //userDatabase.put("John", "green");
    	// open database connection
    	try {
    		Class.forName("com.mysql.jdbc.Driver"); 
    		Connection conn = DriverManager.getConnection(
    				"jdbc:mysql://localhost:3306/customersupport", "root", "password");
    		// try to find a row where the username and password match

    		Statement stmt = conn.createStatement();
    		ResultSet rslt = stmt.executeQuery(
    				"SELECT * FROM User");

    		// no rows match, login is bad
    		while(rslt.next())
    			userDatabase.put(rslt.getString(2), rslt.getString(5));
    		conn.close();        
        } catch(SQLException e) {
            System.out.println(e); 
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        if(request.getParameter("logout") != null)
        {
            session.invalidate();
            response.sendRedirect("login");
            return;
        }
        else if(session.getAttribute("username") != null)
        {
            response.sendRedirect("tickets");
            return;
        }

        request.setAttribute("loginFailed", false);
        request.getRequestDispatcher("/WEB-INF/jsp/view/login.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        if(session.getAttribute("username") != null)
        {
            response.sendRedirect("tickets");
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
     // verify that the username and password aren't empty
        if(username == null || password == null
                || !LoginServlet.userDatabase.containsKey(username) ||
               !password.equals(LoginServlet.userDatabase.get(username)))
        {
            request.setAttribute("loginFailed", true);
            request.getRequestDispatcher("/WEB-INF/jsp/view/login.jsp")
                   .forward(request, response);
        }
        else
        {
            session.setAttribute("username", username);
            request.changeSessionId();
            response.sendRedirect("tickets");
        }   
        
    }
}
