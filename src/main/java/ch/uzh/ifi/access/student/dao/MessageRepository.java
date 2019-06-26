package ch.uzh.ifi.access.student.dao;

import ch.uzh.ifi.access.student.model.evaluation.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {

}
