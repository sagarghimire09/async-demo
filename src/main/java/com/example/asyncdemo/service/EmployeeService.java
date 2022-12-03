package com.example.asyncdemo.service;

import com.example.asyncdemo.entity.Employee;
import com.example.asyncdemo.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Async("asyncTaskExecutor")
    public CompletableFuture<List<Employee>> saveEmployeeAsync(MultipartFile file) throws IOException {
        long startTime = System.currentTimeMillis();
        List<Employee> employeeList = parseCSVFile(file);
        employeeList = employeeRepository.saveAll(employeeList);
        long endTime = System.currentTimeMillis();
        log.info("time taken to save employees by Thread {} is {} ms", Thread.currentThread().getName(),
                endTime - startTime);
        return CompletableFuture.completedFuture(employeeList);
    }

    @Async("asyncTaskExecutor")
    public CompletableFuture<List<Employee>> fetchEmployeeAsync() {
        long startTime = System.currentTimeMillis();
        List<Employee> employeeList = employeeRepository.findAll();
        long endTime = System.currentTimeMillis();
        log.info("time taken to fetch employees by Thread {} is {} ms", Thread.currentThread().getName(),
                endTime - startTime);
        return CompletableFuture.completedFuture(employeeList);
    }

    private List<Employee> parseCSVFile(MultipartFile file) throws IOException {
        final List<Employee> employeeList = new ArrayList<>();
        BufferedReader bufferedReader = null;
        try {
            String line;
            bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(",");
                final Employee employee = Employee.builder()
                        .firstName(data[0])
                        .lastName(data[1])
                        .employmentType(data[2])
                        .build();
                employeeList.add(employee);
            }
        } catch (IndexOutOfBoundsException ex) {
           log.error("Exception while parsing to Employee object", ex);
           throw ex;
        } catch (IOException exception) {
            log.error("IO Exception while reading the employee CSV file", exception);
            throw exception;
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return employeeList;
    }
}
