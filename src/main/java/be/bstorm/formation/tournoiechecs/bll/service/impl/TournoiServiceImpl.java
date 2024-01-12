package be.bstorm.formation.tournoiechecs.bll.service.impl;

import be.bstorm.formation.tournoiechecs.bll.service.TournoiService;
import be.bstorm.formation.tournoiechecs.dal.model.TournoiEntity;
import be.bstorm.formation.tournoiechecs.dal.repository.TournoiRepository;
import be.bstorm.formation.tournoiechecs.pl.model.form.TournoiForm;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TournoiServiceImpl implements TournoiService {

    private final TournoiRepository tournoiRepository;

    public TournoiServiceImpl(TournoiRepository tournoiRepository) {
        this.tournoiRepository = tournoiRepository;
    }


    @Override
    public void creationTournoi(TournoiForm form) {
        if (form == null)
            throw new IllegalArgumentException("form peut pas être null");

        TournoiEntity entity = new TournoiEntity();
        entity.setNom(form.nom());
        entity.setLieu(form.lieu());
        entity.setNombreMinJoueurs(form.nombreMinJoueurs());
        entity.setNombreMaxJoueurs(form.nombreMaxJoueurs());
        entity.setELOMin(form.eLOMin());
        entity.setELOMax(form.eLOMax());
        entity.setCategories(form.categories());
        entity.setStatut(form.statut());
        entity.setRonde(0);
        entity.setWomenOnly(form.womenOnly());
        entity.setDateFinInscriptions(form.dateFinInscriptions());
        entity.setDateCreation(LocalDate.now());
        entity.setDateModification(LocalDate.now());

        tournoiRepository.save(entity);

    }
}
