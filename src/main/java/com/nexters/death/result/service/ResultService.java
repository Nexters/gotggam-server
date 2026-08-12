package com.nexters.death.result.service;

import com.nexters.death.result.dto.ResultCountResponse;
import com.nexters.death.result.repository.ResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;

    public ResultCountResponse countParticipants() {
        long totalParticipants = resultRepository.count();
        return new ResultCountResponse(totalParticipants);
    }
}
