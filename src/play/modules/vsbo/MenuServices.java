/**
 * Very Simple Back Office
 * by Sylvain Gourio
 */
package play.modules.vsbo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import play.modules.vsbo.beans.Menu;
import play.modules.vsbo.beans.SubMenu;

/**
 * Use to construct and cache menu
 * @author Sylvain Gourio
 *
 */
public class MenuServices {

	static MenuServices instance = new MenuServices();
	
	private MenuServices() {
	}
	
	public static MenuServices getInstance() {
		return instance;
	}
	
	public Map<String, Menu> menuMap = new HashMap<String,Menu>();
	
	/**
	 * Get all the menu (used for admin)
	 * @param email
	 * @return
	 */
	public List<Menu> getAllMenus(String email){
		List<Menu> retour = new ArrayList<Menu>( menuMap.values() ); 
		menuCache.put(email, retour);
		return retour;
	}
	
	private Map<String, List<Menu>> menuCache = Collections.synchronizedMap(new HashMap<String, List<Menu>>());
	
	public void clearCache(){
		menuCache.clear();
	}
	
	public List<Menu> getMenuFromCache(String email){
		return menuCache.get(email);
	}
	
	/**
	 * Get accessible menu for roles
	 * @param menuIds
	 * @param email (user as cache key
	 * @return
	 */
	public List<Menu> getMenuByIds(String email, Set<String> menuIds){
		
		List<Menu> retour = new ArrayList<Menu>();
		
		for( Menu menu : menuMap.values()){
			if( menuIds.contains( menu.id ) ){
				Menu menuRetour = new Menu(menu.id, menu.text, menu.link);
				retour.add(menuRetour);
				List<SubMenu> subMenuList = new ArrayList<SubMenu>();
				menuRetour.subMenuList = subMenuList;
				for( SubMenu sm : menu.subMenuList){
					if( menuIds.contains( sm.id ) ){
						subMenuList.add(new SubMenu(sm.id, sm.text, sm.link));
					}
				}
			}
		}
		
		menuCache.put(email, retour);
		return retour;
	}
	
	public void registrerMenu(Menu menu){
		Menu m = menuMap.get(menu.id);
		if( m == null ){
			menuMap.put(menu.id, menu);
		}else{
			m.subMenuList.addAll(menu.subMenuList);
			m.link = menu.link;
			m.text = menu.text;
		}
	}
	
	public void registrerSubMenu(String id, SubMenu subMenu){
		Menu m = menuMap.get(id);
		if( m == null ){
			m = new Menu(id, "toConfigure", "linkToConfigure");
			menuMap.put(id, m);
		}
		m.subMenuList.add(subMenu);
	}
	
	
}
