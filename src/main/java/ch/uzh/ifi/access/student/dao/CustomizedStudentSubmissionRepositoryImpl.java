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
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
class CustomizedStudentSubmissionRepositoryImpl implements CustomizedStudentSubmissionRepository {

    private final MongoTemplate mongoTemplate;

    public CustomizedStudentSubmissionRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Aggregation pipeline:
     * 1. Find all submissions by exerciseId and userId
     * 2. Sort them by version descending
     * 3. Group all submissions by exerciseId
     * 4. Take only first submission for every exerciseId group (the one with the highest version number == most recent)
     * <p>
     * Represent the following aggregation in mongo:
     * { "aggregate" : "__collection__", "pipeline" : [{ "$match" : { "exerciseId" : { "$in" : [<exercise ids>] }, "userId" : "<user id>" } }, { "$sort" : { "version" : -1 } }, { "$group" : { "_id" : "$exerciseId", "submissions" : { "$push" : "$$ROOT" } } }, { "$replaceRoot" : { "newRoot" : { "$arrayElemAt" : ["$submissions", 0] } } }] }
     *
     * @param exerciseIds exercises for which we want the most recent submissions
     * @param userId      student user id
     * @return list of the most recent user submissions for the given exercises
     */
    @Override
    public List<StudentSubmission> findByExerciseIdInAndUserIdAndIsGradedOrderByVersionDesc(List<String> exerciseIds, String userId) {
        Criteria criteria = Criteria.where("exerciseId").in(exerciseIds).and("userId").is(userId).and("isGraded").is(true);
        MatchOperation matchByExerciseIdAndUserId = Aggregation.match(criteria);

        SortOperation sortByVersionDesc = Aggregation.sort(new Sort(Sort.Direction.DESC, "version"));

        GroupOperation groupByExerciseId = Aggregation.group("exerciseId").push("$$ROOT").as("submissions");

        ReplaceRootOperation takeOnlyFirstElement = Aggregation.replaceRoot().withValueOf(ArrayOperators.ArrayElemAt.arrayOf("submissions").elementAt(0));

        Aggregation aggregation = Aggregation.newAggregation(
                matchByExerciseIdAndUserId,
                sortByVersionDesc,
                groupByExerciseId,
                takeOnlyFirstElement);

        AggregationResults<StudentSubmission> results = mongoTemplate.aggregate(aggregation, "studentSubmissions", StudentSubmission.class);

        return results.getMappedResults();
    }

    @Override
    public void invalidateSubmissionsByExerciseId(String exerciseId) {
        Query query = Query.query(Criteria.where("exerciseId").is(exerciseId));
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

        SortOperation sortByVersionDesc = Aggregation.sort(new Sort(Sort.Direction.DESC, "version"));

        GroupOperation groupByExerciseId = Aggregation.group("exerciseId").push("$$ROOT").as("submissions");

        Aggregation aggregation = Aggregation.newAggregation(
                matchByExerciseIdAndUserId,
                sortByVersionDesc,
                groupByExerciseId);

        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "studentSubmissions", Map.class);
        return results.getMappedResults().stream().map(map -> (Map<String, List<StudentSubmission>>) map).collect(Collectors.toList());
    }
}
