package com.multipolar.uiteller.backend.service.iso;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipolar.uiteller.backend.model.ResourceMessage;
import com.multipolar.uiteller.backend.model.converter.ConverterRule;
import com.multipolar.uiteller.backend.model.converter.RequestRule;
import com.multipolar.uiteller.backend.model.converter.Rule;
import com.multipolar.uiteller.backend.repository.ResourceMessageRepository;
import com.multipolar.uiteller.backend.resource.Constants;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class DefaultPdamDtiConverterHandler extends DefaultConverterHandler {

    private static Logger logger = LoggerFactory.getLogger(InquiryPdamDtiConverterHandler.class);

    public DefaultPdamDtiConverterHandler() {
    }

    @Override
    public boolean supports(String s) {
        return StringUtils.equalsAny(s, "pdamDtiInstallationInquiry", "pdamDtiInstallation");
    }

    @Override
    protected void setPrivateMessage(ISOMsg msg, Map<String, Object> map) throws ISOException {
        try {
            Rule rule = getRule();

            msg.set(3, rule.getPcode());
            if (StringUtils.equals(rule.getPcode(), Constants.CODE_INQUIRY) || StringUtils.equals(rule.getPcode(), Constants.CHECK_CODE_INQUIRY))
                msg.set(37, StringUtils.leftPad(getReferenceNumber(), 12, "0"));

            // override routing from parameter
            String routing = FeatureContextHolder.getContext().getInstansiParameter();
            if (StringUtils.isEmpty(routing))
                throw new ISOException("routing is empty");
            msg.set(63, routing);

            RequestRule[] requestRules = rule.getRequest();
            for (RequestRule requestRule : requestRules) {
                int bit = requestRule.getBit();
                ConverterRule[] values = requestRule.getValue();
                String valueForBit = getValueForBit(map, bit, values);
                msg.set(bit, valueForBit);
            }
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (JsonMappingException jme) {
            logger.error("Can't parsing rule file, please validate the json format");
            jme.printStackTrace();
        } catch (IOException iox) {
            logger.error(iox.getMessage());
            iox.printStackTrace();
        }
    }
}
