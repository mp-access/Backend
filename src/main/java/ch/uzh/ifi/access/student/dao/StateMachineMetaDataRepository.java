package ch.uzh.ifi.access.student.dao;

import ch.uzh.ifi.access.student.model.evaluation.StateMachineMetaData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateMachineMetaDataRepository extends MongoRepository<StateMachineMetaData, String> {
}
