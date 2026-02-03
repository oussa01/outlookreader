package com.tctlog.tcttracker.Controllers;
import com.microsoft.graph.models.MessageCollectionResponse;
import com.tctlog.tcttracker.ModelDTO.AppSession;
import com.tctlog.tcttracker.ModelDTO.LoginPath;
import com.tctlog.tcttracker.ModelDTO.MessageBody;
import com.tctlog.tcttracker.Services.OutlookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Objects;

@RestController
@CrossOrigin("*")
public class OutlookController {
    @Autowired
    private OutlookService outlookService;

    @GetMapping("/connect")
    public AppSession connectToOutlook() throws Exception {
        return outlookService.connectToOutlook();
    }
    @GetMapping("/hello")
    public String hello(){
        return "Hello from backend";
    }
    @GetMapping("/token")
    public void DisplayToken(){
        try {
            final String accessToken = outlookService.GetUserToken();
            System.out.println("Access token: " + accessToken);
        }catch (Exception e){
            System.out.println("Error getting access token");
            System.out.println(e.getMessage());
        }
    }
    @GetMapping("/session/validate")
    public AppSession validate(@RequestHeader("Authorization") String token) {
        return outlookService.validate(token);
    }

    @GetMapping("/folders")
    public List<MessageBody> getEmail(
            @RequestParam String folderName, @RequestParam int numberOfMails) throws Exception {
        final MessageCollectionResponse messages = outlookService.getEmailFolderMessages(folderName,numberOfMails);
        return Objects.requireNonNull(messages.getValue()).stream().map(message -> {
            MessageBody body = new MessageBody();
            body.setSubject(message.getSubject());
            body.setFrom(Objects.requireNonNull(Objects.requireNonNull(message.getFrom()).getEmailAddress()).getAddress());
            body.setBody(Objects.requireNonNull(message.getBody()).getContent());
            body.setIsRead(message.getIsRead());
            body.setSender(Objects.requireNonNull(message.getSender()).toString());
            return body;
        }).toList();

    }
}
