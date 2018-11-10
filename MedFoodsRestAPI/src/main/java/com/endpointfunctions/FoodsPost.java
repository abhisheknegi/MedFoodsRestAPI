package com.endpointfunctions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

@SuppressWarnings("unchecked")
public class FoodsPost {

	public static JSONObject postFoods(Connection c, JSONObject input) throws SQLException {

		JSONObject basicValidationResult = foodInputBasicValidation(input);
		if (basicValidationResult.containsKey("ERROR")) {
			return basicValidationResult;
		} else {
			return basicValidationResult;
		}

	}

	@SuppressWarnings("rawtypes")
	private static JSONObject foodInputBasicValidation(JSONObject input) {

		JSONObject response = new JSONObject();
		JSONObject resp = new JSONObject();

		String generalTags = "name,description,disclaimer,consumptionMode,result,condition,symptom,activeIngredient,metabolite,dosage,references,source";
		String dosageTags = "lowerLimit,upperLimit,frequency,unit";
		String referenceTags = "reference,type";

		int cnt = 0;
		for (String tag : generalTags.split(",")) {
			if (!(input.containsKey(tag) && StringUtils.isNotBlank(input.get(tag).toString()))) {
				resp.put("err-" + String.valueOf(++cnt), ("\'" + tag + "\' tag is invalid."));
			} else {
				if ("dosage".equals(tag)) {
					LinkedHashMap dosage = (LinkedHashMap) input.get("dosage");
					for (String dosageTag : dosageTags.split(",")) {
						if (!(dosage.containsKey(dosageTag))) {
							resp.put("err-" + String.valueOf(++cnt), ("\'Dosage-" + dosageTag + "\' tag is missing."));
						} else {
							if (dosageTag.contains("Limit")) {
								if (dosage.get(dosageTag).getClass() != Integer.class) {
									resp.put("err-" + String.valueOf(++cnt),
											("\'Dosage-" + dosageTag + "\' tag is invalid."));
								}
							} else {
								if (StringUtils.isAllBlank(dosage.get(dosageTag).toString())) {
									resp.put("err-" + String.valueOf(++cnt),
											("\'Dosage-" + dosageTag + "\' tag is invalid."));
								}
							}
						}
					}
				}
				if ("references".equals(tag)) {
					ArrayList references = (ArrayList) input.get("references");
					for (int i = 0; i < references.size(); i++) {
						LinkedHashMap reference = (LinkedHashMap) references.get(i);
						for (String referenceTag : referenceTags.split(",")) {
							System.out.println("Tag ===> " + referenceTag);
							if (!reference.containsKey(referenceTag)) {
								resp.put("err-" + String.valueOf(++cnt),
										("\'Reference-" + referenceTag + "\' tag is missing."));
							} else {
								if (StringUtils.isAllBlank(reference.get(referenceTag).toString())) {
									resp.put("err-" + String.valueOf(++cnt),
											("\'Reference-" + referenceTag + "\' tag is invalid."));
								}
							}
						}
					}
				}
			}
		}

		if (resp.isEmpty()) {
			response.put("Validated", "All tags present.");
		} else {
			response.put("ERROR", resp);
		}

		return response;
	}

