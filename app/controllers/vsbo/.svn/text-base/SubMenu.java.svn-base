/**
 * Very Simple Back Office
 * by Sylvain Gourio
 */
package controllers.vsbo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SubMenu {

	/**
	 * Identifiant du menu ( ce sera l'id html du menu )
	 * @return
	 */
	String id() default "";
	/**
	 * Nom de la section dans laquelle on ajoute ce sous menu
	 * @return
	 */
	String parentMenuId() default "";
	
	/**
	 * Si le lien est different de l'action ou se trouve le tag (en theorie c'est a ne pas utiliser)
	 * @return
	 */
	String link() default "";
	
}
