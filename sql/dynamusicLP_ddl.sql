DROP TABLE dynamusic_loyalty_info ;
DROP TABLE dynamusic_loyalty_price_info ;
drop TABLE dynamusic_order_lp ;
DROP TABLE dynamusic_loyalty_payment ;
DROP TABLE dynamusic_loyalty_order_price_info ;


CREATE TABLE dynamusic_pl_order_transaction (
        transaction_id     VARCHAR(32)  not null references dynamusic_lp_transaction(id),
        order_id           VARCHAR(32)  not null references dcspp_order(order_id),
        primary key(transaction_id)
);

CREATE TABLE dynamusic_loyalty_order_price_info (
        info_id          VARCHAR(40) not null REFERENCES dcspp_order_price(amount_info_id),
        received_pl	     integer	,
        conversion       VARCHAR(100),
        bonus_percent    INTEGER,
        bonus_round_rule INTEGER ,
        transaction_id   VARCHAR (32),
        primary key (info_id)
) ;

CREATE TABLE dynamusic_loyalty_payment(
        payment_group_id   VARCHAR(40) not null REFERENCES dcspp_pay_group(payment_group_id),
        spend_pl	         integer	,
        conversion         VARCHAR(100),
        payment_round_rule INTEGER ,
        profile_id         VARCHAR (32),
        primary key (payment_group_id)
) ;

CREATE TABLE dynamusic_plpg_status(
        pg_status_id    VARCHAR(32)  not null references dcspp_pay_status(status_id),
        transaction_id  VARCHAR(32)  references dynamusic_lp_transaction(id),
        pl_amount       INTEGER,
        primary key(pg_status_id)
) ;


commit work;


