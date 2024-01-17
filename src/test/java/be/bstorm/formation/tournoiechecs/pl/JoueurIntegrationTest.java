package be.bstorm.formation.tournoiechecs.pl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class JoueurIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testInscription_OK() throws Exception {
        String requestBody = """
                {
                  "pseudo": "Moi",
                  "email": "moi@moi.be",
                  "dateDeNaissance": "1956-12-16",
                  "genre": "GARCON",
                  "eLO": 1500,
                  "role": "JOUEUR"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/joueur/inscription")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    public void testInscription_BadCredentials() throws Exception {
        String requestBody = """
                {
                  "pseudo": "Moi",
                  "email": "moi@moi.be",
                  "dateDeNaissance": "1956-12-16",
                  "genre": "GARCON",
                  "eLO": 1500,
                  "role": "JOUEUR"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/joueur/inscription")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testInscription_PseudoExists() throws Exception {
        String requestBody = """
                {
                  "pseudo": "Mr Checkmate",
                  "email": "moi@moi.be",
                  "dateDeNaissance": "1956-12-16",
                  "genre": "GARCON",
                  "eLO": 1500,
                  "role": "JOUEUR"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/joueur/inscription")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testInscription_EmailExists() throws Exception {
        String requestBody = """
                {
                  "pseudo": "moi",
                  "email": "checkmate@chess.be",
                  "dateDeNaissance": "1956-12-16",
                  "genre": "GARCON",
                  "eLO": 1500,
                  "role": "JOUEUR"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/joueur/inscription")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testInscription_FormNull() throws Exception {
        String requestBody = """
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/joueur/inscription")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void testLogin_OK() throws Exception {
        String requestBody = """
                { "identifiant":"Mr Checkmate",
                    "motDePasse":"Test1234="}
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/joueur/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testLogin_IdentifiantNotExists() throws Exception {
        String requestBody = """
                { "identifiant":"Javanais",
                    "motDePasse":"Test1234="}
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/joueur/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void testLogin_MauvaisMotDePasse() throws Exception {
        String requestBody = """
                { "identifiant":"Mr Checkmate",
                    "motDePasse":"JeNeConnaisPlusMonMotDePasse"}
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/joueur/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void testLogin_FormNull() throws Exception {
        String requestBody = """
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/joueur/login")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}
