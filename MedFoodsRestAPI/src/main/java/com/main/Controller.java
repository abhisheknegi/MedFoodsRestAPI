package com.main;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.endpointfunctions.Foods;
import com.endpointfunctions.Profiles;
import com.endpointfunctions.Users;

@RestController
@SuppressWarnings("unchecked")
public class Controller {

	private Connection c = null;

	@RequestMapping(value = "/", method = RequestMethod.GET, produces = APPLICATION_JSON)
	public JSONObject getMethod() {
		JSONObject response = new JSONObject();
		response.put("Success", "API is Accessible....");
		return response;
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET, produces = APPLICATION_JSON)
	public JSONObject getUsers() throws SQLException {
		return Users.getUsers(getDBConnection());
	}

	@RequestMapping(value = "/users", method = RequestMethod.POST, produces = APPLICATION_JSON)
	public JSONObject authUser(@RequestBody JSONObject input) throws SQLException {
		return Users.authUser(getDBConnection(), input);
	}

	@RequestMapping(value = "/users/{userId}", method = RequestMethod.GET, produces = APPLICATION_JSON)
	public JSONObject getUserData(@PathVariable(value = "userId") String userId) throws SQLException {
		return Users.getUserData(getDBConnection(), userId);
	}

	@RequestMapping(value = "/users/{userId}", method = RequestMethod.POST, produces = APPLICATION_JSON)
	public JSONObject authUserData(@PathVariable(value = "userId") String userId, @RequestBody JSONObject input)
			throws SQLException {

		return Users.authUserData(getDBConnection(), userId, input);
	}

	@RequestMapping(value = "/profiles", method = RequestMethod.GET, produces = APPLICATION_JSON)
	public JSONObject getProfiles() throws SQLException {
		return Profiles.getProfiles(getDBConnection());
	}

	@RequestMapping(value = "/profiles/{profileId}", method = RequestMethod.GET, produces = APPLICATION_JSON)
	public JSONObject getProfileData(@PathVariable(value = "profileId") String profileId) throws SQLException {
		return Profiles.getProfileData(getDBConnection(), profileId);
	}

	@RequestMapping(value = "/profiles", method = RequestMethod.POST, produces = APPLICATION_JSON)
	public JSONObject postProfiles(@RequestBody JSONObject input) throws SQLException {
		return Profiles.postProfiles(getDBConnection(), input);
	}

	@RequestMapping(value = "/profiles/{profileId}", method = RequestMethod.DELETE, produces = APPLICATION_JSON)
	public JSONObject deleteProfiles(@PathVariable(value = "profileId") String profileId) throws SQLException {
		return Profiles.deleteProfiles(getDBConnection(), profileId);
	}
	
	@RequestMapping(value = "/foods", method = RequestMethod.GET, produces = APPLICATION_JSON)
	public JSONObject getFood() throws SQLException {
		return Foods.getFoods(getDBConnection());
	}

	@RequestMapping(value = "/foods", method = RequestMethod.POST, produces = APPLICATION_JSON)
	public JSONObject postFoods(@RequestBody JSONObject input) throws SQLException {
		return Foods.postFoods(getDBConnection(), input);
	}
	
	@RequestMapping(value = "/foods/{foodId}", method = RequestMethod.GET, produces = APPLICATION_JSON)
	public JSONObject getFoodData(@PathVariable(value = "foodId") String foodId) throws SQLException {
		return Foods.getFoods(getDBConnection());
	}


	private Connection getDBConnection() throws SQLException {

		if (this.c == null || this.c.isClosed()) {
			try {
				Class.forName("org.postgresql.Driver");
				this.c = DriverManager.getConnection(
						"jdbc:postgresql://test-db-instance1.cm3iu1emxqew.us-east-1.rds.amazonaws.com:5432/testdb",
						"csuser", "csuserpass");
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
				System.exit(0);
			}
			System.out.println("Opened database successfully");
			c.setAutoCommit(false);
		}
		return this.c;
	}

}
