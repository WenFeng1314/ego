package com.sxt.enums;

/**
 * @author WWF
 * @title: OrderStatus
 * @projectName ego
 * @description: com.sxt.enums
 * @date 2019/6/3 20:33
 */
public enum OrderStatus {

    PRE_ORDER(0,"预订单"),
    PAY_ORDER(1,"支付了的订单"),
    ORDERED(2,"完成了的订单"),
    CANSEL_ORDER(3,"取消了的订单");
    private Byte code;
    private String desc;
    OrderStatus(Integer code, String desc) {
        this.code = code.byteValue();
        this.desc = desc;
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
    }



}
