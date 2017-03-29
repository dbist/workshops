package com.hortonworks.phoenix;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class PhClientTest {
	public static void main(String[] args) throws SQLException 
	{
		System.out.println("Running Phoenix Java Test Client against Secured Cluster");
		org.apache.log4j.BasicConfigurator.configure();
		@SuppressWarnings("unused")
		Statement stmt = null;
		ResultSet rset = null;
		
		String jdbcUrl;
		String query;
		String colToRead;
		
		try 
		{
			Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
		} 
		catch (ClassNotFoundException e1) 
		{
			System.out.println("Exception Loading Driver");
			e1.printStackTrace();
		}
		
		try 
		{
			Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
		} 
		catch (ClassNotFoundException e1) 
		{
			System.out.println("Exception Loading Driver");
			e1.printStackTrace();
		}
				
		try
		{

			Properties p = new Properties();
			p.load(new FileReader(new File("./conf/app.conf")));
			
			jdbcUrl=p.getProperty("jdbcURL");
			query=p.getProperty("query");
			colToRead=p.getProperty("colToRead");

			System.out.println("Attempting to get a connection");			
			Connection con = DriverManager.getConnection(jdbcUrl);
			System.out.println("Connection is successful");			
			stmt = con.createStatement();
				
			PreparedStatement statement = con.prepareStatement(query);
			System.out.println("Executing Query = "+query);									
			rset = statement.executeQuery();
			while (rset.next()) 
			{
				System.out.println("Reading Column = "+colToRead);						
				System.out.println("Value of Column "+colToRead + ", is - " + rset.getString(colToRead));
			}
			statement.close();
			con.close();
			System.out.println("Attempting to get a connection");						
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
