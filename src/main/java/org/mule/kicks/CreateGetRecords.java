package org.mule.kicks;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

import com.servicenow.sysuser.GetRecords;

public class CreateGetRecords extends AbstractMessageTransformer {

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {

//		WorkerType payload = (WorkerType) message.getPayload();
//		WorkerDataType workerData = payload.getWorkerData();
		
		GetRecords query = new GetRecords();

//		query.setFirstName(workerData.getPersonalData().getNameData().getLegalNameData().getNameDetailData().getFirstName());
//		query.setLastName(workerData.getPersonalData().getNameData().getLegalNameData().getNameDetailData().getLastName());
//		query.setEmail(workerData.getPersonalData().getContactData().getEmailAddressData().get(0).getEmailAddress());
		
		query.setFirstName("Javier");
		query.setLastName("Casal");
		query.setEmail("javier@mail.com");

		message.setPayload(query);
		return message;
	}

}
