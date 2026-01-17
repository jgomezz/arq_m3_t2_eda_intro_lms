package com.tecsup.lms.enrollments.application.query;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EnrollmentQueryRepository {

    private final Map<String, EnrollmentReadModel>
            readModels = new HashMap<>();

    public void save(EnrollmentReadModel readModel) {
        readModels.put(readModel.getEnrollmentId(), readModel);
    }

    public Optional<EnrollmentReadModel> findById(String id) {
        return Optional.ofNullable(readModels.get(id));
    }

    public List<EnrollmentReadModel> findAll() {
        return new ArrayList<>(readModels.values());
    }

}
