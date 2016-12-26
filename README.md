common-code
===========

Common code for typical Java applications

Sending e-mail to an MS Exchange server
-----------

```
	ExchangeMailServer mailServer = new ExchangeMailServer("EXCHANGE.SERVER.COMPANY.com", USER, PASSWORD, DOMAIN) {
		@Override
		protected void addInlineAttachments(AttachmentCollection attachments) throws ServiceVersionException {
			super.addInlineAttachments(attachments);
			attachments.addFileAttachment("MINILOGO.png", getClass().getResourceAsStream("/MINILOGO.png")).setIsInline(true);
		}
	};
	mailServer.sendMail(SUBJECT, BODY, FROM, ATTACHMENTS, TO);
```

