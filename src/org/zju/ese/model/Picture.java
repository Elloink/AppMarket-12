package org.zju.ese.model;

public class Picture 
{ 
    private String title; 
    private String icon; 
 
    public Picture() 
    { 
        super(); 
    } 
 
    public Picture(String title, String icon) 
    { 
        super(); 
        this.title = title; 
        this.icon = icon; 
    } 
 
    public String getTitle() 
    { 
        return title; 
    } 
 
    public void setTitle(String title) 
    { 
        this.title = title; 
    }

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	} 
} 