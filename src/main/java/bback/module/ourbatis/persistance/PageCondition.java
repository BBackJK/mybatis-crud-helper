package bback.module.ourbatis.persistance;

public class PageCondition {

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 1000000;

    protected int pageSize;
    protected int pageIndex;
    protected boolean paging;

    public int getStartPage() {
        initPage();
        return (pageIndex - 1) * pageSize;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void initPage() {
        if (pageIndex < 1) {
            pageIndex = 1;
        }

        if (pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
    }

    public boolean isPaging() {
        return this.paging;
    }

    public void enablePaging() {
        this.paging = true;
    }

    public void disablePaging() {
        this.paging = false;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
