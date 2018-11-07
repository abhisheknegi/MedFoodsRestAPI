package com.endpointfunctions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class Users {

	public static JSONObject getUsers(Connection c) {
		JSONArray profileArray = new JSONArray();
		JSONObject response = new JSONObject();
		try {
			PreparedStatement ps = c.prepareStatement("select user_id, email from users");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("userId", rs.getString(1));
				jo.put("email", rs.getString(2));
				profileArray.add(jo);
			}

			response.put("count", profileArray.size());
			response.put("users", profileArray);

			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQL Error => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			response.put("Error", "SQL Exception, check logs.");
		}

		return response;
	}

	public static JSONObject authUser(Connection c, JSONObject input) {
		JSONObject response = new JSONObject();

		try {
			PreparedStatement ps = c
					.prepareStatement("select user_id, password_salt, password_hash from users where user_id = ?");
			ps.setString(1, input.get("userId").toString());
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				if (rs.getString(2).trim().equals(input.get("passwordSalt").toString())
						&& rs.getString(3).equals(input.get("passwordHash").toString())) {
					response.put("Success", "User Matched");
				} else {
					response.put("Error", "User Found, but password failed.");
				}
			} else {
				response.put("Error", "User not found.");
			}

			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQL Error => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			response.put("Error", "SQL Exception, check logs.");
		}
		return response;
	}

	public static JSONObject getUserData(Connection c, String userId) {
		JSONObject response = new JSONObject();

		try {
			PreparedStatement ps = c.prepareStatement("select user_id, email from users where user_id = ?");
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				response.put("userId", rs.getString(1));
				response.put("email", rs.getString(2));
			} else {
				response.put("response", "Error => User not found.");
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQL Error => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			response.put("Error", "SQL Exception, check logs.");
		}

		return response;
	}

	public static JSONObject authUserData(Connection c, String userId, JSONObject input) {
		JSONObject response = new JSONObject();

		try {
			PreparedStatement ps = c
					.prepareStatement("select user_id, password_salt, password_hash from users where user_id = ?");
			ps.setString(1, userId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				if (rs.getString(2).trim().equals(input.get("passwordSalt").toString())
						&& rs.getString(3).equals(input.get("passwordHash").toString())) {
					response.put("Success", "User Matched");
				} else {
					response.put("Error", "User Found, but password failed.");
				}
			} else {
				response.put("Error", "User not found.");
			}

			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQL Error => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			response.put("Error", "SQL Exception, check logs.");
		}
		return response;
	}

}
