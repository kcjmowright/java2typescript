package java2typescript.jaxrs;

import java.util.List;

import javax.ws.rs.QueryParam;

/**
 *
 */
public class TestDTO {

    @QueryParam("source.test")
    private String test;

    @QueryParam("source.num")
    private Integer num;

    @QueryParam("nums")
    private List<Integer> nums;


    public Integer getNum() {
      return num;
    }

    public void setNum(Integer num) {
      this.num = num;
    }

    public String getTest() {
      return test;
    }

    public void setTest(String test) {
      this.test = test;
    }

    public List<Integer> getNums() {
      return nums;
    }

    public void setNums(List<Integer> nums) {
      this.nums = nums;
    }
}
