package edworld.common.infra;

import static microsoft.exchange.webservices.data.property.complex.EmailAddress.getEmailAddressFromString;
import static microsoft.exchange.webservices.data.property.complex.MessageBody.getMessageBodyFromText;

import java.net.URI;
import java.net.URISyntaxException;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceVersionException;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.AttachmentCollection;

public class ExchangeMailServer {
	private String host;
	private WebCredentials credentials;
	private ExchangeService service;

	public ExchangeMailServer(String host, String userName, String password, String domain) {
		this.host = host;
		this.credentials = new WebCredentials(userName, password, domain);
	}

	public void sendMail(String subject, String text, String from, String[] attachments, String... toRecipients) {
		try {
			EmailMessage msg = new EmailMessage(getService());
			if (from != null)
				msg.setFrom(getEmailAddressFromString(from));
			msg.setSubject(subject);
			msg.setBody(getMessageBodyFromText(text));
			for (String toRecipient : toRecipients)
				msg.getToRecipients().add(toRecipient);
			if (attachments != null)
				for (String fileName : attachments)
					msg.getAttachments().addFileAttachment(fileName);
			addInlineAttachments(msg.getAttachments());
			msg.send();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	protected void addInlineAttachments(AttachmentCollection attachments) throws ServiceVersionException {
	}

	private ExchangeService getService() throws URISyntaxException {
		if (service == null) {
			service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
			service.setUrl(new URI("https://" + host + "/EWS/Exchange.asmx"));
			service.setCredentials(credentials);
		}
		return service;
	}
}
