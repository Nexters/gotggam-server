package com.nexters.gotggam.consent.repository;

import com.nexters.gotggam.consent.entity.ConsentDocument;
import com.nexters.gotggam.consent.entity.ConsentType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsentDocumentRepository extends JpaRepository<ConsentDocument, Long> {

    Optional<ConsentDocument> findFirstByTypeOrderByIdDesc(ConsentType type);
}
