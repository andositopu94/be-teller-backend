package com.multipolar.uiteller.backend.model;

import java.util.Date;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Table(name="http_request_log")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
@Data
public class HttpRequestLog {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private long id;
	
	private String reqMethod;
	private String reqUri;
	private String reqHeaders;
	@Lob
	private String reqBody;
	private String resStatus;
	@Lob
	private String resHeaders;
	@Lob
	private String resBody;
	
	
	
	@Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HttpRequestLog other = (HttpRequestLog) obj;
		if (reqBody == null) {
			if (other.reqBody != null)
				return false;
		} else if (!reqBody.equals(other.reqBody))
			return false;
		if (reqHeaders == null) {
			if (other.reqHeaders != null)
				return false;
		} else if (!reqHeaders.equals(other.reqHeaders))
			return false;
		if (reqMethod == null) {
			if (other.reqMethod != null)
				return false;
		} else if (!reqMethod.equals(other.reqMethod))
			return false;
		if (reqUri == null) {
			if (other.reqUri != null)
				return false;
		} else if (!reqUri.equals(other.reqUri))
			return false;
		if (resBody == null) {
			if (other.resBody != null)
				return false;
		} else if (!resBody.equals(other.resBody))
			return false;
		if (resHeaders == null) {
			if (other.resHeaders != null)
				return false;
		} else if (!resHeaders.equals(other.resHeaders))
			return false;
		if (resStatus == null) {
			if (other.resStatus != null)
				return false;
		} else if (!resStatus.equals(other.resStatus))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((reqBody == null) ? 0 : reqBody.hashCode());
		result = prime * result + ((reqHeaders == null) ? 0 : reqHeaders.hashCode());
		result = prime * result + ((reqMethod == null) ? 0 : reqMethod.hashCode());
		result = prime * result + ((reqUri == null) ? 0 : reqUri.hashCode());
		result = prime * result + ((resBody == null) ? 0 : resBody.hashCode());
		result = prime * result + ((resHeaders == null) ? 0 : resHeaders.hashCode());
		result = prime * result + ((resStatus == null) ? 0 : resStatus.hashCode());
		return result;
	}
}
