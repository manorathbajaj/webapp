package com.manorath.csye6225.config;

import com.amazonaws.services.sns.AmazonSNS;

import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sqs.AmazonSQS;

import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;

import com.manorath.csye6225.model.Bill;
import com.manorath.csye6225.service.BillService;
import com.manorath.csye6225.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


import javax.annotation.PostConstruct;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
public class BackGroundThread {


    @Value("${aws.domain.name}")
    String domainName;

    private final static Logger logger = LoggerFactory.getLogger(BackGroundThread.class);

    AmazonSQS amazonSQS;
    AmazonSNS amazonSNS;

    @Autowired
    BillService billService;

    @PostConstruct
    private void buildAmazon() {
        this.amazonSQS = AmazonSQSClientBuilder.defaultClient();
        this.amazonSNS = AmazonSNSClientBuilder.defaultClient();
    }

    @Scheduled(fixedRate =  10000)
    public void pollQueue() {
        String queue_url = amazonSQS.getQueueUrl("bills-due-queue").getQueueUrl();
        List<Message> messages = amazonSQS.receiveMessage(queue_url).getMessages();
        for(Message m : messages) {
            String[] body = m.getBody().split(",");
            List<Bill> allBills = billService.findBillByUser(body[0]);

            List<Bill> dueBills = allBills.stream().
                    filter(bill -> (Utils.getDiff(bill.getDueDate()) <= Integer.parseInt(body[1])))
                    .collect(Collectors.toList());

            String message = body[0];
            for(Bill bill: dueBills) {
                message = message + "," +domainName.substring(0,domainName.length()-1)+"/v1/bill/"+bill.getId();
            }
            CreateTopicResult createRes = amazonSNS.createTopic("email_due_patients");
            amazonSNS.publish(new PublishRequest(createRes.getTopicArn(),message));
            amazonSQS.deleteMessage(queue_url,m.getReceiptHandle());
            logger.info("published message {}",message);
        }
    }
}
