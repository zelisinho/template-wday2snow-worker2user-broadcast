package org.mule.kicks;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

import com.servicenow.sysuser.Insert;

public class CreateInsert extends AbstractMessageTransformer {

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding)throws TransformerException {
		
		Insert user = new Insert();
		user.setFirstName("Miguel");
		user.setMiddleName("Astic");
		user.setLastName("Oliva");
		
		message.setPayload(user);
		return message;
	}

}
