package com.sxt.enums;

/**
 * @author WWF
 * @title: PayStatus
 * @projectName ego
 * @description: com.sxt.enums
 * @date 2019/6/3 20:45
 */
public enum PayStatus {
    NOT_PAY(0,"未支付"),
    PAYED(1,"已支付"),
    PAY_FAIL(2,"支付未完成");

    private Byte code;
    private String desc;
    PayStatus(Integer code,String desc){
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
