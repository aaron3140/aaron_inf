package common.utils;

import java.security.SecureRandom;

public class RandomNumber {
	public static char[] CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 
		'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	
	//生成6位验证码
	 public static String getrannumber(){
		 return getrannumber(6);
	 } 
	 
	 //生成n位验证码
	 public static String getrannumber(int n){
		 SecureRandom sr = new SecureRandom();
		 char[] chars = new char[n];
		 for (int i = 0; i < n; i++) {
			 chars[i] = CHARS[sr.nextInt(CHARS.length)];
		 }
		 return new String(chars);
	 } 
	 
	 public static void main(String[] args) {
		System.out.println(getrannumber());
	}
	 
}
