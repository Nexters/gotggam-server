package com.nexters.death.question.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nexters.death.TestcontainersConfiguration;
import com.nexters.death.question.entity.Act;
import com.nexters.death.question.entity.Category;
import com.nexters.death.question.entity.Question;
import com.nexters.death.question.entity.QuestionOption;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
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
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("act의 displayOrder, 문항의 displayOrder 순으로 그룹핑된 질문지를 반환하고 비활성 문항은 제외한다")
    void getQuestionnaire() throws Exception {
        Category body = persist(Category.builder().categoryKey("BODY").name("몸").build());

        Act commute = persist(Act.builder().code("commute").label("출근").displayOrder(2).build());
        Act morning = persist(Act.builder().code("morning").label("아침").displayOrder(1).build());

        Question q1 = persist(question("q1", morning, body, 1, true));
        Question q2 = persist(question("q2", commute, body, 2, true));
        persist(question("q3", morning, body, 3, false));

        persist(option(q1, "뭐라도 씹는다.", "제법이다냥.", false));
        persist(option(q1, "커피 수혈은 필수다.", "카페인 자판기냥?", true));
        persist(option(q2, "버튼부터 누른다", "게으르다냥.", false));

        em.flush();
        em.clear();

        mockMvc.perform(get("/api/v1/questions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].act.code").value("morning"))
            .andExpect(jsonPath("$.data[0].act.label").value("아침"))
            .andExpect(jsonPath("$.data[0].questions.length()").value(1))
            .andExpect(jsonPath("$.data[0].questions[0].id").value(q1.getId()))
            .andExpect(jsonPath("$.data[0].questions[0].answers.length()").value(2))
            .andExpect(jsonPath("$.data[0].questions[0].answers[0].answer").value("커피 수혈은 필수다."))
            .andExpect(jsonPath("$.data[0].questions[0].answers[0].feedback").value("카페인 자판기냥?"))
            .andExpect(jsonPath("$.data[0].questions[0].answers[1].answer").value("뭐라도 씹는다."))
            .andExpect(jsonPath("$.data[1].act.code").value("commute"))
            .andExpect(jsonPath("$.data[1].questions.length()").value(1))
            .andExpect(jsonPath("$.data[1].questions[0].id").value(q2.getId()));
    }

    private <T> T persist(T entity) {
        em.persist(entity);
        return entity;
    }

    private Question question(String code, Act act, Category category, int displayOrder, boolean active) {
        return Question.builder()
            .code(code)
            .act(act)
            .content(code + " 내용")
            .category(category)
            .displayOrder(displayOrder)
            .active(active)
            .build();
    }

    private QuestionOption option(Question question, String content, String feedback, boolean positive) {
        return QuestionOption.builder()
            .question(question)
            .content(content)
            .feedback(feedback)
            .positive(positive)
            .lifePenalty(BigDecimal.ZERO)
            .build();
    }
}
