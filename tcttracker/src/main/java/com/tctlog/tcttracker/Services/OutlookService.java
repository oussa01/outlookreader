package com.tctlog.tcttracker.Services;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.DeviceCodeCredential;
import com.azure.identity.DeviceCodeCredentialBuilder;
import com.microsoft.graph.models.MessageCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.tctlog.tcttracker.ModelDTO.AppSession;
import com.tctlog.tcttracker.ModelDTO.LoginPath;
import com.tctlog.tcttracker.handler.LoginWebSocketHandler;
import com.tctlog.tcttracker.handler.SessionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Component

public class OutlookService {
    @Autowired
    private SessionStore activeSessions ;
    private GraphServiceClient userClient;
    private DeviceCodeCredential deviceCodeCredential;
    private LoginPath pendingLogin;
    @Autowired
    private LoginWebSocketHandler loginWebSocketHandler;

    @Value("${app.graphUserScopes}")
    private String graphUserScopes;

    @Value("${app.clientId}")
    private String clientId;

    @Value("${app.tenantId}")
    private String tenantId;

    public AppSession connectToOutlook() {
        //return greetUser();
        return initializeGraph();
    }

    private void initializeGraphForUserAuth(Consumer<LoginPath> loginPathConsumer) throws Exception {
         pendingLogin = new LoginPath();
        deviceCodeCredential = new DeviceCodeCredentialBuilder().clientId(clientId)
                .tenantId(tenantId).challengeConsumer(deviceCodeInfo -> {
            pendingLogin.setLoginUrlPath(deviceCodeInfo.getVerificationUrl());
                    pendingLogin.setGeneratedCode(deviceCodeInfo.getUserCode());
                    pendingLogin.setExpiration(Date.from(deviceCodeInfo.getExpiresOn().toInstant()));
                    loginPathConsumer.accept(pendingLogin);
        }).build();
        final String[] graphUserScope = graphUserScopes.split(",");
        userClient = new GraphServiceClient(deviceCodeCredential, graphUserScope);
    }

    private AppSession initializeGraph() {
        AppSession appSession = new AppSession();
        try {

           initializeGraphForUserAuth(loginWebSocketHandler::sendLoginPath);
           userClient.me().mailFolders()
                   .byMailFolderId("Inbox")
                   .messages()
                   .get(
                           requestConfig -> {
                               assert requestConfig.queryParameters != null;
                               requestConfig.queryParameters.top = 1;
                               requestConfig.queryParameters.orderby = new String[]{"receivedDateTime DESC"};
                           });
           System.out.println("success");
            String sessionToken = UUID.randomUUID().toString();
            appSession.setSessionToken(sessionToken);
            appSession.setType("ok");
            appSession.setAuthenticated(true);
            activeSessions.sessions.put(sessionToken,appSession);
            loginWebSocketHandler.sendSessionToken(appSession);
        } catch (Exception e) {
            System.out.println("Error initializing Graph for user auth");
            System.out.println(e.getMessage());
            appSession.setAuthenticated(false);
        }
        return appSession;

    }
    public AppSession validate(String tokenheader){
        String token = tokenheader.replace("Bearer ", "");
        AppSession session = activeSessions.sessions.get(token);
        session.setExpiration(Instant.now().plus(Duration.ofHours(2)));
        if(session.getExpiration().isBefore(Instant.now())){
            activeSessions.sessions.remove(token);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Session expired");
        }
        if (!session.isAuthenticated()){
            System.out.println("session is not authenticated");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Not Authenticated");
        }
        return  session;
    }
    public String GetUserToken() throws Exception {
        if(deviceCodeCredential==null){
        throw new Exception("Graph has not been initialized for user auth");
        }
        final String[] graphUserScope = graphUserScopes.split(",");
        final TokenRequestContext context = new TokenRequestContext();
        context.addScopes(graphUserScopes);
        final AccessToken token = deviceCodeCredential.getTokenSync(context);
        return token.getToken();
    }
//   Add it later to get logged in user information
//    private User getUser() throws Exception {
//        if (userClient == null) {
//            throw new Exception("Graph has not been initialized for user auth");
//        }
//        return userClient
//                .me()
//                .get(
//                        requestConfig -> {
//                            assert requestConfig.queryParameters != null;
//                            requestConfig.queryParameters.select =
//                                    new String[]{"displayName", "mail", "userPrincipalName"};
//                        });
//    }

//    private String greetUser() {
//        try {
//            final User user = getUser();
//            // For Work/school accounts, email is in mail property
//            // Personal accounts, email is in userPrincipalName
//            final String email = user.getMail() == null ? user.getUserPrincipalName() : user.getMail();
//            return "Hello, " + user.getDisplayName() + "!" + " Email: " + email;
//        } catch (Exception e) {
//            return "Error getting user " + e.getMessage();
//        }
//    }

    public MessageCollectionResponse getEmailFolderMessages(
            String folderName, int numberOfMails) throws Exception {
        if (userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }
        return userClient
                .me()
                .mailFolders()
                .byMailFolderId(folderName)
                .messages()
                .get(
                        requestConfig -> {
                            assert requestConfig.queryParameters != null;
                            requestConfig.queryParameters.top = numberOfMails;
                            requestConfig.queryParameters.orderby = new String[]{"receivedDateTime DESC"};
                        });
    }
}

