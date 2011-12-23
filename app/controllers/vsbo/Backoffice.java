/**
 * Very Simple Back Office
 * by Sylvain Gourio
 */
package controllers.vsbo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import models.vsbo.Role;
import models.vsbo.User;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Play;
import play.cache.Cache;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.i18n.Lang;
import play.modules.vsbo.Menu;
import play.modules.vsbo.MenuServices;
import play.modules.vsbo.SubMenu;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 * Main functions of vsbo
 * 
 * @author Sylvain Gourio
 *
 */
@With(Secure.class)
@Menu(id="menu-vsbo")
public class Backoffice extends Controller{
	
	public static void index(){
		render();
	}
	
	/**
	 * Change the application language
	 * @param locale
	 */
	public static void changeLanguage(String locale){
		Lang.change(locale);
		profil();
	}
	
	public static void profil(){
		String language = Play.configuration.getProperty("application.langs");
		String[] languageList = language.split(",");
		renderArgs.put("languageList", languageList);
		render();
	}
	
	public static void savePasswordProfil(@Required(message="vsbo.old.password.mandatory") String oldPassword, @Required(message="vsbo.password.mandatory") String password){
		oldPassword = User.encodePassword(oldPassword);
		password = User.encodePassword(password);
		if( StringUtils.isBlank( connected().password ) || connected().password.equals(oldPassword) ){
			connected().password = password;
			connected().save();
		}else{
			if( !StringUtils.isBlank( oldPassword ) ){
				Validation.addError("vsbo.password.not.match", "vsbo.password.not.match");
			}
		}
		
		if(Validation.hasErrors()) {
	          params.flash(); // add http parameters to the flash scope
	          Validation.keep(); // keep the errors for the next request
	      }
		profil();
	}
	
	/**
	 * Display all user in the application
	 */
	@SubMenu
	public static void userList(){
		List<User> userList = User.findAll();
		render(userList);
	}
	
	/**
	 * Go on user edit page
	 * @param id
	 */
	public static void editUser(Long id){
		List<Role> roleList = Role.findAll();
		if( id == null ){
			render(roleList);
		}
		User userEdit = User.findById(id);
		
		List<String> userRoleList = new ArrayList<String>();
		if( userEdit.roles != null ){
			for( String s : userEdit.roles.split(",")){
				userRoleList.add(s);
			}
		}
		render(userEdit, userRoleList, roleList);
	}
	/**
	 * Save or update the user
	 * @param id
	 * @param email
	 * @param password
	 */
	public static void saveUser(Long id, String email, String password){
		User user = null;
		if( id == null ){
			user = new User(email);
		}else{
			user = User.findById(id);
			user.email = email;
		}
		user.password = User.encodePassword(password);
		
		String rolesIds = "";
		for(String paramKey : params.all().keySet()){
			if( paramKey.startsWith("role_")){
				rolesIds +="," + paramKey.substring("menu_".length());
			}
		}
		if( rolesIds.length() > 0 ){
			rolesIds = rolesIds.substring(1);
		}
		user.roles = rolesIds;
		user.save();
		userList();
	}
	
	/**
	 * See the application configuration
	 */
	public static void configurationPage(){
		 Properties properties = Play.configuration;
		 render(properties);
	}
	
	/**
	 * Update application configuration
	 */
	public static void updateProperties(){
		for( String s : params.all().keySet()){
			Logger.info("Update configuration: " + s +" = " + params.get(s));
			Play.configuration.put(s, params.get(s));
		}
		 configurationPage();
	}
	
	/**
	 * Clear the application cache.
	 */
	public static void clearCache(){
		Cache.clear();
		MenuServices.getInstance().clearCache();
		cache();
	}
	
	/**
	 * Go to the cache management page
	 */
	@SubMenu()
	public static void cache(){
		render();
	}
	
	/**
	 * Get the current connected user
	 * @return
	 */
	static User connected() {
        return (User)renderArgs.get("user");
    }
	
	
	/**
	 * Set current the active menu
	 */
	@Before(priority=100) // low priority
	protected static void setMenu(){
		
		try {
			Class<?> clazz = Class.forName("controllers."+request.controller);
			if( clazz != null ){
				Menu menu = (Menu) clazz.getAnnotation(Menu.class);
				if( menu != null ){
					renderArgs.put("vsboMenuActive", menu.id());
				}
			}
		} catch (ClassNotFoundException e) {
			Logger.error("", e);
		}
		
		User user = connected();
		List<play.modules.vsbo.beans.Menu > menuList = MenuServices.getInstance().getMenuFromCache(user.email);
		if( menuList == null ){
			Set<String> menuIds = new HashSet<String>();
			boolean admin = false;
			for( String roleId: user.roles.split(",")){
				if( roleId.equals("admin")){
					admin = true;
					break;
				}else{
					Role role = Role.getByName(roleId);
					if( role != null ){
						for(String menuId : role.menuIds.split(",") ){
							menuIds.add(menuId);
						}
					}
					
				}
			}
			if( admin ){
				menuList = MenuServices.getInstance().getAllMenus(user.email);
			}else{
				menuList = MenuServices.getInstance().getMenuByIds(user.email, menuIds);
			}
		}
		
		renderArgs.put("vsboMenu", menuList);
	}

	/**
	 * Go to the role list 
	 */
	@SubMenu()
	public static void roleList(){
		List<Role> roleList = Role.findAll();
		render(roleList);
	}

	/**
	 * Open the role edit page 
	 * @param id
	 */
	public static void editRole(Long id){
		Collection<play.modules.vsbo.beans.Menu> menuList = MenuServices.getInstance().menuMap.values();
		if( id == null ){
			render(menuList);
		}
		Role role = Role.findById(id);
		
		List<String> menuRoleList = new ArrayList<String>();
		if( role.menuIds != null ){
			for( String s : role.menuIds.split(",")){
				menuRoleList.add(s);
			}
		}
		render(role, menuList, menuRoleList);
	}
	
	/**
	 * Save or update role definition
	 * @param id
	 * @param name
	 */
	public static void saveRole(Long id, String name){
		Role role = null;
		if( id == null ){
			role = new Role(name);
		}else{
			role = Role.findById(id);
			role.name = name;
		}
		String menuIds = "";
		for(String paramKey : params.all().keySet()){
			
			if( paramKey.startsWith("menu_")){
				menuIds +="," + paramKey.substring("menu_".length());
			}
			if( paramKey.startsWith("subMenu_")){
				menuIds +="," + paramKey.substring("subMenu_".length());
			}
		}
		
		if( menuIds.length() > 0 ){
			menuIds = menuIds.substring(1);
		}
		role.menuIds = menuIds;
		role.save();
		MenuServices.getInstance().clearCache();
		roleList();
	}
	
}
