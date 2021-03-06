package ru.otus.hw01.dao;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import ru.otus.hw01.dao.exception.AnswerLoadingException;
import ru.otus.hw01.domain.Answer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnswerDaoCsvImpl implements AnswerDao {
    private static final Logger logger = LoggerFactory.getLogger(AnswerDaoCsvImpl.class);
    private final List<Answer> answers = new ArrayList<>();

    AnswerDaoCsvImpl(Resource file) throws AnswerLoadingException {
        try (var csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            csvReader.readNext(); // Пропускаем строку с заголовками
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                int id = Integer.parseInt(values[0]);
                int questionId = Integer.parseInt(values[1]);
                String answerText = values[2];
                boolean isCorrect = Boolean.parseBoolean(values[3]);
                answers.add(new Answer(id, questionId, answerText, isCorrect));
            }
        } catch (FileNotFoundException e) {
            throw new AnswerLoadingException("Answers file (" + file.getFilename() + ") not found", e);
        } catch (IOException e) {
            throw new AnswerLoadingException("Failed to read answers file (" + file.getFilename() + ")", e);
        } catch (CsvValidationException e) {
            throw new AnswerLoadingException("An error occurred while reading the answers file (" + file.getFilename() + ")", e);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            throw new AnswerLoadingException("Answers file contains invalid data (" + file.getFilename() + ")", e);
        }
    }

    @Override
    public List<Answer> getByQuestionId(int id) {
        return answers.stream().filter(it -> it.getQuestionId() == id).collect(Collectors.toList());
    }

    @Override
    public Optional<Answer> getById(int id) {
        return answers.stream().filter(it -> it.getId() == id).findFirst();
    }

    @Override
    public List<Answer> getAll() {
        return answers;
    }
}
