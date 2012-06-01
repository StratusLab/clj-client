(ns eu.stratuslab.client.storage
  "Contains commands to interact with the persistent disk
   service.  All commands take a keyword as the first target
   to indicate the subcommand to execute."
  (:require [clj-http.client :as http]))

(defn list-volumes []                 
  (:body (http/request {:method :get                       
                        :url "https://pdisk.lal.stratuslab.eu:8445/pswd/disks"
                        :insecure? true                                   
                        :basic-auth ["cal" "xxx"]
                        :accept :json
                        :as :json})))

(defn create-volume [size tag]                 
  (get-in (http/request {:method :post                       
                         :url "https://pdisk.lal.stratuslab.eu:8445/pswd/disks"
                         :insecure? true                                   
                         :basic-auth ["cal" "xxx"]
                         :form-params {:size size :tag tag}
                         :accept :json
                         :as :json})
          [:body :uuid]))

(defn delete-volume [uuid]                 
  (get-in (http/request {:method :delete                     
                         :url (str "https://pdisk.lal.stratuslab.eu:8445/pswd/disks/" uuid)
                         :insecure? true                                   
                         :basic-auth ["cal" "xxx"]
                         :accept :json
                         :as :json})
          [:body :uuid]))

(defmulti storage
  (fn [& args] (first args))
  :default nil)

(defmethod storage nil
  [& args]
  "don't know how to deal with nil")

(defmethod storage :list
  [& args]
  (list-volumes))

(defmethod storage :create
  [& args]
  (let [[size tag] (rest args)]
    (create-volume size tag)))

(defmethod storage :delete
  [& args]
  (let [[uuid] (rest args)]
    (delete-volume uuid)))

