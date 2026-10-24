 package com.multipolar.uiteller.backend.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.multipolar.uiteller.backend.model.*;
import com.multipolar.uiteller.backend.repository.InstansiPdamRepository;
import com.multipolar.uiteller.backend.repository.PDAMRepository;
import com.multipolar.uiteller.backend.service.autodebit.AutoDebitMessaging;
import com.multipolar.uiteller.backend.service.autodebit.AutodebitLogService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.multipolar.uiteller.backend.repository.AutodebitLogRepository;
import com.multipolar.uiteller.backend.repository.AutodebitRepository;
import com.multipolar.uiteller.backend.resource.BusinessException;
import com.multipolar.uiteller.backend.security.model.UserDetailImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/apiv2")
public class AutodebitController {
	private final InstansiPdamRepository instansiPdamRepository;
	
	@Autowired
	private AutodebitRepository autodebitRepository;

	@Autowired
	private AutodebitLogService autodebitLogService;

	@Autowired
	private PDAMRepository pdamRepository;

	@Autowired
	private AutoDebitMessaging autoDebitMessaging;

	private static final String[] HQ_BRANCHCODE = {"001", "601"};

	@GetMapping("/autodebits")
	public List<Autodebit> getAutodebits(@RequestParam(name="trxName", required=false) String trxName) {
		UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails(); 
		if (StringUtils.equalsAny(userDetail.getBranchCode(), HQ_BRANCHCODE)) {
			if (StringUtils.isEmpty(trxName))
				return autodebitRepository.findAll();
			else {
				if(StringUtils.equals(trxName, "dti")){
					List<Autodebit> autodebits = autodebitRepository.findAll();
					List<Autodebit> autodebitDti = new ArrayList<>();
					String[] PDAMName = pdamRepository.fetchPDAMName();
					for (Autodebit autodebit:autodebits) {
						if(StringUtils.equalsAny(autodebit.getTransactionName(),PDAMName)){
							autodebitDti.add(autodebit);
						}
					}
					return autodebitDti;
				}
				return autodebitRepository.findByTransactionName(trxName);
			}
		} else {
			if (StringUtils.isEmpty(trxName))
				return autodebitRepository.findByBranchCode(userDetail.getBranchCode());
			else{
				if(StringUtils.equals(trxName, "dti")){
					List<Autodebit> autodebits = autodebitRepository.findByBranchCode(userDetail.getBranchCode());
					List<Autodebit> autodebitDti = new ArrayList<>();
					String[] PDAMName = pdamRepository.fetchPDAMName();
					for (Autodebit autodebit:autodebits) {
						if(StringUtils.equalsAny(autodebit.getTransactionName(),PDAMName)){
							autodebitDti.add(autodebit);
						}
					}
					return autodebitDti;
				}
				return autodebitRepository.findByTransactionNameAndBranchCode(trxName, userDetail.getBranchCode());
			}
		}
	}
	
	@GetMapping("/autodebit/{id}")
	public Autodebit getAutodebit(@PathVariable Long id) {
		return autodebitRepository.findOne(id);
	}
	
	@PostMapping("/autodebit")
	public List<Autodebit> create(@RequestBody Autodebit autodebit) throws BusinessException {
		// check exists
		List<Autodebit> data = autodebitRepository.findByTransactionNameAndCustomerId(autodebit.getTransactionName(), autodebit.getCustomerId());
		if (data.size() > 0) {
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			Autodebit existings = data.get(0);
			throw new BusinessException("Data sudah diinput di cabang " + existings.getBranchCode() + " pada " + format.format(existings.getCreatedAt()));
		}
		
		// if bpjs add fillers with monthNumber = 1
		if (StringUtils.equals(autodebit.getTransactionName(), "bpjs")) {
			autodebit.setFillerText1("monthNumber");
			autodebit.setFillerValue1("1");
		}
		InstansiPdam pdamDti = instansiPdamRepository.findByNamaInstansi(autodebit.getTransactionName());
		if (pdamDti != null) {
			autodebit.setFillerText1("aliasTransactionName");
			autodebit.setFillerValue1("pdamDti");
			autodebit.setFillerText2("kodeInstansi");
			autodebit.setFillerValue2(pdamDti.getKodeInstansi());
		}
		UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();  
		autodebit.setBranchCode(userDetail.getBranchCode());
		autodebitRepository.save(autodebit);
		
		return getAutodebits(null);
	}
	
	@PostMapping("/autodebit/{id}")
	public List<Autodebit> update(@PathVariable Long id, @RequestBody Autodebit autodebit) throws BusinessException {
		Autodebit model = autodebitRepository.findOne(id);
		if (model == null)
			throw new IllegalArgumentException("data not found");

		model.setTransactionName(autodebit.getTransactionName());
		model.setStartDate(autodebit.getStartDate());
		model.setEndDate(autodebit.getEndDate());
		model.setSourceNumber(autodebit.getSourceNumber());
		model.setCustomerId(autodebit.getCustomerId());
		model.setComment(autodebit.getComment());
		model.setUserCreator(autodebit.getUserCreator());

		UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();
		model.setBranchCode(userDetail.getBranchCode());
		
		autodebitRepository.save(model);
		autodebitRepository.flush();
		
		return getAutodebits(null);
	}
	
	@DeleteMapping("/autodebit/{id}")
	public List<Autodebit> softDelete(@PathVariable Long id) {
		Autodebit model = autodebitRepository.findOne(id);
		if (model == null)
			throw new IllegalArgumentException("data not found");
		
		model.setDeleted(1);
		autodebitRepository.save(model);
		
		return getAutodebits(null);
	}
	
	@GetMapping("/autodebitlogs")
	public AutodebitLogResponse getAutodebitLogs(@ModelAttribute AutodebitLogRequest request) {
		UserDetailImpl userDetail = (UserDetailImpl) SecurityContextHolder.getContext().getAuthentication().getDetails();

		return autodebitLogService.searchWithSpecification(request, userDetail.getBranchCode());
	}
}