package com.noticeboard;

public class PageDetails {

    String pagename, pageinfo, privacy;

    public PageDetails(String pagename, String pageinfo, String privacy) {
        this.pagename = pagename;
        this.pageinfo = pageinfo;
        this.privacy = privacy;
    }

    public PageDetails() {
    }

    public String getPagename() {
        return pagename;
    }

    public void setPagename(String pagename) {
        this.pagename = pagename;
    }

    public String getPageinfo() {
        return pageinfo;
    }

    public void setPageinfo(String pageinfo) {
        this.pageinfo = pageinfo;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }
}
