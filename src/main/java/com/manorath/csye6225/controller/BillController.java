package com.manorath.csye6225.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.manorath.csye6225.exception.*;
import com.manorath.csye6225.model.Bill;
import com.manorath.csye6225.model.BillAttachment;
import com.manorath.csye6225.service.BillService;
import com.manorath.csye6225.util.Utils;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class BillController extends GeneralExceptionHandler {

    // hashset to match
    private static  HashSet<String> allowedTypes;
    private final static Logger logger = LoggerFactory.getLogger(BillController.class);
    static {
         allowedTypes = new HashSet<String>();
        allowedTypes.add("image/jpg");
        allowedTypes.add("image/jpeg");
        allowedTypes.add("image/png");
        allowedTypes.add("application/pdf");
    }
    @Autowired
    BillService billService;

    @Autowired
    private StatsDClient statsd;

    @Value("${amazon.s3.bucket}")
    String bucketName;

    @Value("${aws.domain.name}")
    String domainName;

    private AmazonS3 s3Client;
    private AmazonSQS amazonSQS;


    @PostConstruct
    private void buildAmazon() {
        this.s3Client = AmazonS3ClientBuilder.standard().build();
        this.amazonSQS = AmazonSQSClientBuilder.defaultClient();
    }


    @RequestMapping(value = "v2/bill/",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public Bill createBill(@Valid @RequestBody Bill bill,
                             HttpServletResponse response, @RequestHeader(value = "Authorization")String auth) {

        statsd.incrementCounter("BillHttpPOST");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String creds[] = Utils.decode(auth);
        if(bill.getId()!= null || bill.getOwnerID()!= null || bill.getCreatedTs()!= null || bill.getUpdatedTs() != null)
        {
            throw new FiledNotAllowedException("Fields not allowed");
        }
        Bill b = billService.createBill(bill,creds[0]);
        if(b == null) {
            throw new PasswordNotValidException("Please enter a valid password");
        }
        response.setHeader("description","Bill created");
        stopWatch.stop();
        statsd.recordExecutionTime("BillHttpPOST",stopWatch.getLastTaskTimeMillis());
        logger.info("bill post");
        return bill;
    }

    @RequestMapping(value = "v2/bills",
            method = RequestMethod.GET,
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public List<Bill> getAllBills(HttpServletResponse response, @RequestHeader(value = "Authorization")String auth) {
        statsd.incrementCounter("BillHttpGetAll");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String creds[] = Utils.decode(auth);
         List<Bill> bills = billService.findBillByUser(creds[0]);
        stopWatch.stop();
         statsd.recordExecutionTime("BillHttpGetAll",stopWatch.getLastTaskTimeMillis());
        logger.info("bill get");
         return bills;
    }

    @DeleteMapping("v1/bill/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBill(@PathVariable(value = "id") String billId, @RequestHeader(value = "Authorization")String auth) {
        String[] cred = Utils.decode(auth);
        statsd.incrementCounter("BillHttpDelete");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        switch (billService.deleteBill(cred[0],billId)) {
            case 1 :
                throw new BillDoesNotMatchException("bill does not match");
            case 2:
                throw new BillDoesNotExistException("bill does not exist");
             default:
                 break;
        }
        stopWatch.stop();
        logger.info("bill delete");
        statsd.recordExecutionTime("BillHttpDelete",stopWatch.getLastTaskTimeMillis());
    }

    @RequestMapping(value = "v1/bill/{id}",
                method = RequestMethod.GET,
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public Bill getBill(@PathVariable(value = "id") String billId, @RequestHeader(value = "Authorization")String auth) {
        statsd.incrementCounter("BillHttpGetOne");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String[] cred = Utils.decode(auth);

        Bill bill = billService.getBillById(cred[0],billId);
        stopWatch.stop();
        statsd.recordExecutionTime("BillHttpDelete",stopWatch.getLastTaskTimeMillis());
        logger.info("bill get 1");
        return bill;
    }

    @RequestMapping(value = "v1/bill/{id}",
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public Bill updateBill(@Valid @RequestBody Bill bill,
                           HttpServletResponse response, @RequestHeader(value = "Authorization")String auth,@PathVariable(value = "id") String billId) {
        String creds[] = Utils.decode(auth);
        statsd.incrementCounter("BillHttpPut");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        if(bill.getId()!= null || bill.getOwnerID()!= null || bill.getCreatedTs()!= null || bill.getUpdatedTs() != null)
        {
            throw new FiledNotAllowedException("Fields not allowed");
        }

        Bill b = billService.updateBill(creds[0],billId,bill);
        if(b == null) {
            throw new PasswordNotValidException("Please enter a valid password");
        }
        response.setHeader("description","Bill created");
        stopWatch.stop();
        statsd.recordExecutionTime("BillHttpPut",stopWatch.getLastTaskTimeMillis());
        logger.info("bill put");
        return b;
    }

    @RequestMapping(value = "v1/bill/{id}/file", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public BillAttachment fileUpload(@RequestParam("file") MultipartFile file, @RequestHeader(value = "Authorization")String auth
            , @PathVariable(value = "id") String billId) throws IOException, NoSuchAlgorithmException {
        String creds[] = Utils.decode(auth);
        statsd.incrementCounter("FileHttpPost");
        Bill b = billService.getBillById(creds[0],billId);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        if (!allowedTypes.contains(file.getContentType()))
        {
            throw new FileNotSupportedException("file not supported");
        }

        if (b.getAttachment()!= null) {
            throw new FileAlreadyExistsException("File already exists");
        }
        else {

            ObjectMetadata obj = new ObjectMetadata();
            BillAttachment billAttachment = new BillAttachment();
            billAttachment.setFileName(file.getOriginalFilename());
            billAttachment.setUrl(billId + "/" + file.getOriginalFilename());
            billAttachment.setUploadDate(new Date());
            billAttachment.setId(UUID.randomUUID().toString());
            billAttachment.setAttachmentSize(file.getSize());
            // store
            StopWatch stopWatch3 = new StopWatch();
            stopWatch.start();
            s3Client.putObject(bucketName,billId+"/"+file.getOriginalFilename(),file.getInputStream(),new ObjectMetadata());
            stopWatch3.stop();
            statsd.recordExecutionTime("FileS3Put",stopWatch.getLastTaskTimeMillis());
            // get attachment to store metadata
            StopWatch stopWatch4 = new StopWatch();
            stopWatch.start();
            S3Object result = s3Client.getObject(bucketName,billId+"/"+file.getOriginalFilename());
            stopWatch4.stop();
            statsd.recordExecutionTime("FileS3Delete",stopWatch.getLastTaskTimeMillis());
            System.out.println(result.getKey());


            billAttachment.setFileContentType(result.getObjectMetadata().getContentType());
            billAttachment.setMd5Hash(Utils.getMD5(file.getBytes()));
            billAttachment.setLastModified(result.getObjectMetadata().getLastModified());


            b.setAttachment(billAttachment);

            billService.updateBill(creds[0],billId,b);
            stopWatch.stop();
            statsd.recordExecutionTime("FileHttpPost",stopWatch.getLastTaskTimeMillis());
            return billAttachment;
        }
    }

    @RequestMapping(value = "v1/bill/{id}/file/{fileid}", method = RequestMethod.GET,
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public BillAttachment getfile(@RequestHeader(value = "Authorization")String auth
            , @PathVariable(value = "id") String billId, @PathVariable(value = "fileid") String fileid) {
        String creds[] = Utils.decode(auth);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        statsd.incrementCounter("FileHttpGet");
        Bill b = billService.getBillById(creds[0],billId);

        if (b.getAttachment()!= null) {
            if(b.getAttachment().getId().equals(fileid)){
                stopWatch.stop();
                statsd.recordExecutionTime("FileHttpGet",stopWatch.getLastTaskTimeMillis());
                return b.getAttachment();
            }
            else {
                throw new FileDoesNotMatch("File does not exist");
            }
        }
        else {
            throw new FileDoesNotExistException("File does not exist");
        }
    }

    @RequestMapping(value = "v1/bill/{id}/file/{fileid}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletefile(@RequestHeader(value = "Authorization")String auth
            , @PathVariable(value = "id") String billId, @PathVariable(value = "fileid") String fileid) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String creds[] = Utils.decode(auth);

        Bill b = billService.getBillById(creds[0],billId);
        statsd.incrementCounter("FileHttpDelete");
        if (b.getAttachment()!= null) {
            if(b.getAttachment().getId().equals(fileid)){
                StopWatch stopWatch5 = new StopWatch();
                stopWatch.start();
                s3Client.deleteObject(bucketName,b.getAttachment().getUrl());
                statsd.recordExecutionTime("FileHttpDelete",stopWatch.getLastTaskTimeMillis());
                b.setAttachment(null);
                billService.updateBill(creds[0],billId,b);
            }
            else {
                throw new FileDoesNotMatch("File does not exist");
            }
        }
        else {
            throw new FileDoesNotExistException("File does not exist");
        }
        stopWatch.stop();
        statsd.recordExecutionTime("FileHttpDelete",stopWatch.getLastTaskTimeMillis());
    }

    @RequestMapping(value = "/v1/bills/due/{x}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void getDue(@RequestHeader(value = "Authorization")String auth
            , @PathVariable(value = "x") String days) {
        String creds[] = Utils.decode(auth);
        List<Bill> bills = billService.findBillByUser(creds[0]);
        String queue_url = amazonSQS.getQueueUrl("bills-due-queue").getQueueUrl();
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queue_url)
                .withMessageBody(creds[0]+","+days);
        amazonSQS.sendMessage(send_msg_request);
    }
}
