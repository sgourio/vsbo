/**
 * Very Simple Back Office
 * by Sylvain Gourio
 */
package beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Top bar Menu 
 * @author Sylvain Gourio
 */
public class Menu {

	public String id = null;
	public String text = null;
	public String link = null ;
	public List<SubMenu> subMenuList = new ArrayList<SubMenu>();
	

	/**
	 * 
	 * @param id (HTML id)
	 * @param text
	 * @param link
	 */
	public Menu(final String id, final String text, final String link) {
		super();
		this.id = id;
		this.text = text;
		this.link = link;
	}
	
	
	
}
