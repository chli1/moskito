package net.anotheria.moskito.extensions;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.StreamDataBodyPart;
import net.anotheria.moskito.core.plugins.chart.ChartSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;

/**
 * MailGun chart sender.
 *
 */
public class MailgunChartSender implements ChartSender {

    /**
     * The system property for the api key for mailgun api.
     */
    public static final String SYSTEM_PROPERTY_API_KEY = MailgunChartSender.class.getSimpleName() + "APIKey";

    /**
     * System property for sender's address.
     */
    public static final String SYSTEM_PROPERTY_SENDER = MailgunChartSender.class.getSimpleName() + "Sender";

    /**
     * Preinstalled api key, if no other api key is configured.
     */
    private static final String DEFAULT_API_KEY = "key-72d3spjfr06x0knhupx4z1v0e2bgjfk3";

    /**
     * Default sender.
     */
    private static final String DEFAULT_SENDER = "MoSKito Chart Sender <moskito-chart-sender@moskito.org>";

    /**
     * Logger.
     */
    private static Logger log = LoggerFactory.getLogger(MailgunChartSender.class);

    /**
     * Used api key at runtime.
     */
    private final String apiKey;

    /**
     * Used sender at runtime.
     */
    private final String sender;

    /**
     * Client instance.
     */
    private Client client;

    /**
     * Constructor.
     */
    public MailgunChartSender() {
        client = Client.create();

        String anApiKey = System.getProperty(SYSTEM_PROPERTY_API_KEY);
        if (anApiKey == null) {
            log.info("Using default api key, set -D" + SYSTEM_PROPERTY_API_KEY + "=<api_key> if you want to use own key");
            anApiKey = DEFAULT_API_KEY;
        }

        apiKey = anApiKey;
        sender = System.getProperty(SYSTEM_PROPERTY_SENDER, DEFAULT_SENDER);
        log.debug("Sender is " + sender);
        client.addFilter(new HTTPBasicAuthFilter("api", apiKey));
    }

    /**
     * Send chart.
     *
     * @param filename chart file name
     * @param image64 image as byte array
     * @param subject subject of message
     * @param recipients recipients list
     */
    @Override
    public void sendChart(String filename, String image64, String subject, String[] recipients) {

        try {
            WebResource webResource = client.resource("https://api.mailgun.net/v2/moskito.org/messages");

            FormDataMultiPart formData = new FormDataMultiPart();
            formData.field("from", DEFAULT_SENDER);
            for (String recipient : recipients) {
                formData.field("to", recipient.trim());
            }
            formData.field("subject", subject);
            formData.field("text", subject);

            byte[] decodedBytes = DatatypeConverter.parseBase64Binary(image64 );
            formData.bodyPart(new StreamDataBodyPart("attachment", new ByteArrayInputStream(decodedBytes), filename));

            try {
                ClientResponse response = webResource.type(MediaType.MULTIPART_FORM_DATA_TYPE).post(ClientResponse.class, formData);

                if (response.getStatus() != 200) {
                    log.warn("Couldn't send email, status expected 200, got " + response.getStatus());
                } else {
                    byte data[] = new byte[response.getLength()];
                    log.debug("Successfully sent notification mail " + new String(data));

                }
            } catch (Throwable e) {
                e.printStackTrace();
                log.error("Couldn't send email", e);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Couldn't send email", e);
        }
    }

}
