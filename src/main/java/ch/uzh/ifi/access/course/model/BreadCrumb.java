package ch.uzh.ifi.access.course.model;

public class BreadCrumb {
    public String title;
    public String url;
    public int index;

    public BreadCrumb(String title, String url){
        this(title, url, -1);
    }

    public BreadCrumb(String title, String url, int index){
        this.title = title;
        this.url = url;
        this.index = index;
    }
}
