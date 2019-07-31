package com.sxt.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * @author WWF
 * @title: WeichatMessage
 * @projectName ego
 * @description: com.sxt.domain
 * @date 2019/6/2 12:25
 */
public class WeichatMessage {
    @JsonProperty(value = "touser")
    private String toUser;
    @JsonProperty(value = "template_id")
    private String templateId;

    private String url;

    public static Map<String,String> getMap(String value,String color){
        Map<String,String> map=new HashMap<String, String>();
        map.put("value",value);
        map.put("color",color);
        return map;
    }

    private Map<String,Map<String,String>> data;

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Map<String, String>> getData() {
        return data;
    }

    public void setData(Map<String, Map<String, String>> data) {
        this.data = data;
    }
    public WeichatMessage(){}
    public WeichatMessage(String toUser, String templateId, String url, Map<String, Map<String, String>> data) {
        this.toUser = toUser;
        this.templateId = templateId;
        this.url = url;
        this.data = data;
    }
}
