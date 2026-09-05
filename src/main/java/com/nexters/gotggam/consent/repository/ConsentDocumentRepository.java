package com.nexters.gotggam.consent.repository;

import com.nexters.gotggam.consent.entity.ConsentDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsentDocumentRepository extends JpaRepository<ConsentDocument, Long> {
}
