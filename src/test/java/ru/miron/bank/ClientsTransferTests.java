package ru.miron.bank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.miron.bank.api.dto.AccessAndRefreshTokenDto;
import ru.miron.bank.api.dto.LoginPasswordDto;
import ru.miron.bank.api.dto.TransferRequestDto;
import ru.miron.bank.entity.client.Client;
import ru.miron.bank.entity.client.ClientRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
public class ClientsTransferTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper;

    private final static String SENDER_LOGIN = "senderLogin";
    private final static String SENDER_PASSWORD = "senderPassword";

    private final static String RECEIVER_LOGIN = "receiverLogin";
    private final static String RECEIVER_PASSWORD = "receiverPassword";

    private Client senderInit;

    private Client receiverInit;
    private String receiverPassword;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        senderInit = new Client(
                SENDER_LOGIN,
                "hhhh@hh.ru",
                "19993332211",
                Timestamp.from(ZonedDateTime.now().minusYears(20).toInstant()),
                "name",
                "sname",
                "tname",
                passwordEncoder.encode(SENDER_PASSWORD),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100)
        );

        receiverInit = new Client(
                RECEIVER_LOGIN,
                "yyyyy@hh.ru",
                "29993332211",
                Timestamp.from(ZonedDateTime.now().minusYears(20).toInstant()),
                "name",
                "sname",
                "tname",
                passwordEncoder.encode(RECEIVER_PASSWORD),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100)
        );
        clientRepository.deleteAllById(List.of(SENDER_LOGIN, RECEIVER_LOGIN));
        clientRepository.save(senderInit);
        clientRepository.save(receiverInit);
    }

    public ResultActions login(String login, String password) throws Exception {
        return this.mockMvc.perform(post("/api/v1/auth/client/login")
                .content(objectMapper.writeValueAsString(new LoginPasswordDto(login, password)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testLogin() throws Exception {
        var accessToken = getAccessToken(SENDER_LOGIN, SENDER_PASSWORD);
        this.mockMvc.perform(bearer(get("/api/v1/search/clients"), accessToken))
                .andExpect(status().isOk());
    }
    @Test
    public void testCommonTransferSuccess() throws Exception {
        var senderAccessToken = getAccessToken(SENDER_LOGIN, SENDER_PASSWORD);
        var toSendSize = BigDecimal.valueOf(1);
        var transferRequestDto = new TransferRequestDto(RECEIVER_LOGIN, toSendSize);
        mockMvc.perform(bearer(post("/api/v1/clients/self/transactions/transfer"), senderAccessToken)
                        .content(objectMapper.writeValueAsString(transferRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("TRANSFERRED"));
        var actualSender = getActualClient(SENDER_LOGIN);
        var actualReceiver = getActualClient(RECEIVER_LOGIN);
        assertAll(
                () -> assertEquals(senderInit.getAccountSize().subtract(toSendSize)
                        .compareTo(actualSender.getAccountSize()), 0),
                () -> assertEquals(receiverInit.getAccountSize().add(toSendSize)
                        .compareTo(actualReceiver.getAccountSize()), 0)
        );
    }

    @Test
    public void testTransferToUnknownLogin() throws Exception {
        var senderAccessToken = getAccessToken(SENDER_LOGIN, SENDER_PASSWORD);
        var toSendSize = BigDecimal.valueOf(1);
        var transferRequestDto = new TransferRequestDto(UUID.randomUUID().toString(), toSendSize);
        mockMvc.perform(bearer(post("/api/v1/clients/self/transactions/transfer"), senderAccessToken)
                        .content(objectMapper.writeValueAsString(transferRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    public void testTransferWhenNotEnoughMoney() throws Exception {
        var senderAccessToken = getAccessToken(SENDER_LOGIN, SENDER_PASSWORD);
        var toSendSize = BigDecimal.valueOf(10000000);
        var transferRequestDto = new TransferRequestDto(RECEIVER_LOGIN, toSendSize);
        mockMvc.perform(bearer(post("/api/v1/clients/self/transactions/transfer"), senderAccessToken)
                        .content(objectMapper.writeValueAsString(transferRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("NOT_ENOUGH_MONEY"));
    }

    @Test
    void testTransferConcurrentRequests() throws Exception {
        var senderAccessToken = getAccessToken(SENDER_LOGIN, SENDER_PASSWORD);
        int requestsCount = 2000;
        var toSendByRequestSize = BigDecimal.valueOf(1);
        var transferRequestDto = new TransferRequestDto(RECEIVER_LOGIN, toSendByRequestSize);
        CompletableFuture<?>[] futures = IntStream.range(0, requestsCount)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    try {
                        mockMvc.perform(bearer(post("/api/v1/clients/self/transactions/transfer"), senderAccessToken)
                                        .content(objectMapper.writeValueAsString(transferRequestDto))
                                        .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.state").value("TRANSFERRED"));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        var sent = toSendByRequestSize.multiply(BigDecimal.valueOf(requestsCount));
        var actualSender = getActualClient(SENDER_LOGIN);
        var actualReceiver = getActualClient(RECEIVER_LOGIN);
        assertAll(
                () -> assertEquals(senderInit.getAccountSize().subtract(sent)
                        .compareTo(actualSender.getAccountSize()), 0),
                () -> assertEquals(receiverInit.getAccountSize().add(sent)
                        .compareTo(actualReceiver.getAccountSize()), 0)
        );
    }

    public Client getActualClient(String login) {
        return clientRepository.findById(login).get();
    }

    public AccessAndRefreshTokenDto jsonToTokens(String json) throws Exception {
        return objectMapper.readValue(json, AccessAndRefreshTokenDto.class);
    }

    public String getAccessToken(String login, String password) throws Exception {
        var tokensAsString = login(SENDER_LOGIN, SENDER_PASSWORD)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var tokens = jsonToTokens(tokensAsString);
        return tokens.getAccessToken();
    }

    public static MockHttpServletRequestBuilder bearer(MockHttpServletRequestBuilder builder, String bearerToken) {
        return builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
    }

}
