/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2017-2019 AT&T Intellectual Property. All rights
 *                             reserved.
 * ================================================================================
 * Modifications Copyright (c) 2019 Samsung
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END============================================
 * ===================================================================
 *
 */

package org.onap.clamp.clds.dao;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.onap.clamp.clds.model.CldsDictionary;
import org.onap.clamp.clds.model.CldsDictionaryItem;
import org.onap.clamp.clds.model.CldsEvent;
import org.onap.clamp.clds.model.CldsModel;
import org.onap.clamp.clds.model.CldsModelInstance;
import org.onap.clamp.clds.model.CldsModelProp;
import org.onap.clamp.clds.model.CldsMonitoringDetails;
import org.onap.clamp.clds.model.CldsTemplate;
import org.onap.clamp.clds.model.CldsToscaModel;
import org.onap.clamp.clds.model.ValueItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

/**
 * Data Access for CLDS Model tables.
 */
@Repository("cldsDao")
public class CldsDao {

    private static final EELFLogger logger = EELFManager.getInstance().getLogger(CldsDao.class);
    private JdbcTemplate jdbcTemplateObject;
    private SimpleJdbcCall procGetModel;
    private SimpleJdbcCall procGetModelTemplate;
    private SimpleJdbcCall procSetModel;
    private SimpleJdbcCall procInsEvent;
    private SimpleJdbcCall procUpdEvent;
    private SimpleJdbcCall procSetTemplate;
    private SimpleJdbcCall procGetTemplate;
    private SimpleJdbcCall procDelAllModelInstances;
    private SimpleJdbcCall procInsModelInstance;
    private SimpleJdbcCall procDeleteModel;
    private static final String HEALTHCHECK = "Select 1";
    private static final String V_CONTROL_NAME_PREFIX = "v_control_name_prefix";
    private static final String V_CONTROL_NAME_UUID = "v_control_name_uuid";

    private SimpleJdbcCall procInsertToscaModel;
    private SimpleJdbcCall procInsertNewToscaModelVersion;
    private SimpleJdbcCall procInsertDictionary;
    private SimpleJdbcCall procInsertDictionaryElement;

    private static final String DATE_FORMAT = "MM-dd-yyyy HH:mm:ss";

    /**
     * Log message when instantiating.
     */
    @Autowired
    public CldsDao(@Qualifier("cldsDataSource") DataSource dataSource) {
        logger.info("CldsDao instantiating...");
        setDataSource(dataSource);
    }

