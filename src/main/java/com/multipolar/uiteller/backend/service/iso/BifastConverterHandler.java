package com.multipolar.uiteller.backend.service.iso;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.multipolar.uiteller.backend.model.converter.ConverterRule;
import com.multipolar.uiteller.backend.model.converter.RequestRule;
import com.multipolar.uiteller.backend.model.converter.Rule;
import com.multipolar.uiteller.backend.resource.Constants;
import org.apache.commons.lang3.StringUtils;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

@Service
public class BifastConverterHandler extends DefaultConverterHandler{
    private static final String BIFAST = "bifast";
    private  static final String BIFAST_INQUIRY = "bifastInquiry";

    @Override
    public boolean supports(String s) {
        return StringUtils.equals(BIFAST, s) || StringUtils.equals(BIFAST_INQUIRY, s);
    }
    private static Logger logger = LoggerFactory.getLogger(DefaultConverterHandler.class);
    @Override
    protected void setPrivateMessage(ISOMsg msg, Map<String, Object> map) throws ISOException {
        try {
            Rule rule = getRule();

            msg.set(3, rule.getPcode());
//            if (StringUtils.equals(rule.getPcode(), Constants.CODE_INQUIRY) || StringUtils.equals(rule.getPcode(), Constants.CHECK_CODE_INQUIRY))
            msg.set(63, rule.getRouting());
            RequestRule[] requestRules = rule.getRequest();
            for (RequestRule requestRule : requestRules) {
                int bit = requestRule.getBit();
                ConverterRule[] values = requestRule.getValue();
                String valueForBit = getValueForBit(map, bit, values);
                msg.set(bit, valueForBit);
            }
            String ref = "00008" + StringUtils.leftPad(getReferenceNumber(), 12, "0").substring(5);
            logger.debug("REFF BI "+ref);
            msg.set(37, ref);
            logger.debug("REFERENCE NUMBER BI : "+ msg.getString(37));
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
