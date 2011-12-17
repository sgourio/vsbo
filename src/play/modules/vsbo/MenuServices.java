/**
 * Very Simple Back Office
 * by Sylvain Gourio
 */
package play.modules.vsbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import play.cache.Cache;
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
	 * Get accessible menu for roles
	 * @param userRoles (comma separated)
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Menu> getMenuForRole(String roleMenuIds, boolean admin){
		if( admin ){
			return new ArrayList<Menu>( menuMap.values() );
		}
		
		List<Menu> retour = (List<Menu>) Cache.get("menu_" + roleMenuIds );
		if( retour == null ){
			Lock lock = new ReentrantLock();
			lock.lock();
			try{
				retour = (List<Menu>) Cache.get("menu_" + roleMenuIds );
				if( retour == null ){
					retour = new ArrayList<Menu>();
					
					if( roleMenuIds != null  ){
						List<String> menuIds = new ArrayList<String>();
						for( String m : roleMenuIds.split(",") ){
							if( !menuIds.contains(m) ){
								menuIds.add(m);
							}
						}
						
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
					}
					
					Cache.safeAdd("menu_" + roleMenuIds, retour, "1d");
				}
				
			}finally{
				lock.unlock();
			}
			
		}
		
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
