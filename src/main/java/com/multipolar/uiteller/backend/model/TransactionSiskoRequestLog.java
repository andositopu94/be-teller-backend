package com.multipolar.uiteller.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class TransactionSiskoRequestLog {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private long id;

	private String fieldName;

	@Column(length = 3000)
	private String fieldValue;

	private String trxType;

	private String traceNumber;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="common_transaction_sisko_request_id", nullable = false)
	private CommonTransactionSiskoRequestLog commonTransactionSiskoRequestLog;

	public TransactionSiskoRequestLog() {}

	public TransactionSiskoRequestLog(String fieldName, String fieldValue) {
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

	public TransactionSiskoRequestLog(String fieldName, String fieldValue, String trxType, String trace) {
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.trxType = trxType;
		this.traceNumber = trace;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		TransactionSiskoRequestLog that = (TransactionSiskoRequestLog) o;

		if (!fieldName.equals(that.fieldName)) return false;
		if (!fieldValue.equals(that.fieldValue)) return false;
		if (!trxType.equals(that.trxType)) return false;
		return traceNumber != null ? traceNumber.equals(that.traceNumber) : that.traceNumber == null;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + fieldName.hashCode();
		result = 31 * result + fieldValue.hashCode();
		result = 31 * result + trxType.hashCode();
		result = 31 * result + (traceNumber != null ? traceNumber.hashCode() : 0);
		return result;
	}
}
