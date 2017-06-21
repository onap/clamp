/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights
 *                             reserved.
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
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */

package org.onap.clamp.clds.service;

/**
 * Permission class that can be instantiated easily using constructor or factory methods.
 */
public class SecureServicePermission {
    public final static String ALL = "*";

    private String type;
    private String instance;
    private String action;

    /**
     * Factory method to create permission given type, instance, and action.
     *
     * @param type
     * @param instance
     * @param action
     * @return
     */
    public static SecureServicePermission create(String type, String instance, String action) {
        return new SecureServicePermission(type, instance, action);
    }

    /**
     * Factory method to create permission given type and instance.  Default action to ALL/*.
     *
     * @param type
     * @param instance
     * @return
     */
    public static SecureServicePermission create(String type, String instance) {
        return new SecureServicePermission(type, instance);
    }

    /**
     * Factory method to create permission given type.  Default instance and action to ALL/*.
     *
     * @param type
     * @return
     */
    public static SecureServicePermission create(String type) {
        return new SecureServicePermission(type);
    }

    /**
     * Instantiate permission given type, instance, and action.
     *
     * @param type
     * @param instance
     * @param action
     */
    public SecureServicePermission(String type, String instance, String action) {
        this.type = type;
        this.instance = instance;
        this.action = action;
    }

    /**
     * Instantiate permission given type and instance.  Default action to ALL/*.
     *
     * @param type
     * @param instance
     */
    public SecureServicePermission(String type, String instance) {
        this.type = type;
        this.instance = instance;
        this.action = ALL;
    }

    /**
     * Instantiate permission given type.  Default instance and action to ALL/*.
     *
     * @param type
     */
    public SecureServicePermission(String type) {
        this.type = type;
        this.instance = ALL;
        this.action = ALL;
    }

    /**
     * Override toString - return permission in key format
     */
    public String toString() {
        return getKey();
    }

    /**
     * Return Permission in Key format = type, instance, and action separate by pipe character.
     *
     * @return
     */
    public String getKey() {
        return type + "|" + instance + "|" + action;
    }

    /**
     * Return Permission in Key format = type, all instance, and action separate by pipe character.
     *
     * @return
     */
    public String getKeyAllInstance() {
        return type + "|" + ALL + "|" + action;
    }

    /**
     * Return Permission in Key format = type, all instance, and all action separate by pipe character.
     *
     * @return
     */
    public String getKeyAllInstanceAction() {
        return type + "|" + ALL + "|" + ALL;
    }

    /**
     * Return Permission in Key format = type, instance, and all action separate by pipe character.
     *
     * @return
     */
    public String getKeyAllAction() {
        return type + "|" + instance + "|" + ALL;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the instance
     */
    public String getInstance() {
        return instance;
    }

    /**
     * @param instance the instance to set
     */
    public void setInstance(String instance) {
        this.instance = instance;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

}
