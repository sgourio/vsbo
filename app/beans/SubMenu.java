/**
 * Very Simple Back Office
 * by Sylvain Gourio
 */
package beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Toolbar sub menu
 * @author Sylvain Gourio
 */
public class SubMenu {

	public String id = null;
	public String text = null;
	public String link = null ;
	
	
	public SubMenu(String id, String text, String link) {
		super();
		this.id = id;
		this.text = text;
		this.link = link;
	}
	
	
}
