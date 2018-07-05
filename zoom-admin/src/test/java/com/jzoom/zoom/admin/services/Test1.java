package com.jzoom.zoom.admin.services;

import org.beetl.core.statement.VarAssignExpression;

import junit.framework.TestCase;

public class Test1 extends TestCase {
	
	
	public void test() {
		
		String str = "12345678abcABCDefghijk9874321YXWV321";
		char f=str.charAt(0);
		int count = 0;
		for(int i=1 ; i < str.length(); ++i) {
			char c = str.charAt(i);
			if(c-f == 1 || f-c==1) {
				++count;
			}else {
				if(count >= 3) {
					System.out.println(  str.substring(  i-count-1 ,i)  ) ;
				}
				count = 0;
			}
			f=c;
			
		}
	}

}
