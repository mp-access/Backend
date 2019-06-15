package ch.uzh.ifi.access.config;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApiTokenRepository extends MongoRepository<ApiToken, String> {
}
