package com.data2semantics.yasgui.client.helpers;

/*
 * #%L
 * YASGUI
 * %%
 * Copyright (C) 2013 Laurens Rietveld
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

public class TooltipProperties {
	public static String POS_TOP_LEFT = "top left";
	public static String POS_TOP_CENTER = "top center";
	public static String POS_TOP_RIGHT = "top right";
	public static String POS_RIGHT_TOP = "right top";
	public static String POS_RIGHT_CENTER = "right center";
	public static String POS_RIGHT_BOTTOM = "right bottom";
	public static String POS_BOTTOM_RIGHT = "bottom right";
	public static String POS_BOTTOM_CENTER = "bottom center";
	public static String POS_BOTTOM_LEFT = "bottom left";
	public static String POS_LEFT_BOTTOM = "left bottom";
	public static String POS_LEFT_CENTER = "left center";
	public static String POS_LEFT_TOP = "left top";
	public static String POS_CENTER = "center";
	
	
	
	private String my = POS_TOP_LEFT;
	private String at = POS_BOTTOM_RIGHT;
	private int xOffset = 0;
	private int yOffset = 0;
	private String id;
	private String content;
	public String getMy() {
		return my;
	}
	public void setMy(String my) {
		this.my = my;
	}
	public String getAt() {
		return at;
	}
	public void setAt(String at) {
		this.at = at;
	}
	public int getXOffset() {
		return xOffset;
	}
	public void setXOffset(int xOffset) {
		this.xOffset = xOffset;
	}
	public int getYOffset() {
		return yOffset;
	}
	public void setYOffset(int yOffset) {
		this.yOffset = yOffset;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
