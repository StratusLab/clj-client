(ns eu.stratuslab.client.storage
  "Contains commands to interact with the persistent disk
   service.  All commands take a keyword as the first target
   to indicate the subcommand to execute."
  (:use [eu.stratuslab.client.configuration :only (*cfg*)])
  (:require [clj-http.client :as http]))

(defn- list-volumes [opts]
  (let [request-opts (merge {:method :get} opts)]
    (:body (http/request request-opts))))

(defn- create-volume [opts size tag]
  (let [request-opts (merge {:method :post                                   
                             :form-params {:size size :tag tag}}
                            opts)]  
    (get-in (http/request request-opts) [:body :uuid])))

(defn- delete-volume [opts]
  (let [request-opts (merge {:method :delete} opts)]
    (get-in (http/request request-opts) [:body :uuid])))

(defn extract-options [ & args]
  (let [uuid (first args)
        host (or (:pdisk_endpoint *cfg*) (:endpoint *cfg*))
        username (:username *cfg*)
        password (:password *cfg*)]
    {:url (str "https://" host ":8445/pswd/disks/" uuid)
     :basic-auth [username password]
     :insecure? true
     :accept :json
     :as :json}))

(defmulti storage
  (fn [& args] (first args))
  :default nil)

(defmethod storage nil
  [& args]
  "don't know how to deal with nil")

(defmethod storage :list
  [& args]
  (list-volumes (extract-options)))

(defmethod storage :create
  [& args]
  (let [[size tag] (rest args)]
    (create-volume (extract-options) size tag)))

(defmethod storage :delete
  [& args]
  (let [[uuid] (rest args)]
    (delete-volume (extract-options uuid))))
