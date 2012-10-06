/**
 * Very Simple Back Office
 * by Sylvain Gourio
 */
package controllers.vsbo;

import java.util.HashMap;
import java.util.Map;

import models.vsbo.User;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import play.Play;
import play.data.validation.Required;
import play.i18n.Messages;
import play.libs.OAuth2;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Scope.Params;
import play.mvc.results.Redirect;

import com.google.gson.JsonObject;

/**
 * Secure the application
 * @author Sylvain Gourio
 *
 */
public class Secure extends Controller{

	private static final Logger logger = Logger.getLogger(Secure.class);
	
	 public static OAuth2 GOOGLE = new OAuth2(
			 "https://accounts.google.com/o/oauth2/auth",
			 "https://accounts.google.com/o/oauth2/auth",
			Play.configuration.getProperty("oAuth.ClientID"),
			Play.configuration.getProperty("oAuth.ClientSecret")
    );
	
	@Before(unless={"disconnect"}, priority=0)
    static void setUser() {
        User user = null;
        String email = session.get("email");
        if ( email != null ) {
            user = User.getByEmail(session.get("email"));
            if( user == null ){
            	String admin = Play.configuration.getProperty("vsbo.admin");
            	if( admin != null){
            		String[] adminTab = admin.split(",");
            		for( String a : adminTab ){
            			if( StringUtils.trim(a).equals(email) ){
            				user = new User();
            				user.email = email;
            				user.roles = "admin";
            				user.save();
            			}
            		}
            	}
            }
            if( user == null ){
            	logger.info("Connexion de " + email + " refusée");
            }
            renderArgs.put("user", user);
        }
    }
	
	@Before(unless={"auth"})
	static void controlAccess() {
		User user = connected();
		if( user == null ){
        	render("/vsbo/login.html");
        }
	}
	
	public static User connected() {
        return (User)renderArgs.get("user");
    }
	
	public static void disconnect(){
		session.remove("email");
		controlAccess();
	}
	
	/**
	 * 3 requetes sont faites
	 * 1/ Recuperation d'un accesscode a partir du clientID de l'application
	 * 2/ Recuperation d'un access token pour l'utilisateur
	 * 3/ Recuperation des infos de l'utilisateur
	 */
	public static void auth() {
		
		if( connected() != null ){
			Backoffice.index();
		}
		
        String clientID = Play.configuration.getProperty("oAuth.ClientID");
        String clientSecret = Play.configuration.getProperty("oAuth.ClientSecret");
        String authorizationURL = "https://accounts.google.com/o/oauth2/auth";
        String tokenURL = "https://accounts.google.com/o/oauth2/token";
        String appBaseUrl = Play.configuration.getProperty("application.baseUrl", "application.baseUrl");
        String callBack = appBaseUrl + play.mvc.Router.reverse("vsbo.Secure.auth").url;
        String scope = "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile";

        
        if (OAuth2.isCodeResponse()) {
            String accessCode = Params.current().get("code");
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("client_id", clientID);
            params.put("client_secret", clientSecret);
            params.put("redirect_uri", callBack);
            params.put("code", accessCode);
            params.put("grant_type", "authorization_code");
            HttpResponse response = WS.url(tokenURL).params(params).post();

            JsonObject jsonObject = (JsonObject) response.getJson();
            String accessToken = jsonObject.get("access_token").getAsString();

            // recuperer des infos sur l'utilisateur
            String userInfoURL = "https://www.googleapis.com/oauth2/v1/userinfo";
            params = new HashMap<String, Object>();
            params.put("access_token", accessToken);
            response = WS.url(userInfoURL).params(params).get();

            jsonObject = (JsonObject) response.getJson();
            String email = jsonObject.get("email").getAsString();
            session.put("email", email);
            logger.info("Connexion de " + email);
            setUser();
            Backoffice.index();
        }
        throw new Redirect( authorizationURL + "?client_id=" + clientID + "&redirect_uri=" + callBack +"&scope=" + scope +"&state=%2Fprofile" + "&response_type=code");
    }
	
	public static void vsboAuth(@Required(message="Indiquez votre email") String email,@Required(message="Indiquez votre mot de passe") String password) {
		User user = User.findUser(email, password);
		if( user != null ){
			session.put("email", email);
			setUser();
            Backoffice.index();
		}else{
			String errorMessage = Messages.get("vsbo.error.login");
			render("/vsbo/login.html", errorMessage);
		}
		
	}
	
	static String authURL() {
        return play.mvc.Router.getFullUrl("vsbo.Secure.auth");
    }
}
