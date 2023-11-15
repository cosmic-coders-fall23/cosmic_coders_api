package org.cosmiccoders.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cosmiccoders.api.dto.HighScoreDto;
import org.cosmiccoders.api.dto.RegistrationDto;
import org.cosmiccoders.api.model.HighScore;
import org.cosmiccoders.api.model.UserEntity;
import org.cosmiccoders.api.services.GameService;
import org.cosmiccoders.api.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CosmicCodersApiApplicationTests {
    @MockBean
    private UserService userService;

    @MockBean
    private GameService gameService;

    @MockBean
    private Principal principal;

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser(username = "user123")
    void checkUser() throws Exception {
        String url = "/user/me";
        UserEntity testUser = new UserEntity(
                0L,
                "user123",
                "user@gmail.com",
                "pass",
                LocalDateTime.now().minusDays(2L),
                LocalDateTime.now(),
                true,
                new ArrayList<>()
        );
        when(userService.findByUsername("user123")).thenReturn(testUser);

        this.mvc.perform(get(url))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user123")
    void addHighScoreValidUser() throws Exception {
        String url = "/user/addHighScore";
        UserEntity testUser = new UserEntity(
                0L,
                "user123",
                "user@gmail.com",
                "pass",
                LocalDateTime.now().minusDays(2L),
                LocalDateTime.now(),
                true,
                new ArrayList<>()
        );
        HighScoreDto highScoreDto = new HighScoreDto(2100);

        when(principal.getName()).thenReturn("user123");
        when(userService.findByUsername("user123")).thenReturn(testUser);
        HighScore highScore = new HighScore(0L, testUser, 1800);
        when(gameService.getHighScore(testUser)).thenReturn(highScore);

        MvcResult result = this.mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(highScoreDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String message = result.getResponse().getContentAsString();
        assertThat(message).contains("Successfully added high score");
    }

    @Test
    @WithMockUser(username = "invalidUser")
    void addHighScoreInvalidUser() throws Exception {
        String url = "/user/addHighScore";
        HighScoreDto highScoreDto = new HighScoreDto(2100);

        when(userService.findByUsername("invalidUser")).thenReturn(null);

        this.mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(highScoreDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user123")
    void addHighScoreInvalidScore() throws Exception {
        String url = "/user/addHighScore";
        UserEntity testUser = new UserEntity(
                0L,
                "user123",
                "user@gmail.com",
                "pass",
                LocalDateTime.now().minusDays(2L),
                LocalDateTime.now(),
                true,
                new ArrayList<>()
        );
        HighScoreDto highScoreDto = new HighScoreDto(1800);

        when(principal.getName()).thenReturn("user123");
        when(userService.findByUsername("user123")).thenReturn(testUser);
        HighScore highScore = new HighScore(0L, testUser, 2100);
        when(gameService.getHighScore(testUser)).thenReturn(highScore);

        MvcResult result = this.mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(highScoreDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String message = result.getResponse().getContentAsString();
        assertThat(message).contains("Invalid high score");
    }

    @Test
    void saveValidUser() throws Exception {
        String url = "/auth/register";
        RegistrationDto registrationDto = new RegistrationDto(
                "user123",
                "user@gmail.com",
                "password123"
        );
        when(userService.findByUsername("user123")).thenReturn(null);
        when(userService.findByEmail("user@gmail.com")).thenReturn(null);

        MvcResult result = this.mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registrationDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String message = result.getResponse().getContentAsString();
        assertThat(message).contains("User registered successfully");
    }

    @Test
    void saveUserInvalidUsername() throws Exception {
        String url = "/auth/register";
        RegistrationDto registrationDto = new RegistrationDto(
                "user123",
                "user@gmail.com",
                "password123"
        );
        UserEntity existingUser = new UserEntity(
                0L,
                "user123",
                "existingUser@gmail.com",
                "pass",
                LocalDateTime.now().minusDays(2L),
                LocalDateTime.now(),
                true,
                new ArrayList<>()
        );

        when(userService.findByUsername("user123")).thenReturn(existingUser);
        when(userService.findByEmail("user@gmail.com")).thenReturn(null);

        MvcResult result = this.mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registrationDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String message = result.getResponse().getContentAsString();
        assertThat(message).contains("Username already exists");
    }

    @Test
    void saveUserInvalidEmail() throws Exception {
        String url = "/auth/register";
        RegistrationDto registrationDto = new RegistrationDto(
                "user123",
                "user@gmail.com",
                "password123"
        );
        UserEntity existingUser = new UserEntity(
                0L,
                "existingUser234",
                "user@gmail.com",
                "pass",
                LocalDateTime.now().minusDays(2L),
                LocalDateTime.now(),
                true,
                new ArrayList<>()
        );

        when(userService.findByUsername("user123")).thenReturn(null);
        when(userService.findByEmail("user@gmail.com")).thenReturn(existingUser);

        MvcResult result = this.mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(registrationDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String message = result.getResponse().getContentAsString();
        assertThat(message).contains("Email already exists");
    }

    public static String json(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
