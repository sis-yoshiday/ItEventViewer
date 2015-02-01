package org.iteventviewer.app.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

/**
 * Created by yuki_yoshida on 15/02/01.
 */
@Qualifier @Retention(RetentionPolicy.RUNTIME)
public @interface ForApplication {
}