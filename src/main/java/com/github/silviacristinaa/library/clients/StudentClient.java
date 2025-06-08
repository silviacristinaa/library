package com.github.silviacristinaa.library.clients;

import com.github.silviacristinaa.library.dtos.responses.StudentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "student", url = "http://localhost:8080/api/v1")
public interface StudentClient {

    @GetMapping("/students/{id}")
    StudentResponseDto findById(@PathVariable Long id);
}
