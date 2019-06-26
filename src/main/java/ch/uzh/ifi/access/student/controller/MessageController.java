package ch.uzh.ifi.access.student.controller;

import ch.uzh.ifi.access.student.model.evaluation.Message;
import ch.uzh.ifi.access.student.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students/messages")
public class MessageController {

    private final MessageService service;

    @Autowired
    public MessageController(MessageService service) {
        this.service = service;
    }

    @GetMapping
    public List<Message> getMessages() {
        return service.getAll();
    }

    @PostMapping
    public Message createMessage(@RequestBody Message message) {
        Assert.notNull(message, "Cannot save empty message object.");
        return service.save(message);
    }

}
