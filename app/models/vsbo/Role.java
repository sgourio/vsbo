/**
 * Very Simple Back Office
 * by Sylvain Gourio
 */
package models.vsbo;

import javax.persistence.Column;
import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * User role define wich menu is displayed for user
 * @author Sylvain Gourio
 */
@Entity(name="vsbo_role")
public class Role extends Model {

	@Column(unique=true, nullable=false)
	public String name; // admin, edit
	public String menuIds; // liste de menus separe par des virgules 
	
	public Role(String name) {
		this.name = name;
	}
	
	public static Role getByName(String name){
		return find("byName", name).first();
	}
	
}
