package br.com.useautentica.backend.controller.admin;

import br.com.useautentica.backend.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ColorAdminControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${admin.seed.email}")
    private String adminEmail;

    @Value("${admin.seed.password}")
    private String adminPassword;

    private String adminToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", adminEmail,
                                "password", adminPassword
                        ))))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    @Test
    void createsColorWhenAuthenticatedAsAdmin() throws Exception {
        mockMvc.perform(post("/api/admin/colors")
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Cor " + UUID.randomUUID(),
                                "hexCode", "#FF0000"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void updatesColor() throws Exception {
        String token = adminToken();
        MvcResult createResult = mockMvc.perform(post("/api/admin/colors")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Cor " + UUID.randomUUID(),
                                "hexCode", "#FF0000"
                        ))))
                .andReturn();
        String id = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();
        String newName = "Cor Editada " + UUID.randomUUID();

        mockMvc.perform(put("/api/admin/colors/{id}", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", newName,
                                "hexCode", "#00FF00"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.hexCode").value("#00FF00"));
    }

    @Test
    void rejectsInvalidHexCode() throws Exception {
        mockMvc.perform(post("/api/admin/colors")
                        .header("Authorization", "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Cor " + UUID.randomUUID(),
                                "hexCode", "vermelho"
                        ))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsCreateWithoutToken() throws Exception {
        mockMvc.perform(post("/api/admin/colors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "Sem token", "hexCode", "#000000"))))
                .andExpect(status().isUnauthorized());
    }
}
