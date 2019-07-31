package com.sxt.enums;

/**
 * @author WWF
 * @title: ShipingStatus
 * @projectName ego
 * @description: com.sxt.enums
 * @date 2019/6/3 20:50
 */
public enum ShipingStatus {
    NOT_SHIP(0,"未发货"),
    SHIPING(1,"运输中"),
    SHIPED(2,"已签收");

    private Byte code;
    private String desc;
    ShipingStatus(Integer code,String desc){
        this.code=code.byteValue();
        this.desc=desc;
    }

    public Byte getCode() {
        return code;
    }

    public void setCode(Byte code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }}
