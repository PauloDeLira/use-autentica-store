package br.com.useautentica.backend.controller.admin;

import br.com.useautentica.backend.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductImageAdminControllerTest extends AbstractIntegrationTest {

    // Assinatura real de JPEG (SOI + APP0), necessária porque a validação de
    // upload agora inspeciona os bytes do arquivo, não o Content-Type declarado.
    private static final byte[] JPEG_BYTES = {
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0, 0, 0
    };

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

    private String createProduct(String token) throws Exception {
        MvcResult categoryResult = mockMvc.perform(post("/api/admin/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("name", "Categoria " + UUID.randomUUID()))))
                .andReturn();
        String categoryId = objectMapper.readTree(categoryResult.getResponse().getContentAsString()).get("id").asText();

        MvcResult productResult = mockMvc.perform(post("/api/admin/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Camiseta Oversized", "price", 89.90, "categoryId", categoryId))))
                .andReturn();
        return objectMapper.readTree(productResult.getResponse().getContentAsString()).get("id").asText();
    }

    @Test
    void uploadsImageForProduct() throws Exception {
        String token = adminToken();
        String productId = createProduct(token);
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", JPEG_BYTES);

        mockMvc.perform(multipart("/api/admin/products/{productId}/images", productId)
                        .file(file)
                        .param("altText", "Foto da camiseta")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.altText").value("Foto da camiseta"))
                .andExpect(jsonPath("$.displayOrder").value(0));
    }

    @Test
    void rejectsUnsupportedFormat() throws Exception {
        String token = adminToken();
        String productId = createProduct(token);
        MockMultipartFile file = new MockMultipartFile("file", "arquivo.pdf", "application/pdf", "%PDF-1.4".getBytes());

        mockMvc.perform(multipart("/api/admin/products/{productId}/images", productId)
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("IMAGE_FORMAT_NOT_SUPPORTED"));
    }

    @Test
    void rejectsFileWithSpoofedImageContentType() throws Exception {
        String token = adminToken();
        String productId = createProduct(token);
        // Content-Type e nome declaram uma imagem, mas os bytes reais são de outro formato.
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", "%PDF-1.4".getBytes());

        mockMvc.perform(multipart("/api/admin/products/{productId}/images", productId)
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("IMAGE_FORMAT_NOT_SUPPORTED"));
    }

    @Test
    void deletesImage() throws Exception {
        String token = adminToken();
        String productId = createProduct(token);
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", JPEG_BYTES);

        MvcResult uploadResult = mockMvc.perform(multipart("/api/admin/products/{productId}/images", productId)
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andReturn();
        String imageId = objectMapper.readTree(uploadResult.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(delete("/api/admin/products/{productId}/images/{imageId}", productId, imageId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void includesUploadedImageInPublicProductDetail() throws Exception {
        String token = adminToken();
        String productId = createProduct(token);
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", JPEG_BYTES);

        mockMvc.perform(multipart("/api/admin/products/{productId}/images", productId)
                        .file(file)
                        .param("altText", "Foto da camiseta")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.images.length()").value(1))
                .andExpect(jsonPath("$.images[0].altText").value("Foto da camiseta"))
                .andExpect(jsonPath("$.images[0].url").value(org.hamcrest.Matchers.startsWith("/uploads/")));
    }

    @Test
    void rejectsUploadWithoutToken() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "foto.jpg", "image/jpeg", JPEG_BYTES);
        mockMvc.perform(multipart("/api/admin/products/{productId}/images", UUID.randomUUID())
                        .file(file))
                .andExpect(status().isUnauthorized());
    }
}
