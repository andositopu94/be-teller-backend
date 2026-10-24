package com.multipolar.uiteller.backend.service.autodebit;

import com.multipolar.uiteller.backend.model.InstansiPdam;
import com.multipolar.uiteller.backend.repository.InstansiPdamRepository;
import com.multipolar.uiteller.backend.repository.PDAMRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PDAMDtiAutoDebit extends AbstractAutodebitProcessor {

    @Autowired
    private InstansiPdamRepository instansiPdamRepository;
    @Autowired
    private PDAMRepository pdamRepository;
    String feature;
    @Override
    public boolean supports(String feature) {
        this.feature=feature;
        return StringUtils.equals(feature, "pdamDti");
    }

    @Override
    public String getMessageRouting() {
        return null;
    }

    @Override
    public String getMessageRouting(String instansi) {
        List<InstansiPdam> instansiPdams = instansiPdamRepository.findAll();
        for (InstansiPdam instansiPdam: instansiPdams) {
            if (instansiPdam.getNamaInstansi().equalsIgnoreCase(instansi)){
                return instansiPdam.getKodeInstansi();
            }
        }
        return null;
    }

    @Override
    protected void setErrorMappingKey(Map<String, Object> map) {
        map.put("errorKey", feature.replaceAll("\\s+", ""));
        map.put("errorInquiryKey", "inquiry"+feature.replaceAll("\\s+", ""));
    }

}
