package com.nexters.death.result.service;

import com.nexters.death.question.entity.Question;
import com.nexters.death.question.entity.QuestionOption;

record AnsweredQuestion(Question question, QuestionOption option) {
}
