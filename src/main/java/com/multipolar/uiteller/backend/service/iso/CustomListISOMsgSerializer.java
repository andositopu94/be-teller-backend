package com.multipolar.uiteller.backend.service.iso;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.multipolar.uiteller.backend.security.model.ISOMsgList;
import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
import org.jpos.iso.ISOMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonComponent
@Component
public class CustomListISOMsgSerializer extends JsonSerializer<ISOMsgList> {

    @Autowired
    private MessageConverterFactory converterFactory;

    @SuppressWarnings("unchecked")
    @Override
    public void serialize(ISOMsgList isoMsgList, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String feature = FeatureContextHolder.getContext().getFeatureName();
        List<ISOMsg> msgList = isoMsgList.getIsoMsg();
        MessageConverterHandler converter = converterFactory.get(feature);
        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        for (ISOMsg msg : msgList) {
        	try {
        		mapList.add(converter.doConvertToJSon(msg, false));
        	} catch (Exception ex) {
        		ex.printStackTrace();
        	}
        }
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("result");
        jsonGenerator.writeStartArray();
        for (Map<String, Object> objectMap : mapList) {
            jsonGenerator.writeStartObject();
            for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
                if (entry.getValue() instanceof String)
                    jsonGenerator.writeStringField(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
                else if (entry.getValue() instanceof Map) {
                    map = (Map<String, Object>) entry.getValue();
                } else if (entry.getValue() instanceof List<?>) {
            		jsonGenerator.writeFieldName(entry.getKey());
            		jsonGenerator.writeStartArray();
            		List<?> list = (List<?>)entry.getValue();
            		for (Object obj : list) {
            			jsonGenerator.writeObject(obj);
            		}
            		jsonGenerator.writeEndArray();
                } else {
                    jsonGenerator.writeStringField(entry.getKey(), entry.getValue() ==  null ? "" : entry.getValue().toString());
                }
            }
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
        writeIterativeMap(jsonGenerator, "status", map);
        jsonGenerator.writeEndObject();
    }

    private void writeIterativeMap(JsonGenerator generator, String key, Map<String, Object> innerMap) throws IOException {
        generator.writeFieldName(key);
        generator.writeStartObject();
        for (Map.Entry<String, Object> entry : innerMap.entrySet()) {
            if (entry.getValue() instanceof String)
                generator.writeStringField(entry.getKey(), entry.getValue().toString());
            else {
                generator.writeStartObject();
            }
        }
        generator.writeEndObject();
    }
}
