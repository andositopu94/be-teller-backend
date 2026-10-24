    package com.multipolar.uiteller.backend.controller;

    import com.multipolar.uiteller.backend.security.model.ISOMsgList;
    import com.multipolar.uiteller.backend.security.model.OtherChannel;
    import com.multipolar.uiteller.backend.service.MiddlewareGatewayServiceImpl;
    import com.multipolar.uiteller.backend.servletfilter.FeatureContextHolder;
    import org.apache.commons.lang.StringUtils;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping("/apiv6")
    public class OtherChannelController {

        @Autowired
        private MiddlewareGatewayServiceImpl middlewareGatewayService;
        private static final String SIPD = "sipd";

        @PostMapping("/**")
        public ISOMsgList request(@RequestBody OtherChannel otherChannel) {
            String context = FeatureContextHolder.getContext().getFeatureName();

            ISOMsgList isoMsgList = new ISOMsgList();
            if(StringUtils.equals(context, SIPD)){
                isoMsgList.setIsoMsg(middlewareGatewayService.getSipdDetailResponse(otherChannel.getId()));
            }else {
                isoMsgList.setIsoMsg(middlewareGatewayService.getDetailResponse(otherChannel.getCard(), otherChannel.getId()));
            }
            return isoMsgList;
        }
    }
