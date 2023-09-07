create table store
(
    id bigint auto_increment primary key,
    created_date datetime(6) null,
    modified_date datetime(6) null,
    address varchar(255) null,
    code varchar(255) null,
    name varchar(255) null,
    sector varchar(255) null,
    status varchar(255) null,
    manager_id bigint null,
    constraint FK_manager_store
        foreign key (manager_id) references users (id)
);

create table company
(
    id bigint auto_increment primary key,
    created_date datetime(6)  null,
    modified_date datetime(6)  null,
    code varchar(255) null,
    name varchar(255) null,
    sector varchar(255) null,
    status varchar(255) null
);

CREATE TABLE users
(
    id bigint auto_increment primary key,
    created_date datetime(6) null,
    modified_date datetime(6) null,
    birth date null,
    email varchar(255) not null,
    login_id varchar(255) null,
    name varchar(255) null,
    password varchar(200) not null,
    phone_number varchar(255) null,
    role varchar(255) null,
    status varchar(255) null,
    company_id bigint null,
    manager_store_id bigint null,
    CONSTRAINT UK_users_email_unique
        UNIQUE (email),
    CONSTRAINT FK_users_company_id_foreign
        FOREIGN KEY (company_id) REFERENCES company (id),
    CONSTRAINT FK_users_manager_store_id_foreign
        FOREIGN KEY (manager_store_id) REFERENCES store (id)
);

CREATE TABLE part_timer
(
    id bigint auto_increment primary key,
    created_date datetime(6) null,
    modified_date datetime(6) null,
    part_timer_id bigint not null,
    store_id bigint not null,
    CONSTRAINT FK_part_timer_store_id_foreign
        FOREIGN KEY (store_id) REFERENCES store (id),
    CONSTRAINT FK_part_timer_part_timer_id_foreign
        FOREIGN KEY (part_timer_id) REFERENCES users (id)
);

CREATE TABLE friend
(
    id bigint auto_increment primary key,
    created_date datetime(6) null,
    modified_date datetime(6) null,
    relation_status varchar(255) null,
    receiver_id bigint not null,
    sender_id bigint not null,
    CONSTRAINT FK_friend_sender_id_foreign
        FOREIGN KEY (sender_id) REFERENCES users (id),
    CONSTRAINT FK_friend_receiver_id_foreign
        FOREIGN KEY (receiver_id) REFERENCES users (id)
);

CREATE TABLE business
(
    id bigint auto_increment primary key,
    created_date datetime(6) null,
    modified_date datetime(6) null,
    relation_status varchar(255) null,
    manager_id bigint not null,
    supervisor_id bigint not null,
    CONSTRAINT FK_business_supervisor_id_foreign
        FOREIGN KEY (supervisor_id) REFERENCES users (id),
    CONSTRAINT FK_business_manager_id_foreign
        FOREIGN KEY (manager_id) REFERENCES users (id)
);

CREATE TABLE product
(
    id bigint auto_increment primary key,
    created_date datetime(6) null,
    modified_date datetime(6) null,
    expiration_date date null,
    image_url varchar(255) null,
    location varchar(255) null,
    name varchar(255) null,
    order_freq bigint null,
    price bigint null,
    receiving_date date null,
    require_quant bigint null,
    site_to_order varchar(255) null,
    status varchar(255) null,
    stock_quant bigint null,
    store_condition varchar(255) null,
    vendor varchar(255) null,
    store_id bigint not null,
    CONSTRAINT FK_product_store_id
        FOREIGN KEY (store_id) REFERENCES store (id)
);

CREATE TABLE board
(
    id bigint auto_increment primary key,
    created_date datetime(6) null,
    modified_date datetime(6) null,
    content text null,
    hit int not null,
    status varchar(255) null,
    title varchar(255) null,
    writer_id bigint null,
    CONSTRAINT FK_board_writer_id
        FOREIGN KEY (writer_id) REFERENCES users (id)
);

CREATE TABLE board_like
(
    id bigint auto_increment primary key,
    board_id bigint not null,
    user_id bigint not null,
    CONSTRAINT FK_board_like_user_id
        FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT FK_board_like_board_id
        FOREIGN KEY (board_id) REFERENCES board (id)
);

CREATE TABLE comment
(
    id bigint auto_increment primary key,
    created_date datetime(6) null,
    modified_date datetime(6) null,
    content text null,
    image varchar(255) null,
    status varchar(255) null,
    board_id bigint not null,
    writer_id bigint not null,
    CONSTRAINT FK_comment_writer_id
        FOREIGN KEY (writer_id) REFERENCES users (id),
    CONSTRAINT FK_comment_board_id
        FOREIGN KEY (board_id) REFERENCES board (id)
);

CREATE TABLE reply
(
    id bigint auto_increment primary key,
    created_date datetime(6) null,
    modified_date datetime(6) null,
    content text null,
    image varchar(255) null,
    status varchar(255) null,
    comment_id bigint not null,
    writer_id bigint not null,
    CONSTRAINT FK_reply_comment_id
        FOREIGN KEY (comment_id) REFERENCES comment (id),
    CONSTRAINT FK_reply_writer_id
        FOREIGN KEY (writer_id) REFERENCES users (id)
);

CREATE TABLE share
(
    id bigint auto_increment primary key,
    created_date datetime(6) null,
    modified_date datetime(6) null,
    category varchar(255) null,
    content text null,
    file varchar(255) null,
    status varchar(255) null,
    title varchar(255) null,
    business_id bigint not null,
    CONSTRAINT FK_share_business_id
        FOREIGN KEY (business_id) REFERENCES business (id)
);
