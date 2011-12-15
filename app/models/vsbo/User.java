/**
 * Very Simple Back Office
 * by Sylvain Gourio
 */
package models.vsbo;

import java.security.MessageDigest;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.UniqueConstraint;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import play.db.jpa.Model;

/**
 * vsbo users
 * @author Sylvain Gourio
 *
 */
@Entity(name="vsbo_user")
public class User extends Model {

	private static final Logger logger = Logger.getLogger(User.class);
	
    public String access_token;
    @Column(unique=true, nullable=false)
    public String email;
    public String roles; // admin, edit
    public String password;


    public static User getByEmail(String email) {
        return find("email", email).first();
    }
    
    public User() {
    	
	}
    
    public User( String email ){
    	this.email = email;
    }
    
    public static User findUser(String email, String realPassword){
    	String pass = encodePassword(realPassword);
    	return find("byEmailAndPassword", email, pass).first();
    }

    public static String encodePassword(String password){
		String hash = password;
		try{
			if( password != null ){
				MessageDigest md = MessageDigest.getInstance("SHA"); //step 2
				md.update(password.getBytes("UTF-8"));
			    byte raw[] = md.digest(); //step 4
			    hash = new String (Base64.encodeBase64(raw)); //step 5
			}
		}catch(Exception e){
			logger.error("",e);
		}
		return hash;
	}
}
