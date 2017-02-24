package com.mytetris;

import java.awt.Polygon;


public class test {

	public static void main(String[] args) {
		int [] x = new int []{100,200,200,100};
		int [] y = new int[]{100,100,200,200};
		 Polygon polygon = new Polygon(x,y,4);

		 boolean result = polygon.contains(130,180);
		 
		 System.out.println(result);
	}
}
