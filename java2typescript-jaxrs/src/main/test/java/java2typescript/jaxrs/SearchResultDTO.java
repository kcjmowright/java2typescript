package java2typescript.jaxrs;

import java.util.Collection;

public class SearchResultDTO<T> {

  private long total;

  private int page;

  private int pageSize;

  private int count;

  private Collection<T> results;

  public SearchResultDTO() {
  }

  public long getTotal() {
    return this.total;
  }

  public int getPage() {
    return this.page;
  }

  public int getPageSize() {
    return this.pageSize;
  }

  public int getCount() {
    return this.count;
  }

  public Collection<T> getResults() {
    return this.results;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public void setResults(Collection<T> results) {
    this.results = results;
  }
}
