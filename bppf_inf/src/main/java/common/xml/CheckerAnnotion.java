package common.xml;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface CheckerAnnotion {
	public static  final String  TYPE_STR = "str";
	public static  final String  TYPE_NUM = "num";
	public int len() default -1;
	public String type();
	public boolean strict() default false;
	public boolean required() default false;
	public String regex() default "";
}
