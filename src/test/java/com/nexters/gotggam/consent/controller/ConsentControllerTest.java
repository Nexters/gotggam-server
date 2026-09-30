package com.nexters.gotggam.consent.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexters.gotggam.TestcontainersConfiguration;
import com.nexters.gotggam.consent.entity.ConsentDocument;
import com.nexters.gotggam.consent.entity.ConsentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ConsentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("약관 유형별로 나중에 넣은 버전 한 건씩을 enum 순서대로 반환한다")
    void getLatestDocuments() throws Exception {
        persist(document(ConsentType.TERMS_OF_SERVICE, "v1.0.0", "# 이용약관 v1.0.0"));
        persist(document(ConsentType.PRIVACY_POLICY, "v1.0.0", "# 처리방침 v1.0.0"));
        persist(document(ConsentType.PRIVACY_POLICY, "v1.1.0", "# 처리방침 v1.1.0"));

        em.flush();
        em.clear();

        mockMvc.perform(get("/api/v1/consent-documents"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].type").value("PRIVACY_POLICY"))
            .andExpect(jsonPath("$.data[0].version").value("v1.1.0"))
            .andExpect(jsonPath("$.data[0].content").value("# 처리방침 v1.1.0"))
            .andExpect(jsonPath("$.data[1].type").value("TERMS_OF_SERVICE"))
            .andExpect(jsonPath("$.data[1].version").value("v1.0.0"))
            .andExpect(jsonPath("$.data[1].content").value("# 이용약관 v1.0.0"));
    }

    @Test
    @DisplayName("약관 유형 중 하나라도 문서가 없으면 404를 반환한다")
    void getLatestDocumentsWhenMissing() throws Exception {
        persist(document(ConsentType.PRIVACY_POLICY, "v1.0.0", "# 처리방침 v1.0.0"));

        em.flush();
        em.clear();

        mockMvc.perform(get("/api/v1/consent-documents"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error.code").value("CONSENT_003"));
    }

    private <T> T persist(T entity) {
        em.persist(entity);
        return entity;
    }

    private ConsentDocument document(ConsentType type, String version, String content) {
        return ConsentDocument.builder()
            .type(type)
            .version(version)
            .content(content)
            .build();
    }
}
