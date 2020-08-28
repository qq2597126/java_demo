package io.netty.decoder;

import java.io.Serializable;

/**
 * @author lcy
 * @DESC:
 * @date 2020/8/28.
 */
public class JsonMessageBo implements Serializable {
    private Integer id;
    private String message;
    private Boolean isSuccess;

    public JsonMessageBo() {
    }

    public JsonMessageBo(Integer id, String message, Boolean isSuccess) {
        this.id = id;
        this.message = message;
        this.isSuccess = isSuccess;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    @Override
    public String toString() {
        return "JsonMessageBo{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", isSuccess=" + isSuccess +
                '}';
    }
}
