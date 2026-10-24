package com.multipolar.uiteller.backend.service.logger;

import com.multipolar.uiteller.backend.model.TransactionLog;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// FIXME belum selesai
@Service
public class PbbStsTransactionLogger extends DefaultTransactionLogger {
    private static final String SUPPORTED_URL = "pbb";

    @Override
    public List<TransactionLog> addLog(Map<String, Object> map) {
        List<TransactionLog> logs = super.addLog(map);

        final Map<String, String> params = new HashMap<>();
        params.put("denda", map.get("denda") == null || map.get("denda").toString().trim().isEmpty() ? "0" : map.get("denda").toString());
        params.put("jumlahPokok", map.get("jumlahPokok") == null || map.get("jumlahPokok").toString().trim().isEmpty() ? "0" : map.get("jumlahPokok").toString());
        params.put("masaPajak", map.get("masaPajak") == null ? "" : map.get("masaPajak").toString());
        params.put("mataAnggaran", map.get("mataAnggaran") == null ? "" : map.get("mataAnggaran").toString());
        params.put("namaWp", map.get("namaWp") == null ? "" : map.get("namaWp").toString());
        params.put("noSKP", map.get("noSKP") == null ? "" : map.get("noSKP").toString());
        params.put("noWp", map.get("noWp") == null ? "" : map.get("noWp").toString());
        params.put("alamat", map.get("alamat") == null ? "" : map.get("alamat").toString());
        params.put("uraian", map.get("uraian") == null ? "" : map.get("uraian").toString());
        if (map.get("checkNumber") != null)
            params.put("checkNumber", map.get("checkNumber").toString());
        if (map.get("checkPageNumber") != null)
            params.put("checkPageNumber", map.get("checkPageNumber").toString());
        for (Map.Entry<String, String> entry : params.entrySet()) {
            TransactionLog log = new TransactionLog(entry.getKey(), entry.getValue());
            logs.add(log);
        }

        // destination name
        String destinationName = FeatureContextHolder.getContext().getDestinationName();
        TransactionLog destinationLog = new TransactionLog("destinationName", destinationName);
        logs.add(destinationLog);

        return logs;
    }

    @Override
    public boolean supports(String s) {
        return StringUtils.equals(s, SUPPORTED_URL);
    }
}
