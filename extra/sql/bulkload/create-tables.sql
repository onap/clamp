
    create table dictionary (
       name varchar(255) not null,
        created_by varchar(255),
        created_timestamp datetime(6),
        dictionary_second_level integer,
        dictionary_type varchar(255),
        updated_by varchar(255),
        updated_timestamp datetime(6),
        primary key (name)
    ) engine=InnoDB;

    create table dictionary_elements (
       name varchar(255) not null,
        created_by varchar(255),
        created_timestamp datetime(6),
        description varchar(255),
        short_name varchar(255) not null,
        subdictionary_id varchar(255) not null,
        type varchar(255) not null,
        updated_by varchar(255),
        updated_timestamp datetime(6),
        primary key (name)
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

    create table loop_templates (
       name varchar(255) not null,
        blueprint_yaml MEDIUMTEXT not null,
        created_by varchar(255),
        created_timestamp datetime(6),
        svg_representation MEDIUMTEXT,
        updated_by varchar(255),
        updated_timestamp datetime(6),
        primary key (name)
    ) engine=InnoDB;

    create table loops (
       name varchar(255) not null,
        blueprint_yaml MEDIUMTEXT not null,
        created_by varchar(255),
        created_timestamp datetime(6),
        dcae_blueprint_id varchar(255),
        dcae_deployment_id varchar(255),
        dcae_deployment_status_url varchar(255),
        global_properties_json json,
        last_computed_state varchar(255) not null,
        operational_policy_schema json,
        svg_representation MEDIUMTEXT,
        updated_by varchar(255),
        updated_timestamp datetime(6),
        loop_template_id varchar(255),
        service_uuid varchar(255),
        primary key (name)
    ) engine=InnoDB;

    create table loops_microservicepolicies (
       loop_id varchar(255) not null,
        microservicepolicy_id varchar(255) not null,
        primary key (loop_id, microservicepolicy_id)
    ) engine=InnoDB;

    create table micro_service_models (
       name varchar(255) not null,
        blueprint_yaml varchar(255) not null,
        created_by varchar(255),
        created_timestamp datetime(6),
        flowOrder integer,
        policy_type varchar(255) not null,
        updated_by varchar(255),
        updated_timestamp datetime(6),
        policy_model_version varchar(255),
        policy_model_type varchar(255),
        primary key (name)
    ) engine=InnoDB;

    create table micro_service_policies (
       name varchar(255) not null,
        context varchar(255) not null,
        created_by varchar(255),
        created_timestamp datetime(6),
        device_type_scope varchar(255) not null,
        json_representation json not null,
        model_type varchar(255) not null,
        policy_tosca MEDIUMTEXT not null,
        properties json,
        shared bit not null,
        updated_by varchar(255),
        updated_timestamp datetime(6),
        micro_service_model_id varchar(255),
        primary key (name)
    ) engine=InnoDB;

    create table operational_policies (
       name varchar(255) not null,
        configurations_json json,
        loop_id varchar(255) not null,
        policy_model_version varchar(255),
        policy_model_type varchar(255),
        primary key (name)
    ) engine=InnoDB;

    create table policy_models (
       version varchar(255) not null,
        model_type varchar(255) not null,
        created_by varchar(255),
        created_timestamp datetime(6),
        policy_tosca MEDIUMTEXT,
        updated_by varchar(255),
        updated_timestamp datetime(6),
        primary key (version, model_type)
    ) engine=InnoDB;

    create table services (
       service_uuid varchar(255) not null,
        name varchar(255) not null,
        resource_details json,
        service_details json,
        primary key (service_uuid)
    ) engine=InnoDB;

    create table templates_microservicemodels (
       loop_template_id varchar(255) not null,
        micro_service_model_id varchar(255) not null,
        primary key (loop_template_id, micro_service_model_id)
    ) engine=InnoDB;

    alter table dictionary_elements 
       add constraint UK_qxkrvsrhp26m60apfvxphpl3d unique (short_name);

    alter table dictionary_elements 
       add constraint FK8a4d6tuuhtovlkjl6lp8lo8r7 
       foreign key (name) 
       references dictionary (name);

    alter table loop_logs 
       add constraint FK1j0cda46aickcaoxqoo34khg2 
       foreign key (loop_id) 
       references loops (name);

    alter table loops 
       add constraint FK2bmio9q5fsno1c1itbdi37wpu 
       foreign key (loop_template_id) 
       references loop_templates (name);

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

    alter table micro_service_models 
       add constraint FKlkcffpnuavcg65u5o4tr66902 
       foreign key (policy_model_version, policy_model_type) 
       references policy_models (version, model_type);

    alter table micro_service_policies 
       add constraint FK5p7lipy9m2v7d4n3fvlclwse 
       foreign key (micro_service_model_id) 
       references micro_service_models (name);

    alter table operational_policies 
       add constraint FK1ddoggk9ni2bnqighv6ecmuwu 
       foreign key (loop_id) 
       references loops (name);

    alter table operational_policies 
       add constraint FKlsyhfkoqvkwj78ofepxhoctip 
       foreign key (policy_model_version, policy_model_type) 
       references policy_models (version, model_type);

    alter table templates_microservicemodels 
       add constraint FKld2v9i0lfxhonu432nmr2mgx3 
       foreign key (micro_service_model_id) 
       references micro_service_models (name);

    alter table templates_microservicemodels 
       add constraint FK8ce4l60kwyeb58l0wos37mi2y 
       foreign key (loop_template_id) 
       references loop_templates (name);
