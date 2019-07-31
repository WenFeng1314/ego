package com.sxt.vo;

import java.util.List;

/**
 * @author WWF
 * @title: EasyUIDataGrid
 * @projectName ego
 * @description: com.sxt.vo
 * @date 2019/5/22 19:21
 */
public class EasyUIDataGrid {
    /**
     * 总条数
     */
    private  long total;
    /**
     * 显示内容
     */
    private List<?> rows;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }
    public EasyUIDataGrid(){}
    public EasyUIDataGrid(long total, List<?> rows) {
        this.total = total;
        this.rows = rows;
    }
}
