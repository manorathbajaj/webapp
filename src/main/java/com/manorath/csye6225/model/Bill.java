package com.manorath.csye6225.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "BILLS")
public class Bill {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Getter
    private String id;

    @Getter
    @Setter
    @Column(name = "created_ts")
    private Date createdTs;

    @Getter
    @Setter
    @Column(name = "updated_ts")
    private Date updatedTs;

    @Getter
    @Setter
    @Column(name = "owner_id")
    private String ownerID;

    @Getter
    @Setter
    @Column(name = "vendor")
    @NotBlank(message = "Vendor should not be empty")
    private String vendor;

    @Getter
    @Setter
    @Column(name = "bill_date")
    @NotNull(message = "Bill date should not be empty")
    private Date billDate;

    @Getter
    @Setter
    @Column(name = "due_date")
    @NotNull(message = "Due date should not be empty")
    private Date dueDate;

    @Getter
    @Setter
    @Column(name = "amount_due")
    @DecimalMin(value = "00r.01", message = "Due amoount should not be less than 0.01")
    private BigDecimal amountDue;

    @Getter
    @Setter
    @Column(name = "categories")
    @Size(min = 1, message = "There should be atleast 1 category")
    @ElementCollection
    @CollectionTable(name = "categories_table", joinColumns = @JoinColumn(name = "id"))
    private List<String> categories;

    @Getter
    @Setter
    @Column(name = "payment_status")
    @NotNull(message = "payment status should not be empty")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Getter
    @Setter
    @Embedded
    @Column(name = "attachment")
    private File attachment;
}
