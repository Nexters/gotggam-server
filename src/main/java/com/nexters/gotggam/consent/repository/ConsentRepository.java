package com.nexters.gotggam.consent.repository;

import com.nexters.gotggam.consent.entity.Consent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {
}
