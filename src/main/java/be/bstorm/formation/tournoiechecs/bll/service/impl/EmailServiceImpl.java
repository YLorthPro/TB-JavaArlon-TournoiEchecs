package be.bstorm.formation.tournoiechecs.bll.service.impl;

import be.bstorm.formation.tournoiechecs.bll.config.MailTemplate;
import be.bstorm.formation.tournoiechecs.dal.model.JoueurEntity;
import be.bstorm.formation.tournoiechecs.dal.model.TournoiEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Service
public class EmailServiceImpl {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine springTemplateEngine;

    public EmailServiceImpl(JavaMailSender mailSender, SpringTemplateEngine springTemplateEngine) {
        this.mailSender = mailSender;
        this.springTemplateEngine = springTemplateEngine;
    }

    public void nouveauJoueurCree(JoueurEntity joueur) throws MessagingException {
        Map<String, Object> properties = new HashMap<>();
        properties.put("joueur", joueur);
        MailTemplate mail = MailTemplate.builder()
                .to(joueur.getEmail())
                .from("checkmate@chess.be")
                .htmlTemplate(new MailTemplate.HtmlTemplate("creationJoueur", properties))
                .subject("Bienvenue "+joueur.getPseudo())
                .build();
        sendMimeMessage(mail);
    }

    public void nouveauTournoiCree(JoueurEntity joueur, TournoiEntity tournoi) throws MessagingException {
        Map<String, Object> properties = new HashMap<>();
        properties.put("joueur", joueur);
        properties.put("tournoi", tournoi);
        MailTemplate mail = MailTemplate.builder()
                .to(joueur.getEmail())
                .from("checkmate@chess.be")
                .htmlTemplate(new MailTemplate.HtmlTemplate("creationTournoi", properties))
                .subject("Création nouveau tournoi")
                .build();
        sendMimeMessage(mail);
    }

    private void sendMimeMessage(MailTemplate mail) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_RELATED,
                StandardCharsets.UTF_8.name());

        String html = getHtmlContent(mail);
        helper.setTo(mail.getTo());
        helper.setFrom(mail.getFrom());
        helper.setSubject(mail.getSubject());
        helper.setText(html, true);
        mailSender.send(message);
    }

    private String getHtmlContent(MailTemplate mailTemplate){
        Context context = new Context();
        context.setVariables(mailTemplate.getHtmlTemplate().getProps());
        return springTemplateEngine.process(mailTemplate.getHtmlTemplate().getTemplate(), context);
    }
}
