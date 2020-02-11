package com.manorath.csye6225.controller;

import com.manorath.csye6225.exception.*;
import com.manorath.csye6225.model.Bill;
import com.manorath.csye6225.model.BillAttachment;
import com.manorath.csye6225.service.BillService;
import com.manorath.csye6225.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@RestController
public class BillController extends GeneralExceptionHandler {

    // hashset to match
    private static  HashSet<String> allowedTypes;
    static {
         allowedTypes = new HashSet<String>();
        allowedTypes.add("image/jpg");
        allowedTypes.add("image/jpeg");
        allowedTypes.add("image/png");
        allowedTypes.add("application/pdf");
    }

    @Autowired
    BillService billService;

    @RequestMapping(value = "v1/bill/",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public Bill createBill(@Valid @RequestBody Bill bill,
                             HttpServletResponse response, @RequestHeader(value = "Authorization")String auth) {
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
        return bill;
    }

    @RequestMapping(value = "v1/bills",
            method = RequestMethod.GET,
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public List<Bill> getAllBills(HttpServletResponse response, @RequestHeader(value = "Authorization")String auth) {
        String creds[] = Utils.decode(auth);
        return billService.findBillByUser(creds[0]);
    }

    @DeleteMapping("v1/bill/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBill(@PathVariable(value = "id") String billId, @RequestHeader(value = "Authorization")String auth) {
        String[] cred = Utils.decode(auth);
        switch (billService.deleteBill(cred[0],billId)) {
            case 1 :
                throw new BillDoesNotMatchException("bill does not match");
            case 2:
                throw new BillDoesNotExistException("bill does not exist");
             default:
                 break;
        }
    }

    @RequestMapping(value = "v1/bill/{id}",
                method = RequestMethod.GET,
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public Bill getBill(@PathVariable(value = "id") String billId, @RequestHeader(value = "Authorization")String auth) {
        String[] cred = Utils.decode(auth);
        return billService.getBillById(cred[0],billId);
    }

    @RequestMapping(value = "v1/bill/{id}",
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public Bill updateBill(@Valid @RequestBody Bill bill,
                           HttpServletResponse response, @RequestHeader(value = "Authorization")String auth,@PathVariable(value = "id") String billId) {
        String creds[] = Utils.decode(auth);
        if(bill.getId()!= null || bill.getOwnerID()!= null || bill.getCreatedTs()!= null || bill.getUpdatedTs() != null)
        {
            throw new FiledNotAllowedException("Fields not allowed");
        }

        Bill b = billService.updateBill(creds[0],billId,bill);
        if(b == null) {
            throw new PasswordNotValidException("Please enter a valid password");
        }
        response.setHeader("description","Bill created");
        return b;
    }

    @RequestMapping(value = "v1/bill/{id}/file", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public BillAttachment fileUpload(@RequestParam("file") MultipartFile file, @RequestHeader(value = "Authorization")String auth
            , @PathVariable(value = "id") String billId) throws IOException, NoSuchAlgorithmException {
        String creds[] = Utils.decode(auth);

        Bill b = billService.getBillById(creds[0],billId);

        //file.getContentType().equals(MediaType.IMAGE_JPEG_VALUE) || file.getContentType().equals(MediaType.IMAGE_PNG_VALUE)
        //                || file.getContentType().equals(MediaType.APPLICATION_PDF_VALUE) || file.getContentType().equals("image/jpg")
        if (!allowedTypes.contains(file.getContentType()))
        {
            throw new FileNotSupportedException("file not supported");
        }

        if (b.getAttachment()!= null) {
            throw new FileAlreadyExistsException("File already exists");
        }
        else {

            BillAttachment billAttachment = new BillAttachment();
            billAttachment.setFileName(file.getOriginalFilename());
            billAttachment.setUrl("/var/tmp/csye6225/" + billId + "/" + file.getOriginalFilename());
            billAttachment.setUploadDate(new Date());
            billAttachment.setId(UUID.randomUUID().toString());
            billAttachment.setAttachmentSize(file.getSize());
            billAttachment.setMd5Hash(Utils.getMD5(file.getBytes()));
            billAttachment.setFileContentType(file.getContentType());
            b.setAttachment(billAttachment);
            billService.updateBill(creds[0],billId,b);
            return billAttachment;
        }
    }

    @RequestMapping(value = "v1/bill/{id}/file/{fileid}", method = RequestMethod.GET,
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public BillAttachment getfile(@RequestHeader(value = "Authorization")String auth
            , @PathVariable(value = "id") String billId, @PathVariable(value = "fileid") String fileid) {
        String creds[] = Utils.decode(auth);

        Bill b = billService.getBillById(creds[0],billId);

        if (b.getAttachment()!= null) {
            if(b.getAttachment().getId().equals(fileid)){
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
        String creds[] = Utils.decode(auth);
        Bill b = billService.getBillById(creds[0],billId);

        if (b.getAttachment()!= null) {
            if(b.getAttachment().getId().equals(fileid)){
                File convertFile = new File("/var/tmp/csye6225/" + billId + "/" +b.getAttachment().getFileName());

                if(convertFile.exists()) {
                    convertFile.delete();
                    convertFile = new File("/var/tmp/csye6225/" + billId);
                    convertFile.delete();
                }

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
    }
}
