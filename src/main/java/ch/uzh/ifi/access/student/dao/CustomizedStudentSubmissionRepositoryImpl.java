package ch.uzh.ifi.access.student.dao;

import ch.uzh.ifi.access.student.model.StudentSubmission;
import com.mongodb.client.result.UpdateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Repository
class CustomizedStudentSubmissionRepositoryImpl implements CustomizedStudentSubmissionRepository {

    private static final Logger logger = LoggerFactory.getLogger(CustomizedStudentSubmissionRepositoryImpl.class);

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
        logger.debug(String.format("Invalidated %d submissions", result.getModifiedCount()));
    }

    @Override
    public boolean existsByUserIdAndHasNoResultOrConsoleNotOlderThan10min(String userId) {
        Query query = Query.query(Criteria
                .where("userId").is(userId)
                .and("console").exists(false)
                .and("result").exists(false)
                .and("timestamp").gt(Instant.now().minus(10, ChronoUnit.MINUTES)));
        return mongoTemplate.exists(query, StudentSubmission.class);
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
     * @param exerciseId exercise id
     * @return list of the most recent user submissions for the given exercises
     */
    @Override
    public List<StudentSubmission> findLastGradedSubmissionForEachUserByExerciseId(String exerciseId) {
        Criteria criteria = Criteria.where("exerciseId").is(exerciseId).and("isGraded").is(true);
        MatchOperation matchByExerciseId = Aggregation.match(criteria);

        SortOperation sortByVersionDesc = Aggregation.sort(new Sort(Sort.Direction.DESC, "version"));

        GroupOperation groupByExerciseId = Aggregation.group("userId").push("$$ROOT").as("submissions");

        ReplaceRootOperation takeOnlyFirstElement = Aggregation.replaceRoot().withValueOf(ArrayOperators.ArrayElemAt.arrayOf("submissions").elementAt(0));

        MatchOperation matchOnlyInvalidated = Aggregation.match(Criteria.where("isInvalid").is(true));

        Aggregation aggregation = Aggregation.newAggregation(
                matchByExerciseId,
                sortByVersionDesc,
                groupByExerciseId,
                takeOnlyFirstElement,
                matchOnlyInvalidated
        );

        AggregationResults<StudentSubmission> results = mongoTemplate.aggregate(aggregation, "studentSubmissions", StudentSubmission.class);

        return results.getMappedResults();
    }
}
