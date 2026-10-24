package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.security.model.Check;
import com.multipolar.uiteller.backend.security.model.CheckResponse;
import com.multipolar.uiteller.backend.security.model.Detail;
import com.multipolar.uiteller.backend.security.model.DetailResponse;
import com.multipolar.uiteller.backend.service.CoreGatewayServiceImpl;
import com.multipolar.uiteller.backend.service.SiskohatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apiv3")
public class AccountController {

    @Autowired
    private CoreGatewayServiceImpl coreGatewayService;
    @Autowired
    private SiskohatService siskohatService;

    @PostMapping("/detail")
    public DetailResponse getDetail(@RequestBody Detail detail) {
        DetailResponse detailResponse = coreGatewayService.getDetailResponse(detail.getId());
        return siskohatService.getDetail(detailResponse);
    }

    @PostMapping("/check")
    public CheckResponse getCheck(@RequestBody Check check) {
        CheckResponse checkResponse = coreGatewayService.getCheckResponse(check.getSourceNumber(), check.getChkNumber(), check.getChkPage());
        if (checkResponse == null)
            throw new IllegalArgumentException("Time-out exception, host give no response");
        return checkResponse;
    }
}