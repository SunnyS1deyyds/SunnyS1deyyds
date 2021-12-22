package com.itheima.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    private Integer counts = 0;//总记录数
    private Integer pagesize;//页大小
    private Integer pages = 0;//总页数
    private Integer page;//当前页码
    private List<T> items = Collections.emptyList(); //列表

    public PageResult(int page, int pagesize, int counts, List<T> list) {
        this.page = page;
        this.pagesize = pagesize;
        this.items = list;
        this.counts = counts;
        this.pages = counts % pagesize == 0 ? counts / pagesize : counts / pagesize + 1;
    }
}