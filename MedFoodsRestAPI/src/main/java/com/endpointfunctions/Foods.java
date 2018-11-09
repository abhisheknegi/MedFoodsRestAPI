package com.endpointfunctions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class Foods {

	public static JSONObject getFoods(Connection c) {
		JSONObject response = new JSONObject();
		JSONArray profileArray = new JSONArray();
		try {
			PreparedStatement ps = c
					.prepareStatement("select food_id, name, description, disclaimer, image from foods");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("foodId", rs.getInt(1));
				jo.put("name", rs.getString(2));
				jo.put("description", rs.getString(3));
				jo.put("disclaimer", rs.getString(4));
				jo.put("image", rs.getString(5));
				profileArray.add(jo);
			}

			response.put("count", profileArray.size());
			response.put("foodItems", profileArray);

			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQL Error => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			response.put("Error", "SQL Exception in getFoods, check logs.");
		}

		return response;
	}

	public static JSONObject getFoodData(Connection c, int foodId) {

		JSONObject response = new JSONObject();

		try {
			PreparedStatement ps = c
					.prepareStatement("select name, description, disclaimer from foods where food_id = ?");
			ps.setInt(1, foodId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				response.put("foodId", foodId);
				response.put("name", rs.getString(1));
				response.put("description", rs.getString(2));
				response.put("disclaimer", rs.getString(3));
				response = getActiveIngredients(c, foodId, response);
				response = getMetabolites(c, foodId, response);
				response = getRecommendationData(c, foodId, response);
			} else {
				response.put("response", "Error => Food item not found.");
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQL Error => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			response.put("Error", "SQL Exception in getFoodData, check logs.");
		}
		
		if(response.containsKey("Error")) {
			String errmsg = (String) response.get("Error");
			response.clear();
			response.put("Error", errmsg);
		}

		return response;
	}

	public static JSONObject getActiveIngredients(Connection c, int foodId, JSONObject response) {
		try {
			PreparedStatement ps = c
					.prepareStatement("select active_ingredient_name from food_active_ingredients where food_id = ?");
			ps.setInt(1, foodId);
			ResultSet rs = ps.executeQuery();
			List<String> items = new ArrayList<>();
			while (rs.next()) {
				items.add(rs.getString(1));
			}
			response.put("activeIngredient", String.join(",", items));
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQL Error getActiveIngredients => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			response.put("Error", "SQL Exception in getActiveIngredients, check logs.");
		}

		return response;
	}

	public static JSONObject getMetabolites(Connection c, int foodId, JSONObject response) {
		try {
			PreparedStatement ps = c.prepareStatement("select metabolite_name from food_metabolites where food_id = ?");
			ps.setInt(1, foodId);
			ResultSet rs = ps.executeQuery();
			List<String> items = new ArrayList<>();
			while (rs.next()) {
				items.add(rs.getString(1));
			}
			response.put("metabolite", String.join(",", items));
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQL Error getMetabolites => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			response.put("Error", "SQL Exception in getMetabolites, check logs.");
		}

		return response;
	}

	public static JSONObject getRecommendationData(Connection c, int foodId, JSONObject response) {
		try {
			PreparedStatement ps = c.prepareStatement(
					"select recommendation_id, consumption_mode, result, dosage_lower, dosage_upper, dosage_frequency, dosage_unit, condition_id, symptom_id from recommendations where food_id = ?");
			ps.setInt(1, foodId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				response.put("recommendationId", rs.getInt(1));
				response.put("consumptionMode", rs.getString(2));
				response.put("result", rs.getString(3));

				JSONObject dosage = new JSONObject();
				dosage.put("lowerLimit", rs.getInt(4));
				dosage.put("upperLimit", rs.getInt(5));
				dosage.put("frequency", rs.getString(6));
				dosage.put("unit", rs.getString(7));
				response.put("dosage", dosage);

				response = Conditions.getConditionData(c, rs.getInt(8), response);
				response = Symptoms.getSymptomData(c, rs.getInt(9), response);
				response = getRecommendationReferences(c, rs.getInt(1), response);
				
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQL Error getRecommendationData => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			response.put("Error", "SQL Exception in getRecommendationData, check logs.");
		}

		return response;
	}

	public static JSONObject getRecommendationReferences(Connection c, int recommendationId, JSONObject response) {
		JSONArray referenceArray = new JSONArray();
		try {
			PreparedStatement ps = c.prepareStatement("select reference, reference_type from bibliography where recommendation_id = ?");
			ps.setInt(1, recommendationId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("type", rs.getString(2));
				jo.put("reference", rs.getString(1));
				referenceArray.add(jo);
			}
			response.put("references", referenceArray);
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQL Error getMetabolites => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			response.put("Error", "SQL Exception in getRecommendationReferences, check logs.");
		}

		return response;
	}

	// To be completed
	public static JSONObject postFoods(Connection c, JSONObject input) throws SQLException {

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
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQL Error => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			if (e.getSQLState().equals("23505")) {
				response.put("Error", "Profile already exists.");
				c.rollback();
			}
		}
		if (i == 1) {
			try {
				ps = c.prepareStatement(
						"insert into users (user_id, password_salt, password_hash, email, last_update) values(?,?,?,?,?)");
				ps.setString(1, input.get("userId").toString());
				ps.setString(2, input.get("passwordSalt").toString());
				ps.setString(3, input.get("passwordHash").toString());
				ps.setString(4, input.get("email").toString());
				ps.setObject(5, LocalDateTime.now());
				i = ps.executeUpdate();
				ps.close();
				if (i == 1) {
					c.commit();
					response.put("response", "Success");
				} else {
					c.rollback();
					response.put("response", "Error");
				}
			} catch (SQLException e) {
				c.rollback();
				System.out.println("SQL Error => " + e.getErrorCode() + ", Msg => " + e.getMessage());
				if (e.getSQLState().equals("23505")) {
					response.put("Error", "User already exists.");
				}
			}
		}

		return response;

	}

}
