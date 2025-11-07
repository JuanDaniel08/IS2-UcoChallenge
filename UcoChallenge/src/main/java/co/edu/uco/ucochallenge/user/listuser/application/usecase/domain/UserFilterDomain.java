package co.edu.uco.ucochallenge.user.listuser.application.usecase.domain;

public class UserFilterDomain {
    private final int page;
    private final int size;
    private final String nameFilter;

    public UserFilterDomain(int page, int size, String nameFilter) {
        this.page = page;
        this.size = size;
        this.nameFilter = nameFilter;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public String getNameFilter() {
        return nameFilter;
    }
}