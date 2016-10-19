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

	/**
	 * Sends an e-mail to the specified list of recipient addresses. Before a
	 * sequence of carbon copy recipients, add the following special address:
	 * <code>--cc</code>. Before a sequence of blind carbon copy recipients, add
	 * one of the following special addresses: <code>--bcc</code> or
	 * <code>--cco</code>.
	 * 
	 * @param subject
	 *            the message's subject, title or purpose
	 * @param text
	 *            textual message or any HTML contents
	 * @param from
	 *            the sender's e-mail address
	 * @param attachments
	 *            array of names corresponding to the attached files
	 * @param toRecipients
	 *            sequence of e-mail addresses corresponding to the recipients.
	 *            Those defined after the special addresses <code>--cc</code>
	 *            and <code>--bcc</code> will be treated as carbon copy and
	 *            blind carbon copy recipients, respectively.
	 */
	public void sendMail(String subject, String text, String from, String[] attachments, String... toRecipients) {
		try {
			EmailMessage msg = new EmailMessage(getService());
			if (from != null)
				msg.setFrom(getEmailAddressFromString(from));
			msg.setSubject(subject);
			msg.setBody(getMessageBodyFromText(text));
			String recipientType = "to";
			for (String toRecipient : toRecipients)
				if (toRecipient.equalsIgnoreCase("--to"))
					recipientType = "to";
				else if (toRecipient.equalsIgnoreCase("--cc"))
					recipientType = "cc";
				else if (toRecipient.equalsIgnoreCase("--bcc") || toRecipient.equalsIgnoreCase("--cco"))
					recipientType = "bcc";
				else if (recipientType.equalsIgnoreCase("cc"))
					msg.getCcRecipients().add(toRecipient);
				else if (recipientType.equalsIgnoreCase("bcc"))
					msg.getBccRecipients().add(toRecipient);
				else
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
