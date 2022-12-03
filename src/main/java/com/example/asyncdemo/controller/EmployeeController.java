package com.example.asyncdemo.controller;

import com.example.asyncdemo.entity.Employee;
import com.example.asyncdemo.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping(value = "/employee", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> saveEmployee(@RequestParam MultipartFile[] files) throws IOException {
        log.info("entering saveEmployee");
        try {
            for (MultipartFile file : files) {
                employeeService.saveEmployeeAsync(file);
            }
        } catch (Exception exception) {
           log.error("Exception while saving employees", exception);
           throw exception;
        }
        log.info("exiting saveEmployee");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/employee")
    public CompletableFuture<ResponseEntity<List<Employee>>> fetchEmployee() throws Exception {
        try {
            CompletableFuture<List<Employee>> employeeFuture = employeeService.fetchEmployeeAsync();
            return employeeFuture.thenApply(ResponseEntity::ok);
        } catch (Exception exception) {
            log.error("Exception while fetching all Employees", exception);
            throw exception;
        }
    }

}
