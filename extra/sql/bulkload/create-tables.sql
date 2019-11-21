
    create table dictionary (
       dictionary_id varchar(255) not null,
        created_by varchar(255),
        dictionary_name varchar(255) not null,
        timestamp datetime(6),
        dictionary_sec_level integer,
        dictionary_type varchar(255),
        modified_by varchar(255),
        primary key (dictionary_id)
    ) engine=InnoDB;

    create table dictionary_elements (
       dict_element_id varchar(255) not null,
        created_by varchar(255),
        dict_element_description varchar(255),
        dict_element_name varchar(255) not null,
        dict_element_short_name varchar(255) not null,
        dict_element_type varchar(255) not null,
        timestamp datetime(6),
        subdictionary_id varchar(255) not null,
        modified_by varchar(255),
        dictionary_id varchar(255),
        primary key (dict_element_id)
    ) engine=InnoDB;

    create table hibernate_sequence (
       next_val bigint
    ) engine=InnoDB;

    insert into hibernate_sequence values ( 1 );

    create table loop_logs (
       id bigint not null,
        log_component varchar(255) not null,
        log_instant datetime(6) not null,
        log_type varchar(255) not null,
        message MEDIUMTEXT not null,
        loop_id varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table loop_template (
       template_id varchar(255) not null,
        svg_image MEDIUMTEXT,
        template_name varchar(255) not null,
        template_yaml MEDIUMTEXT,
        timestamp datetime(6),
        updated_by varchar(255),
        primary key (template_id)
    ) engine=InnoDB;

    create table loop_template_policy (
       template_policy_id varchar(255) not null,
        policy_model_id varchar(255),
        template_id varchar(255),
        primary key (template_policy_id)
    ) engine=InnoDB;

    create table loops (
       name varchar(255) not null,
        blueprint_yaml MEDIUMTEXT not null,
        dcae_blueprint_id varchar(255),
        dcae_deployment_id varchar(255),
        dcae_deployment_status_url varchar(255),
        global_properties_json json,
        last_computed_state varchar(255) not null,
        operational_policy_schema json,
        svg_representation MEDIUMTEXT,
        service_uuid varchar(255),
        primary key (name)
    ) engine=InnoDB;

    create table loops_microservicepolicies (
       loop_id varchar(255) not null,
        microservicepolicy_id varchar(255) not null,
        primary key (loop_id, microservicepolicy_id)
    ) engine=InnoDB;

    create table micro_service_policies (
       name varchar(255) not null,
        json_representation json not null,
        model_type varchar(255) not null,
        policy_tosca MEDIUMTEXT not null,
        properties json,
        shared bit not null,
        primary key (name)
    ) engine=InnoDB;

    create table model_policy_properties (
       policy_name varchar(255) not null,
        context varchar(255) not null,
        device_type_scope varchar(255) not null,
        timestamp datetime(6),
        name varchar(255),
        config_json MEDIUMTEXT,
        policy_type varchar(255),
        user_id varchar(255),
        model_id varchar(255) not null,
        primary key (policy_name)
    ) engine=InnoDB;

    create table operational_policies (
       name varchar(255) not null,
        configurations_json json,
        loop_id varchar(255) not null,
        primary key (name)
    ) engine=InnoDB;

    create table services (
       service_uuid varchar(255) not null,
        name varchar(255) not null,
        resource_details json,
        service_details json,
        primary key (service_uuid)
    ) engine=InnoDB;

    create table tosca_model (
       tosca_model_id varchar(255) not null,
        timestamp datetime(6),
        policy_type varchar(255) not null,
        tosca_model_name varchar(255) not null,
        user_id varchar(255),
        primary key (tosca_model_id)
    ) engine=InnoDB;

    create table tosca_model_revision (
       tosca_model_revision_id varchar(255) not null,
        created_timestamp datetime(6),
        last_updated_timestamp datetime(6),
        tosca_model_json json,
        tosca_model_yaml MEDIUMTEXT,
        user_id varchar(255),
        version double precision,
        tosca_model_id varchar(255),
        primary key (tosca_model_revision_id)
    ) engine=InnoDB;

    alter table dictionary 
       add constraint UK_qk6of3c7j1jxvbnsu2y7whaeq unique (dictionary_name);

    alter table dictionary_elements 
       add constraint UK_ptlxs9ghd0t0o2qy7y088025e unique (dict_element_name);

    alter table dictionary_elements 
       add constraint UK_9o4n1u9qkmy1kkuwlmfrd130b unique (dict_element_short_name);

    alter table loop_template 
       add constraint UK_12l9mwo1kbpysv6ldcds8yypr unique (template_name);

    alter table tosca_model 
       add constraint UK_r3kar8qslk568dgeqtwtdfat6 unique (policy_type);

    alter table tosca_model 
       add constraint UK_t23qeixymkxekll0vr3junxaf unique (tosca_model_name);

    alter table dictionary_elements 
       add constraint FKn87bpgpm9i56w7uko585rbkgn 
       foreign key (dictionary_id) 
       references dictionary (dictionary_id);

    alter table loop_logs 
       add constraint FK1j0cda46aickcaoxqoo34khg2 
       foreign key (loop_id) 
       references loops (name);

    alter table loop_template_policy 
       add constraint FKoa34urq7mn1u4rprd4mf210s5 
       foreign key (template_id) 
       references loop_template (template_id);

    alter table loops 
       add constraint FK4b9wnqopxogwek014i1shqw7w 
       foreign key (service_uuid) 
       references services (service_uuid);

    alter table loops_microservicepolicies 
       add constraint FKem7tp1cdlpwe28av7ef91j1yl 
       foreign key (microservicepolicy_id) 
       references micro_service_policies (name);

    alter table loops_microservicepolicies 
       add constraint FKsvx91jekgdkfh34iaxtjfgebt 
       foreign key (loop_id) 
       references loops (name);

    alter table model_policy_properties 
       add constraint FK55irqjn5nj03uyno6j7g6bpxo 
       foreign key (model_id) 
       references loops (name);

    alter table operational_policies 
       add constraint FK1ddoggk9ni2bnqighv6ecmuwu 
       foreign key (loop_id) 
       references loops (name);

    alter table tosca_model_revision 
       add constraint FKr1sxp8a7muheptemtj47i5fac 
       foreign key (tosca_model_id) 
       references tosca_model (tosca_model_id);
