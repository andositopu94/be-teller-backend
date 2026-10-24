package com.multipolar.uiteller.backend.controller;

import com.multipolar.uiteller.backend.model.ResourceMessage;
import com.multipolar.uiteller.backend.repository.ResourceMessageRepository;
import com.multipolar.uiteller.backend.resource.BusinessException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class ResourceMessageController {
    @Autowired
    private ResourceMessageRepository resourceMessageRepository;

    @GetMapping("/resourceMessages")
    public List<ResourceMessage> getAll() {
        return resourceMessageRepository.findAll();
    }

    @GetMapping("/resourceMessagesTrxType")
    public List<ResourceMessage> getAllTrxtype() {
        return resourceMessageRepository.findTrxType();
    }

    @PostMapping("/resourceMessage")
    public List<ResourceMessage> create(@RequestBody ResourceMessage resourceMessage) throws InterruptedException, BusinessException, IOException {
        ResourceMessage rm = resourceMessageRepository.findFirstByCode(resourceMessage.getCode());
        resourceMessage.setCodeName(rm.getCodeName());
        try{
        resourceMessageRepository.save(resourceMessage);
        }
        catch (DataIntegrityViolationException e){
        	throw new BusinessException("Kode respon untuk jenis transaksi ini sudah terdaftar");
        }
        return resourceMessageRepository.findAll();
    }

    @PostMapping("/resourceMessage/{id}")
    public List<ResourceMessage> update(@PathVariable Long id, @RequestBody ResourceMessage resourceMessage) throws InterruptedException, BusinessException, IOException{
        ResourceMessage old = resourceMessageRepository.findOne(id);
        try{
        resourceMessageRepository.save(old);
        }
        catch (DataIntegrityViolationException e){
        	throw new BusinessException("Kode respon untuk jenis transaksi ini sudah terdaftar");
        }
        return resourceMessageRepository.findAll();
    }

    @DeleteMapping("resourceMessage/{id}")
    public List<ResourceMessage> delete(@PathVariable Long id) {
        ResourceMessage resourceMessage = resourceMessageRepository.findOne(id);
        resourceMessageRepository.delete(resourceMessage);

        return resourceMessageRepository.findAll();
    }

    @GetMapping("resourceMessage/{id}")
    public ResourceMessage findById(@PathVariable Long id) {
        return resourceMessageRepository.findOne(id);
    }
}
