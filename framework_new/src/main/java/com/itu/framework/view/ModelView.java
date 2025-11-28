package com.itu.framework.view;

public class ModelView {
    String view;

    public ModelView() {
    }

    public ModelView(String view) {
        this.setView(view);;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}
