package ch.uzh.ifi.access.student.dao;

import ch.uzh.ifi.access.student.dto.UserMigrationResult;
import ch.uzh.ifi.access.student.model.StudentSubmission;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
class CustomizedStudentSubmissionRepositoryImpl implements CustomizedStudentSubmissionRepository {

    private MongoTemplate mongoTemplate;

    public CustomizedStudentSubmissionRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void invalidateSubmissionsByExerciseId(String exerciseId) {
        Query query = Query.query(Criteria.where("exerciseId").is(exerciseId));
        Update update = Update.update("isInvalid", true);

        UpdateResult result = mongoTemplate.updateMulti(query, update, StudentSubmission.class);
        log.debug(String.format("Invalidated %d submissions", result.getModifiedCount()));
    }

    @Override
    public void invalidateSubmissionsByExerciseIdAndUserId(String exerciseId, String userId) {
        Query query = Query.query(Criteria.where("exerciseId").is(exerciseId).and("userId").is(userId));
        Update update = Update.update("isInvalid", true);

        UpdateResult result = mongoTemplate.updateMulti(query, update, StudentSubmission.class);
        log.debug(String.format("Invalidated %d submissions", result.getModifiedCount()));
    }

    @Override
    public boolean existsByUserIdAndHasNoResultOrConsoleNotOlderThan10min(String userId) {
        Query query = Query.query(Criteria
                .where("userId").is(userId)
                .and("console").exists(false)
                .and("result").exists(false)
                .and("timestamp").gt(Instant.now().minus(1, ChronoUnit.MINUTES)));
        return mongoTemplate.exists(query, StudentSubmission.class);
    }

    /**
     * Sets all submissions with userId == beforeId to afterId and inverts version number to maintain consistency of submissions.
     * For example if before there were two submissions s1 and s2 with version 0 and 1,
     * after the migration the submissions would have versions s1.version == -2 and s2.version == -1
     * This way the versioning remains consistent (s1 is still the first submitted version -> has the lowest version number)
     * and does not interfere with other submissions that might have occurred with the new account.
     *
     * @param from the userId of the old account
     * @param to  the userId of the new account
     * @return number of submissions which were changed
     */
    @Override
    public UserMigrationResult migrateUserSubmissions(String from, String to) {
        List<Map<String, List<StudentSubmission>>> submissionsByExercises = submissionsByExercises(from);

        UserMigrationResult userMigrationResult = new UserMigrationResult(from, to);
        int totalUpdated = 0;
        int submissionToMigrate = submissionsByExercises
                .stream()
                .filter(map -> map.containsKey("submissions"))
                .map(map -> map.get("submissions"))
                .mapToInt(List::size)
                .sum();

        try {
            for (Map<String, List<StudentSubmission>> submissionsByExercise : submissionsByExercises) {
                List<StudentSubmission> submissions = submissionsByExercise.get("submissions");
                for (StudentSubmission submission : submissions) {
                    submission.setVersion(submission.getVersion() - submissions.size());
                    submission.setUserId(to);
                    mongoTemplate.save(submission);
                    totalUpdated++;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to migrated submissions for {} to {}", from, to, e);
            userMigrationResult.setSuccess(false);
        }

        userMigrationResult.setNumberOfSubmissionsMigrated(totalUpdated);
        userMigrationResult.setNumberOfSubmissionsToMigrate(submissionToMigrate);
        userMigrationResult.setSuccess(totalUpdated == submissionToMigrate);
        return userMigrationResult;
    }

    private List<Map<String, List<StudentSubmission>>> submissionsByExercises(String userId) {
        Criteria criteria = Criteria.where("userId").is(userId);
        MatchOperation matchByExerciseIdAndUserId = Aggregation.match(criteria);

        SortOperation sortByVersionDesc = Aggregation.sort(Sort.by(Sort.Direction.DESC, "version"));

        GroupOperation groupByExerciseId = Aggregation.group("exerciseId").push("$$ROOT").as("submissions");

        Aggregation aggregation = Aggregation.newAggregation(
                matchByExerciseIdAndUserId,
                sortByVersionDesc,
                groupByExerciseId);

        List<Map<String, List<StudentSubmission>>> submissionsById = new ArrayList<>();
        mongoTemplate.aggregate(aggregation, "studentSubmissions", Map.class).getMappedResults().forEach(submissionsById::add);
        return submissionsById;
    }
}
