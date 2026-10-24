package com.multipolar.uiteller.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Data
@Audited
public class TransactionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private long id;

    private String fieldName;

    private String fieldValue;

    private String trxType;

    private String traceNumber;

    @Transient
    private String alias;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "common_transaction_id", nullable = false)
    private CommonTransactionLog commonTransaction;

    public TransactionLog() {
    }

    public TransactionLog(String fieldName, String fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public TransactionLog(String fieldName, String fieldValue, String trxType, String trace) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.trxType = trxType;
        this.traceNumber = trace;
    }

    public TransactionLog(String fieldName, String fieldValue, String trxType, String trace, CommonTransactionLog commonTransaction) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.trxType = trxType;
        this.traceNumber = trace;
        this.commonTransaction = commonTransaction;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TransactionLog other = (TransactionLog) obj;
        if (fieldName == null) {
            if (other.fieldName != null)
                return false;
        } else if (!fieldName.equals(other.fieldName))
            return false;
        if (traceNumber == null) {
            if (other.traceNumber != null)
                return false;
        } else if (!traceNumber.equals(other.traceNumber))
            return false;
        if (trxType == null) {
            if (other.trxType != null)
                return false;
        } else if (!trxType.equals(other.trxType))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
        result = prime * result + ((traceNumber == null) ? 0 : traceNumber.hashCode());
        result = prime * result + ((trxType == null) ? 0 : trxType.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "TransactionLog{" +
                "id=" + id +
                ", fieldName='" + fieldName + '\'' +
                ", fieldValue='" + fieldValue + '\'' +
                ", trxType='" + trxType + '\'' +
                ", traceNumber='" + traceNumber + '\'' +
                '}';
    }
}
