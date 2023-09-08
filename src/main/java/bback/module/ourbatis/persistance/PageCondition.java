package bback.module.ourbatis.persistance;

public class PageCondition {

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 1000000;

    private int pageSize;
    private int pageIndex;
    private boolean paging;

    public int getPageSize() {
        return pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public boolean isPaging() {
        return paging;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getStartPage() {
        initPage();
        return (getPageIndex() - 1) * getPageSize();
    }

    private void initPage() {
        if (getPageIndex() < 1) {
            setPageIndex(1);
        }

        if (getPageSize() < 1) {
            setPageSize(DEFAULT_PAGE_SIZE);
        }
    }

    public void enablePaging() {
        this.paging = true;
    }

    public void disablePaging() {
        this.paging = false;
    }
}
