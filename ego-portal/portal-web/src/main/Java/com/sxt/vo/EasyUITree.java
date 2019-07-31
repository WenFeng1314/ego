package com.sxt.vo;

import java.util.List;

/**
 * @author WWF
 * @title: EasyUITree
 * @projectName ego
 * @description: com.sxt.vo
 * @date 2019/5/22 21:13
 */
public class EasyUITree {
    private String id;//节点id
    private String text;//要显示的节点
    private String state;//节点状态
    private String checked;//显示节点是否选中
    private String attributes;//节点的其它属性
    private List<EasyUITree> children;//节点的子节点

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public List<EasyUITree> getChildren() {
        return children;
    }

    public void setChildren(List<EasyUITree> children) {
        this.children = children;
    }
    public EasyUITree(){}
    public EasyUITree(String id, String text, String state, String checked, String attributes, List<EasyUITree> children) {
        this.id = id;
        this.text = text;
        this.state = state;
        this.checked = checked;
        this.attributes = attributes;
        this.children = children;
    }
}
