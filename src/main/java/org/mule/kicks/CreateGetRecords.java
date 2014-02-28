package org.mule.kicks;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

import com.servicenow.sysuser.GetRecords;
import com.workday.hr.WorkerDataType;
import com.workday.hr.WorkerType;

public class CreateGetRecords extends AbstractMessageTransformer {

	@Override
	public Object transformMessage(MuleMessage message, String outputEncoding) throws TransformerException {

		WorkerType payload = (WorkerType) message.getPayload();
		WorkerDataType workerData = payload.getWorkerData();
		
		GetRecords query = new GetRecords();
		query.setFirstName(workerData.getPersonalData().getNameData().getLegalNameData().getNameDetailData().getFirstName());
		query.setLastName(workerData.getPersonalData().getNameData().getLegalNameData().getNameDetailData().getLastName());
		query.setEmail(workerData.getPersonalData().getContactData().getEmailAddressData().get(0).getEmailAddress());

		message.setPayload(query);
		return message;
	}

}
