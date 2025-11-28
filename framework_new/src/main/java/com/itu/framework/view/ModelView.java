package com.itu.framework.view;

public class ModelView {
    String view;

    private java.util.Map<String, Object> data = new java.util.HashMap<>();

    public ModelView() {
    }

    public ModelView(String view) {
        this.setView(view);
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public void addObject(String key, Object value) {
        this.data.put(key, value);
    }

    public java.util.Map<String, Object> getData() {
        return data;
    }
}
