package bback.module.ourbatis.interceptors;

public interface ListableDelegator extends Comparable<ListableDelegator> {

    default int getSort() {
        return 1;
    }

    default int compareTo(ListableDelegator other) {
        return Integer.compare(this.getSort(), other.getSort());
    }
}
