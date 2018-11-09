package com.endpointfunctions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class Conditions {

	public static JSONObject getConditions(Connection c) {
		JSONObject response = new JSONObject();
		JSONArray profileArray = new JSONArray();
		try {
			PreparedStatement ps = c.prepareStatement("select condition_id, name, description from conditions");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("conditionId", rs.getInt(1));
				jo.put("name", rs.getString(2));
				jo.put("description", rs.getString(3));
				profileArray.add(jo);
			}

			response.put("count", profileArray.size());
			response.put("conditions", profileArray);

			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQL Error getConditions => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			response.put("Error", "SQL Exception in getConditions, check logs.");
		}

		return response;
	}

	public static JSONObject getConditionData(Connection c, int condId, JSONObject response) {
		try {
			PreparedStatement ps = c
					.prepareStatement("select name, description from conditions where condition_id = ?");
			ps.setInt(1, condId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				JSONObject condition = new JSONObject();
				condition.put("conditionId", condId);
				condition.put("name", rs.getString(1));
				condition.put("description", rs.getString(2));
				response.put("condition", condition);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			System.out.println("SQL Error getConditionData => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			response.put("Error", "SQL Exception in getConditionData, check logs.");
		}

		return response;
	}

}
