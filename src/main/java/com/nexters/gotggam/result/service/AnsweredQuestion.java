package com.nexters.gotggam.result.service;

import com.nexters.gotggam.question.entity.Question;
import com.nexters.gotggam.question.entity.QuestionOption;

record AnsweredQuestion(Question question, QuestionOption option) {
}
