package com.multipolar.uiteller.backend.service.autodebit;

import com.multipolar.uiteller.backend.model.*;
import com.multipolar.uiteller.backend.repository.AutodebitLogRepository;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AutodebitLogService {

    @Autowired
    private AutodebitLogRepository autodebitLogRepository;

    private static final String[] HQ_BRANCHCODE = {"001", "601"};

    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("ddMMyyyy");

    @Transactional(readOnly = true)
    public AutodebitLogResponse searchWithSpecification(final AutodebitLogRequest request, final String branchCode) {
        Specification<AutodebitLog> specification = new Specification<AutodebitLog>() {
            @Override
            public Predicate toPredicate(Root<AutodebitLog> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                List<Predicate> predicates = new ArrayList<>();
                List<Order> orders = new ArrayList<>();

                Join<AutodebitLog, Autodebit> autodebitJoin = root.join("autodebit");
                predicates.add(builder.equal(autodebitJoin.get("deleted"), 0));

                if (!StringUtils.equalsAny(branchCode, HQ_BRANCHCODE)) {
                    predicates.add(builder.equal(autodebitJoin.get("branchCode"), branchCode));
                }

                if (request.getTrxName() != null) {
                    if (request.getTrxName().equals("dti")) {
                        Join<Autodebit, InstansiPdam> pdamJoin = autodebitJoin.join("instansiPdam");
                    } else {
                        predicates.add(builder.equal(autodebitJoin.get("transactionName"), request.getTrxName()));
                    }
                }

                if (request.getTrxDate() != null) {
                    DateTime theDate = formatter.parseDateTime(request.getTrxDate());
                    Date startDate = theDate.withTimeAtStartOfDay().toDate();
                    Date endDate = theDate.plusDays(1).withTimeAtStartOfDay().toDate();

                    predicates.add(builder.between(root.get("createdAt").as(Date.class), startDate, endDate));
                }

                if (request.getSearchValue() != null) {
                    predicates.add(builder.or(
                            builder.like(builder.lower(autodebitJoin.<String>get("transactionName")), "%" + request.getSearchValue().toLowerCase() + "%"),
                            builder.like(builder.lower(root.<String>get("inquiryTraceNumber")), "%" + request.getSearchValue().toLowerCase() + "%"),
                            builder.like(builder.lower(root.<String>get("paymentTraceNumber")), "%" + request.getSearchValue().toLowerCase() + "%"),
                            builder.like(builder.lower(root.<String>get("ref")), "%" + request.getSearchValue().toLowerCase() + "%"),
                            builder.like(builder.lower(autodebitJoin.<String>get("customerId")), "%" + request.getSearchValue().toLowerCase() + "%"),
                            builder.like(builder.lower(autodebitJoin.<String>get("sourceNumber")), "%" + request.getSearchValue().toLowerCase() + "%"),
                            builder.like(builder.lower(autodebitJoin.<String>get("userCreator")), "%" + request.getSearchValue().toLowerCase() + "%"),
                            builder.like(builder.lower(autodebitJoin.<String>get("branchCode")), "%" + request.getSearchValue().toLowerCase() + "%")
                    ));
                }

                //set order
                String orderColumn = request.getOrderColumn();
                String prefix = "autodebit.";
                int position = orderColumn.indexOf(prefix);

                if (position != -1) {
                    //order by column on Autodebit table
                    orderColumn = orderColumn.substring(position + prefix.length());
                    if (request.getOrderDirection().equals("desc")) {
                        orders.add(builder.desc(autodebitJoin.get(orderColumn)));
                    } else {
                        orders.add(builder.asc(autodebitJoin.get(orderColumn)));
                    }
                } else {
                    //order by column on AutodebitLog table
                    if (request.getOrderDirection().equals("desc")) {
                        orders.add(builder.desc(root.get(orderColumn)));
                    } else {
                        orders.add(builder.asc(root.get(orderColumn)));
                    }
                }
                query.orderBy(orders);

                return builder.and(predicates.toArray(new Predicate[0]));
            }
        };

        Pageable pageable = new PageRequest(request.getPage(), request.getSize());
        Page<AutodebitLog> autodebitLogs = autodebitLogRepository.findAll(specification, pageable);

        return AutodebitLogResponse.builder()
                .trxName(request.getTrxName())
                .trxDate(request.getTrxDate())
                .page(request.getPage())
                .size(request.getSize())
                .searchValue(request.getSearchValue())
                .orderColumn(request.getOrderColumn())
                .orderDirection(request.getOrderDirection())
                .totalPage(autodebitLogs.getTotalPages())
                .totalRecords(autodebitLogs.getTotalElements())
                .logList(autodebitLogs.getContent())
                .build();
    }
}
