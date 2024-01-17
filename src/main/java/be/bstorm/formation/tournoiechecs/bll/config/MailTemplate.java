package be.bstorm.formation.tournoiechecs.bll.config;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class MailTemplate {
    @Data
    public static class HtmlTemplate{
        private String template;
        private Map<String, Object> props;

        public HtmlTemplate(String template, Map<String, Object> props) {
            this.template = template;
            this.props = props;
        }
    }

    private String from;
    private String to;
    private HtmlTemplate htmlTemplate;
    private String subject;
}
