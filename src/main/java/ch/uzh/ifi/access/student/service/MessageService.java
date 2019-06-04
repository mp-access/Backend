package ch.uzh.ifi.access.student.service;

import ch.uzh.ifi.access.student.dao.MessageRepository;
import ch.uzh.ifi.access.student.model.evaluation.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepo;

    @Autowired
    public MessageService(MessageRepository messageRepo) {
        this.messageRepo = messageRepo;
    }

    public List<Message> getAll() {
        return messageRepo.findAll();
    }

    public Message save(Message message) {
        return messageRepo.save(message);
    }

}
