(ns eu.stratuslab.client.marketplace
  "Contains commands to interact with the Marketplace.
   All commands take a keyword as the first target
   to indicate the subcommand to execute."
  (:use [eu.stratuslab.client.configuration :only (*cfg*)])
  (:require [clj-http.client :as http]))

(defn- list-metadata [opts]
  (let [request-opts (merge {:method :get} opts)]
    (:body (http/request request-opts))))

(defn- extract-options [ & args]
  (let [host (or (:marketplace_endpoint *cfg*) "marketplace.stratuslab.eu")]
    {:url (str "https://" host ":443/metadata/")
     :insecure? true}))

(defmulti marketplace
  (fn [& args] (first args))
  :default nil)

(defmethod marketplace nil
  [& args]
  "don't know how to deal with nil")

(defmethod marketplace :list
  [& args]
  (list-metadata (extract-options)))
