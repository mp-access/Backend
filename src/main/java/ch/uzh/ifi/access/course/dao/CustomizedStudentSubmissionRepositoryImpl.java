package ch.uzh.ifi.access.course.dao;

import ch.uzh.ifi.access.course.model.workspace.StudentSubmission;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public List<StudentSubmission> findByExerciseIdInAndUserIdOrderByVersionDesc(List<String> exerciseIds, String userId) {
        Criteria criteria = Criteria.where("exerciseId").in(exerciseIds).and("userId").is(userId);
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
}
