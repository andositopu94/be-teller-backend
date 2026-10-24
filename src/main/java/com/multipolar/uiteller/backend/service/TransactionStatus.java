package com.multipolar.uiteller.backend.service;

import com.multipolar.uiteller.backend.resource.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class TransactionStatus {

    /**
     * @param status 0 = Berhasil | 1 = Gagal | 2 = Unknown
     * @return
     */
    public String getStatus(String status) {
        if (status.equalsIgnoreCase("0")) {
            return "Berhasil";
        } else if (status.equalsIgnoreCase("1")) {
            return "Gagal";
        } else {
            return "Unknown";
        }
    }

    public String getSts(String sts) {
        if (StringUtils.equals(sts, Constants.ZERO))
            return "WHERE STATUS_LOG = '" + Constants.ZERO + "' ";
        else
            return "WHERE STATUS_LOG != '" + Constants.ZERO + "' ";
    }

    public String getLog(String sts) {
        if (StringUtils.equals(sts, Constants.ZERO))
            return "STATUS_LOG = '" + Constants.ZERO + "' ";
        else
            return "STATUS_LOG != '" + Constants.ZERO + "' ";
    }
}
