package com.inf2.service.domain;

import com.inf2.dao.AdvisorDAO;
import com.inf2.dao.impl.AdvisorDAOImpl;
import com.inf2.domain.Advisor;
import com.inf2.dto.advisor.AdvisorCreateRequest;
import com.inf2.dto.auth.UserUpdateRequest;
import com.inf2.mapper.AdvisorMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

@Singleton
public class AdvisorService {

    @Inject
    private AdvisorDAO advisorDAO;


    public Advisor getAdvisorById(UUID id) {
        return advisorDAO.find(id);
    }

    public Advisor getAdvisorByEmail(String email) {
        return advisorDAO.findByEmail(email);
    }

    public Advisor createAdvisor(AdvisorCreateRequest advisorCreateRequest) {

        Advisor user = AdvisorMapper.toAdvisor(advisorCreateRequest);

        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        return advisorDAO.save(user);
    }

    public void updateAdvisor(UUID id, UserUpdateRequest userUpdateRequest){
        advisorDAO.update(id, userUpdateRequest);
    }

    public void deleteAdvisor(UUID id) {
        advisorDAO.delete(id);
    }
}
