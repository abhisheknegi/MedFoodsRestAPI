package com.endpointfunctions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class Symptoms {
	
	public static JSONObject getSymptoms(Connection c) {
		JSONObject response = new JSONObject();
		JSONArray profileArray = new JSONArray();
		try {
			PreparedStatement ps = c.prepareStatement("select symptom_id, name, description from symptoms");
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("symptomId", rs.getInt(1));
				jo.put("name", rs.getString(2));
				jo.put("description", rs.getString(3));
				profileArray.add(jo);
			}

			response.put("count", profileArray.size());
			response.put("symptoms", profileArray);
			
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQL Error getSymptoms => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			response.put("Error", "SQL Exception in getSymptoms, check logs.");
		}

		return response;
	}
	
	public static JSONObject getSymptomData(Connection c, int sympId, JSONObject response) {
		try {
			PreparedStatement ps = c
					.prepareStatement("select name, description from symptoms where symptom_id = ?");
			ps.setInt(1, sympId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				JSONObject symptom = new JSONObject();
				symptom.put("symptomId", sympId);
				symptom.put("name", rs.getString(1));
				symptom.put("description", rs.getString(2));
				response.put("symptoms", symptom);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQL Error getSymptomData => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			response.put("Error", "SQL Exception in getSymptomData, check logs.");
		}

		return response;
	}

}
