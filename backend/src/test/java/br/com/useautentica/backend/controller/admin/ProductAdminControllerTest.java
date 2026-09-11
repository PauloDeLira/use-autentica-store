package br.com.useautentica.backend.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductAdminControllerTest {

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

    private String createCategory(String token) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/admin/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "Categoria " + UUID.randomUUID()))))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    void createsProductWithActiveCategory() throws Exception {
        String token = adminToken();
        String categoryId = createCategory(token);

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Camiseta Oversized",
                                "price", 89.90,
                                "categoryId", categoryId
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category.id").value(categoryId));
    }

    @Test
    void rejectsProductWithInactiveCategory() throws Exception {
        String token = adminToken();
        String categoryId = createCategory(token);

        mockMvc.perform(patch("/api/admin/categories/{id}/active", categoryId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("active", false))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Camiseta Oversized",
                                "price", 89.90,
                                "categoryId", categoryId
                        ))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("CATEGORY_INACTIVE"));
    }

    @Test
    void deletesProduct() throws Exception {
        String token = adminToken();
        String categoryId = createCategory(token);

        MvcResult createResult = mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Camiseta Oversized",
                                "price", 89.90,
                                "categoryId", categoryId
                        ))))
                .andReturn();
        String productId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(delete("/api/admin/products/{id}", productId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void rejectsDeleteWhenProductHasVariants() throws Exception {
        String token = adminToken();
        String categoryId = createCategory(token);

        MvcResult productResult = mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Camiseta Oversized",
                                "price", 89.90,
                                "categoryId", categoryId
                        ))))
                .andReturn();
        String productId = objectMapper.readTree(productResult.getResponse().getContentAsString()).get("id").asText();

        MvcResult sizeResult = mockMvc.perform(post("/api/admin/sizes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "T" + UUID.randomUUID().toString().substring(0, 8), "displayOrder", 1))))
                .andReturn();
        String sizeId = objectMapper.readTree(sizeResult.getResponse().getContentAsString()).get("id").asText();

        MvcResult colorResult = mockMvc.perform(post("/api/admin/colors")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "C" + UUID.randomUUID().toString().substring(0, 8), "hexCode", "#FF0000"))))
                .andReturn();
        String colorId = objectMapper.readTree(colorResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(post("/api/admin/products/{id}/variants", productId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "sizeId", sizeId, "colorId", colorId, "stockQuantity", 5))))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/admin/products/{id}", productId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("PRODUCT_HAS_VARIANTS"));
    }

    @Test
    void rejectsCreateWithoutToken() throws Exception {
        mockMvc.perform(post("/api/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Sem token",
                                "price", 10.0,
                                "categoryId", UUID.randomUUID().toString()
                        ))))
                .andExpect(status().isUnauthorized());
    }
}
