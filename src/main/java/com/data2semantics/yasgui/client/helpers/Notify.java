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

public class Notify {
	public enum Type {
		ALERT, SUCCESS, ERROR, WARNING, INFORMATION, CONFIRM;
	}
	private static String LAYOUT = "bottomRight";
	private static String THEME = "defaultTheme";

	private Type type;
	private String text;
	private boolean dismissQueue;
	
	public Notify() {
		
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	
	
//    layout: 'top',
//    theme: 'defaultTheme',
//    type: 'alert',
//    text: '',
//    dismissQueue: true, // If you want to use queue feature set this true
//    template: '<div class="noty_message"><span class="noty_text"></span><div class="noty_close"></div></div>',
//    animation: {
//        open: {height: 'toggle'},
//        close: {height: 'toggle'},
//        easing: 'swing',
//        speed: 500 // opening & closing animation speed
//    },
//    timeout: false, // delay for closing event. Set false for sticky notifications
//    force: false, // adds notification to the beginning of queue when set to true
//    modal: false,
//    maxVisible: 5, // you can set max visible notification for dismissQueue true option
//    closeWith: ['click'], // ['click', 'button', 'hover']
//    callback: {
//        onShow: function() {},
//        afterShow: function() {},
//        onClose: function() {},
//        afterClose: function() {}
//    },
//    buttons: false // an array of buttons
}
