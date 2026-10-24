package com.multipolar.uiteller.backend.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ResourceMessage implements Serializable {

    private static final long serialVersionUID = 2588984667657982697L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Long Id;
    private String code;
    private String codeName;
    private String message;
    private String value;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceMessage that = (ResourceMessage) o;

        if (!Id.equals(that.Id)) return false;
        if (!code.equals(that.code)) return false;
        if (!message.equals(that.message)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = Id.hashCode();
        result = 31 * result + code.hashCode();
        result = 31 * result + message.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ResourceMessage{" +
                "Id=" + Id +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
