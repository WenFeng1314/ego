
package com.sxt.vo;
import com.sxt.vo.GoodsVo;

import java.io.Serializable;
import java.util.List;


/**
 * 搜索对象结果
 * @author WWF
 * @title: SearchResult
 * @projectName ego
 * @description: com.sxt.vo
 * @date 2019/5/27 15:38
 */
public class SearchResult implements Serializable {
    //当前页
    private Integer currentPage;
    //总条数
    private Long total;
    //搜索处理的goods对象
    private List<GoodsVo> result;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<GoodsVo> getResult() {
        return result;
    }

    public void setResult(List<GoodsVo> result) {
        this.result = result;
    }
    public SearchResult(){}
    public SearchResult(Integer currentPage, Long total, List<GoodsVo> result) {
        this.currentPage = currentPage;
        this.total = total;
        this.result = result;
    }
}
