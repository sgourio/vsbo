/**
 * Very Simple Back Office
 * by Sylvain Gourio
 */
package services;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.mvc.Controller;
import controllers.vsbo.Menu;
import controllers.vsbo.SubMenu;

public class BackofficePlugin extends PlayPlugin{

	/**
	 * Constuct vsbo menu
	 */
	@Override
	public void afterApplicationStart() {
		MenuServices.getInstance().menuMap = new HashMap<String, beans.Menu>();
		
		List<Class<?>> controllers = new ArrayList<Class<?>>();
        for (Class clazz : Play.classloader.getAllClasses()) {
            if (Controller.class.isAssignableFrom(clazz)) {
                controllers.add(clazz);
            }
        }
	    
        for (final Class<?> clazz : controllers) {
        	// @Menu
            if (clazz.isAnnotationPresent(Menu.class)) {
            	
            	Menu menuAnnotation =  clazz.getAnnotation(Menu.class);
        	
        		// nouveau menu
        		String link = menuAnnotation.link();
        		if( StringUtils.isEmpty(link)){
        			link = null;
        		}
        		Logger.info("add menu " +  clazz.getName() );
        		beans.Menu menu = new beans.Menu(menuAnnotation.id(), "menu." + clazz.getName(), link);
        		MenuServices.getInstance().registrerMenu(menu);
            	
        		// trouver les methods de cette classe (et seulement de celle la)
        		Class superClass = clazz.getSuperclass();
        		List<Method> superClassMethod = new ArrayList<Method>();
        		for( Method method : superClass.getMethods() ){
        			superClassMethod.add(method);
        		}
        		
            	for( Method method : clazz.getMethods()){
					if( !superClassMethod.contains(method) &&  method.isAnnotationPresent(SubMenu.class)){
						Logger.info("add menu " + method.getName() );
						SubMenu subMenuAnnotation = method.getAnnotation(SubMenu.class);
		        		link = subMenuAnnotation.link();
		        		if( StringUtils.isEmpty(link)){
		        			link = play.mvc.Router.reverse(clazz.getName()+"." +  method.getName()).url;
		        		}
		        		String id = subMenuAnnotation.id();
		        		if( StringUtils.isBlank(id )){
		        			id = menuAnnotation.id() + "." + method.getName();
		        		}
						beans.SubMenu subMenu = new beans.SubMenu(id, "menu." + clazz.getName() + "." + method.getName(), link );
						String parentId = subMenuAnnotation.parentMenuId();
						if( StringUtils.isEmpty(parentId) ){
							parentId = menuAnnotation.id();
						}
						MenuServices.getInstance().registrerSubMenu(parentId, subMenu);
						//sousMenuList.add(new beans.Menu("vsbo.menu."+method.getName(), play.mvc.Router.getFullUrl(clazz.getName()+"." +  method.getName() ) ));
					}
				}
            }
        }
	        
	}
}
