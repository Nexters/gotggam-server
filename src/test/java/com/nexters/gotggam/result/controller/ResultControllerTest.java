package com.nexters.gotggam.result.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexters.gotggam.TestcontainersConfiguration;
import com.nexters.gotggam.consent.dto.ConsentRequest;
import com.nexters.gotggam.consent.entity.ConsentType;
import com.nexters.gotggam.consent.repository.ConsentRepository;
import com.nexters.gotggam.policy.entity.AgeWeight;
import com.nexters.gotggam.policy.entity.LifeExpectancyPolicy;
import com.nexters.gotggam.question.entity.Act;
import com.nexters.gotggam.question.entity.Category;
import com.nexters.gotggam.question.entity.Question;
import com.nexters.gotggam.question.entity.QuestionOption;
import com.nexters.gotggam.question.entity.SpecialRule;
import com.nexters.gotggam.result.dto.AnswerRequest;
import com.nexters.gotggam.result.dto.CharacterRequest;
import com.nexters.gotggam.result.dto.SurveyResultRequest;
import com.nexters.gotggam.result.entity.Gender;
import com.nexters.gotggam.result.repository.ResultAnswerRepository;
import com.nexters.gotggam.result.repository.ResultCharacterRepository;
import com.nexters.gotggam.result.repository.ResultRepository;
import com.nexters.gotggam.result.repository.ResultSpecialRuleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ResultControllerTest {

    private static final Instant FIXED_NOW = Instant.parse("2026-08-13T12:00:00Z");
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Clock clock;

    @Autowired
    private ObjectMapper objectMapper;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private ResultAnswerRepository resultAnswerRepository;

    @Autowired
    private ResultCharacterRepository resultCharacterRepository;

    @Autowired
    private ResultSpecialRuleRepository resultSpecialRuleRepository;

    @Autowired
    private ConsentRepository consentRepository;

    private Long q1Id;
    private Long q2Id;
    private Long q3Id;
    private Long q4Id;
    private Long q1Positive;
    private Long q1Negative;
    private Long q1HugeNegative;
    private Long q2Positive;
    private Long q2Negative;
    private Long q3Positive;
    private Long q3Negative;
    private Long q4Positive;
    private Long q4Negative;
    private String q1Rule;
    private String q2Rule;
    private String q3Rule;
    private List<ConsentRequest> validConsents;

    @BeforeEach
    void seedFixtures() {
        given(clock.instant()).willReturn(FIXED_NOW);
        given(clock.getZone()).willReturn(KST);

        Category body = persist(Category.builder().categoryKey("BODY").name("몸").build());
        Category mind = persist(Category.builder().categoryKey("MIND").name("마음").build());
        Category attitude = persist(Category.builder().categoryKey("ATTITUDE").name("삶·태도").build());
        Act act = persist(Act.builder().code("morning").label("아침").displayOrder(1).build());

        Question question1 = persist(question("q1", act, body, 1));
        Question question2 = persist(question("q2", act, mind, 2));
        Question question3 = persist(question("q3", act, attitude, 3));
        Question question4 = persist(question("q4", act, body, 4));
        q1Id = question1.getId();
        q2Id = question2.getId();
        q3Id = question3.getId();
        q4Id = question4.getId();

        q1Positive = persist(option(question1, true, "0.00")).getId();
        q1Negative = persist(option(question1, false, "5.00")).getId();
        q1HugeNegative = persist(option(question1, false, "100.00")).getId();
        q2Positive = persist(option(question2, true, "0.00")).getId();
        q2Negative = persist(option(question2, false, "3.00")).getId();
        q3Positive = persist(option(question3, true, "0.00")).getId();
        q3Negative = persist(option(question3, false, "4.00")).getId();
        q4Positive = persist(option(question4, true, "0.00")).getId();
        q4Negative = persist(option(question4, false, "2.00")).getId();

        q1Rule = "규칙1-몸";
        q2Rule = "규칙2-마음";
        q3Rule = "규칙3-태도";
        persist(SpecialRule.builder().question(question1).description(q1Rule).build());
        persist(SpecialRule.builder().question(question2).description(q2Rule).build());
        persist(SpecialRule.builder().question(question3).description(q3Rule).build());
        persist(SpecialRule.builder().question(question4).description("규칙4-몸2").build());

        persist(LifeExpectancyPolicy.builder()
            .maleExpectancy(new BigDecimal("80.00"))
            .femaleExpectancy(new BigDecimal("86.00"))
            .minRemainingLife(new BigDecimal("20.00"))
            .build());

        persist(ageWeight(10, 19, "2.50", "3.00", "2.00"));
        persist(ageWeight(20, 29, "2.00", "2.50", "2.00"));
        persist(ageWeight(30, 39, "1.75", "1.50", "1.50"));
        persist(ageWeight(40, 49, "1.50", "0.50", "1.00"));
        persist(ageWeight(50, 59, "1.00", "0.50", "0.50"));
        persist(ageWeight(60, 69, "0.50", "0.25", "0.25"));
        persist(ageWeight(70, 79, "0.04", "0.04", "0.04"));

        validConsents = List.of(
            new ConsentRequest(ConsentType.PRIVACY_POLICY, "1.0", true),
            new ConsentRequest(ConsentType.TERMS_OF_SERVICE, "1.0", true)
        );

        em.flush();
    }

    @Test
    @DisplayName("모든 문항에 부정 응답하면 카테고리별 페널티와 페널티 높은 순 특별준수사항 3개를 반환한다")
    void createResult_allNegative() throws Exception {
        LocalDate birthDate = LocalDate.of(1990, 6, 15);
        SurveyResultRequest request = new SurveyResultRequest(
            "김철수",
            birthDate,
            Gender.MALE,
            "건강하게 오래오래 살고싶다",
            List.of(
                new AnswerRequest(q1Id, q1Negative),
                new AnswerRequest(q2Id, q2Negative),
                new AnswerRequest(q3Id, q3Negative),
                new AnswerRequest(q4Id, q4Negative)
            ),
            new CharacterRequest((short) 1, (short) 2, (short) 3, (short) 4, (short) 5),
            validConsents
        );

        // 36세(30~39 구간, body 1.75/mind 1.5/attitude 1.5): 몸 7.00×1.75=12.25, 마음 3.00×1.5=4.50,
        // 삶·태도 4.00×1.5=6.00 → 합 22.75 → 80−22.75=57.25 → 57 (현재 나이 36보다 커서 바닥 미적용)
        int expectedLife = 57;

        mockMvc.perform(post("/api/v1/results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.resultId").exists())
            .andExpect(jsonPath("$.data.shareToken").exists())
            .andExpect(jsonPath("$.data.name").value("김철수"))
            .andExpect(jsonPath("$.data.gender").value("MALE"))
            .andExpect(jsonPath("$.data.expectedLife").value(expectedLife))
            .andExpect(jsonPath("$.data.todayMessage").value("건강하게 오래오래 살고싶다"))
            .andExpect(jsonPath("$.data.warningMessage").isNotEmpty())
            .andExpect(jsonPath("$.data.character.faceType").value(1))
            .andExpect(jsonPath("$.data.categoryPenalties.length()").value(3))
            .andExpect(jsonPath("$.data.categoryPenalties[0].categoryName").value("몸"))
            .andExpect(jsonPath("$.data.categoryPenalties[0].penalty").value(12))
            .andExpect(jsonPath("$.data.categoryPenalties[1].categoryName").value("마음"))
            .andExpect(jsonPath("$.data.categoryPenalties[1].penalty").value(5))
            .andExpect(jsonPath("$.data.categoryPenalties[2].categoryName").value("삶·태도"))
            .andExpect(jsonPath("$.data.categoryPenalties[2].penalty").value(6))
            .andExpect(jsonPath("$.data.specialRules.length()").value(3))
            .andExpect(jsonPath("$.data.specialRules[0]").value(q1Rule))
            .andExpect(jsonPath("$.data.specialRules[1]").value(q3Rule))
            .andExpect(jsonPath("$.data.specialRules[2]").value(q2Rule));

        assertThat(resultRepository.count()).isEqualTo(1);
        assertThat(resultAnswerRepository.count()).isEqualTo(4);
        assertThat(resultCharacterRepository.count()).isEqualTo(1);
        assertThat(resultSpecialRuleRepository.count()).isEqualTo(3);
        assertThat(consentRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("모두 긍정 응답하면 특별준수사항은 고정 문구 1개만 반환하고 저장하지 않는다")
    void createResult_allPositive() throws Exception {
        SurveyResultRequest request = new SurveyResultRequest(
            "김영희",
            LocalDate.of(1995, 3, 20),
            Gender.FEMALE,
            null,
            List.of(
                new AnswerRequest(q1Id, q1Positive),
                new AnswerRequest(q2Id, q2Positive),
                new AnswerRequest(q3Id, q3Positive),
                new AnswerRequest(q4Id, q4Positive)
            ),
            new CharacterRequest((short) 1, (short) 1, (short) 1, (short) 1, (short) 1),
            validConsents
        );

        mockMvc.perform(post("/api/v1/results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.todayMessage").isNotEmpty())
            .andExpect(jsonPath("$.data.specialRules.length()").value(1));

        assertThat(resultSpecialRuleRepository.count()).isZero();
    }

    @Test
    @DisplayName("예상수명이 현재 나이 이하로 내려가면 현재 나이+5로 바닥 처리한다")
    void createResult_flooredAtInputAgePlus5() throws Exception {
        // 86세(70~79 구간으로 클램프, 가중치 0.04): 몸 102×0.04=4.08, 마음 3×0.04=0.12, 삶·태도 4×0.04=0.16
        // → 80−4.36=75.64, 현재 나이 86 이하이므로 86+5=91로 바닥 처리
        SurveyResultRequest request = new SurveyResultRequest(
            "박노인",
            LocalDate.of(1940, 1, 1),
            Gender.MALE,
            null,
            List.of(
                new AnswerRequest(q1Id, q1HugeNegative),
                new AnswerRequest(q2Id, q2Negative),
                new AnswerRequest(q3Id, q3Negative),
                new AnswerRequest(q4Id, q4Negative)
            ),
            new CharacterRequest((short) 1, (short) 1, (short) 1, (short) 1, (short) 1),
            validConsents
        );

        mockMvc.perform(post("/api/v1/results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.expectedLife").value(91));
    }

    @Test
    @DisplayName("answers에 null 원소가 있으면 400과 입력값 검증 에러코드를 반환한다")
    void createResult_nullAnswerElement() throws Exception {
        SurveyResultRequest request = new SurveyResultRequest(
            "김철수",
            LocalDate.of(1990, 6, 15),
            Gender.MALE,
            null,
            Collections.singletonList(null),
            new CharacterRequest((short) 1, (short) 1, (short) 1, (short) 1, (short) 1),
            validConsents
        );

        mockMvc.perform(post("/api/v1/results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.code").value("COMMON_004"));
    }

    @Test
    @DisplayName("활성 문항 중 답변이 누락되면 400을 반환한다")
    void createResult_incompleteSurvey() throws Exception {
        SurveyResultRequest request = new SurveyResultRequest(
            "김철수",
            LocalDate.of(1990, 6, 15),
            Gender.MALE,
            null,
            List.of(
                new AnswerRequest(q1Id, q1Negative),
                new AnswerRequest(q2Id, q2Negative),
                new AnswerRequest(q3Id, q3Negative)
            ),
            new CharacterRequest((short) 1, (short) 1, (short) 1, (short) 1, (short) 1),
            validConsents
        );

        mockMvc.perform(post("/api/v1/results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.code").value("RESULT_002"));
    }

    @Test
    @DisplayName("이름이 비어있으면 400과 입력값 검증 에러코드를 반환한다")
    void createResult_blankName() throws Exception {
        SurveyResultRequest request = new SurveyResultRequest(
            "",
            LocalDate.of(1990, 6, 15),
            Gender.MALE,
            null,
            List.of(new AnswerRequest(q1Id, q1Negative)),
            new CharacterRequest((short) 1, (short) 1, (short) 1, (short) 1, (short) 1),
            validConsents
        );

        mockMvc.perform(post("/api/v1/results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.code").value("COMMON_004"));
    }

    @Test
    @DisplayName("생년월일이 미래이면 400과 입력값 검증 에러코드를 반환한다")
    void createResult_futureBirthDate() throws Exception {
        SurveyResultRequest request = new SurveyResultRequest(
            "김철수",
            LocalDate.now().plusYears(1),
            Gender.MALE,
            null,
            List.of(
                new AnswerRequest(q1Id, q1Negative),
                new AnswerRequest(q2Id, q2Negative),
                new AnswerRequest(q3Id, q3Negative),
                new AnswerRequest(q4Id, q4Negative)
            ),
            new CharacterRequest((short) 1, (short) 1, (short) 1, (short) 1, (short) 1),
            validConsents
        );

        mockMvc.perform(post("/api/v1/results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.code").value("COMMON_004"));
    }

    @Test
    @DisplayName("필수 동의 항목 중 하나라도 동의하지 않으면 400을 반환한다")
    void createResult_consentNotAgreed() throws Exception {
        SurveyResultRequest request = new SurveyResultRequest(
            "김철수",
            LocalDate.of(1990, 6, 15),
            Gender.MALE,
            null,
            List.of(
                new AnswerRequest(q1Id, q1Negative),
                new AnswerRequest(q2Id, q2Negative),
                new AnswerRequest(q3Id, q3Negative),
                new AnswerRequest(q4Id, q4Negative)
            ),
            new CharacterRequest((short) 1, (short) 1, (short) 1, (short) 1, (short) 1),
            List.of(
                new ConsentRequest(ConsentType.PRIVACY_POLICY, "1.0", false),
                new ConsentRequest(ConsentType.TERMS_OF_SERVICE, "1.0", true)
            )
        );

        mockMvc.perform(post("/api/v1/results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.code").value("CONSENT_001"));
    }

    @Test
    @DisplayName("동의 항목 중 일부 유형이 누락되면 400을 반환한다")
    void createResult_incompleteConsent() throws Exception {
        SurveyResultRequest request = new SurveyResultRequest(
            "김철수",
            LocalDate.of(1990, 6, 15),
            Gender.MALE,
            null,
            List.of(
                new AnswerRequest(q1Id, q1Negative),
                new AnswerRequest(q2Id, q2Negative),
                new AnswerRequest(q3Id, q3Negative),
                new AnswerRequest(q4Id, q4Negative)
            ),
            new CharacterRequest((short) 1, (short) 1, (short) 1, (short) 1, (short) 1),
            List.of(new ConsentRequest(ConsentType.PRIVACY_POLICY, "1.0", true))
        );

        mockMvc.perform(post("/api/v1/results")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.code").value("CONSENT_002"));
    }

    private <T> T persist(T entity) {
        em.persist(entity);
        return entity;
    }

    private Question question(String code, Act act, Category category, int displayOrder) {
        return Question.builder()
            .code(code)
            .act(act)
            .content(code + " 내용")
            .category(category)
            .displayOrder(displayOrder)
            .active(true)
            .build();
    }

    private AgeWeight ageWeight(int startAge, int endAge, String body, String mind, String attitude) {
        return AgeWeight.builder()
            .startAge((short) startAge)
            .endAge((short) endAge)
            .bodyWeight(new BigDecimal(body))
            .mindWeight(new BigDecimal(mind))
            .attitudeWeight(new BigDecimal(attitude))
            .build();
    }

    private QuestionOption option(Question question, boolean positive, String lifePenalty) {
        return QuestionOption.builder()
            .question(question)
            .content("선택지")
            .positive(positive)
            .lifePenalty(new BigDecimal(lifePenalty))
            .build();
    }
}