    /**
     * When dataSource is provided, instantiate spring jdbc objects.
     *
     * @param dataSource
     *        the data source
     */
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplateObject = new JdbcTemplate(dataSource);
        this.procGetModel = new SimpleJdbcCall(dataSource).withProcedureName("get_model");
        this.procGetModelTemplate = new SimpleJdbcCall(dataSource).withProcedureName("get_model_template");
        this.procSetModel = new SimpleJdbcCall(dataSource).withProcedureName("set_model");
        this.procInsEvent = new SimpleJdbcCall(dataSource).withProcedureName("ins_event");
        this.procUpdEvent = new SimpleJdbcCall(dataSource).withProcedureName("upd_event");
        this.procGetTemplate = new SimpleJdbcCall(dataSource).withProcedureName("get_template");
        this.procSetTemplate = new SimpleJdbcCall(dataSource).withProcedureName("set_template");
        this.procInsModelInstance = new SimpleJdbcCall(dataSource).withProcedureName("ins_model_instance");
        this.procDelAllModelInstances = new SimpleJdbcCall(dataSource).withProcedureName("del_all_model_instances");
        this.procDeleteModel = new SimpleJdbcCall(dataSource).withProcedureName("del_model");
        this.procInsertToscaModel = new SimpleJdbcCall(dataSource).withProcedureName("set_tosca_model");
        this.procInsertNewToscaModelVersion = new SimpleJdbcCall(dataSource)
            .withProcedureName("set_new_tosca_model_version");
        this.procInsertDictionary = new SimpleJdbcCall(dataSource).withProcedureName("set_dictionary");
        this.procInsertDictionaryElement = new SimpleJdbcCall(dataSource).withProcedureName("set_dictionary_elements");
    }

    /**
     * Get a model from the database given the model name.
     *
     * @param modelName
     *        the model name
     * @return the model
     */
    public CldsModel getModel(String modelName) {
        return getModel(modelName, null);
    }

    // Get a model from the database given the model name or a controlNameUuid.
    private CldsModel getModel(String modelName, String controlNameUuid) {
        CldsModel model = new CldsModel();
        model.setName(modelName);
        SqlParameterSource in = new MapSqlParameterSource().addValue("v_model_name", modelName)
            .addValue(V_CONTROL_NAME_UUID, controlNameUuid);
        Map<String, Object> out = logSqlExecution(procGetModel, in);
        populateModelProperties(model, out);
        return model;
    }

    /**
     * Get a model from the database given the controlNameUuid.
     *
     * @param controlNameUuid
     *        the control name uuid
     * @return the model by uuid
     */
    public CldsModel getModelByUuid(String controlNameUuid) {
        return getModel(null, controlNameUuid);
    }

    /**
     * Get a model and template information from the database given the model name.
     *
     * @param modelName
     *        the model name
     * @return model model template
     */

    public CldsModel getModelTemplate(String modelName) {
        CldsModel model = new CldsModel();
        model.setName(modelName);
        SqlParameterSource in = new MapSqlParameterSource().addValue("v_model_name", modelName);
        Map<String, Object> out = logSqlExecution(procGetModelTemplate, in);
        populateModelProperties(model, out);
        Map<String, Object> modelResults = logSqlExecution(procGetModel, in);
        Object modelResultObject = modelResults.get("#result-set-1");
        if (modelResultObject instanceof ArrayList) {
            for (Object currModelInstance : (List<Object>) modelResultObject) {
                if (currModelInstance instanceof HashMap) {
                    HashMap<String, String> modelInstanceMap = (HashMap<String, String>) currModelInstance;
                    CldsModelInstance modelInstance = new CldsModelInstance();
                    modelInstance.setModelInstanceId(modelInstanceMap.get("model_instance_id"));
                    modelInstance.setVmName(modelInstanceMap.get("vm_name"));
                    modelInstance.setLocation(modelInstanceMap.get("location"));
                    model.getCldsModelInstanceList().add(modelInstance);
                    logger.info("value of currModel: {}", currModelInstance);
                }
            }
        }
        return model;
    }

    /**
     * Update model in the database using parameter values and return updated model
     * object.
     *
     * @param model
     *        the model
     * @param userid
     *        the userid
     * @return model
     */
    public CldsModel setModel(CldsModel model, String userid) {
        SqlParameterSource in = new MapSqlParameterSource().addValue("v_model_name", model.getName())
            .addValue("v_template_id", model.getTemplateId()).addValue("v_user_id", userid)
            .addValue("v_model_prop_text", model.getPropText())
            .addValue("v_model_blueprint_text", model.getBlueprintText())
            .addValue("v_service_type_id", model.getTypeId()).addValue("v_deployment_id", model.getDeploymentId())
            .addValue("v_deployment_status_url", model.getDeploymentStatusUrl())
            .addValue("v_control_name_prefix", model.getControlNamePrefix())
            .addValue(V_CONTROL_NAME_UUID, model.getControlNameUuid());
        Map<String, Object> out = logSqlExecution(procSetModel, in);
        model.setControlNamePrefix((String) out.get(V_CONTROL_NAME_PREFIX));
        model.setControlNameUuid((String) out.get(V_CONTROL_NAME_UUID));
        model.setId((String) (out.get("v_model_id")));
        model.getEvent().setId((String) (out.get("v_event_id")));
        model.getEvent().setActionCd((String) out.get("v_action_cd"));
        model.getEvent().setActionStateCd((String) out.get("v_action_state_cd"));
        model.getEvent().setProcessInstanceId((String) out.get("v_event_process_instance_id"));
        model.getEvent().setUserid((String) out.get("v_event_user_id"));
        return model;
    }

    /**
     * Inserts new modelInstance in the database using parameter values and return
     * updated model object.
     *
     * @param model
     *        the model
     * @param modelInstancesList
     *        the model instances list
     */
    public void insModelInstance(CldsModel model, List<CldsModelInstance> modelInstancesList) {
        // Delete all existing model instances for given controlNameUUID
        logger.debug("deleting instances for: {}", model.getControlNameUuid());
        delAllModelInstances(model.getControlNameUuid());
        if (modelInstancesList == null) {
            logger.debug("modelInstancesList == null");
        } else {
            for (CldsModelInstance currModelInstance : modelInstancesList) {
                logger.debug("v_control_name_uuid={}", model.getControlNameUuid());
                logger.debug("v_vm_name={}", currModelInstance.getVmName());
                logger.debug("v_location={}", currModelInstance.getLocation());
                SqlParameterSource in = new MapSqlParameterSource()
                    .addValue(V_CONTROL_NAME_UUID, model.getControlNameUuid())
                    .addValue("v_vm_name", currModelInstance.getVmName())
                    .addValue("v_location", currModelInstance.getLocation());
                Map<String, Object> out = logSqlExecution(procInsModelInstance, in);
                model.setId((String) (out.get("v_model_id")));
                CldsModelInstance modelInstance = new CldsModelInstance();
                modelInstance.setLocation(currModelInstance.getLocation());
                modelInstance.setVmName(currModelInstance.getVmName());
                modelInstance.setModelInstanceId((String) (out.get("v_model_instance_id")));
                model.getCldsModelInstanceList().add(modelInstance);
            }
        }
    }

    /**
     * Insert an event in the database - require either modelName or
     * controlNamePrefix/controlNameUuid.
     *
     * @param modelName
     *        the model name
     * @param controlNamePrefix
     *        the control name prefix
     * @param controlNameUuid
     *        the control name uuid
     * @param cldsEvent
     *        the clds event
     * @return clds event
     */
    public CldsEvent insEvent(String modelName, String controlNamePrefix, String controlNameUuid, CldsEvent cldsEvent) {
        CldsEvent event = new CldsEvent();
        SqlParameterSource in = new MapSqlParameterSource().addValue("v_model_name", modelName)
            .addValue(V_CONTROL_NAME_PREFIX, controlNamePrefix).addValue(V_CONTROL_NAME_UUID, controlNameUuid)
            .addValue("v_user_id", cldsEvent.getUserid()).addValue("v_action_cd", cldsEvent.getActionCd())
            .addValue("v_action_state_cd", cldsEvent.getActionStateCd())
            .addValue("v_process_instance_id", cldsEvent.getProcessInstanceId());
        Map<String, Object> out = logSqlExecution(procInsEvent, in);
        event.setId((String) (out.get("v_event_id")));
        return event;
    }

    private String delAllModelInstances(String controlNameUUid) {
        SqlParameterSource in = new MapSqlParameterSource().addValue(V_CONTROL_NAME_UUID, controlNameUUid);
        Map<String, Object> out = logSqlExecution(procDelAllModelInstances, in);
        return (String) (out.get("v_model_id"));
    }

    /**
     * Update event with process instance id.
     *
     * @param eventId
     *        the event id
     * @param processInstanceId
     *        the process instance id
     */
    public void updEvent(String eventId, String processInstanceId) {
        SqlParameterSource in = new MapSqlParameterSource().addValue("v_event_id", eventId)
            .addValue("v_process_instance_id", processInstanceId);
        logSqlExecution(procUpdEvent, in);
    }

    /**
     * Return list of model names.
     *
     * @return model names
     */
    public List<ValueItem> getModelNames() {
        String sql = "SELECT model_name FROM model ORDER BY 1;";
        return jdbcTemplateObject.query(sql, new ValueItemMapper());
    }

    /**
     * Update template in the database using parameter values and return updated
     * template object.
     *
     * @param template
     *        the template
     * @param userid
     *        the userid
     */
    public void setTemplate(CldsTemplate template, String userid) {
        SqlParameterSource in = new MapSqlParameterSource().addValue("v_template_name", template.getName())
            .addValue("v_user_id", userid).addValue("v_template_bpmn_text", template.getBpmnText())
            .addValue("v_template_image_text", template.getImageText())
            .addValue("v_template_doc_text", template.getPropText());
        Map<String, Object> out = logSqlExecution(procSetTemplate, in);
        template.setId((String) (out.get("v_template_id")));
        template.setBpmnUserid((String) (out.get("v_template_bpmn_user_id")));
        template.setBpmnId((String) (out.get("v_template_bpmn_id")));
        template.setImageId((String) (out.get("v_template_image_id")));
        template.setImageUserid((String) out.get("v_template_image_user_id"));
        template.setPropId((String) (out.get("v_template_doc_id")));
        template.setPropUserid((String) out.get("v_template_doc_user_id"));
    }

    /**
     * Return list of template names.
     *
     * @return template names
     */
    public List<ValueItem> getTemplateNames() {
        String sql = "SELECT template_name FROM template ORDER BY 1;";
        return jdbcTemplateObject.query(sql, new ValueItemMapper());
    }

    /**
     * Get a template from the database given the model name.
     *
     * @param templateName
     *        the template name
     * @return model template
     */
    public CldsTemplate getTemplate(String templateName) {
        CldsTemplate template = new CldsTemplate();
        template.setName(templateName);
        SqlParameterSource in = new MapSqlParameterSource().addValue("v_template_name", templateName);
        Map<String, Object> out = logSqlExecution(procGetTemplate, in);
        template.setId((String) (out.get("v_template_id")));
        template.setBpmnUserid((String) (out.get("v_template_bpmn_user_id")));
        template.setBpmnId((String) (out.get("v_template_bpmn_id")));
        template.setBpmnText((String) (out.get("v_template_bpmn_text")));
        template.setImageId((String) (out.get("v_template_image_id")));
        template.setImageUserid((String) out.get("v_template_image_user_id"));
        template.setImageText((String) out.get("v_template_image_text"));
        template.setPropId((String) (out.get("v_template_doc_id")));
        template.setPropUserid((String) out.get("v_template_doc_user_id"));
        template.setPropText((String) out.get("v_template_doc_text"));
        return template;
    }

    private static Map<String, Object> logSqlExecution(SimpleJdbcCall call, SqlParameterSource source) {
        try {
            return call.execute(source);
        } catch (Exception e) {
            logger.error("Exception occured in " + source.getClass().getCanonicalName() + ": " + e);
            throw e;
        }
    }

    /**
     * Do health check.
     */
    public void doHealthCheck() {
        jdbcTemplateObject.execute(HEALTHCHECK);
    }

    /**
     * Method to get deployed/active models with model properties.
     *
     * @return list of CldsModelProp
     */
    public List<CldsModelProp> getDeployedModelProperties() {
        List<CldsModelProp> cldsModelPropList = new ArrayList<>();
        String modelsSql = "select m.model_id, m.model_name, mp.model_prop_id, mp.model_prop_text FROM model m, "
            + "model_properties mp, event e "
            + "WHERE m.model_prop_id = mp.model_prop_id and m.event_id = e.event_id and e.action_cd = 'DEPLOY'";
        List<Map<String, Object>> rows = jdbcTemplateObject.queryForList(modelsSql);
        CldsModelProp cldsModelProp = null;
        for (Map<String, Object> row : rows) {
            cldsModelProp = new CldsModelProp();
            cldsModelProp.setId((String) row.get("model_id"));
            cldsModelProp.setName((String) row.get("model_name"));
            cldsModelProp.setPropId((String) row.get("model_prop_id"));
            cldsModelProp.setPropText((String) row.get("model_prop_text"));
            cldsModelPropList.add(cldsModelProp);
        }
        return cldsModelPropList;
    }

    /**
     * Method to get deployed/active models with model properties.
     *
     * @return list of CLDS-Monitoring-Details: CLOSELOOP_NAME | Close loop name
     *         used in the CLDS application (prefix: ClosedLoop- + unique ClosedLoop
     *         ID) MODEL_NAME | Model Name in CLDS application SERVICE_TYPE_ID |
     *         TypeId returned from the DCAE application when the ClosedLoop is
     *         submitted (DCAEServiceTypeRequest generated in DCAE application).
     *         DEPLOYMENT_ID | Id generated when the ClosedLoop is deployed in DCAE.
     *         TEMPLATE_NAME | Template used to generate the ClosedLoop model.
     *         ACTION_CD | Current state of the ClosedLoop in CLDS application.
     */
    public List<CldsMonitoringDetails> getCldsMonitoringDetails() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        List<CldsMonitoringDetails> cldsMonitoringDetailsList = new ArrayList<>();
        String modelsSql = "SELECT CONCAT(M.CONTROL_NAME_PREFIX, M.CONTROL_NAME_UUID) AS CLOSELOOP_NAME , "
            + "M.MODEL_NAME, M.SERVICE_TYPE_ID, M.DEPLOYMENT_ID, T.TEMPLATE_NAME, E.ACTION_CD, E.USER_ID, E.TIMESTAMP "
            + "FROM MODEL M, TEMPLATE T, EVENT E " + "WHERE M.TEMPLATE_ID = T.TEMPLATE_ID AND M.EVENT_ID = E.EVENT_ID "
            + "ORDER BY ACTION_CD";
        List<Map<String, Object>> rows = jdbcTemplateObject.queryForList(modelsSql);
        CldsMonitoringDetails cldsMonitoringDetails = null;
        for (Map<String, Object> row : rows) {
            cldsMonitoringDetails = new CldsMonitoringDetails();
            cldsMonitoringDetails.setCloseloopName((String) row.get("CLOSELOOP_NAME"));
            cldsMonitoringDetails.setModelName((String) row.get("MODEL_NAME"));
            cldsMonitoringDetails.setServiceTypeId((String) row.get("SERVICE_TYPE_ID"));
            cldsMonitoringDetails.setDeploymentId((String) row.get("DEPLOYMENT_ID"));
            cldsMonitoringDetails.setTemplateName((String) row.get("TEMPLATE_NAME"));
            cldsMonitoringDetails.setAction((String) row.get("ACTION_CD"));
            cldsMonitoringDetails.setUserid((String) row.get("USER_ID"));
            cldsMonitoringDetails.setTimestamp(sdf.format(row.get("TIMESTAMP")));
            cldsMonitoringDetailsList.add(cldsMonitoringDetails);
        }
        return cldsMonitoringDetailsList;
    }

    /**
     * Method to delete model from database.
     *
     * @param modelName
     *        the model name
     */
    public void deleteModel(String modelName) {
        SqlParameterSource in = new MapSqlParameterSource().addValue("v_model_name", modelName);
        logSqlExecution(procDeleteModel, in);
    }

    private void populateModelProperties(CldsModel model, Map out) {
        model.setControlNamePrefix((String) out.get(V_CONTROL_NAME_PREFIX));
        model.setControlNameUuid((String) out.get(V_CONTROL_NAME_UUID));
        model.setId((String) (out.get("v_model_id")));
        model.setTemplateId((String) (out.get("v_template_id")));
        model.setTemplateName((String) (out.get("v_template_name")));
        model.setBpmnText((String) out.get("v_template_bpmn_text"));
        model.setPropText((String) out.get("v_model_prop_text"));
        model.setImageText((String) out.get("v_template_image_text"));
        model.setDocText((String) out.get("v_template_doc_text"));
        model.setBlueprintText((String) out.get("v_model_blueprint_text"));
        model.getEvent().setId((String) (out.get("v_event_id")));
        model.getEvent().setActionCd((String) out.get("v_action_cd"));
        model.getEvent().setActionStateCd((String) out.get("v_action_state_cd"));
        model.getEvent().setProcessInstanceId((String) out.get("v_event_process_instance_id"));
        model.getEvent().setUserid((String) out.get("v_event_user_id"));
        model.setTypeId((String) out.get("v_service_type_id"));
        model.setDeploymentId((String) out.get("v_deployment_id"));
        model.setDeploymentStatusUrl((String) out.get("v_deployment_status_url"));
    }

    /**
     * Method to retrieve a tosca models by Policy Type from database.
     *
     * @return List of CldsToscaModel
     */
    public List<CldsToscaModel> getAllToscaModels() {
        return getToscaModel(null, null);
    }

    /**
     * Method to retrieve a tosca models by Policy Type from database.
     *
     * @param policyType
     *        the policy type
     * @return List of CldsToscaModel
     */
    public List<CldsToscaModel> getToscaModelByPolicyType(String policyType) {
        return getToscaModel(null, policyType);
    }

    /**
     * Method to retrieve a tosca models by toscaModelName, version from database.
     *
     * @param toscaModelName
     *        the tosca model name
     * @return List of CldsToscaModel
     */
    public List<CldsToscaModel> getToscaModelByName(String toscaModelName) {
        return getToscaModel(toscaModelName, null);
    }

    // Retrieve the latest tosca model for a policy type or by tosca model name

    private List<CldsToscaModel> getToscaModel(String toscaModelName, String policyType) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        List<CldsToscaModel> cldsToscaModels = new ArrayList<>();

        String toscaModelSql = new StringBuilder("SELECT tm.tosca_model_name, tm.tosca_model_id, tm.policy_type, " +
                "tmr.tosca_model_revision_id, tmr.tosca_model_json, tmr.version, tmr.user_id, tmr.createdTimestamp, " +
                "tmr.lastUpdatedTimestamp")
                .append(toscaModelName != null ? (", tmr.tosca_model_yaml") : "")
                .append(" FROM tosca_model tm, tosca_model_revision tmr WHERE tm.tosca_model_id = tmr.tosca_model_id")
                .append(toscaModelName != null ? (" AND tm.tosca_model_name = '" + toscaModelName + "'") : "")
                .append(policyType != null ? (" AND tm.policy_type = '" + policyType + "'") : "")
                .append(" AND tmr.version = (select max(version) from tosca_model_revision st where tmr.tosca_model_id=st.tosca_model_id)")
                .toString();

        List<Map<String, Object>> rows = jdbcTemplateObject.queryForList(toscaModelSql);

        if (rows != null) {
            rows.forEach(row -> {
                CldsToscaModel cldsToscaModel = new CldsToscaModel();
                cldsToscaModel.setId((String) row.get("tosca_model_id"));
                cldsToscaModel.setPolicyType((String) row.get("policy_type"));
                cldsToscaModel.setToscaModelName((String) row.get("tosca_model_name"));
                cldsToscaModel.setUserId((String) row.get("user_id"));
                cldsToscaModel.setRevisionId((String) row.get("tosca_model_revision_id"));
                cldsToscaModel.setToscaModelJson((String) row.get("tosca_model_json"));
                cldsToscaModel.setVersion(((Double) row.get("version")));
                cldsToscaModel.setCreatedDate(sdf.format(row.get("createdTimestamp")));
                cldsToscaModel.setLastUpdatedDate(sdf.format(row.get("lastUpdatedTimestamp")));
                if (toscaModelName != null) {
                    cldsToscaModel.setToscaModelYaml((String) row.get("tosca_model_yaml"));
                }
                cldsToscaModels.add(cldsToscaModel);
            });
        }
        return cldsToscaModels;
    }

    /**
     * Method to upload a new version of Tosca Model Yaml in Database.
     *
     * @param cldsToscaModel
     *        the clds tosca model
     * @param userId
     *        the user id
     * @return CldsToscaModel clds tosca model
     */
    public CldsToscaModel updateToscaModelWithNewVersion(CldsToscaModel cldsToscaModel, String userId) {
        SqlParameterSource in = new MapSqlParameterSource().addValue("v_tosca_model_id", cldsToscaModel.getId())
            .addValue("v_version", cldsToscaModel.getVersion())
            .addValue("v_tosca_model_yaml", cldsToscaModel.getToscaModelYaml())
            .addValue("v_tosca_model_json", cldsToscaModel.getToscaModelJson()).addValue("v_user_id", userId);
        Map<String, Object> out = logSqlExecution(procInsertNewToscaModelVersion, in);
        cldsToscaModel.setRevisionId((String) (out.get("v_revision_id")));
        return cldsToscaModel;
    }

    /**
     * Method to upload a new Tosca model Yaml in DB. Default version is 1.0
     *
     * @param cldsToscaModel
     *        the clds tosca model
     * @param userId
     *        the user id
     * @return CldsToscaModel clds tosca model
     */
    public CldsToscaModel insToscaModel(CldsToscaModel cldsToscaModel, String userId) {
        SqlParameterSource in = new MapSqlParameterSource()
            .addValue("v_tosca_model_name", cldsToscaModel.getToscaModelName())
            .addValue("v_policy_type", cldsToscaModel.getPolicyType())
            .addValue("v_tosca_model_yaml", cldsToscaModel.getToscaModelYaml())
            .addValue("v_tosca_model_json", cldsToscaModel.getToscaModelJson())
            .addValue("v_version", cldsToscaModel.getVersion()).addValue("v_user_id", userId);
        Map<String, Object> out = logSqlExecution(procInsertToscaModel, in);
        cldsToscaModel.setId((String) (out.get("v_tosca_model_id")));
        cldsToscaModel.setRevisionId((String) (out.get("v_revision_id")));
        cldsToscaModel.setUserId((String) out.get("v_user_id"));
        return cldsToscaModel;
    }

    /**
     * Method to insert a new Dictionary in Database.
     *
     * @param cldsDictionary
     *        the clds dictionary
     */
    public void insDictionary(CldsDictionary cldsDictionary) {
        SqlParameterSource in = new MapSqlParameterSource()
            .addValue("v_dictionary_name", cldsDictionary.getDictionaryName())
            .addValue("v_user_id", cldsDictionary.getCreatedBy());
        Map<String, Object> out = logSqlExecution(procInsertDictionary, in);
        cldsDictionary.setDictionaryId((String) (out.get("v_dictionary_id")));
    }

    /**
     * Method to update Dictionary with new info in Database.
     *
     * @param dictionaryId
     *        the dictionary id
     * @param cldsDictionary
     *        the clds dictionary
     * @param userId
     *        the user id
     */
    public void updateDictionary(String dictionaryId, CldsDictionary cldsDictionary, String userId) {

        String dictionarySql = new StringBuilder("UPDATE dictionary SET dictionary_name = '")
                .append(cldsDictionary.getDictionaryName())
                .append("', modified_by = '").append(userId)
                .append("'WHERE dictionary_id = '").append(dictionaryId).append("'")
                .toString();
        jdbcTemplateObject.update(dictionarySql);
        cldsDictionary.setUpdatedBy(userId);
    }

    /**
     * Method to get list of Dictionaries from the Database.
     *
     * @param dictionaryId
     *        the dictionary id
     * @param dictionaryName
     *        the dictionary name
     * @return dictionary
     */
    public List<CldsDictionary> getDictionary(String dictionaryId, String dictionaryName) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        List<CldsDictionary> dictionaries = new ArrayList<>();

        String whereFilter = " WHERE ";
        if (dictionaryName != null) {
            whereFilter += "dictionary_name = '" + dictionaryName + "'";
            if (dictionaryId != null){
                whereFilter += " AND dictionary_id= '" + dictionaryId + "'";
            }
        } else if (dictionaryId != null) {
            whereFilter += "dictionary_id = '" + dictionaryId + "'";
        } else {
            whereFilter = "";
        }
        String dictionarySql = new StringBuilder("SELECT dictionary_id, dictionary_name, created_by, " +
                "modified_by, timestamp FROM dictionary")
                .append(whereFilter).toString();

        List<Map<String, Object>> rows = jdbcTemplateObject.queryForList(dictionarySql);

        if (rows != null) {
            rows.forEach(row -> {
                CldsDictionary cldsDictionary = new CldsDictionary();
                cldsDictionary.setDictionaryId((String) row.get("dictionary_id"));
                cldsDictionary.setDictionaryName((String) row.get("dictionary_name"));
                cldsDictionary.setCreatedBy((String) row.get("created_by"));
                cldsDictionary.setUpdatedBy((String) row.get("modified_by"));
                cldsDictionary.setLastUpdatedDate(sdf.format(row.get("timestamp")));
                dictionaries.add(cldsDictionary);
            });
        }
        return dictionaries;
    }

    /**
     * Method to insert a new Dictionary Element for given dictionary in Database.
     *
     * @param cldsDictionaryItem
     *        the clds dictionary item
     * @param userId
     *        the user id
     */
    public void insDictionarElements(CldsDictionaryItem cldsDictionaryItem, String userId) {
        SqlParameterSource in = new MapSqlParameterSource()
            .addValue("v_dictionary_id", cldsDictionaryItem.getDictionaryId())
            .addValue("v_dict_element_name", cldsDictionaryItem.getDictElementName())
            .addValue("v_dict_element_short_name", cldsDictionaryItem.getDictElementShortName())
            .addValue("v_dict_element_description", cldsDictionaryItem.getDictElementDesc())
            .addValue("v_dict_element_type", cldsDictionaryItem.getDictElementType()).addValue("v_user_id", userId);
        Map<String, Object> out = logSqlExecution(procInsertDictionaryElement, in);
        cldsDictionaryItem.setDictElementId((String) (out.get("v_dict_element_id")));
    }

    /**
     * Method to update Dictionary Elements with new info for a given dictionary in
     * Database.
     *
     * @param dictionaryElementId
     *        the dictionary element id
     * @param cldsDictionaryItem
     *        the clds dictionary item
     * @param userId
     *        the user id
     */
    public void updateDictionaryElements(String dictionaryElementId, CldsDictionaryItem cldsDictionaryItem,
        String userId) {

        String dictionarySql = new StringBuilder().append("UPDATE dictionary_elements SET dict_element_name = '")
                .append(cldsDictionaryItem.getDictElementName())
                .append("', dict_element_short_name = '").append(cldsDictionaryItem.getDictElementShortName())
                .append("', dict_element_description= '").append(cldsDictionaryItem.getDictElementDesc())
                .append("', dict_element_type = '").append(cldsDictionaryItem.getDictElementType())
                .append("', modified_by = '").append(userId).append("'")
                .append(" WHERE dict_element_id = '")
                .append(dictionaryElementId).append("'")
                .toString();
        jdbcTemplateObject.update(dictionarySql);
        cldsDictionaryItem.setUpdatedBy(userId);
    }

    /**
     * Method to get list of all dictionary elements for a given dictionary in the
     * Database.
     *
     * @param dictionaryName
     *        the dictionary name
     * @param dictionaryId
     *        the dictionary id
     * @param dictElementShortName
     *        the dict element short name
     * @return dictionary elements
     */
    public List<CldsDictionaryItem> getDictionaryElements(String dictionaryName, String dictionaryId,
        String dictElementShortName) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        List<CldsDictionaryItem> dictionaryItems = new ArrayList<>();
        String dictionarySql = new StringBuilder("SELECT de.dict_element_id, de.dictionary_id, de.dict_element_name, " +
                "de.dict_element_short_name, de.dict_element_description, de.dict_element_type, de.created_by, " +
                "de.modified_by, de.timestamp FROM dictionary_elements de, " +
                "dictionary d WHERE de.dictionary_id = d.dictionary_id")
                .append((dictionaryId != null) ? (" AND d.dictionary_id = '" + dictionaryId + "'") : "")
                .append((dictElementShortName != null) ? (" AND de.dict_element_short_name = '" + dictElementShortName + "'") : "")
                .append((dictionaryName != null) ? (" AND dictionary_name = '" + dictionaryName + "'") : "").toString();

        List<Map<String, Object>> rows = jdbcTemplateObject.queryForList(dictionarySql);

        if (rows != null) {
            rows.forEach(row -> {
                CldsDictionaryItem dictionaryItem = new CldsDictionaryItem();
                dictionaryItem.setDictElementId((String) row.get("dict_element_id"));
                dictionaryItem.setDictionaryId((String) row.get("dictionary_id"));
                dictionaryItem.setDictElementName((String) row.get("dict_element_name"));
                dictionaryItem.setDictElementShortName((String) row.get("dict_element_short_name"));
                dictionaryItem.setDictElementDesc((String) row.get("dict_element_description"));
                dictionaryItem.setDictElementType((String) row.get("dict_element_type"));
                dictionaryItem.setCreatedBy((String) row.get("created_by"));
                dictionaryItem.setUpdatedBy((String) row.get("modified_by"));
                dictionaryItem.setLastUpdatedDate(sdf.format(row.get("timestamp")));
                dictionaryItems.add(dictionaryItem);
            });
        }
        return dictionaryItems;
    }

    /**
     * Method to get Map of all dictionary elements with key as dictionary short
     * name and value as the full name.
     *
     * @param dictionaryElementType
     *        the dictionary element type
     * @return Map of dictionary elements as key value pair
     */
    public Map<String, String> getDictionaryElementsByType(String dictionaryElementType) {
        Map<String, String> dictionaryItems = new HashMap<>();
        String dictionarySql = new StringBuilder("SELECT dict_element_name, dict_element_short_name " +
                "FROM dictionary_elements WHERE dict_element_type = '")
                .append(dictionaryElementType).append("'").toString();

        List<Map<String, Object>> rows = jdbcTemplateObject.queryForList(dictionarySql);

        if (rows != null) {
            rows.forEach(row -> {
                dictionaryItems.put(((String) row.get("dict_element_short_name")),
                    ((String) row.get("dict_element_name")));
            });
        }
        return dictionaryItems;
    }
}