	public static JSONObject foodInputInDepthValidation(Connection c, JSONObject input) {

		JSONObject response = new JSONObject();
		JSONObject resp = new JSONObject();
		int cnt = 0;

		int foodId = 0;
		int consumptionModeId = 0;
		int resultId = 0;

		try {
			PreparedStatement ps = c
					.prepareStatement("select food_id, name, description, disclaimer, image from foods where name = ?");
			ps.setString(1, input.get("name").toString().toLowerCase());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				resp.put("err-" + String.valueOf(++cnt), input.get("name").toString() + " already exists in database");
			} else {
				ps.close();
				rs.close();
				ps = c.prepareStatement("select MAX(food_id) from foods");
				rs = ps.executeQuery();
				if (rs.next()) {
					foodId = rs.getInt(1) + 1;
				}
				ps.close();
				rs.close();
			}
		} catch (SQLException e) {
			System.out.println("SQL Error foods validation => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			resp.put("Error", "SQL Exception in foods validation, check logs.");
		}

		try {
			PreparedStatement ps = c.prepareStatement(
					"select consumption_mode_id, consumption_mode from consumption_modes where consumption_modes = ?");
			ps.setString(1, input.get("consumptionMode").toString().toLowerCase());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				resp.put("err-" + String.valueOf(++cnt),
						input.get("consumptionMode").toString() + " already exists in database");
			} else {
				ps.close();
				rs.close();
				ps = c.prepareStatement("select MAX(consumption_mode_id) from consumption_modes");
				rs = ps.executeQuery();
				if (rs.next()) {
					consumptionModeId = rs.getInt(1) + 1;
				}
				ps.close();
				rs.close();
			}
		} catch (SQLException e) {
			System.out.println("SQL Error consumption_modes => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			resp.put("Error", "SQL Exception in consumption_modes, check logs.");
		}

		try {
			PreparedStatement ps = c.prepareStatement("select result_id, result from results where result_id = ?");
			ps.setString(1, input.get("result").toString().toLowerCase());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				resp.put("err-" + String.valueOf(++cnt),
						input.get("result").toString() + " already exists in database");
			} else {
				ps.close();
				rs.close();
				ps = c.prepareStatement("select MAX(result_id) from results");
				rs = ps.executeQuery();
				if (rs.next()) {
					resultId = rs.getInt(1) + 1;
				}
				ps.close();
				rs.close();
			}
		} catch (SQLException e) {
			System.out.println("SQL Error results => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			resp.put("Error", "SQL Exception in results, check logs.");
		}

		try {
			PreparedStatement ps = c.prepareStatement("select source_id, source from sources where source_id = ?");
			ps.setString(1, input.get("source").toString().toLowerCase());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				resp.put("err-" + String.valueOf(++cnt),
						input.get("source").toString() + " already exists in database");
			} else {
				ps.close();
				rs.close();
				ps = c.prepareStatement("select MAX(source_id) from source");
				rs = ps.executeQuery();
				if (rs.next()) {
					resultId = rs.getInt(1) + 1;
				}
				ps.close();
				rs.close();
			}
		} catch (SQLException e) {
			System.out.println("SQL Error sources => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			resp.put("Error", "SQL Exception in sources, check logs.");
		}
		
		try {
			PreparedStatement ps = c.prepareStatement("select active_ingredient_id, food_id, active_ingredient_name from food_active_ingredients where food_id = ? and active_ingredient_name = ?");
			ps.setString(1, input.get("activeIngredient").toString().toLowerCase());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				resp.put("err-" + String.valueOf(++cnt),
						input.get("activeIngredient").toString() + " already exists in database");
			} else {
				ps.close();
				rs.close();
				ps = c.prepareStatement("select MAX(active_ingredient_id) from food_active_ingredients");
				rs = ps.executeQuery();
				if (rs.next()) {
					resultId = rs.getInt(1) + 1;
				}
				ps.close();
				rs.close();
			}
		} catch (SQLException e) {
			System.out.println("SQL Error food_active_ingredients => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			resp.put("Error", "SQL Exception in food_active_ingredients, check logs.");
		}
		
		try {
			PreparedStatement ps = c.prepareStatement("select dosage_frequency_id, dosage_frequency from dosage_frequencies where dosage_frequency_id = ?");
			ps.setString(1, input.get("dosage").toString().toLowerCase());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				resp.put("err-" + String.valueOf(++cnt),
						input.get("source").toString() + " already exists in database");
			} else {
				ps.close();
				rs.close();
				ps = c.prepareStatement("select MAX(dosage_frequency_id) from dosage_frequencies");
				rs = ps.executeQuery();
				if (rs.next()) {
					resultId = rs.getInt(1) + 1;
				}
				ps.close();
				rs.close();
			}
		} catch (SQLException e) {
			System.out.println("SQL Error dosage_frequencies => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			resp.put("Error", "SQL Exception in dosage_frequencies, check logs.");
		}
		
		try {
			PreparedStatement ps = c.prepareStatement("select dosage_unit_id, dosage_unit from dosage_units where dosage_unit_id = ?");
			ps.setString(1, input.get("dosage_unit").toString().toLowerCase());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				resp.put("err-" + String.valueOf(++cnt),
						input.get("dosage_unit").toString() + " already exists in database");
			} else {
				ps.close();
				rs.close();
				ps = c.prepareStatement("select MAX(dosage_unit_id) from dosage_units");
				rs = ps.executeQuery();
				if (rs.next()) {
					resultId = rs.getInt(1) + 1;
				}
				ps.close();
				rs.close();
			}
		} catch (SQLException e) {
			System.out.println("SQL Error dosage_units => " + e.getErrorCode() + ", Msg => " + e.getMessage());
			resp.put("Error", "SQL Exception in dosage_units, check logs.");
		}

		return response;
	}

}
