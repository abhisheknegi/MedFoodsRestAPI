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
			PreparedStatement ps = c.prepareStatement("select name, description from conditions");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				JSONObject jo = new JSONObject();
				jo.put("name", rs.getString(1));
				jo.put("description", rs.getString(2));
				profileArray.add(jo);
			}

			response.put("count", profileArray.size());
			response.put("conditions", profileArray);

			rs.close();
			ps.close();
		} catch (SQLException e) {

		}

		return response;
	}

	public static JSONObject getConditionData(Connection c, int condId, JSONObject response) {
		try {
			PreparedStatement ps = c
					.prepareStatement("select name, description from conditions where condition_id = ?");
			ps.setInt(1, condId);
			ResultSet rs1 = ps.executeQuery();
			if (rs1.next()) {
				JSONObject condition = new JSONObject();
				condition.put("name", rs1.getString(1));
				condition.put("description", rs1.getString(2));
				response.put("condition", condition);
			}
			rs1.close();
			ps.close();
		} catch (SQLException e) {

		}

		return response;
	}

}
