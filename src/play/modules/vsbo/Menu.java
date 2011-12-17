/**
 * Very Simple Back Office
 * by Sylvain Gourio
 */
package play.modules.vsbo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add the function on top bar Menu
 * Define label in messages
 * menu.controllers.vsbo.{action}.{method}  
 * @author Sylvain Gourio
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Menu {
	/**
	 * Top menu id, used to add sub-menu
	 * @return
	 */
	String id();
	
	/**
	 * Use when you don't want to add sub-menu
	 * Default: "" 
	 * @return
	 */
	String link() default "";
	
}
