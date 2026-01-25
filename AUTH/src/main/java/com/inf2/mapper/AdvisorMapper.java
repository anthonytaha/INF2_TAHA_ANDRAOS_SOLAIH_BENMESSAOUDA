package com.inf2.mapper;

import com.inf2.domain.Advisor;
import com.inf2.dto.advisor.AdvisorCreateRequest;
import jakarta.inject.Singleton;

public class AdvisorMapper {
    public static Advisor toAdvisor(AdvisorCreateRequest request){
        return new Advisor(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getPassword(),
            request.getDateOfBirth(),
            request.getDepartmentCode()
        );
    }
}
