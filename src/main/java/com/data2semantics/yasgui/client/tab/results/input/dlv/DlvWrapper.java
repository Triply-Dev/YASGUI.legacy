package com.data2semantics.yasgui.client.tab.results.input.dlv;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Delimiter separated results (e.g. csv or tsv)
 *
 */
public class DlvWrapper extends JavaScriptObject {
  protected DlvWrapper() { }
  public final native int length() /*-{ return this.length; }-*/;
  public final native Row getRow(int i) /*-{ return this[i];     }-*/;
}