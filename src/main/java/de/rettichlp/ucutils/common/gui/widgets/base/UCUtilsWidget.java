package de.rettichlp.ucutils.common.gui.widgets.base;

import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@IndexAnnotated
public @interface UCUtilsWidget {

    String registryName();

    double defaultX() default 0.0;

    double defaultY() default 0.0;

    boolean defaultEnabled() default true;
}
