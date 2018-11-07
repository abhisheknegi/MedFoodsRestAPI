package com.endpointfunctions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class Profiles {

	public static JSONObject getProfiles(Connection c) {
		JSONObject response = new JSONObject();
		JSONArray profileArray = new JSONArray();
		try {
			PreparedStatement ps = c.prepareStatement("select user_prof_id, user_id from user_profile");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("user_prof_id", rs.getString(1));
				jo.put("user_id", rs.getString(2));
				profileArray.add(jo);
			}

			response.put("count", profileArray.size());
			response.put("profiles", profileArray);

			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQL Error => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			response.put("Error", "SQL Exception, check logs.");
		}

		return response;
	}

	public static JSONObject getProfileData(Connection c, String profileId) {

		JSONObject response = new JSONObject();

		try {
			PreparedStatement ps = c.prepareStatement(
					"select user_prof_id, user_id, age_range_id, phone from user_profile where user_prof_id = ?");
			ps.setString(1, profileId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				response.put("user_prof_id", rs.getString(1));
				response.put("user_id", rs.getString(2));
				response.put("age_range_id", rs.getInt(3));
				response.put("phone", rs.getString(4));
			} else {
				response.put("response", "Error => Profile not found.");
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQL Error => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			response.put("Error", "SQL Exception, check logs.");
		}

		return response;
	}

	public static JSONObject postProfiles(Connection c, JSONObject input) {

		PreparedStatement ps;
		int i = 0;
		JSONObject response = new JSONObject();
		try {
			ps = c.prepareStatement(
					"insert into user_profile (user_prof_id, user_id, age_range_id, phone) values(?,?,?,?)");
			ps.setString(1, input.get("userProfileId").toString());
			ps.setString(2, input.get("userId").toString());
			ps.setInt(3, Integer.valueOf(input.get("ageRangeId").toString()));
			ps.setString(4, input.get("phone").toString());
			i = ps.executeUpdate();
			if (i == 1) {
				ps.close();
				ps = c.prepareStatement(
						"insert into users (user_id, password_salt, password_hash, email, last_update) values(?,?,?,?,?)");
				ps.setString(1, input.get("userId").toString());
				ps.setString(2, input.get("passwordSalt").toString());
				ps.setString(3, input.get("passwordHash").toString());
				ps.setString(4, input.get("email").toString());
				ps.setObject(5, LocalDateTime.now());
				i = ps.executeUpdate();
				ps.close();
			}
			if (i == 1) {
				c.commit();
				response.put("response", "Success");
			} else {
				c.rollback();
				response.put("response", "Error");
			}
		} catch (SQLException e) {
			System.out.println("SQL Error => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			response.put("Error", "SQL Exception, check logs.");
		}

		return response;
	}

	public static JSONObject deleteProfiles(Connection c, String profileId) {

		JSONObject response = new JSONObject();
		PreparedStatement ps;
		int i = 0;
		try {
			ps = c.prepareStatement("select user_id from user_profile where user_prof_id = ?");
			ps.setString(1, profileId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				ps = c.prepareStatement("delete from users where user_id = ?");
				ps.setString(1, rs.getString(1));
				if (ps.executeUpdate() == 1) {
					ps = c.prepareStatement("delete from user_profile where user_prof_id = ?");
					ps.setString(1, profileId);
					i = ps.executeUpdate();
				}
				if (i == 1) {
					c.commit();
					response.put("response", "Success");
				} else {
					c.rollback();
					response.put("response", "Error");
				}
			}
			ps.close();
			rs.close();
		} catch (SQLException e) {
			System.out.println("SQL Error => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			response.put("Error", "SQL Exception, check logs.");
		}

		return response;
	}

}
