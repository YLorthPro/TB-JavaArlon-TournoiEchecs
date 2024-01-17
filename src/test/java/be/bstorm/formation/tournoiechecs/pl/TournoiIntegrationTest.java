package be.bstorm.formation.tournoiechecs.pl;

import be.bstorm.formation.tournoiechecs.dal.model.Resultat;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TournoiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void creationTournoi_OK() throws Exception {
        String requestBody = """
            {
              "nom": "Tournoi de Printemps",
              "lieu": "Arlon",
              "nombreMinJoueurs": 2,
              "nombreMaxJoueurs": 32,
              "eLOMin": 1200,
              "eLOMax": 1800,
              "categories": ["SENIOR", "VETERAN"],
              "statut": "EN_COURS",
              "womenOnly": true,
              "dateFinInscriptions": "2100-03-01"
            }
            """;

        mockMvc.perform(post("/api/tournoi/creation")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void creationTournoi_MaxJoueursMoinsQueMinJoueurs() throws Exception {
        String requestBody = """
            {
              "nom": "Tournoi de Printemps",
              "lieu": "Arlon",
              "nombreMinJoueurs": 10,
              "nombreMaxJoueurs": 5,
              "eLOMin": 1200,
              "eLOMax": 1800,
              "categories": ["SENIOR", "VETERAN"],
              "statut": "EN_COURS",
              "womenOnly": true,
              "dateFinInscriptions": "2100-03-01"
            }
            """;

        mockMvc.perform(post("/api/tournoi/creation")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void creationTournoi_EloMinPlusGrandEloMax() throws Exception {
        String requestBody = """
            {
              "nom": "Tournoi d'Echec",
              "lieu": "Arlon",
              "nombreMinJoueurs": 10,
              "nombreMaxJoueurs": 20,
              "eLOMin": 1900,
              "eLOMax": 1500,
              "categories": ["SENIOR", "VETERAN"],
              "statut": "EN_COURS",
              "womenOnly": true,
              "dateFinInscriptions": "2100-03-01"
            }
            """;
        mockMvc.perform(post("/api/tournoi/creation")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
    @Test
    @WithMockUser(roles = "ADMIN")
    public void creationTournoi_ChampsManquant() throws Exception {
        String requestBody = """
            {
              "lieu": "Arlon",
              "nombreMinJoueurs": 10,
              "nombreMaxJoueurs": 15,
              "eLOMin": 1200,
              "eLOMax": 1800,
              "categories": ["SENIOR", "VETERAN"],
              "statut": "EN_COURS",
              "womenOnly": true,
              "dateFinInscriptions": "2100-03-01"
            }
            """;

        mockMvc.perform(post("/api/tournoi/creation")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void creationTournoi_CategoriesEstVide() throws Exception {
        String requestBody = """
            {
              "nom": "Tournoi d'Echec",
              "lieu": "Arlon",
              "nombreMinJoueurs": 10,
              "nombreMaxJoueurs": 20,
              "eLOMin": 1200,
              "eLOMax": 1500,
              "categories": [],
              "statut": "EN_COURS",
              "womenOnly": true,
              "dateFinInscriptions": "2100-03-01"
            }
            """;
        mockMvc.perform(post("/api/tournoi/creation")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void creationTournoi_DateFinInscriptionTropProche() throws Exception {
        String requestBody = """
            {
              "nom": "Tournoi de Printemps",
              "lieu": "Arlon",
              "nombreMinJoueurs": 2,
              "nombreMaxJoueurs": 32,
              "eLOMin": 1200,
              "eLOMax": 1800,
              "categories": ["SENIOR", "VETERAN"],
              "statut": "EN_COURS",
              "womenOnly": true,
              "dateFinInscriptions": "2014-03-01"
            }
            """;

        mockMvc.perform(post("/api/tournoi/creation")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void creationTournoi_FormNull() throws Exception {
        String requestBody = """
            """;

        mockMvc.perform(post("/api/tournoi/creation")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "JOUEUR")
    public void creationTournoi_MauvaisRole() throws Exception {
        String requestBody = """
            {
              "nom": "Tournoi de Printemps",
              "lieu": "Arlon",
              "nombreMinJoueurs": 2,
              "nombreMaxJoueurs": 32,
              "eLOMin": 1200,
              "eLOMax": 1800,
              "categories": ["SENIOR", "VETERAN"],
              "statut": "EN_COURS",
              "womenOnly": true,
              "dateFinInscriptions": "2100-03-01"
            }
            """;

        mockMvc.perform(post("/api/tournoi/creation")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void suppressionTournoi_OK() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/api/tournoi/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void suppressionTournoi_IdNotFound() throws Exception {
        Long id = -99L;

        mockMvc.perform(delete("/api/tournoi/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void suppressionTournoi_EnCours() throws Exception {
        Long id = 2L;

        mockMvc.perform(delete("/api/tournoi/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "JOUEUR")
    public void suppressionTournoi_MauvaisRole() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/api/tournoi/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    public void top10() throws Exception {
        mockMvc.perform(get("/api/tournoi/top10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(greaterThan(0))))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void recherche_Ok() throws Exception {
        String requestBody = """
            {
              "nom": "Test tournament",
              "statut": "EN_ATTENTE_DE_JOUEURS",
              "categories": [
                "JUNIOR"
              ]
            }
            """;

        mockMvc.perform(post("/api/tournoi/recherche?page=0&size=10")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThan(0))))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void recherche_Success_EmptyList() throws Exception {
        String requestBody = """
            {
              "nom": "Wimbledododone",
              "statut": "EN_ATTENTE_DE_JOUEURS",
              "categories": [
                "JUNIOR"
              ]
            }
            """;

        mockMvc.perform(post("/api/tournoi/recherche?page=0&size=10")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))  // vérifier si le contenu est vide
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "JOUEUR")
    public void recherche_PasBonRole() throws Exception {
        String requestBody = """
            {
              "nom": "Test tournament",
              "statut": "EN_ATTENTE_DE_JOUEURS",
              "categories": [
                "JUNIOR"
              ]
            }
            """;

        mockMvc.perform(post("/api/tournoi/recherche?page=0&size=10")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getTournoiById_OK() throws Exception {
        int id = 3;

        mockMvc.perform(get("/api/tournoi/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getTournoiById_NotFound() throws Exception {
        int id = -99;

        mockMvc.perform(get("/api/tournoi/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void getTournoiById_PasBonRole() throws Exception {
        int id = 3;

        mockMvc.perform(get("/api/tournoi/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @WithMockUser(roles = "JOUEUR", username = "Mr Checkmate")
    void testInscriptionTournoi_OK() throws Exception {
        Long tournoiId = 3L;

        mockMvc.perform(put("/api/tournoi/inscription/" + tournoiId))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "JOUEUR", username = "Mr Checkmate")
    void testInscriptionTournoi_TournoiNotFound() throws Exception {
        Long tournoiId = -99L;

        mockMvc.perform(put("/api/tournoi/inscription/" + tournoiId))
                .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(roles = "JOUEUR", username = "Test")
    void testInscriptionTournoi_JoueurNotFound() throws Exception {
        Long tournoiId = 3L;

        mockMvc.perform(put("/api/tournoi/inscription/" + tournoiId))
                .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(roles = "JOUEUR", username = "Mr Checkmate")
    void testInscriptionTournoi_ConditionInscriptionPasBonne() throws Exception {
        Long tournoiId = 2L;

        mockMvc.perform(put("/api/tournoi/inscription/" + tournoiId))
                .andExpect(status().isBadRequest());

    }

    @Test
    void testInscriptionTournoi_PasConnecte() throws Exception {
        Long tournoiId = 3L;

        mockMvc.perform(put("/api/tournoi/inscription/" + tournoiId))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "Mr Checkmate")
    void testDesinscriptionTournoi_OK() throws Exception {
        Long tournoiId = 3L;

        mockMvc.perform(patch("/api/tournoi/desinscription/" + tournoiId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "Mr Checkmate")
    void testDesinscriptionTournoi_TournoiNotFound() throws Exception {
        Long tournoiId = -99L;

        mockMvc.perform(patch("/api/tournoi/desinscription/" + tournoiId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "JOUEUR", username = "Test")
    void testDesinscriptionTournoi_JoueurNotFound() throws Exception {
        Long tournoiId = 3L;

        mockMvc.perform(patch("/api/tournoi/desinscription/" + tournoiId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "Mr Checkmate")
    void testDesinscriptionTournoi_JoueurPasInscrit() throws Exception {
        Long tournoiId = 1L;

        mockMvc.perform(patch("/api/tournoi/desinscription/" + tournoiId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDesinscriptionTournoi_PasConnecte() throws Exception {
        Long tournoiId = 3L;

        mockMvc.perform(put("/api/tournoi/inscription/" + tournoiId))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDemarrerTournoi_OK() throws Exception {
        Long tournoiId = 4L;

        mockMvc.perform(patch("/api/tournoi/demarrer/" + tournoiId))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDemarrerTournoi_TournoiNotFOund() throws Exception {
        Long tournoiId = -99L;

        mockMvc.perform(patch("/api/tournoi/demarrer/" + tournoiId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDemarrerTournoi_TournoiPasPret() throws Exception {
        Long tournoiId = 3L;

        mockMvc.perform(patch("/api/tournoi/demarrer/" + tournoiId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "JOUEUR")
    void testDemarrerTournoi_PasBonRole() throws Exception {
        Long tournoiId = 4L;

        mockMvc.perform(patch("/api/tournoi/demarrer/" + tournoiId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testModifierResultatRencontre_OK() throws Exception {
        Long rencontreId = 9L;

        Resultat resultat = Resultat.BLANC;

                String resultatJson = new ObjectMapper().writeValueAsString(resultat);

        mockMvc.perform(post("/api/tournoi/rencontre/" + rencontreId + "/resultat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resultatJson))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testModifierResultatRencontre_RencontreNotFound() throws Exception {
        Long rencontreId = -99L;

        Resultat resultat = Resultat.BLANC;

        String resultatJson = new ObjectMapper().writeValueAsString(resultat);

        mockMvc.perform(post("/api/tournoi/rencontre/" + rencontreId + "/resultat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resultatJson))
                .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testModifierResultatRencontre_PasBonneRonde() throws Exception {
        Long rencontreId = 1L;

        Resultat resultat = Resultat.BLANC;

        String resultatJson = new ObjectMapper().writeValueAsString(resultat);

        mockMvc.perform(post("/api/tournoi/rencontre/" + rencontreId + "/resultat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resultatJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(roles = "JOUEUR")
    void testModifierResultatRencontre_PasBonRole() throws Exception {
        Long rencontreId = 3L;

        Resultat resultat = Resultat.BLANC;

        String resultatJson = new ObjectMapper().writeValueAsString(resultat);

        mockMvc.perform(post("/api/tournoi/rencontre/" + rencontreId + "/resultat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resultatJson))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void passerTourSuivantTest_OK() throws Exception {
        Long tournoiId = 2L;
        mockMvc.perform(patch("/api/tournoi/tourSuivant/" + tournoiId))
                .andExpect(status().isOk());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void passerTourSuivantTest_RondePasFinie() throws Exception {
        Long tournoiId = 2L;
        mockMvc.perform(patch("/api/tournoi/tourSuivant//" + tournoiId))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void passerTourSuivantTest_TournoiNotFound() throws Exception {
        Long tournoiId = -99L;
        mockMvc.perform(patch("/api/tournoi/tourSuivant//" + tournoiId))
                .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(roles = "JOUEUR")
    public void passerTourSuivantTest_PasBonRole() throws Exception {
        Long tournoiId = 2L;
        mockMvc.perform(patch("/api/tournoi/tourSuivant/" + tournoiId))
                .andExpect(status().isForbidden());

    }

    @Test
    public void afficherTableauScoresTest_OK() throws Exception {
        Long tournoiId = 2L;
        int ronde = 1;

        mockMvc.perform(get("/api/tournoi/" + tournoiId + "/tableauScores/" + ronde)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }
}
