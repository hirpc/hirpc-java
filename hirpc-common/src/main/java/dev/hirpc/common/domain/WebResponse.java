package dev.hirpc.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;


/**
 * author: JT
 * date: 2020/6/7
 * title:
 */
//@ApiModel(value = "响应数据")
public class WebResponse<T> {

   //    @ApiModelProperty(value = "响应码, 200: 正常返回")
   private Integer code;

   //    @ApiModelProperty(value = "响应返回信息")
   @JsonInclude(JsonInclude.Include.NON_NULL)
   private String msg;

   //    @ApiModelProperty(value = "响应返回数据")
   @JsonInclude(JsonInclude.Include.NON_NULL)
   private T data;

   public WebResponse<T> setCode(Integer code) {
      this.code = code;
      return this;
   }

   public WebResponse<T> setMsg(String msg) {
      this.msg = msg;
      return this;
   }

   public  WebResponse<T> data(T data) {
      this.data = data;
      return this;
   }

   public  WebResponse<T> setData(T data) {
      this.data = data;
      return this;
   }

   public Integer getCode() {
      return code;
   }

   public String getMsg() {
      return msg;
   }

   public T getData() {
      return data;
   }
}

