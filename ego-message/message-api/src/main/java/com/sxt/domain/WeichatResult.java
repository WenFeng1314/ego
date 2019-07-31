package com.sxt.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**微信响应对象
 * @author WWF
 * @title: WeichatResult
 * @projectName ego
 * @description: com.sxt.domain
 * @date 2019/6/2 12:35
 */
public class WeichatResult {
    @JsonProperty(value = "errcode")
    private Integer errCode;

    @JsonProperty(value = "errmsg")
    private String errMsg;
    @JsonProperty(value = "msgid")
    private Long msgId;

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }
    public WeichatResult(){}
    public WeichatResult(Integer errCode, String errMsg, Long msgId) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.msgId = msgId;
    }
}
