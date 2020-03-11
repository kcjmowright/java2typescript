package java2typescript.function;

import javax.ws.rs.QueryParam;

public class PagingSortDTO {
  @QueryParam("page")
  private Integer page;

  @QueryParam("pageSize")
  private Integer pageSize;

  @QueryParam("sort")
  private String sort;

  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }
}
