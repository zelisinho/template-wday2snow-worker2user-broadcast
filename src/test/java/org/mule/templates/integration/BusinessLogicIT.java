/**
 * Mule Anypoint Template
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.integration;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.context.notification.NotificationException;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;

import com.mulesoft.module.batch.BatchTestHelper;
import com.servicenow.usermanagement.sysuser.GetRecordsResponse;

/**
 * The objective of this class is to validate the correct behavior of the flows
 * for this Anypoint Template that make calls to external systems.
 */
public class BusinessLogicIT extends AbstractTemplateTestCase {

	private static final long TIMEOUT_MILLIS = 30000;
	private static final long DELAY_MILLIS = 500;
	protected static final String PATH_TO_TEST_PROPERTIES = "./src/test/resources/mule.test.properties";
	protected static final int TIMEOUT_SEC = 60;
	private static final String PHONE_NUMBER = "232-2323";
	private static final String STREET = "999 Main St";
	private static final String CITY = "San Francisco";
	private static final String LAST_NAME = "Vukovic1";
	private static final String FIRST_NAME = "Darko1";
	private BatchTestHelper helper;
	private Map<String, String> user = new HashMap<String, String>();	
	private static String WORKDAY_ID;	
    private String EMAIL = "drvukovic25334@live.com";	
    private String SNOW_ID;
    
    @BeforeClass
    public static void beforeTestClass() {
        System.setProperty("poll.startDelayMillis", "8000");
        System.setProperty("poll.frequencyMillis", "30000");               
    }

    @Before
    public void setUp() throws Exception {
		final Properties props = new Properties();
    	try {
    		props.load(new FileInputStream(PATH_TO_TEST_PROPERTIES));
    	} catch (Exception e) {
    	   logger.error("Error occured while reading mule.test.properties", e);
    	} 
    	WORKDAY_ID = props.getProperty("wday.testuser.id");
    	helper = new BatchTestHelper(muleContext);
		stopFlowSchedulers(POLL_FLOW_NAME);
		registerListeners();
		
		createTestDataInSandBox();
    }

    @After
    public void tearDown() throws MuleException, Exception {
    	deleteTestDataFromSandBox();
    }
    
    private void registerListeners() throws NotificationException {
		muleContext.registerListener(pipelineListener);
	}
    
    private void createTestDataInSandBox() throws MuleException, Exception {
		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("updateWorkdayEmployee");
		flow.initialise();
		logger.info("updating a workday employee...");
		try {
			flow.process(getTestEvent(prepareEdit(), MessageExchangePattern.REQUEST_RESPONSE));						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    private Map<String, String> prepareEdit(){			
		user.put("Location", "San_Francisco_site");
		user.put("Phone", PHONE_NUMBER);
		user.put("Email", EMAIL);
		user.put("ExtId__c", WORKDAY_ID);
		user.put("Street", STREET + System.currentTimeMillis());
		user.put("Country", "USA");
		user.put("State", "USA-CA");
		user.put("City", CITY);
		user.put("PostalCode", "94105");
		user.put("LastModifiedDate", String.valueOf(System.currentTimeMillis()));
		return user;
	}
        
	@Test
    public void testMainFlow() throws Exception {
		Thread.sleep(10000);
		runSchedulersOnce(POLL_FLOW_NAME);
		waitForPollToRun();
		helper.awaitJobTermination(TIMEOUT_MILLIS, DELAY_MILLIS);
		helper.assertJobWasSuccessful();	
		
		SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("getSnowUsers");
		flow.initialise();
		
		MuleEvent response = flow.process(getTestEvent(EMAIL, MessageExchangePattern.REQUEST_RESPONSE));
		GetRecordsResponse snowRes = ((GetRecordsResponse)response.getMessage().getPayload());
		logger.info("snow requests: " + snowRes.getGetRecordsResult().size());
		
		Assert.assertTrue("There should be a user in ServiceNow.", snowRes.getGetRecordsResult().size() == 1);
		Assert.assertEquals("First name should be set", snowRes.getGetRecordsResult().get(0).getFirstName(), FIRST_NAME);
		Assert.assertEquals("Last name should be set", snowRes.getGetRecordsResult().get(0).getLastName(), LAST_NAME);
		Assert.assertEquals("City should be set", snowRes.getGetRecordsResult().get(0).getCity(), CITY);
		Assert.assertEquals("Street should be set", snowRes.getGetRecordsResult().get(0).getStreet(), user.get("Street"));		
		Assert.assertEquals("Home Phone number should be set", snowRes.getGetRecordsResult().get(0).getHomePhone(), "+1  " + PHONE_NUMBER);		
		
		SNOW_ID = snowRes.getGetRecordsResult().get(0).getSysId();
    }    
    
    private void deleteTestDataFromSandBox() throws MuleException, Exception {
    	logger.info("deleting test data...");
		
    	SubflowInterceptingChainLifecycleWrapper flow = getSubFlow("deleteSnowUsers");
		flow.initialise();		
		flow.process(getTestEvent(SNOW_ID));							

	}       
	
}
