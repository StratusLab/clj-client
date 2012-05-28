(ns eu.stratuslab.client.compute
  "Contains commands to interact with OpenNebula."
  (:require [clj-http.client :as http]))

(defmulti compute
  (fn [& args] (first args))
  :default nil)

(defmethod compute nil
  [& args]
  "don't know how to deal with nil")

(defmethod compute :list
  [& args]
  "list volumes")

(defmethod compute :create
  [& args]
  "create volume")

(defmethod compute :delete
  [& args]
  "delete volumes")


