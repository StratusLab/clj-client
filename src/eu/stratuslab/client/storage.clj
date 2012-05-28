(ns eu.stratuslab.client.storage
  "Contains commands to interact with the persistent disk
   service.  All commands take a keyword as the first target
   to indicate the subcommand to execute."
  (:require [clj-http.client :as http]))

(defmulti storage
  (fn [& args] (first args))
  :default nil)

(defmethod storage nil
  [& args]
  "don't know how to deal with nil")

(defmethod storage :list
  [& args]
  "list volumes")

(defmethod storage :create
  [& args]
  "create volume")

(defmethod storage :delete
  [& args]
  "delete volumes")
